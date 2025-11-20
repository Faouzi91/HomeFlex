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
import { LoadingService } from "src/app/core/services/loading/loading.service";
import { ToastService } from "src/app/core/services/toast/toast.service";
import { LoginRequest } from "src/app/models/user.model";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [
    IonicModule,
    ReactiveFormsModule, // Needed for [formGroup]
    TranslateModule, // <-- Add this to use 'translate' pipe
    CommonModule,
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
    private translate: TranslateService,
    private loadingService: LoadingService,
    private toast: ToastService
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

  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched(this.loginForm);
      return;
    }

    this.loading = true;
    this.error = "";

    await this.loadingService.show(
      this.translate.instant("auth.loggingIn") || "Signing in..."
    );

    const payload: LoginRequest = this.loginForm.value;

    this.authService.login(payload).subscribe({
      next: async () => {
        await this.loadingService.hide();
        this.loading = false;

        await this.toast.success(
          this.translate.instant("auth.loginSuccess") || "Login successful!"
        );

        setTimeout(() => {
          this.router.navigate(["/"]);
        }, 500);
      },
      error: async (err) => {
        await this.loadingService.forceHide();
        this.loading = false;

        const message =
          err?.error?.message ||
          this.translate.instant("errors.loginFailed") ||
          "Login failed. Please check your credentials.";

        this.error = message;
        await this.toast.error(message);
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

  goToForgotPassword(): void {
    this.router.navigate(["/auth/forgot-password"]);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
