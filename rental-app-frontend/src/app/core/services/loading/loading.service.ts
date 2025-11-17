// ====================================
// loading.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { LoadingController } from "@ionic/angular";

@Injectable({
  providedIn: "root",
})
export class LoadingService {
  private loading?: HTMLIonLoadingElement;

  constructor(private loadingController: LoadingController) {}

  async show(message: string = "Loading..."): Promise<void> {
    this.loading = await this.loadingController.create({
      message: message,
      spinner: "crescent",
    });
    await this.loading.present();
  }

  async hide(): Promise<void> {
    if (this.loading) {
      await this.loading.dismiss();
      this.loading = undefined;
    }
  }
}
