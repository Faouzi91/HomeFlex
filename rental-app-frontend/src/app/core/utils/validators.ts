// validators.ts
import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export class CustomValidators {
  static phoneNumber(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const phoneRegex = /^\+?[1-9]\d{1,14}$/;
      return phoneRegex.test(control.value) ? null : { invalidPhone: true };
    };
  }

  static password(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) return null;
      const hasUpperCase = /[A-Z]/.test(control.value);
      const hasLowerCase = /[a-z]/.test(control.value);
      const hasNumber = /[0-9]/.test(control.value);
      const isLengthValid = control.value.length >= 8;

      const valid = hasUpperCase && hasLowerCase && hasNumber && isLengthValid;
      return valid
        ? null
        : {
            weakPassword: {
              hasUpperCase,
              hasLowerCase,
              hasNumber,
              isLengthValid,
            },
          };
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
