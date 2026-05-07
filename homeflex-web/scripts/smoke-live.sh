#!/bin/sh
set -eu

BASE_URL="${1:-http://localhost:8001}"

check() {
  url="$1"
  code="$(curl -s -o /dev/null -w '%{http_code}' "$url")"
  if [ "$code" != "200" ]; then
    echo "Smoke check failed for $url (HTTP $code)" >&2
    exit 1
  fi
  echo "OK  $url"
}

check "$BASE_URL/"
check "$BASE_URL/properties"
check "$BASE_URL/admin/login"
check "$BASE_URL/api/v1/config"
