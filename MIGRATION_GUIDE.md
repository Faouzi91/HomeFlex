# HomeFlex Migration Guide: Maven → Gradle & Java 17 → Java 21 & Angular 17 → Angular 18

## Overview
This document outlines the complete migration from Maven to Gradle, Java 17 to Java 21, and Angular 17 to Angular 18 LTS.

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

## Frontend Migration: Angular 17 → Angular 18 LTS

### What's Changed
1. **Angular**: ^17.0.0 → **^18.0.0** (LTS)
2. **TypeScript**: ~5.2.2 → **~5.4.0**
3. **Angular Material**: ^17.0.0 → **^18.0.0**
4. **Angular CDK**: ^17.0.0 → **^18.0.0**
5. **Angular DevKit**: ^17.3.0 → **^18.0.0**
6. **zone.js**: ~0.14.0 → **~0.15.0**

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

### Breaking Changes & Notes

#### Typed FormGroups (Optional but Recommended)
Angular 18 encourages strong typing for reactive forms. If your code uses `FormGroup<T>`, ensure types are properly defined:

```typescript
// Before (Angular 17)
bookingForm = this.fb.group({
  requestedDate: [null, Validators.required],
  message: ["", Validators.maxLength(500)]
});

// After (Angular 18 - with proper typing)
bookingForm: FormGroup<{
  requestedDate: FormControl<Date | null>;
  message: FormControl<string>;
}> = this.fb.group({
  requestedDate: [null, Validators.required],
  message: ["", Validators.maxLength(500)]
});
```

#### Standalone Component Changes
Angular 18 continues support for standalone components (already in use). No changes required unless you're combining old module-based and standalone components.

#### Signal-Based APIs (Optional)
Angular 18 introduces signals (new reactive primitive). Your existing RxJS code will continue to work, but consider migrating over time.

#### Capacitor Plugin Updates
Mobile build commands remain the same:
```bash
npm run build:mobile     # Build and sync to Capacitor
npm run android:build    # Build Android app
npm run ios:build        # Build iOS app
```

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

**Last Updated**: March 2026
**Status**: Complete Migration Ready for Testing
