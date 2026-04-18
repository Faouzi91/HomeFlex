#!/usr/bin/env bash
# =============================================================================
# HomeFlex — Full API surface smoke test
# =============================================================================
# Boots the docker-compose stack, waits for health, logs in as seeded admin /
# landlord / tenant, then hits every /api/v1/* endpoint plus frontend SPA routes
# and reports a PASS/FAIL summary.
#
# Usage:
#   ./scripts/test-all-apis.sh            # full run (boot + test)
#   SKIP_BOOT=1 ./scripts/test-all-apis.sh # assume stack already up
#   VERBOSE=1 ./scripts/test-all-apis.sh  # print response bodies
# =============================================================================

set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

BACKEND="${BACKEND:-http://localhost:8080}"
FRONTEND="${FRONTEND:-http://localhost:8001}"
MINIO="${MINIO:-http://localhost:9000}"
BASE="$BACKEND/api/v1"
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

ADMIN_JAR="$WORK/admin.jar"
LL_JAR="$WORK/landlord.jar"
T_JAR="$WORK/tenant.jar"
PUB_JAR="$WORK/pub.jar"

PASS=0
FAIL=0
SKIP=0
FAILURES=()

# Colors
if [ -t 1 ]; then
  RED=$'\033[31m'; GRN=$'\033[32m'; YEL=$'\033[33m'; DIM=$'\033[2m'; RST=$'\033[0m'
else
  RED=""; GRN=""; YEL=""; DIM=""; RST=""
fi

log()   { printf '%s\n' "$*"; }
info()  { printf '%s==>%s %s\n' "$DIM" "$RST" "$*"; }
header(){ printf '\n%s── %s ──%s\n' "$YEL" "$*" "$RST"; }

# -----------------------------------------------------------------------------
# Boot stack
# -----------------------------------------------------------------------------
if [ "${SKIP_BOOT:-0}" != "1" ]; then
  header "Starting docker compose stack"
  docker compose up -d --build backend frontend db redis rabbitmq elasticsearch minio \
    >/dev/null 2>&1 || { log "${RED}docker compose up failed${RST}"; exit 1; }
fi

header "Waiting for backend health"
for i in $(seq 1 60); do
  code=$(curl -sS -o /dev/null -w "%{http_code}" "$BACKEND/api/v1/config" 2>/dev/null || true)
  [ "$code" = "200" ] && { info "backend ready"; break; }
  sleep 2
  [ "$i" = "60" ] && { log "${RED}backend never became healthy${RST}"; exit 1; }
done

header "Waiting for frontend health"
for i in $(seq 1 30); do
  code=$(curl -sS -o /dev/null -w "%{http_code}" "$FRONTEND/" 2>/dev/null || true)
  [ "$code" = "200" ] && { info "frontend ready"; break; }
  sleep 2
done

# -----------------------------------------------------------------------------
# Auth setup — seed logins
# -----------------------------------------------------------------------------
csrf_of() { awk '$6=="XSRF-TOKEN"{print $7}' "$1" | tail -1; }

# login: fresh jar, seeds CSRF, authenticates. Retries up to 5× on 429.
login() {
  local jar=$1 email=$2 pass=$3
  rm -f "$jar"
  curl -sS -c "$jar" "$BASE/config" >/dev/null
  local attempts=0 code
  while [ $attempts -lt 5 ]; do
    local tok=$(csrf_of "$jar")
    code=$(curl -sS -b "$jar" -c "$jar" -H "X-XSRF-TOKEN: $tok" \
      -H "Content-Type: application/json" \
      -d "{\"email\":\"$email\",\"password\":\"$pass\"}" \
      -w "%{http_code}" -o /dev/null \
      "$BASE/auth/login")
    [ "$code" = "200" ] && { echo "$code"; return 0; }
    [ "$code" = "429" ] && { sleep 35; attempts=$((attempts+1)); continue; }
    attempts=$((attempts+1)); sleep 2
  done
  echo "$code"
}

