import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiValueResponse, User } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class UserApi extends BaseApi {
  getMe(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/me`);
  }

  updateProfile(payload: {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string | null;
    languagePreference?: string;
  }): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/users/me`, payload);
  }

  changePassword(payload: {
    currentPassword: string;
    newPassword: string;
  }): Observable<ApiValueResponse<string>> {
    return this.http.put<ApiValueResponse<string>>(`${this.baseUrl}/users/me/password`, payload);
  }

  uploadAvatar(file: File): Observable<User> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<User>(`${this.baseUrl}/users/me/avatar`, formData);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/${id}`);
  }
}
