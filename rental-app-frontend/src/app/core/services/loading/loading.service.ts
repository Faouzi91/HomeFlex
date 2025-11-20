// ====================================
// loading.service.ts - Improved Version
// ====================================
import { Injectable } from "@angular/core";
import { LoadingController } from "@ionic/angular";

@Injectable({
  providedIn: "root",
})
export class LoadingService {
  private loading: HTMLIonLoadingElement | null = null;
  private isPresented = false;
  private dismissTimeout: any;

  constructor(private loadingController: LoadingController) {}

  async show(message: string = "Loading..."): Promise<void> {
    // Clear any pending dismiss timeout
    if (this.dismissTimeout) {
      clearTimeout(this.dismissTimeout);
      this.dismissTimeout = null;
    }

    // If already showing, just update the message
    if (this.isPresented && this.loading) {
      this.loading.message = message;
      return;
    }

    // If not showing, create new loader
    try {
      this.loading = await this.loadingController.create({
        message,
        spinner: "crescent",
        backdropDismiss: false,
        cssClass: "custom-loading",
      });

      await this.loading.present();
      this.isPresented = true;
    } catch (error) {
      console.error("Error showing loader:", error);
      this.isPresented = false;
      this.loading = null;
    }
  }

  async hide(): Promise<void> {
    // Add small delay to prevent flashing for very quick operations
    this.dismissTimeout = setTimeout(async () => {
      if (this.loading && this.isPresented) {
        try {
          await this.loading.dismiss();
        } catch (error) {
          // Silently handle "overlay does not exist" errors
          console.warn("Error dismissing loader:", error);
        } finally {
          this.loading = null;
          this.isPresented = false;
        }
      }
    }, 100); // 100ms delay
  }

  async forceHide(): Promise<void> {
    // Immediately hide without delay (for error cases)
    if (this.dismissTimeout) {
      clearTimeout(this.dismissTimeout);
      this.dismissTimeout = null;
    }

    if (this.loading && this.isPresented) {
      try {
        await this.loading.dismiss();
      } catch (error) {
        console.warn("Error force dismissing loader:", error);
      } finally {
        this.loading = null;
        this.isPresented = false;
      }
    }

    // Extra safety: dismiss all loaders
    try {
      const topLoader = await this.loadingController.getTop();
      if (topLoader) {
        await topLoader.dismiss();
      }
    } catch (error) {
      // Ignore
    }
  }

  isLoading(): boolean {
    return this.isPresented;
  }
}
