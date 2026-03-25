// ====================================
// app.component.ts - Initialize Capacitor
// ====================================
import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { CapacitorService } from "./core/services/capacitor/capacitor.service";
import { AuthService } from "./core/services/auth/auth.service";
import { IonicModule } from "@ionic/angular";
import { RouterModule } from "@angular/router";
import { HeaderComponent } from "./shared/components/header/header.component";
import { TranslateModule } from "@ngx-translate/core";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html", // <-- use template file
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [IonicModule, RouterModule, HeaderComponent, TranslateModule]
})
export class AppComponent implements OnInit {
  [x: string]: any;
  constructor(
    private translate: TranslateService,
    private capacitorService: CapacitorService,
    private authService: AuthService
  ) {
    this.initializeApp();
  }

  ngOnInit() {
    // Additional initialization if needed
  }

  private initializeApp(): void {
    // Set default language
    this.translate.setDefaultLang("en");

    // Try to use user's preferred language or browser language
    const user = this.authService.getCurrentUser();
    const browserLang = this.translate.getBrowserLang();
    const preferredLang = user?.languagePreference || browserLang || "en";
    this.translate.use(
      ["en", "fr"].includes(preferredLang) ? preferredLang : "en"
    );

    // Initialize native features
    if (this.capacitorService.isNativePlatform()) {
      this.capacitorService.setStatusBarStyle(false);
      this.capacitorService.hideSplashScreen();
      this.capacitorService.initializePushNotifications();
    }
  }
}
