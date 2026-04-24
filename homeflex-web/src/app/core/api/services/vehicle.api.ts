import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApiListResponse,
  ApiPageResponse,
  ConditionReport,
  Vehicle,
  VehicleBooking,
  VehicleSearchParams,
} from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class VehicleApi extends BaseApi {
  search(params: VehicleSearchParams): Observable<ApiPageResponse<Vehicle>> {
    return this.http.get<ApiPageResponse<Vehicle>>(`${this.baseUrl}/vehicles/search`, {
      params: this.buildParams(params),
    });
  }

  getById(id: string): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.baseUrl}/vehicles/${id}`);
  }

  trackView(id: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/vehicles/${id}/view`, {});
  }

  getAvailability(id: string, startDate: string, endDate: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/vehicles/${id}/availability`, {
      params: this.buildParams({ startDate, endDate }),
    });
  }

  createDraft(payload: {
    vehicleId: string;
    startDate: string;
    endDate: string;
    message?: string | null;
  }): Observable<VehicleBooking> {
    return this.http.post<VehicleBooking>(
      `${this.baseUrl}/vehicles/${payload.vehicleId}/bookings/draft`,
      payload,
    );
  }

  initiatePayment(
    vehicleId: string,
    bookingId: string,
  ): Observable<{ clientSecret: string; paymentIntentId: string }> {
    return this.http.post<{ clientSecret: string; paymentIntentId: string }>(
      `${this.baseUrl}/vehicles/${vehicleId}/bookings/${bookingId}/pay`,
      {},
    );
  }

  getMyBookings(): Observable<ApiListResponse<VehicleBooking>> {
    return this.http.get<ApiListResponse<VehicleBooking>>(`${this.baseUrl}/vehicles/my-bookings`);
  }

  create(payload: Record<string, unknown>): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.baseUrl}/vehicles`, payload);
  }

  update(id: string, payload: Record<string, unknown>): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.baseUrl}/vehicles/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/vehicles/${id}`);
  }

  getMine(): Observable<ApiPageResponse<Vehicle>> {
    return this.http.get<ApiPageResponse<Vehicle>>(`${this.baseUrl}/vehicles/my-vehicles`);
  }

  uploadImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return this.http.post<void>(`${this.baseUrl}/vehicles/${id}/images`, formData);
  }

  getConditionReports(id: string): Observable<ApiListResponse<ConditionReport>> {
    return this.http.get<ApiListResponse<ConditionReport>>(
      `${this.baseUrl}/vehicles/${id}/condition`,
    );
  }

  createConditionReport(id: string, report: Record<string, unknown>): Observable<ConditionReport> {
    return this.http.post<ConditionReport>(`${this.baseUrl}/vehicles/${id}/condition`, report);
  }

  getActiveBookings(id: string): Observable<ApiListResponse<VehicleBooking>> {
    return this.http.get<ApiListResponse<VehicleBooking>>(
      `${this.baseUrl}/vehicles/${id}/bookings`,
    );
  }
}
