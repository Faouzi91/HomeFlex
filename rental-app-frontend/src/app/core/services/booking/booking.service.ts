// ====================================
// booking.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";

export interface Booking {
  id: string;
  property: any;
  tenant: any;
  bookingType: string;
  requestedDate?: Date;
  startDate?: Date;
  endDate?: Date;
  status: string;
  message?: string;
  landlordResponse?: string;
  createdAt: Date;
}

export interface BookingRequest {
  propertyId: string;
  bookingType: "VIEWING" | "RENTAL" | "PURCHASE";
  requestedDate?: Date;
  startDate?: Date;
  endDate?: Date;
  message?: string;
  numberOfOccupants?: number;
}

@Injectable({
  providedIn: "root",
})
export class BookingService {
  private apiUrl = `${environment.apiUrl}/bookings`;

  constructor(private http: HttpClient) {}

  createBooking(request: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(this.apiUrl, request);
  }

  getMyBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/my-bookings`);
  }

  getPropertyBookings(propertyId: string): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/property/${propertyId}`);
  }

  getBookingById(id: string): Observable<Booking> {
    return this.http.get<Booking>(`${this.apiUrl}/${id}`);
  }

  approveBooking(id: string, response?: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/approve`, {
      message: response,
    });
  }

  rejectBooking(id: string, reason: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/reject`, {
      message: reason,
    });
  }

  cancelBooking(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.apiUrl}/${id}/cancel`, {});
  }
}
