import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PropertyLease } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class LeaseApi extends BaseApi {
  getMine(): Observable<PropertyLease[]> {
    return this.http.get<PropertyLease[]>(`${this.baseUrl}/leases/my`);
  }

  getByBooking(bookingId: string): Observable<PropertyLease> {
    return this.http.get<PropertyLease>(`${this.baseUrl}/leases/booking/${bookingId}`);
  }

  generate(bookingId: string): Observable<PropertyLease> {
    return this.http.post<PropertyLease>(
      `${this.baseUrl}/leases/booking/${bookingId}/generate`,
      {},
    );
  }

  sign(leaseId: string): Observable<PropertyLease> {
    return this.http.post<PropertyLease>(`${this.baseUrl}/leases/${leaseId}/sign`, {});
  }

  uploadTemplate(propertyId: string, file: File): Observable<PropertyLease> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<PropertyLease>(
      `${this.baseUrl}/leases/property/${propertyId}/template`,
      formData,
    );
  }
}
