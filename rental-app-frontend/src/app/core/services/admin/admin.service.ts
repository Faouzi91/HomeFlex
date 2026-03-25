import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
// 👇 IMPORT CENTRAL MODELS INSTEAD OF DEFINING LOCAL ONES
import { Property } from 'src/app/models/property.model';
import { User } from 'src/app/models/user.model';
import { environment } from 'src/app/environments/environment';

export interface Analytics {
  totalUsers: number;
  totalProperties: number;
  totalBookings: number;
  pendingProperties: number;
  [key: string]: any;
}

export interface Report {
  id: string;
  type: string;
  message: string;
  status: string;
  createdAt: Date;
}

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getPendingProperties(page: number = 0, size: number = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<{ data: Property[] }>(`${this.apiUrl}/properties/pending`, { params });
  }

  approveProperty(id: string): Observable<Property> {
    return this.http.patch<Property>(`${this.apiUrl}/properties/${id}/approve`, {});
  }

  rejectProperty(id: string, reason: string): Observable<Property> {
    return this.http.patch<Property>(`${this.apiUrl}/properties/${id}/reject`, {
      reason,
    });
  }

  getUsers(page: number = 0, size: number = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<{ data: User[] }>(`${this.apiUrl}/users`, {
      params,
    });
  }

  suspendUser(id: string): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/users/${id}/suspend`, {});
  }

  activateUser(id: string): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/users/${id}/activate`, {});
  }

  getAnalytics(): Observable<Analytics> {
    return this.http.get<Analytics>(`${this.apiUrl}/analytics`);
  }

  getReports(page: number = 0, size: number = 20) {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<{ data: Report[] }>(`${this.apiUrl}/reports`, {
      params,
    });
  }

  resolveReport(id: string): Observable<Report> {
    return this.http.patch<Report>(`${this.apiUrl}/reports/${id}/resolve`, {});
  }
}