header "Seeding logins"
curl -sS -c "$PUB_JAR" "$BASE/config" >/dev/null
info "admin login:    $(login "$ADMIN_JAR" admin@homeflex.com       admin123)"
info "landlord login: $(login "$LL_JAR"    landlord@test.com        'Landlord@123')"
info "tenant login:   $(login "$T_JAR"     tenant@test.com          'Tenant@123')"

# -----------------------------------------------------------------------------
# Assertion helpers
# -----------------------------------------------------------------------------
# relogin: full re-login when email/pass are provided (prevents 15-min token expiry),
# or CSRF-only refresh when called with just a jar.
relogin() {
  local jar=$1 email="${2:-}" pass="${3:-}"
  if [ -n "$email" ] && [ -n "$pass" ]; then
    login "$jar" "$email" "$pass" >/dev/null
  else
    curl -sS -c "$jar" -b "$jar" "$BASE/config" >/dev/null 2>&1
  fi
}

# assert METHOD PATH EXPECTED JAR [DATA]
# EXPECTED may be a code (200) or a set (200,201,409) or a range (2xx).
assert() {
  local method=$1 path=$2 expected=$3 jar=$4 data="${5:-}"
  local body="$WORK/body" code hdrs=() attempts=0

  while [ $attempts -le 2 ]; do
    hdrs=()
    if [ -f "$jar" ]; then
      local tok=$(csrf_of "$jar")
      [ -n "$tok" ] && hdrs+=("-H" "X-XSRF-TOKEN: $tok")
    fi
    if [ -n "$data" ]; then
      code=$(curl -sS -o "$body" -w "%{http_code}" -X "$method" -b "$jar" -c "$jar" \
        "${hdrs[@]}" -H "Content-Type: application/json" --data-raw "$data" "$BASE$path" 2>/dev/null)
    else
      code=$(curl -sS -o "$body" -w "%{http_code}" -X "$method" -b "$jar" -c "$jar" \
        "${hdrs[@]}" "$BASE$path" 2>/dev/null)
    fi
    if [ "$code" = "429" ] && [ $attempts -lt 2 ]; then
      local wait
      wait=$(python3 -c "import sys,json; d=json.loads(open('$body').read()); print(int(d.get('retryAfter',30))+2)" 2>/dev/null || echo 35)
      log "  ${YEL}rate-limited on $method $path — waiting ${wait}s${RST}"
      sleep "$wait"
      attempts=$((attempts+1))
      continue
    fi
    break
  done

  local ok=0
  case "$expected" in
    *xx)
      local prefix="${expected%xx}"
      [[ "$code" == "${prefix}"* ]] && ok=1 ;;
    *,*)
      IFS=',' read -ra codes <<< "$expected"
      for c in "${codes[@]}"; do [ "$c" = "$code" ] && ok=1; done ;;
    *)
      [ "$code" = "$expected" ] && ok=1 ;;
  esac

  if [ "$ok" = "1" ]; then
    PASS=$((PASS+1))
    printf '  %s✓%s %-6s %-3s %s\n' "$GRN" "$RST" "$method" "$code" "$path"
  else
    FAIL=$((FAIL+1))
    local snippet=$(head -c 200 "$body" | tr '\n\r\t' '   ')
    FAILURES+=("$method $path → $code (expected $expected) :: $snippet")
    printf '  %s✗%s %-6s %-3s %s   %sexpected %s%s\n' "$RED" "$RST" "$method" "$code" "$path" "$DIM" "$expected" "$RST"
    [ "${VERBOSE:-0}" = "1" ] && printf '      %s%s%s\n' "$DIM" "$snippet" "$RST"
  fi
  rm -f "$body"
}

