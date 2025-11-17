// ====================================
// app.component.ts - Initialize Capacitor
// ====================================
import { Component, OnInit } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { CapacitorService } from "./core/services/capacitor/capacitor.service";
import { AuthService } from "./core/services/auth/auth.service";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html", // <-- use template file
  styleUrls: ["./app.component.scss"],
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
