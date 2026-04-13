import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Amenity,
  Analytics,
  ApiPageResponse,
  Property,
  ReportItem,
  SystemConfig,
  User,
} from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class AdminApi extends BaseApi {
  getAnalytics(): Observable<Analytics> {
    return this.http.get<Analytics>(`${this.baseUrl}/admin/analytics`);
  }

  getPendingProperties(page = 0, size = 6): Observable<ApiPageResponse<Property>> {
    return this.http.get<ApiPageResponse<Property>>(`${this.baseUrl}/admin/properties/pending`, {
      params: this.buildParams({ page, size }),
    });
  }

  approveProperty(id: string): Observable<Property> {
    return this.http.patch<Property>(`${this.baseUrl}/admin/properties/${id}/approve`, {});
  }

  rejectProperty(id: string, reason: string): Observable<Property> {
    return this.http.patch<Property>(`${this.baseUrl}/admin/properties/${id}/reject`, { reason });
  }

  getReports(page = 0, size = 20): Observable<ApiPageResponse<ReportItem>> {
    return this.http.get<ApiPageResponse<ReportItem>>(`${this.baseUrl}/admin/reports`, {
      params: this.buildParams({ page, size }),
    });
  }

  resolveReport(id: string, reason?: string): Observable<ReportItem> {
    return this.http.patch<ReportItem>(`${this.baseUrl}/admin/reports/${id}/resolve`, { reason });
  }

  getUsers(page = 0, size = 20): Observable<ApiPageResponse<User>> {
    return this.http.get<ApiPageResponse<User>>(`${this.baseUrl}/admin/users`, {
      params: this.buildParams({ page, size }),
    });
  }

  suspendUser(id: string): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/admin/users/${id}/suspend`, {});
  }

  activateUser(id: string): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/admin/users/${id}/activate`, {});
  }

  getSystemConfigs(): Observable<SystemConfig[]> {
    return this.http.get<SystemConfig[]>(`${this.baseUrl}/admin/configs`);
  }

  updateSystemConfig(key: string, value: string): Observable<SystemConfig> {
    return this.http.patch<SystemConfig>(`${this.baseUrl}/admin/configs/${key}`, null, {
      params: this.buildParams({ value }),
    });
  }

  createAmenity(amenity: {
    name: string;
    nameFr: string;
    icon: string;
    category: string;
  }): Observable<Amenity> {
    return this.http.post<Amenity>(`${this.baseUrl}/admin/amenities`, amenity);
  }

  deleteAmenity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/admin/amenities/${id}`);
  }
}
