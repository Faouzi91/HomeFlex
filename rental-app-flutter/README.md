# HomeFlex Mobile App

Cross-platform mobile application for the HomeFlex rental marketplace, built with **Flutter**.

## Features

- **Property & Vehicle Search**: Unified search interface with advanced filters and sorting.
- **Booking Management**: Mobile-first workflow for requesting and managing reservations.
- **Real-time Chat**: Direct messaging between users via WebSockets.
- **Identity & Profiles**: Secure user authentication and KYC status visibility.
- **Native Experience**: High-performance UI optimized for both Android and iOS.

## Tech Stack

- **Framework**: Flutter
- **Language**: Dart
- **State Management**: Riverpod / Provider (consistent with `rental-backend` features)
- **API Client**: Dio with JWT interceptors
- **CI/CD**: Automated builds via GitHub Actions

## Getting Started

Requires Flutter SDK 3.x+.

1. **Install dependencies**:

   ```bash
   flutter pub get
   ```

2. **Run the application**:

   ```bash
   flutter run
   ```

3. **Build for release**:
   ```bash
   flutter build apk --release
   flutter build ios --release
   ```

Refer to the root `README.md` for backend connection details.