# Fetch a JSON field via python3 (avoids jq dependency)
jfield() { python3 -c "import sys,json
try:
  d=json.load(sys.stdin)
  for k in '$1'.split('.'):
    if isinstance(d,list): d=d[int(k)] if k.isdigit() else d[0][k]
    else: d=d.get(k,'')
  print(d or '')
except Exception: print('')"; }

# -----------------------------------------------------------------------------
# 1. Public / Config / Stats
# -----------------------------------------------------------------------------
header "Public / Config / Stats / Currencies"
assert GET  /config                               200 "$PUB_JAR"
assert GET  /stats                                200 "$PUB_JAR"
assert GET  /currencies/rates                     200 "$PUB_JAR"
assert GET  "/currencies/convert?from=USD&to=XAF&amount=10" 200 "$PUB_JAR"

# -----------------------------------------------------------------------------
# 2. Auth
# -----------------------------------------------------------------------------
header "Auth"
assert POST /auth/register 200,409 "$PUB_JAR" '{"firstName":"Test","lastName":"User","email":"smoketest+'"$RANDOM"'@homeflex.test","password":"Str0ng@Pass!","phoneNumber":"+237699000'"$((RANDOM%1000))"'","role":"TENANT"}'
assert POST /auth/login    200     "$PUB_JAR" '{"email":"tenant@test.com","password":"Tenant@123"}'
assert POST /auth/forgot-password 200,400 "$PUB_JAR" '{"email":"tenant@test.com"}'
assert POST /auth/reset-password  400     "$PUB_JAR" '{"token":"invalid","newPassword":"X@12345678"}'
assert GET  "/auth/verify?token=invalid" 400,404 "$PUB_JAR"
assert POST "/auth/otp/send?phoneNumber=%2B237699000000" 200,400,503 "$PUB_JAR" '{}'
assert POST "/auth/otp/verify?phoneNumber=%2B237699000000&otp=000000" 200,400 "$PUB_JAR" '{}'
REFRESH_JAR="$WORK/refresh.jar"
cp "$T_JAR" "$REFRESH_JAR"
assert POST /auth/refresh  200,401 "$REFRESH_JAR" '{}'

# -----------------------------------------------------------------------------
# 3. Users
# -----------------------------------------------------------------------------
header "Users"
relogin "$T_JAR" tenant@test.com 'Tenant@123'
assert GET  /users/me              200 "$T_JAR"
assert PUT  /users/me              200 "$T_JAR" '{"firstName":"Jane","lastName":"Tenant","phoneNumber":"+237987654321"}'
assert PUT  /users/me/password     400,401 "$T_JAR" '{"currentPassword":"wrong","newPassword":"New@Pass123"}'
assert PUT  /users/me/language     200 "$T_JAR" '{"language":"en"}'

TID=$(curl -sS -b "$T_JAR" "$BASE/users/me" | jfield id)
info "tenant id: $TID"
[ -n "$TID" ] && assert GET /users/$TID 200,403,404 "$T_JAR"

# -----------------------------------------------------------------------------
# 4. Properties — public
# -----------------------------------------------------------------------------
header "Properties (public)"
assert GET  /properties/cities            200 "$PUB_JAR"
assert GET  /properties/search            200 "$PUB_JAR"

PID=$(curl -sS -b "$LL_JAR" "$BASE/properties/my-properties" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("data",[{}])[0].get("id","")) if d.get("data") else print("")' 2>/dev/null)
info "sample property id: $PID"
if [ -n "$PID" ]; then
  assert GET  /properties/$PID               200 "$PUB_JAR"
  assert GET  /properties/$PID/similar       200 "$PUB_JAR"
  assert POST /properties/$PID/view          200,204 "$PUB_JAR" '{}'
  assert GET  "/properties/compare?ids=$PID" 200 "$PUB_JAR"
  assert GET  "/properties/$PID/availability?start=2026-05-01&end=2026-05-31" 200 "$PUB_JAR"
  assert GET  /properties/$PID/pricing/recommendation 200,403 "$LL_JAR"
else
  SKIP=$((SKIP+6))
fi

# -----------------------------------------------------------------------------
# 5. Properties — landlord/admin CRUD
# -----------------------------------------------------------------------------
header "Properties (landlord/admin)"
relogin "$LL_JAR"    landlord@test.com  'Landlord@123'
relogin "$ADMIN_JAR" admin@homeflex.com  admin123
assert GET  /properties/my-properties      200 "$LL_JAR"
NEW_PROP='{"title":"Smoke Test Prop","description":"Smoke test.","propertyType":"APARTMENT","listingType":"RENT","price":100000,"currency":"XAF","address":"R.Smoke 1","city":"Douala","stateProvince":"Littoral","country":"Cameroon","latitude":4.05,"longitude":9.76,"bedrooms":1,"bathrooms":1,"areaSqm":40}'
assert POST /properties/json               201 "$LL_JAR" "$NEW_PROP"
NEWID=$(curl -sS -b "$LL_JAR" -c "$LL_JAR" \
  -H "X-XSRF-TOKEN: $(csrf_of "$LL_JAR")" -H "Content-Type: application/json" \
  -d "$NEW_PROP" "$BASE/properties/json" | jfield id)
info "created property id: $NEWID"
if [ -n "$NEWID" ]; then
  assert PUT    /properties/$NEWID/json    200 "$LL_JAR" "$NEW_PROP"
  assert GET    /properties/$NEWID/reports 200 "$LL_JAR"
  assert PATCH  /admin/properties/$NEWID/approve 200 "$ADMIN_JAR" '{}'
  assert DELETE /properties/$NEWID         200,204 "$LL_JAR"
fi
[ -n "$PID" ] && assert POST /properties/$PID/report 200,201,400 "$T_JAR" '{"reason":"INAPPROPRIATE","details":"Smoke test"}'

# -----------------------------------------------------------------------------
# 6. Bookings
# -----------------------------------------------------------------------------
header "Bookings"
relogin "$T_JAR" tenant@test.com 'Tenant@123'
assert GET  /bookings/my-bookings          200 "$T_JAR"
[ -n "$PID" ] && assert GET /bookings/property/$PID 200 "$LL_JAR"
BK="{\"propertyId\":\"$PID\",\"bookingType\":\"RENTAL\",\"startDate\":\"2027-03-10\",\"endDate\":\"2027-04-10\",\"numberOfOccupants\":1,\"message\":\"smoke\"}"
assert POST /bookings 201,409 "$T_JAR" "$BK"
BKID=$(curl -sS -b "$T_JAR" "$BASE/bookings/my-bookings" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("data",[{}])[0].get("id","")) if d.get("data") else print("")' 2>/dev/null)
info "sample booking id: $BKID"
if [ -n "$BKID" ]; then
  assert GET   /bookings/$BKID                200 "$T_JAR"
  assert PATCH /bookings/$BKID/cancel         200,400,409 "$T_JAR" '{}'
fi

# -----------------------------------------------------------------------------
# 7. Leases
# -----------------------------------------------------------------------------
header "Leases"
assert GET  /leases/my                      200 "$T_JAR"
[ -n "$BKID" ] && assert GET /leases/booking/$BKID 200,404 "$T_JAR"

# -----------------------------------------------------------------------------
# 8. Favorites
# -----------------------------------------------------------------------------
header "Favorites"
relogin "$T_JAR" tenant@test.com 'Tenant@123'
assert GET  /favorites                      200 "$T_JAR"
if [ -n "$PID" ]; then
  assert POST   /favorites/$PID             200,201,409 "$T_JAR" '{}'
  assert GET    /favorites/check/$PID       200 "$T_JAR"
  assert DELETE /favorites/$PID             200,204 "$T_JAR"
fi

# -----------------------------------------------------------------------------
# 9. Reviews
# -----------------------------------------------------------------------------
header "Reviews"
[ -n "$PID" ] && assert GET /reviews/property/$PID         200 "$PUB_JAR"
[ -n "$PID" ] && assert GET /reviews/property/$PID/average 200 "$PUB_JAR"
[ -n "$TID" ] && assert GET /reviews/tenant/$TID           200,401 "$PUB_JAR"
[ -n "$TID" ] && assert GET /reviews/tenant/$TID/average   200,401 "$PUB_JAR"

# -----------------------------------------------------------------------------
# 10. Vehicles
# -----------------------------------------------------------------------------
header "Vehicles"
assert GET  /vehicles/search               200,429 "$PUB_JAR"
VID=$(curl -sS -b "$LL_JAR" "$BASE/vehicles/my-vehicles" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("data",[{}])[0].get("id","")) if d.get("data") else print("")' 2>/dev/null)
info "sample vehicle id: $VID"
assert GET  /vehicles/my-vehicles          200 "$LL_JAR"
assert GET  /vehicles/my-bookings          200 "$T_JAR"
if [ -n "$VID" ]; then
  assert GET  /vehicles/$VID               200 "$PUB_JAR"
  assert POST /vehicles/$VID/view          200,204,401 "$T_JAR" '{}'
  assert GET  "/vehicles/$VID/availability?startDate=2026-05-01&endDate=2026-05-15" 200 "$PUB_JAR"
  assert GET  /vehicles/$VID/bookings      200 "$LL_JAR"
  assert GET  /vehicles/$VID/condition     200 "$LL_JAR"
fi

# -----------------------------------------------------------------------------
# 11. Chat
# -----------------------------------------------------------------------------
header "Chat"
relogin "$T_JAR" tenant@test.com 'Tenant@123'
assert GET  /chat/rooms                    200 "$T_JAR"
ROOM=$(curl -sS -b "$T_JAR" "$BASE/chat/rooms" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("data",[{}])[0].get("id","")) if d.get("data") else print("")' 2>/dev/null)
info "sample room id: $ROOM"
if [ -n "$ROOM" ]; then
  assert GET   /chat/rooms/$ROOM/messages  200 "$T_JAR"
  assert POST  /chat/rooms/$ROOM/messages  200,201 "$T_JAR" '{"message":"smoke test ping"}'
  assert PATCH /chat/rooms/$ROOM/read      200,204 "$T_JAR" '{}'
fi

# -----------------------------------------------------------------------------
# 12. Notifications
# -----------------------------------------------------------------------------
header "Notifications"
relogin "$T_JAR" tenant@test.com 'Tenant@123'
assert GET    /notifications               200 "$T_JAR"
NID=$(curl -sS -b "$T_JAR" "$BASE/notifications" | python3 -c 'import sys,json;d=json.load(sys.stdin);print(d.get("data",[{}])[0].get("id","")) if d.get("data") else print("")' 2>/dev/null)
[ -n "$NID" ] && assert PATCH /notifications/$NID/read 200,204 "$T_JAR" '{}'
assert PATCH  /notifications/read-all      200,204 "$T_JAR" '{}'
assert POST   /notifications/fcm-token     200     "$T_JAR" '{"token":"smoke-fcm-token"}'

# -----------------------------------------------------------------------------
# 13. Finance / Payouts / KYC
# -----------------------------------------------------------------------------
header "Finance / Payouts / KYC"
relogin "$T_JAR"  tenant@test.com   'Tenant@123'
relogin "$LL_JAR" landlord@test.com 'Landlord@123'
assert GET  /finance/receipts              200 "$T_JAR"
assert GET  /payouts/summary               200 "$LL_JAR"
assert POST /payouts/connect/onboard       200,400,503 "$LL_JAR" '{}'
assert GET  /kyc/status                    200 "$LL_JAR"
assert POST /kyc/session                   200,400,409,503 "$LL_JAR" '{}'

# -----------------------------------------------------------------------------
# 14. Maintenance
# -----------------------------------------------------------------------------
header "Maintenance"
assert GET  /maintenance/my                200 "$T_JAR"
assert GET  /maintenance/landlord          200 "$LL_JAR"

# -----------------------------------------------------------------------------
# 15. Disputes
# -----------------------------------------------------------------------------
header "Disputes"
assert GET  /disputes                      200,401,403 "$T_JAR"
assert GET  /disputes                      200,401,403 "$ADMIN_JAR"

# -----------------------------------------------------------------------------
# 16. Insurance
# -----------------------------------------------------------------------------
header "Insurance"
assert GET  /insurance/plans               200 "$T_JAR"

# -----------------------------------------------------------------------------
# 17. Agencies
# -----------------------------------------------------------------------------
header "Agencies"
assert GET  /agencies                      200,429 "$PUB_JAR"

# -----------------------------------------------------------------------------
# 18. GDPR
# -----------------------------------------------------------------------------
header "GDPR"
assert GET  /gdpr/export                   200 "$T_JAR"

# -----------------------------------------------------------------------------
# 19. Admin
# -----------------------------------------------------------------------------
header "Admin"
relogin "$ADMIN_JAR" admin@homeflex.com admin123
assert GET  /admin/analytics               200 "$ADMIN_JAR"
assert GET  /admin/users                   200 "$ADMIN_JAR"
assert GET  /admin/properties/pending      200 "$ADMIN_JAR"
assert GET  /admin/reports                 200 "$ADMIN_JAR"
assert GET  /admin/configs                 200 "$ADMIN_JAR"
assert POST /admin/properties/reindex      200 "$ADMIN_JAR" '{}'
USR=$(curl -sS -b "$ADMIN_JAR" "$BASE/admin/users" | python3 -c 'import sys,json;d=json.load(sys.stdin);data=d.get("data",[]);t=[u for u in data if u.get("role")=="TENANT"];print(t[0]["id"]) if t else print("")' 2>/dev/null)
if [ -n "$USR" ]; then
  assert PATCH /admin/users/$USR/suspend   200,204 "$ADMIN_JAR" '{}'
  assert PATCH /admin/users/$USR/activate  200,204 "$ADMIN_JAR" '{}'
fi

# -----------------------------------------------------------------------------
# 20. Frontend SPA routes (reachability)
# -----------------------------------------------------------------------------
header "Frontend SPA routes"
for r in / /properties /vehicles /login /register /password-reset /support /admin/login /workspace; do
  code=$(curl -sS -o /dev/null -w "%{http_code}" "$FRONTEND$r")
  if [ "$code" = "200" ]; then
    PASS=$((PASS+1)); printf '  %s✓%s GET    %-3s %s\n' "$GRN" "$RST" "$code" "$r"
  else
    FAIL=$((FAIL+1)); FAILURES+=("FRONTEND $r → $code")
    printf '  %s✗%s GET    %-3s %s\n' "$RED" "$RST" "$code" "$r"
  fi
done

# -----------------------------------------------------------------------------
# 21. MinIO health + bucket
# -----------------------------------------------------------------------------
header "MinIO"
for url in "$MINIO/minio/health/live" "$MINIO/rental-app-media/"; do
  code=$(curl -sS -o /dev/null -w "%{http_code}" "$url")
  if [ "$code" = "200" ]; then
    PASS=$((PASS+1)); printf '  %s✓%s GET    %-3s %s\n' "$GRN" "$RST" "$code" "$url"
  else
    FAIL=$((FAIL+1)); FAILURES+=("MINIO $url → $code")
    printf '  %s✗%s GET    %-3s %s\n' "$RED" "$RST" "$code" "$url"
  fi
done

# -----------------------------------------------------------------------------
# Summary
# -----------------------------------------------------------------------------
header "Summary"
TOTAL=$((PASS+FAIL))
printf 'Passed: %s%d%s   Failed: %s%d%s   Total: %d\n' "$GRN" "$PASS" "$RST" "$RED" "$FAIL" "$RST" "$TOTAL"
if [ "$FAIL" -gt 0 ]; then
  log ""
  log "${RED}Failures:${RST}"
  for f in "${FAILURES[@]}"; do printf '  • %s\n' "$f"; done
  exit 1
fi
exit 0
