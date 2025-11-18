// ====================================
// core/services/admin.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";

export interface Property {
  id: string;
  title: string;
  status: string;
  [key: string]: any;
}

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  isActive: boolean;
}

export interface Analytics {
  totalUsers: number;
  totalProperties: number;
  totalBookings: number;
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
  providedIn: "root",
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getPendingProperties(
    page: number = 0,
    size: number = 20
  ): Observable<Property[]> {
    const params = new HttpParams().set("page", page).set("size", size);
    return this.http.get<Property[]>(`${this.apiUrl}/properties/pending`, {
      params,
    });
  }

  approveProperty(id: string): Observable<Property> {
    return this.http.patch<Property>(
      `${this.apiUrl}/properties/${id}/approve`,
      {}
    );
  }

  rejectProperty(id: string, reason: string): Observable<Property> {
    return this.http.patch<Property>(`${this.apiUrl}/properties/${id}/reject`, {
      reason,
    });
  }

  getUsers(page: number = 0, size: number = 20): Observable<User[]> {
    const params = new HttpParams().set("page", page).set("size", size);
    return this.http.get<User[]>(`${this.apiUrl}/users`, { params });
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

  getReports(page: number = 0, size: number = 20): Observable<Report[]> {
    const params = new HttpParams().set("page", page).set("size", size);
    return this.http.get<Report[]>(`${this.apiUrl}/reports`, { params });
  }

  resolveReport(id: string): Observable<Report> {
    return this.http.patch<Report>(`${this.apiUrl}/reports/${id}/resolve`, {});
  }
}
