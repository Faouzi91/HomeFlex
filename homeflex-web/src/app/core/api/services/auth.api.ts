import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse, ApiValueResponse } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class AuthApi extends BaseApi {
  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  socialLogin(provider: string, token: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/${provider}`, { token });
  }

  register(payload: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string | null;
    role: string;
  }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/register`, payload);
  }

  forgotPassword(email: string): Observable<ApiValueResponse<string>> {
    return this.http.post<ApiValueResponse<string>>(`${this.baseUrl}/auth/forgot-password`, {
      email,
    });
  }

  resetPassword(token: string, newPassword: string): Observable<ApiValueResponse<string>> {
    return this.http.post<ApiValueResponse<string>>(`${this.baseUrl}/auth/reset-password`, {
      token,
      newPassword,
    });
  }

  sendOtp(phoneNumber: string): Observable<ApiValueResponse<string>> {
    return this.http.post<ApiValueResponse<string>>(`${this.baseUrl}/auth/otp/send`, null, {
      params: this.buildParams({ phoneNumber }),
    });
  }

  verifyOtp(phoneNumber: string, otp: string): Observable<ApiValueResponse<boolean>> {
    return this.http.post<ApiValueResponse<boolean>>(`${this.baseUrl}/auth/otp/verify`, null, {
      params: this.buildParams({ phoneNumber, otp }),
    });
  }

  refresh(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/refresh`, {});
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/auth/logout`, {});
  }
}
