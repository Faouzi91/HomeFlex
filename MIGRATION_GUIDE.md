# HomeFlex Migration Guide: Platform Refactor Baseline

## Overview
This guide reflects the current refactor baseline: Gradle + Java 21 backend modernization, DDD/EDA scaffolding, and frontend migration progress with remaining stabilization work.

## Backend Migration: Maven → Gradle with Java 21

### What's Changed
1. **Replaced**: `pom.xml` → `build.gradle`
2. **Java Version**: Updated from Java 17 → **Java 21 LTS**
3. **Spring Boot**: Maintained at version 3.5.7 (compatible with Java 21)
4. **Build Tool**: Gradle 8.5 with wrapper

### File Structure
```
rental-backend/
├── build.gradle          # New: Gradle build file
├── settings.gradle       # New: Project settings
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.properties  # New: Gradle version configuration
│       └── gradle-wrapper.jar          # New: Gradle wrapper (auto-downloaded)
├── gradlew              # New: Gradle wrapper script (Unix/Linux)
├── gradlew.bat          # New: Gradle wrapper script (Windows)
├── pom.xml              # OLD: Can be removed after migration is tested
└── mvnw, mvnw.cmd       # OLD: Can be removed after migration is tested
```

### Build Commands

#### Using Gradle Wrapper (Recommended)

**Windows:**
```bash
cd rental-backend
gradlew build                    # Full build including tests
gradlew build -x test           # Build without tests
gradlew bootRun                 # Run the application
```

**Unix/Linux/Mac:**
```bash
cd rental-backend
./gradlew build                 # Full build including tests
./gradlew build -x test        # Build without tests
./gradlew bootRun              # Run the application
```

#### Using System Gradle (requires Gradle 8.5+ installed)
```bash
gradle build
gradle build -x test
gradle bootRun
```

### Gradle Wrapper Initialization

If the wrapper jar doesn't exist, run **once**:
```bash
gradle wrapper --gradle-version 8.5
```

This will download and cache the specified Gradle version for the project.

### Key Dependencies Migrated
- Spring Boot Starters (Data JPA, Security, Web, WebSocket, Mail, etc.)
- JWT (JJWT 0.12.3)
- AWS SDK for S3 (v2.21.0)
- Firebase Admin SDK (9.2.0)
- Google OAuth (2.2.0)
- Lombok
- ModelMapper
- SpringDoc OpenAPI
- PostgreSQL driver

### Verifying the Build

1. **Check Java Version** (must be Java 21+):
   ```bash
   java -version
   ```

2. **Run Gradle Build**:
   ```bash
   gradlew build -q
   ```

3. **Run the Application**:
   ```bash
   gradlew bootRun
   ```

4. **Build JAR Package**:
   ```bash
   gradlew bootJar
   ```
   Output: `build/libs/rental-backend.jar`

---

## Frontend Migration Status

### What's Changed
1. Frontend architecture migration toward standalone-first routing/state patterns has started.
2. Duplicate realtime connection ownership is being consolidated.
3. Environment path/config alignment was corrected for Angular workspace builds.
4. Additional stabilization is required due to Angular/Ionic dependency compatibility constraints.

### Updated Dependencies
```json
{
  "@angular/animations": "^18.0.0",
  "@angular/cdk": "^18.0.0",
  "@angular/common": "^18.0.0",
  "@angular/compiler": "^18.0.0",
  "@angular/core": "^18.0.0",
  "@angular/forms": "^18.0.0",
  "@angular/material": "^18.0.0",
  "@angular/platform-browser": "^18.0.0",
  "@angular/platform-browser-dynamic": "^18.0.0",
  "@angular/router": "^18.0.0",
  "@angular/service-worker": "^18.0.0",
  "@angular-devkit/build-angular": "^18.0.0",
  "@angular/cli": "^18.0.0",
  "@angular/compiler-cli": "^18.0.0",
  "typescript": "~5.4.0",
  "zone.js": "~0.15.0"
}
```

### Migration Steps

1. **Clear Node Modules & Lock Files** (recommended):
   ```bash
   cd rental-app-frontend
   rm -r node_modules package-lock.json    # Unix/Mac
   rmdir /s /q node_modules                 # Windows (PowerShell)
   del package-lock.json                    # Windows (cmd)
   ```

2. **Install Dependencies**:
   ```bash
   npm install
   ```

