// ====================================
// toast.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { ToastController } from "@ionic/angular";

@Injectable({
  providedIn: "root",
})
export class ToastService {
  constructor(private toastController: ToastController) {}

  async success(message: string, duration: number = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "success",
      position: "top",
      icon: "checkmark-circle",
    });
    await toast.present();
  }

  async error(message: string, duration: number = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "danger",
      position: "top",
      icon: "alert-circle",
    });
    await toast.present();
  }

  async info(message: string, duration: number = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "primary",
      position: "top",
      icon: "information-circle",
    });
    await toast.present();
  }

  async warning(message: string, duration: number = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message: message,
      duration: duration,
      color: "warning",
      position: "top",
      icon: "warning",
    });
    await toast.present();
  }
}
