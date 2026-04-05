import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/app/environments/environment';
import {
  Vehicle,
  VehicleBooking,
  VehicleBookingCreateRequest,
  VehicleSearchParams,
} from 'src/app/models/vehicle.model';
import { ApiPageResponse, ApiListResponse } from 'src/app/types/api.types';

@Injectable({
  providedIn: 'root',
})
export class VehicleService {
  private apiUrl = `${environment.apiUrl}/vehicles`;

  constructor(private http: HttpClient) {}

  search(params: VehicleSearchParams): Observable<ApiPageResponse<Vehicle>> {
    let httpParams = new HttpParams();
    if (params.brand) httpParams = httpParams.set('brand', params.brand);
    if (params.model) httpParams = httpParams.set('model', params.model);
    if (params.city) httpParams = httpParams.set('city', params.city);
    if (params.transmission) httpParams = httpParams.set('transmission', params.transmission);
    if (params.fuelType) httpParams = httpParams.set('fuelType', params.fuelType);
    if (params.minPrice != null)
      httpParams = httpParams.set('minPrice', params.minPrice.toString());
    if (params.maxPrice != null)
      httpParams = httpParams.set('maxPrice', params.maxPrice.toString());
    if (params.page != null) httpParams = httpParams.set('page', params.page.toString());
    if (params.size != null) httpParams = httpParams.set('size', params.size.toString());

    return this.http.get<ApiPageResponse<Vehicle>>(`${this.apiUrl}/search`, {
      params: httpParams,
    });
  }

  getById(id: string): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.apiUrl}/${id}`);
  }

  incrementViewCount(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/view`, {});
  }

  checkAvailability(id: string, startDate: string, endDate: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${id}/availability`, {
      params: { startDate, endDate },
    });
  }

  getActiveBookings(id: string): Observable<VehicleBooking[]> {
    return this.http
      .get<ApiListResponse<VehicleBooking>>(`${this.apiUrl}/${id}/bookings`)
      .pipe(map((r) => r.data));
  }

  createBooking(request: VehicleBookingCreateRequest): Observable<VehicleBooking> {
    return this.http.post<VehicleBooking>(`${this.apiUrl}/${request.vehicleId}/bookings`, request);
  }

  getMyBookings(): Observable<VehicleBooking[]> {
    return this.http
      .get<ApiListResponse<VehicleBooking>>(`${this.apiUrl}/my-bookings`)
      .pipe(map((r) => r.data));
  }
}
