// ====================================
// core/services/capacitor.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { Capacitor } from "@capacitor/core";
import { Camera, CameraResultType, CameraSource } from "@capacitor/camera";
import { Geolocation } from "@capacitor/geolocation";
import { PushNotifications } from "@capacitor/push-notifications";
import { StatusBar, Style } from "@capacitor/status-bar";
import { SplashScreen } from "@capacitor/splash-screen";

@Injectable({
  providedIn: "root",
})
export class CapacitorService {
  isNativePlatform(): boolean {
    return Capacitor.isNativePlatform();
  }

  getPlatform(): string {
    return Capacitor.getPlatform();
  }

  // Camera
  async takePicture(
    source: CameraSource = CameraSource.Camera
  ): Promise<string> {
    try {
      const image = await Camera.getPhoto({
        quality: 90,
        allowEditing: false,
        resultType: CameraResultType.DataUrl,
        source: source,
      });
      return image.dataUrl!;
    } catch (error) {
      console.error("Error taking picture:", error);
      throw error;
    }
  }

  async selectImages(limit: number = 5): Promise<string[]> {
    try {
      const images = await Camera.pickImages({
        quality: 90,
        limit: limit,
      });

      return images.photos.map((photo) => photo.webPath || "");
    } catch (error) {
      console.error("Error selecting images:", error);
      throw error;
    }
  }

  // Geolocation
  async getCurrentPosition(): Promise<{ latitude: number; longitude: number }> {
    try {
      const position = await Geolocation.getCurrentPosition();
      return {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };
    } catch (error) {
      console.error("Error getting location:", error);
      throw error;
    }
  }

  async checkGeolocationPermissions(): Promise<boolean> {
    try {
      const status = await Geolocation.checkPermissions();
      return status.location === "granted";
    } catch (error) {
      console.error("Error checking geolocation permissions:", error);
      return false;
    }
  }

  async requestGeolocationPermissions(): Promise<boolean> {
    try {
      const status = await Geolocation.requestPermissions();
      return status.location === "granted";
    } catch (error) {
      console.error("Error requesting geolocation permissions:", error);
      return false;
    }
  }

  // Push Notifications
  async initializePushNotifications(): Promise<void> {
    if (!this.isNativePlatform()) {
      console.warn("Push notifications only work on native platforms");
      return;
    }

    try {
      // Request permission
      let permStatus = await PushNotifications.checkPermissions();

      if (permStatus.receive === "prompt") {
        permStatus = await PushNotifications.requestPermissions();
      }

      if (permStatus.receive !== "granted") {
        throw new Error("User denied permissions!");
      }

      // Register with FCM
      await PushNotifications.register();

      // Listeners
      PushNotifications.addListener("registration", (token) => {
        console.log("Push registration success, token: " + token.value);
        this.saveFCMToken(token.value);
      });

      PushNotifications.addListener("registrationError", (error) => {
        console.error("Error on registration: " + JSON.stringify(error));
      });

      PushNotifications.addListener(
        "pushNotificationReceived",
        (notification) => {
          console.log("Push received: " + JSON.stringify(notification));
          // Handle notification when app is in foreground
        }
      );

      PushNotifications.addListener(
        "pushNotificationActionPerformed",
        (notification) => {
          console.log("Push action performed: " + JSON.stringify(notification));
          // Handle notification tap
        }
      );
    } catch (error) {
      console.error("Error initializing push notifications:", error);
    }
  }

  private saveFCMToken(token: string): void {
    // Send token to your backend
    // this.http.post('/api/fcm/register', { token }).subscribe();
    localStorage.setItem("fcm_token", token);
  }

  // Status Bar
  async setStatusBarStyle(dark: boolean = false): Promise<void> {
    if (this.isNativePlatform()) {
      try {
        await StatusBar.setStyle({
          style: dark ? Style.Dark : Style.Light,
        });
      } catch (error) {
        console.error("Error setting status bar style:", error);
      }
    }
  }

  async hideStatusBar(): Promise<void> {
    if (this.isNativePlatform()) {
      await StatusBar.hide();
    }
  }

  async showStatusBar(): Promise<void> {
    if (this.isNativePlatform()) {
      await StatusBar.show();
    }
  }

  // Splash Screen
  async hideSplashScreen(): Promise<void> {
    if (this.isNativePlatform()) {
      await SplashScreen.hide();
    }
  }
}
