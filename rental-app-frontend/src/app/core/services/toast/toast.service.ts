// ====================================
// toast.service.ts - Improved Version
// ====================================
import { Injectable } from "@angular/core";
import { ToastController } from "@ionic/angular";

@Injectable({
  providedIn: "root",
})
export class ToastService {
  private currentToast: HTMLIonToastElement | null = null;

  constructor(private toastController: ToastController) {}

  async success(message: string, duration: number = 3000): Promise<void> {
    await this.dismissCurrent();

    this.currentToast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "success",
      position: "top",
      icon: "checkmark-circle",
      cssClass: "toast-success",
      buttons: [
        {
          icon: "close",
          role: "cancel",
        },
      ],
    });

    await this.currentToast.present();

    // Clear reference after duration
    setTimeout(() => {
      this.currentToast = null;
    }, duration);
  }

  async error(message: string, duration: number = 4000): Promise<void> {
    await this.dismissCurrent();

    this.currentToast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "danger",
      position: "top",
      icon: "alert-circle",
      cssClass: "toast-error",
      buttons: [
        {
          icon: "close",
          role: "cancel",
        },
      ],
    });

    await this.currentToast.present();

    setTimeout(() => {
      this.currentToast = null;
    }, duration);
  }

  async info(message: string, duration: number = 3000): Promise<void> {
    await this.dismissCurrent();

    this.currentToast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "primary",
      position: "top",
      icon: "information-circle",
      cssClass: "toast-info",
      buttons: [
        {
          icon: "close",
          role: "cancel",
        },
      ],
    });

    await this.currentToast.present();

    setTimeout(() => {
      this.currentToast = null;
    }, duration);
  }

  async warning(message: string, duration: number = 3500): Promise<void> {
    await this.dismissCurrent();

    this.currentToast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "warning",
      position: "top",
      icon: "warning",
      cssClass: "toast-warning",
      buttons: [
        {
          icon: "close",
          role: "cancel",
        },
      ],
    });

    await this.currentToast.present();

    setTimeout(() => {
      this.currentToast = null;
    }, duration);
  }

  private async dismissCurrent(): Promise<void> {
    if (this.currentToast) {
      try {
        await this.currentToast.dismiss();
      } catch (error) {
        // Silently handle if toast already dismissed
      }
      this.currentToast = null;
    }
  }

  async dismissAll(): Promise<void> {
    try {
      const topToast = await this.toastController.getTop();
      if (topToast) {
        await topToast.dismiss();
      }
    } catch (error) {
      // Ignore
    }
    this.currentToast = null;
  }
}
