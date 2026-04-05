// ====================================
// capacitor.config.ts
// ====================================
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.realestate.rental',
  appName: 'Real Estate Rental',
  webDir: 'dist/rental-app-frontend',
  server: {
    androidScheme: 'https',
    iosScheme: 'https',
    hostname: 'app.realestate.com',
    // For development only - remove in production
    // url: 'http://192.168.1.100:4200',
    // cleartext: true
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 2000,
      launchAutoHide: true,
      backgroundColor: '#4F46E5',
      androidSplashResourceName: 'splash',
      androidScaleType: 'CENTER_CROP',
      showSpinner: false,
      androidSpinnerStyle: 'large',
      iosSpinnerStyle: 'small',
      spinnerColor: '#FFFFFF',
      splashFullScreen: true,
      splashImmersive: true,
    },
    PushNotifications: {
      presentationOptions: ['badge', 'sound', 'alert'],
    },
    Camera: {
      permissions: ['camera', 'photos'],
    },
    Geolocation: {
      permissions: ['location'],
    },
  },
  android: {
    buildOptions: {
      keystorePath: 'path/to/keystore.jks',
      keystorePassword: '',
      keystoreAlias: '',
      keystoreAliasPassword: '',
    },
  },
  ios: {
    contentInset: 'automatic',
    scrollEnabled: true,
  },
};

export default config;
