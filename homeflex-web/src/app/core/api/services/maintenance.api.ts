import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  MaintenanceRequest,
  MaintenanceRequestCreateRequest,
  MaintenanceStatusUpdateRequest,
} from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class MaintenanceApi extends BaseApi {
  create(payload: MaintenanceRequestCreateRequest): Observable<MaintenanceRequest> {
    return this.http.post<MaintenanceRequest>(`${this.baseUrl}/maintenance`, payload);
  }

  uploadImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    return this.http.post<void>(`${this.baseUrl}/maintenance/${id}/images`, formData);
  }

  updateStatus(
    id: string,
    payload: MaintenanceStatusUpdateRequest,
  ): Observable<MaintenanceRequest> {
    return this.http.patch<MaintenanceRequest>(`${this.baseUrl}/maintenance/${id}/status`, payload);
  }

  getMine(): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(`${this.baseUrl}/maintenance/my`);
  }

  getLandlord(): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(`${this.baseUrl}/maintenance/landlord`);
  }

  getById(id: string): Observable<MaintenanceRequest> {
    return this.http.get<MaintenanceRequest>(`${this.baseUrl}/maintenance/${id}`);
  }
}
