import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiListResponse, Booking, BookingModificationRequest } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class BookingApi extends BaseApi {
  create(payload: {
    propertyId: string;
    bookingType: string;
    requestedDate?: string | null;
    startDate?: string | null;
    endDate?: string | null;
    message?: string | null;
    numberOfOccupants?: number | null;
    /** Required for HOTEL/GUESTHOUSE/HOSTEL/RESORT property types */
    roomTypeId?: string | null;
    /** How many rooms to reserve (hotel bookings only, defaults to 1) */
    numberOfRooms?: number | null;
  }): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/bookings/draft`, payload);
  }

  initiatePayment(id: string): Observable<{ clientSecret: string; paymentIntentId: string }> {
    return this.http.post<{ clientSecret: string; paymentIntentId: string }>(
      `${this.baseUrl}/bookings/${id}/pay`,
      {},
    );
  }

  getMine(): Observable<ApiListResponse<Booking>> {
    return this.http.get<ApiListResponse<Booking>>(`${this.baseUrl}/bookings/my-bookings`);
  }

  getByProperty(propertyId: string): Observable<ApiListResponse<Booking>> {
    return this.http.get<ApiListResponse<Booking>>(
      `${this.baseUrl}/bookings/property/${propertyId}`,
    );
  }

  approve(id: string, message?: string): Observable<Booking> {
    return this.http.patch<Booking>(
      `${this.baseUrl}/bookings/${id}/approve`,
      message ? { message } : {},
    );
  }

  reject(id: string, message: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/reject`, { message });
  }

  cancel(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/cancel`, {});
  }

  earlyCheckout(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/early-checkout`, {});
  }

  getById(id: string): Observable<Booking> {
    return this.http.get<Booking>(`${this.baseUrl}/bookings/${id}`);
  }

  requestModification(id: string, request: BookingModificationRequest): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/bookings/${id}/modify`, request);
  }

  approveModification(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/modify/approve`, {});
  }

  rejectModification(id: string, reason?: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/modify/reject`, {
      message: reason,
    });
  }

  retryPayment(id: string): Observable<{ clientSecret: string; paymentIntentId: string }> {
    return this.http.post<{ clientSecret: string; paymentIntentId: string }>(
      `${this.baseUrl}/bookings/${id}/retry-payment`,
      {},
    );
  }
}
