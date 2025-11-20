import { CommonModule } from "@angular/common";
import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
} from "@angular/forms";
import { Router } from "@angular/router";
import { IonicModule } from "@ionic/angular";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { CapacitorService } from "src/app/core/services/capacitor/capacitor.service";
import { ToastService } from "src/app/core/services/toast/toast.service";
import { UserService } from "src/app/core/services/user/user.service";

function matchPasswords(control: AbstractControl) {
  const newPassword = control.get("newPassword")?.value;
  const confirmPassword = control.get("confirmPassword")?.value;
  return newPassword === confirmPassword ? null : { passwordMismatch: true };
}

@Component({
  selector: "app-profile",
  standalone: true,
  imports: [IonicModule, CommonModule, ReactiveFormsModule, TranslateModule],
  templateUrl: "./profile.component.html",
  styleUrl: "./profile.component.scss",
})
export class ProfileComponent implements OnInit {
  @ViewChild("fileInput", { static: false })
  fileInputRef!: ElementRef<HTMLInputElement>;

  user: any = null;
  profileForm: FormGroup;
  passwordForm: FormGroup;
  saving = false;
  changingPassword = false;

  constructor(
    private userService: UserService,
    public authService: AuthService,
    private fb: FormBuilder,
    private toast: ToastService,
    private capacitor: CapacitorService,
    private translate: TranslateService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      firstName: ["", [Validators.required, Validators.minLength(2)]],
      lastName: ["", [Validators.required, Validators.minLength(2)]],
      phoneNumber: [""],
      language: ["en"],
    });

    this.passwordForm = this.fb.group(
      {
        currentPassword: ["", Validators.required],
        newPassword: ["", [Validators.required, Validators.minLength(6)]],
        confirmPassword: ["", Validators.required],
      },
      { validators: matchPasswords }
    );
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    const u = this.authService.getCurrentUser();
    if (u) {
      this.user = u;
      this.patchForm(u);
    } else {
      this.userService.getCurrentUser().subscribe({
        next: (res) => {
          this.user = res;
          this.patchForm(res);
        },
        error: (err) => {
          console.error("Failed to load user", err);
          this.toast.error(this.translate.instant("errors.loadUserFailed"));
        },
      });
    }
  }

  patchForm(user: any): void {
    this.profileForm.patchValue({
      firstName: user.firstName || "",
      lastName: user.lastName || "",
      phoneNumber: user.phoneNumber || "",
      language: user.language || "en",
    });
  }

  getUserInitials(): string {
    if (!this.user) return "";
    const f = this.user.firstName?.charAt(0) || "";
    const l = this.user.lastName?.charAt(0) || "";
    return (f + l).toUpperCase();
  }

  openEdit(): void {
    const el = document.querySelector("section") as HTMLElement;
    if (el) el.scrollIntoView({ behavior: "smooth" });
  }

  async pickAvatar(): Promise<void> {
    try {
      if (this.capacitor.isNativePlatform()) {
        const dataUrl = await this.capacitor.takePicture();
        const blob = await (await fetch(dataUrl)).blob();
        const file = new File([blob], `avatar_${Date.now()}.png`, {
          type: blob.type,
        });
        await this.uploadAvatar(file);
      } else {
        this.fileInputRef.nativeElement.click();
      }
    } catch (err) {
      console.error("Avatar error", err);
      this.toast.error(this.translate.instant("profile.avatarError"));
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      this.toast.error(this.translate.instant("errors.fileTooLarge"));
      return;
    }

    // Validate file type
    if (!file.type.startsWith("image/")) {
      this.toast.error(this.translate.instant("errors.invalidFileType"));
      return;
    }

    this.uploadAvatar(file);
  }

  uploadAvatar(file: File): void {
    this.saving = true;
    this.userService.uploadAvatar(file).subscribe({
      next: (user) => {
        this.saving = false;
        this.user = user;
        localStorage.setItem("current_user", JSON.stringify(user));
        this.toast.success(this.translate.instant("profile.avatarUpdated"));

        // Update auth service to reflect changes
        this.authService.updateCurrentUser(user);
      },
      error: (err) => {
        this.saving = false;
        console.error("Upload avatar error", err);
        const message =
          err?.error?.message || this.translate.instant("profile.avatarError");
        this.toast.error(message);
      },
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;

    this.saving = true;
    const payload = this.profileForm.value;

    this.userService.updateProfile(payload).subscribe({
      next: (user) => {
        this.saving = false;
        this.user = user;
        localStorage.setItem("current_user", JSON.stringify(user));

        // Update language if changed
        if (payload.language !== this.translate.currentLang) {
          this.translate.use(payload.language);
          localStorage.setItem("app_language", payload.language);
        }

        this.toast.success(this.translate.instant("profile.saved"));

        // Update auth service
        this.authService.updateCurrentUser(user);
      },
      error: (err) => {
        this.saving = false;
        console.error("Save profile error", err);
        const message =
          err?.error?.message || this.translate.instant("profile.saveFailed");
        this.toast.error(message);
      },
    });
  }

  resetProfileForm(): void {
    this.patchForm(this.user);
  }

  submitPassword(): void {
    if (this.passwordForm.invalid) return;

    this.changingPassword = true;
    const { currentPassword, newPassword } = this.passwordForm.value;

    this.userService
      .changePassword({ currentPassword, newPassword })
      .subscribe({
        next: () => {
          this.changingPassword = false;
          this.passwordForm.reset();
          this.toast.success(this.translate.instant("profile.passwordUpdated"));
        },
        error: (err) => {
          this.changingPassword = false;
          console.error("Change password error", err);
          const message =
            err?.error?.message ||
            this.translate.instant("profile.passwordFailed");
          this.toast.error(message);
        },
      });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/auth/login"]);
  }

  confirmDeleteAccount(): void {
    if (confirm(this.translate.instant("profile.deleteConfirm"))) {
      this.toast.info(this.translate.instant("profile.deleteContactAdmin"));
    }
  }

  openChangePassword(): void {
    const el = document.querySelector("#password-section") as HTMLElement;
    if (el) el.scrollIntoView({ behavior: "smooth" });
  }

  goBack(): void {
    this.router.navigate(["/properties"]);
  }
}
