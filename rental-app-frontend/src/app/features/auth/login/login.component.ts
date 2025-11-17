import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router } from "@angular/router";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { CapacitorService } from "src/app/core/services/capacitor/capacitor.service";
import { IonicModule } from "@ionic/angular";
import { AuthService } from "src/app/core/services/auth/auth.service";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [
    IonicModule,
    ReactiveFormsModule, // Needed for [formGroup]
    TranslateModule, // <-- Add this to use 'translate' pipe
  ],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.scss",
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  error = "";

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private capacitorService: CapacitorService,
    private router: Router,
    private translate: TranslateService
  ) {
    this.loginForm = this.fb.group({
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit(): void {
    // Redirect if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(["/properties"]);
    }
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = "";

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.loading = false;
        this.router.navigate(["/properties"]);
      },
      error: (error) => {
        this.loading = false;
        this.error = error.error?.message || "Login failed";
      },
    });
  }

  async loginWithGoogle(): Promise<void> {
    try {
      this.loading = true;

      if (this.capacitorService.isNativePlatform()) {
        // Use Capacitor Google Auth plugin
        // const { GoogleAuth } = await import('@codetrix-studio/capacitor-google-auth');
        // const result = await GoogleAuth.signIn();
        // this.authService.googleLogin(result.authentication.idToken).subscribe(...);
      } else {
        // Use web Google Sign-In
        // Implement web Google Sign-In here
      }
    } catch (error) {
      console.error("Google login error:", error);
      this.error = "Google login failed";
    } finally {
      this.loading = false;
    }
  }

  goToRegister(): void {
    this.router.navigate(["/auth/register"]);
  }
}
