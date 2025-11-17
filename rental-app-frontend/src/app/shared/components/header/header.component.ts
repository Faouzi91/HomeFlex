import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { User } from "src/app/models/user.model";
import { TranslateService } from "@ngx-translate/core";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
})
export class HeaderComponent {
  user: User | null = null;
  selectedLang = "en";

  constructor(
    private auth: AuthService,
    private router: Router,
    private translate: TranslateService
  ) {
    this.auth.currentUser$.subscribe((u) => {
      this.user = u;
    });

    // Init selectedLang from current translate language
    this.selectedLang = this.translate.currentLang || "en";
  }

  navigate(path: string) {
    this.router.navigate([path]);
  }

  logout() {
    this.auth.logout();
    this.router.navigate(["/auth/login"]);
  }

  changeLanguage(event: any) {
    this.translate.use(event.detail.value);
  }
}
