import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export class CustomValidators {
  static phoneNumber(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const phoneRegex = /^\+?[1-9]\d{1,14}$/;
      return phoneRegex.test(control.value) ? null : { invalidPhone: true };
    };
  }

  // Password strength: weak / medium / strong
  static passwordStrength(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const hasLower = /[a-z]/.test(value);
      const hasUpper = /[A-Z]/.test(value);
      const hasNumber = /[0-9]/.test(value);
      const hasSymbol = /[^A-Za-z0-9]/.test(value);
      const isLongEnough = value.length >= 8;

      const score =
        (hasLower ? 1 : 0) +
        (hasUpper ? 1 : 0) +
        (hasNumber ? 1 : 0) +
        (hasSymbol ? 1 : 0) +
        (isLongEnough ? 1 : 0);

      if (score <= 2) return { weak: true };
      if (score === 3 || score === 4) return { medium: true };
      return null; // strong
    };
  }

  static matchPasswords(
    passwordField: string,
    confirmPasswordField: string
  ): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get(passwordField);
      const confirmPassword = formGroup.get(confirmPasswordField);
      if (!password || !confirmPassword) return null;

      return password.value === confirmPassword.value
        ? null
        : { passwordMismatch: true };
    };
  }
}
