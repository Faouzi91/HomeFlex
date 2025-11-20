import { Component, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from "@angular/forms";
import { IonicModule } from "@ionic/angular";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { ToastService } from "src/app/core/services/toast/toast.service";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { LoadingService } from "src/app/core/services/loading/loading.service";

function matchPasswords(control: AbstractControl) {
  const pw = control.get("password")?.value;
  const cpw = control.get("confirmPassword")?.value;
  return pw === cpw ? null : { passwordMismatch: true };
}

@Component({
  selector: "app-reset-password",
  standalone: true,
  imports: [IonicModule, ReactiveFormsModule, TranslateModule],
  templateUrl: "./reset-password.component.html",
  styleUrls: ["./reset-password.component.scss"],
})
export class ResetPasswordComponent implements OnInit {
  form: FormGroup;
  loading = false;
  token: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService,
    private translate: TranslateService,
    private loadingService: LoadingService
  ) {
    this.form = this.fb.group(
      {
        password: ["", [Validators.required, Validators.minLength(6)]],
        confirmPassword: ["", Validators.required],
      },
      { validators: matchPasswords }
    );
  }

  get password() {
    return this.form.get("password")!;
  }

  ngOnInit(): void {
    // token might be sent in query param: /auth/reset-password?token=abc...
    this.token = this.route.snapshot.queryParamMap.get("token");
    // fallback: route param /auth/reset-password/:token
    if (!this.token) {
      this.token = this.route.snapshot.paramMap.get("token");
    }
    if (!this.token) {
      // no token — show message and redirect to forgot page
      this.toast.error(this.translate.instant("auth.resetNoToken"));
      this.router.navigate(["/auth/forgot-password"]);
    }
  }

  onSubmit(): void {
    if (this.form.invalid || !this.token) return;

    this.loadingService.show(this.translate.instant("auth.resettingPassword"));

    this.auth.resetPassword(this.token, this.password.value).subscribe({
      next: (res) => {
        this.loadingService.hide();
        const msg = res?.message || this.translate.instant("auth.resetSuccess");
        this.toast.success(msg);
        this.router.navigate(["/auth/login"]);
      },
      error: (err) => {
        this.loadingService.hide();
        const msg =
          err?.error?.message || this.translate.instant("auth.resetFailed");
        this.toast.error(msg);
      },
    });
  }

  goBack(): void {
    this.router.navigate(["/auth/login"]);
  }
}
