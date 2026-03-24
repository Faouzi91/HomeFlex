# IDE Setup Instructions for Gradle & Java 21

## IntelliJ IDEA

### Prerequisites
1. **Install Java 21 JDK**
2. **Clone/Open the project**

### Configure Java 21

1. Go to **File → Project Structure → Project**
2. Set "SDK" to **Java 21** (download if not available)
3. Set "Language Level" to **21**
4. Click **Apply** and **OK**

### Gradle Configuration

1. Go to **File → Settings → Build, Execution, Deployment → Gradle**
2. Set "Gradle JVM" to **Java 21**
3. Ensure "Use Gradle from: Specified location" is pointing to gradlew in the project
4. Click **OK**

### Refresh Gradle Project

In the Gradle toolbar on the right:
1. Click the **Refresh** button (circular arrows icon)
2. Wait for dependencies to download from Maven Central

### Build & Run

- **Build**: Gradle → Tasks → build → build
- **Run**: Click green **Run** button or **Shift + F10**
- **Tests**: Gradle → Tasks → verification → test

---

## VSCode (with Extension Pack for Java)

### Prerequisites
1. **Install**: Extension Pack for Java (includes Gradle support)
2. **Install Java 21 JDK**

### Configure JAVA_HOME

**Windows**:
```powershell
# Add to system environment variables:
JAVA_HOME = C:\Program Files\Java\jdk-21
```

**Unix/Mac**:
```bash
# Add to ~/.zshrc or ~/.bash_profile
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH
```

Then restart VSCode and terminal.

### Open Backend Project

1. Open folder: `rental-backend`
2. VSCode will detect `build.gradle` and `settings.gradle`
3. A notification appears: "Gradle Configuration detected"
4. Click **Install** if prompted to install Gradle extension

### Build Tasks

In VSCode terminal:
```bash
./gradlew build          # Full build
./gradlew build -x test  # Build without tests
./gradlew bootRun        # Run the app
```

Or use the Gradle view:
- Click **Explorer** → **Gradle** tab → expand tasks → double-click desired task

---

## Eclipse IDE

### Prerequisites
1. **Install**: Buildship Gradle Integration plugin
2. **Install Java 21 JDK**

### Import Project

1. Go to **File → Import → Existing Gradle Project**
2. Select the `rental-backend` folder
3. Click **Next** → **Finish**

### Configure Build Path

1. Right-click project → **Properties**
2. Go to **Java Build Path**
3. Set **JRE System Library** to **JavaSE-21**
4. Click **Apply and Close**

### Build

1. Right-click project → **Gradle → Refresh Gradle Project**
2. Right-click project → **Project → Build All** (or use `Ctrl + B`)

---

## Command Line (Any OS)

### macOS / Linux

```bash
# Navigate to project
cd rental-backend

# Build
./gradlew build

# Run
./gradlew bootRun

# Tests
./gradlew test

# Build JAR
./gradlew bootJar
# Output: build/libs/rental-backend.jar
```

### Windows (PowerShell)

```powershell
cd rental-backend

# Build
.\gradlew.bat build

# Run
.\gradlew.bat bootRun

# Tests
.\gradlew.bat test

# Build JAR
.\gradlew.bat bootJar
# Output: build\libs\rental-backend.jar
```

### Windows (CMD)

```cmd
cd rental-backend

REM Build
gradlew.bat build

REM Run
gradlew.bat bootRun

REM Tests
gradlew.bat test

REM Build JAR
gradlew.bat bootJar
REM Output: build\libs\rental-backend.jar
```

---

## Frontend (Angular 18) Setup

### VSCode Setup

1. **Install Extensions**:
   - Angular Language Service
   - ESLint
   - Prettier (optional)

2. **Open terminal** in `rental-app-frontend` folder

3. **Install dependencies**:
   ```bash
   npm install
   ```

4. **Start dev server**:
   ```bash
   npm start
   # Runs on http://localhost:4200
   ```

### IntelliJ IDEA

1. Open folder: `rental-app-frontend`
2. Right-click `package.json` → **Run npm scripts**
3. Select **start** and click
4. App starts on `http://localhost:4200`

### Build for Production

```bash
npm run build:prod
# Output in: dist/rental-app-frontend/
```

---

## Troubleshooting

### Gradle Build in IDE Shows Old Java Version

**Solution**:
1. Invalidate caches: **File → Invalidate Caches**
2. Close IDE
3. Delete `.gradle` folder in project: `rm -r .gradle`
4. Reopen IDE and refresh Gradle

### Package Dependencies Not Resolved (Red squiggles)

**Backend (Gradle)**:
1. Right-click project → **Project Structure** (or **Build → Rebuild**)
2. Wait for re-indexing to complete

**Frontend (npm)**:
```bash
# Clear cache and reinstall
rm -r node_modules package-lock.json
npm install
```

### Unable to Run Tests

Ensure JUnit 5 is available:
```bash
# Check gradle dependencies
./gradlew dependencies --configuration testRuntime
```

---

## Environment Configuration

### Backend `.env` or `application.yml`

Create/Update: `rental-backend/src/main/resources/application-dev.yml`

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:postgresql://localhost:5432/rental_db
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
  
server:
  port: 8080
```

Run with:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Frontend Environment

Create: `rental-app-frontend/src/environments/environment.ts`

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

For production:
```typescript
// src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.production.example.com/api'
};
```

---

## Performance Tips

### Gradle Build Speed

Add to `~/.gradle/gradle.properties` (or create `gradle.properties` in project):

```properties
# Enable Gradle daemon for faster builds
org.gradle.daemon=true

# Use parallel builds
org.gradle.parallel=true

# Configure build threads
org.gradle.workers.max=8

# Enable build cache
org.gradle.build.cache=true
```

### Angular Development

Use Ivy compiler (default in Angular 18):
```bash
npm start  # Uses Ivy by default
```

For faster rebuilds during development:
```bash
npm start -- --poll=5000  # Reduces file polling
```

---

**Last Updated**: March 2026