3. **Run Tests** (ensure compatibility):
   ```bash
   npm test
   ```

4. **Build Application**:
   ```bash
   npm run build
   ```

5. **Serve Locally**:
   ```bash
   npm start
   # Application runs at http://localhost:4200
   ```

### Compatibility Notes

#### Key Point
The remaining frontend issues are not simple syntax migrations; they are dependency/runtime compatibility mismatches that need a focused stabilization pass.

### Verification

1. **Check TypeScript Version**:
   ```bash
   npx tsc --version
   # Should output: Version 5.4.0 or similar
   ```

2. **Run Development Server**:
   ```bash
   npm start
   # Navigate to http://localhost:4200
   ```

3. **Build for Production**:
   ```bash
   npm run build:prod
   ```

4. **Run Lint** (if configured):
   ```bash
   npm run lint
   ```

---

## Testing the Full Migration

### Backend Test Checklist
- [ ] Java 21 installed and set as `JAVA_HOME`
- [ ] `gradle build` completes successfully
- [ ] `gradle bootRun` starts the server without errors
- [ ] API endpoints respond (test with `curl` or Postman)
- [ ] Database migrations run successfully

### Frontend Test Checklist
- [ ] `npm install` completes without errors
- [ ] `npm start` runs development server
- [ ] Application loads in browser at `localhost:4200`
- [ ] All features work (search, booking, chat, etc.)
- [ ] `npm run build:prod` creates production bundle
- [ ] No TypeScript compilation errors

### Integration Test
1. Start backend: `gradlew bootRun`
2. Start frontend: `npm start`
3. Test full user flow: register → search properties → book → message

---

## Cleanup (Optional but Recommended)

Once migration is tested and stable, you can remove Maven artifacts:

```bash
# Remove old Maven files
rm pom.xml
rm mvnw
rm mvnw.cmd
rm -r .mvn

# Remove old build/cache directories
rm -r target
rm -r build  # Keep if needed temporarily for comparison
```

Do NOT remove `pom.xml` until you've thoroughly tested the Gradle build in your entire deployment pipeline.

---

## System Requirements

### Backend
- **Java 21 LTS** or later
- Gradle 8.5+ (provided via wrapper, no manual install needed)
- PostgreSQL 12+ (for database)

### Frontend
- **Node.js 18+** or **20+**
- npm 9+ (comes with Node.js)
- Angular CLI 18.0.0+

---

## Troubleshooting

### Gradle Build Fails: "Could not find or load main class"
**Solution**: The gradle wrapper jar wasn't downloaded. Run:
```bash
gradle wrapper --gradle-version 8.5
# Then retry: gradlew build
```

### TypeScript Compilation Errors After npm install
**Solution**: Angular 18 is stricter with typing. Check for type errors:
```bash
npm run lint    # or directly run tsc
npx tsc --noEmit
```

### Port Already in Use
**Backend** (port 8080):
```bash
gradlew bootRun --args='--server.port=8081'
```

**Frontend** (port 4200):
```bash
npm start -- --port 4300
```

### Database Connection Issues
Ensure PostgreSQL is running and `application.yml` has correct credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/rental_db
    username: postgres
    password: your_password
```

---

## Additional Resources

- [Gradle Documentation](https://docs.gradle.org/)
- [Spring Boot with Gradle](https://spring.io/guides/gs/gradle/)
- [Angular 18 Upgrade Guide](https://angular.io/guide/upgrade-to-latest-version)
- [Java 21 Features](https://docs.oracle.com/en/java/javase/21/docs/api/)
- [TypeScript 5.4 Release Notes](https://www.typescriptlang.org/docs/handbook/release-notes/typescript-5-4.html)

---

## Summary of Changes

| Component | Before | After | Status |
|-----------|--------|-------|--------|
| Backend Build | Maven (pom.xml) | Gradle (build.gradle) | ✅ Migrated |
| Java Version | 17 LTS | 21 LTS | ✅ Upgraded |
| Spring Boot | 3.5.7 | 3.5.7 | ✅ Compatible |
| Frontend Framework | Angular 17 | Angular 18 LTS | ✅ Upgraded |
| TypeScript | 5.2.2 | 5.4.0 | ✅ Upgraded |
| Build Tool (Frontend) | npm | npm | ✅ No change |

---

**Last Updated**: 2026-03-24
**Status**: Backend largely stabilized; frontend stabilization in progress
