import { JsonPipe } from "@angular/common";
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
import { CustomValidators } from "src/app/core/utils/validators";
import { UserRole, RegisterRequest } from "src/app/models/user.model";

@Component({
  selector: "app-register",
  standalone: true,
  imports: [IonicModule, ReactiveFormsModule, TranslateModule, JsonPipe],
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false;
  error = "";

  roles = [
    { label: "Tenant", value: UserRole.TENANT },
    { label: "Landlord", value: UserRole.LANDLORD },
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private translate: TranslateService
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

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.loading = true;
    this.error = "";

    const payload: RegisterRequest = this.registerForm.value;

    this.authService.register(payload).subscribe({
      next: (response) => {
        this.loading = false;
        this.router.navigate(["/properties"]);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || "Registration failed";
      },
    });
  }

  goToLogin(): void {
    this.router.navigate(["/auth/login"]);
  }

  // Helper for template: check password strength
  get passwordStrength() {
    const errors = this.registerForm.get("password")?.errors;
    if (!errors) return "strong";
    if (errors.weak) return "weak";
    if (errors.medium) return "medium";
    return "";
  }

  get passwordsMatch(): boolean {
    return !this.registerForm.errors?.passwordMismatch;
  }
}
