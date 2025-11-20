import { Component } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { IonicModule } from "@ionic/angular";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { ToastService } from "src/app/core/services/toast/toast.service";
import { Router } from "@angular/router";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { LoadingService } from "src/app/core/services/loading/loading.service";

@Component({
  selector: "app-forgot-password",
  standalone: true,
  imports: [IonicModule, ReactiveFormsModule, TranslateModule],
  templateUrl: "./forgot-password.component.html",
  styleUrls: ["./forgot-password.component.scss"],
})
export class ForgotPasswordComponent {
  form: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private toast: ToastService,
    private router: Router,
    private translate: TranslateService,
    private loadingService: LoadingService
  ) {
    this.form = this.fb.group({
      email: ["", [Validators.required, Validators.email]],
    });
  }

  get email() {
    return this.form.get("email")!;
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    this.loadingService.show(this.translate.instant("auth.sendingResetLink"));

    const email = this.email.value;

    this.auth.forgotPassword(email).subscribe({
      next: (res) => {
        this.loadingService.hide();
        const msg =
          res?.message || this.translate.instant("auth.resetEmailSent");
        this.toast.success(msg);
        this.router.navigate(["/auth/login"]);
      },
      error: (err) => {
        this.loadingService.hide();
        const msg =
          err?.error?.message ||
          this.translate.instant("auth.resetEmailFailed");
        this.toast.error(msg);
      },
    });
  }

  goBack(): void {
    this.router.navigate(["/auth/login"]);
  }
}
