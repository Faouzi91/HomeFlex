import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiListResponse } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class LeaseApi extends BaseApi {
  getMine(): Observable<ApiListResponse<any>> {
    return this.http.get<ApiListResponse<any>>(`${this.baseUrl}/leases/my`);
  }

  getByBooking(bookingId: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/leases/booking/${bookingId}`);
  }

  generate(bookingId: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/leases/booking/${bookingId}/generate`, {});
  }

  sign(leaseId: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/leases/${leaseId}/sign`, {});
  }

  uploadTemplate(propertyId: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.baseUrl}/leases/property/${propertyId}/template`, formData);
  }
}
