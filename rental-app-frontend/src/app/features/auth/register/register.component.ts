import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router } from "@angular/router";
import { IonicModule } from "@ionic/angular";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { LoadingService } from "src/app/core/services/loading/loading.service";
import { ToastService } from "src/app/core/services/toast/toast.service";
import { CustomValidators } from "src/app/core/utils/validators";
import { UserRole, RegisterRequest } from "src/app/models/user.model";

@Component({
  selector: "app-register",
  standalone: true,
  imports: [IonicModule, CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false; // Keep for button state
  error = "";

  roles = [
    { label: "Tenant", value: UserRole.TENANT },
    { label: "Landlord", value: UserRole.LANDLORD },
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService,
    private loadingService: LoadingService,
    private toast: ToastService
  ) {
    this.registerForm = this.fb.group(
      {
        firstName: ["", [Validators.required, Validators.minLength(2)]],
        lastName: ["", [Validators.required, Validators.minLength(2)]],
        email: ["", [Validators.required, Validators.email]],
        phoneNumber: ["", CustomValidators.phoneNumber()],
        password: [
          "",
          [Validators.required, CustomValidators.passwordStrength()],
        ],
        confirmPassword: ["", Validators.required],
        role: ["", Validators.required],
      },
      {
        validators: CustomValidators.matchPasswords(
          "password",
          "confirmPassword"
        ),
      }
    );
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(["/properties"]);
    }
  }

  async onSubmit(): Promise<void> {
    if (this.registerForm.invalid) {
      this.markFormGroupTouched(this.registerForm);
      return;
    }

    this.loading = true;
    this.error = "";

    // Show loading overlay
    await this.loadingService.show(
      this.translate.instant("auth.creatingAccount") || "Creating account..."
    );

    const payload: RegisterRequest = this.registerForm.value;

    this.authService.register(payload).subscribe({
      next: async () => {
        await this.loadingService.hide();
        this.loading = false;

        await this.toast.success(
          this.translate.instant("auth.registrationSuccess") ||
            "Registration successful!"
        );

        // Navigate after a short delay to allow toast to show
        setTimeout(() => {
          this.router.navigate(["/"]);
        }, 500);
      },
      error: async (err) => {
        // Force hide loader immediately on error
        await this.loadingService.forceHide();
        this.loading = false;

        const message =
          err?.error?.message ||
          this.translate.instant("errors.registrationFailed") ||
          "Registration failed. Please try again.";

        this.error = message;

        await this.toast.error(message);
      },
    });
  }

  goToLogin(): void {
    this.router.navigate(["/auth/login"]);
  }

  // Helper to mark all fields as touched (shows validation errors)
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key) => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  // Helper for template: check password strength
  get passwordStrength(): string {
    const passwordControl = this.registerForm.get("password");
    const errors = passwordControl?.errors;

    if (!passwordControl?.value) return "";
    if (!errors) return "strong";
    if (errors["weak"]) return "weak";
    if (errors["medium"]) return "medium";

    return "";
  }

  get passwordsMatch(): boolean {
    return !this.registerForm.errors?.["passwordMismatch"];
  }
}
