// ====================================
// core/services/user.service.ts
// ====================================
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/app/environments/environment';
import { User } from 'src/app/models/user.model';

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface LanguageUpdateRequest {
  language: string; // "en" or "fr"
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateProfile(request: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, request);
  }

  uploadAvatar(file: File): Observable<User> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<User>(`${this.apiUrl}/me/avatar`, formData);
  }

  changePassword(request: ChangePasswordRequest): Observable<any> {
    return this.http.put(`${this.apiUrl}/me/password`, request);
  }

  updateLanguage(request: LanguageUpdateRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me/language`, request);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }
}
