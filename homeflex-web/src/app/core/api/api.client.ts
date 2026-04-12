import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Agency,
  Analytics,
  ApiListResponse,
  ApiPageResponse,
  ApiValueResponse,
  AuthResponse,
  Booking,
  BookingModificationRequest,
  ChatRoom,
  Dispute,
  InsurancePlan,
  InsurancePolicy,
  MaintenanceRequest,
  MaintenanceRequestCreateRequest,
  MaintenanceStatus,
  MaintenanceStatusUpdateRequest,
  Message,
  NotificationItem,
  PricingRecommendation,
  Property,
  PropertySearchParams,
  Receipt,
  ReportItem,
  Review,
  ReviewCreateRequest,
  User,
  Vehicle,
  VehicleBooking,
  VehicleSearchParams,
} from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class ApiClient {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  createMaintenanceRequest(
    payload: MaintenanceRequestCreateRequest,
  ): Observable<MaintenanceRequest> {
    return this.http.post<MaintenanceRequest>(`${this.baseUrl}/maintenance`, payload);
  }

  uploadMaintenanceImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    return this.http.post<void>(`${this.baseUrl}/maintenance/${id}/images`, formData);
  }

  updateMaintenanceStatus(
    id: string,
    payload: MaintenanceStatusUpdateRequest,
  ): Observable<MaintenanceRequest> {
    return this.http.patch<MaintenanceRequest>(`${this.baseUrl}/maintenance/${id}/status`, payload);
  }

  getMyMaintenanceRequests(): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(`${this.baseUrl}/maintenance/my`);
  }

  getLandlordMaintenanceRequests(): Observable<MaintenanceRequest[]> {
    return this.http.get<MaintenanceRequest[]>(`${this.baseUrl}/maintenance/landlord`);
  }

  getMaintenanceRequest(id: string): Observable<MaintenanceRequest> {
    return this.http.get<MaintenanceRequest>(`${this.baseUrl}/maintenance/${id}`);
  }

  searchProperties(params: PropertySearchParams): Observable<ApiPageResponse<Property>> {
    return this.http.get<ApiPageResponse<Property>>(`${this.baseUrl}/properties/search`, {
      params: this.buildParams(params),
    });
  }

  getProperty(id: string): Observable<Property> {
    return this.http.get<Property>(`${this.baseUrl}/properties/${id}`);
  }

  trackPropertyView(id: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${id}/view`, {});
  }

  getSimilarProperties(id: string): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/properties/${id}/similar`);
  }

  getFavorites(): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/favorites`);
  }

  isFavorite(propertyId: string): Observable<ApiValueResponse<boolean>> {
    return this.http.get<ApiValueResponse<boolean>>(
      `${this.baseUrl}/favorites/check/${propertyId}`,
    );
  }

  addFavorite(propertyId: string): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/favorites/${propertyId}`, {});
  }

  removeFavorite(propertyId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/favorites/${propertyId}`);
  }

  getReviews(propertyId: string): Observable<ApiListResponse<Review>> {
    return this.http.get<ApiListResponse<Review>>(`${this.baseUrl}/reviews/property/${propertyId}`);
  }

  getAverageRating(propertyId: string): Observable<ApiValueResponse<number>> {
    return this.http.get<ApiValueResponse<number>>(
      `${this.baseUrl}/reviews/property/${propertyId}/average`,
    );
  }

  reportProperty(payload: {
    propertyId: string;
    reason: string;
    description: string;
  }): Observable<ReportItem> {
    return this.http.post<ReportItem>(`${this.baseUrl}/properties/${payload.propertyId}/report`, {
      reason: payload.reason,
      description: payload.description,
    });
  }

  createPropertyBooking(payload: {
    propertyId: string;
    bookingType: string;
    requestedDate?: string | null;
    startDate?: string | null;
    endDate?: string | null;
    message?: string | null;
    numberOfOccupants?: number | null;
  }): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/bookings`, payload);
  }

  getMyPropertyBookings(): Observable<ApiListResponse<Booking>> {
    return this.http.get<ApiListResponse<Booking>>(`${this.baseUrl}/bookings/my-bookings`);
  }

  getPropertyBookings(propertyId: string): Observable<ApiListResponse<Booking>> {
    return this.http.get<ApiListResponse<Booking>>(
      `${this.baseUrl}/bookings/property/${propertyId}`,
    );
  }

  approvePropertyBooking(id: string, message?: string): Observable<Booking> {
    return this.http.patch<Booking>(
      `${this.baseUrl}/bookings/${id}/approve`,
      message ? { message } : {},
    );
  }

  rejectPropertyBooking(id: string, message: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/reject`, { message });
  }

  cancelPropertyBooking(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/cancel`, {});
  }

  requestBookingModification(id: string, request: BookingModificationRequest): Observable<Booking> {
    return this.http.post<Booking>(`${this.baseUrl}/bookings/${id}/modify`, request);
  }

  approveBookingModification(id: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/modify/approve`, {});
  }

  rejectBookingModification(id: string, reason?: string): Observable<Booking> {
    return this.http.patch<Booking>(`${this.baseUrl}/bookings/${id}/modify/reject`, {
      message: reason,
    });
  }

  searchVehicles(params: VehicleSearchParams): Observable<ApiPageResponse<Vehicle>> {
    return this.http.get<ApiPageResponse<Vehicle>>(`${this.baseUrl}/vehicles/search`, {
      params: this.buildParams(params),
    });
  }

  getVehicle(id: string): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.baseUrl}/vehicles/${id}`);
  }

  trackVehicleView(id: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/vehicles/${id}/view`, {});
  }

  getVehicleAvailability(id: string, startDate: string, endDate: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/vehicles/${id}/availability`, {
      params: this.buildParams({ startDate, endDate }),
    });
  }

  createVehicleBooking(payload: {
    vehicleId: string;
    startDate: string;
    endDate: string;
    message?: string | null;
  }): Observable<VehicleBooking> {
    return this.http.post<VehicleBooking>(
      `${this.baseUrl}/vehicles/${payload.vehicleId}/bookings`,
      payload,
    );
  }

  getMyVehicleBookings(): Observable<ApiListResponse<VehicleBooking>> {
    return this.http.get<ApiListResponse<VehicleBooking>>(`${this.baseUrl}/vehicles/my-bookings`);
  }

  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, payload);
  }

  socialLogin(provider: string, token: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/${provider}`, { token });
  }

  register(payload: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string | null;
    role: string;
  }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/register`, payload);
  }

  forgotPassword(email: string): Observable<ApiValueResponse<string>> {
    return this.http.post<ApiValueResponse<string>>(`${this.baseUrl}/auth/forgot-password`, {
      email,
    });
  }

  refresh(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/refresh`, {});
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/auth/logout`, {});
  }

  getMe(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/me`);
  }

  updateProfile(payload: {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string | null;
    languagePreference?: string;
  }): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/users/me`, payload);
  }

  changePassword(payload: {
    currentPassword: string;
    newPassword: string;
  }): Observable<ApiValueResponse<string>> {
    return this.http.put<ApiValueResponse<string>>(`${this.baseUrl}/users/me/password`, payload);
  }

  createChatRoom(payload: {
    propertyId: string;
    tenantId: string;
    landlordId: string;
  }): Observable<ChatRoom> {
    return this.http.post<ChatRoom>(`${this.baseUrl}/chat/rooms`, payload);
  }

  getChatRooms(): Observable<ApiListResponse<ChatRoom>> {
    return this.http.get<ApiListResponse<ChatRoom>>(`${this.baseUrl}/chat/rooms`);
  }

  getChatMessages(roomId: string): Observable<ApiListResponse<Message>> {
    return this.http.get<ApiListResponse<Message>>(`${this.baseUrl}/chat/rooms/${roomId}/messages`);
  }

  sendMessage(roomId: string, message: string): Observable<Message> {
    return this.http.post<Message>(`${this.baseUrl}/chat/rooms/${roomId}/messages`, { message });
  }

  getNotifications(unreadOnly = false): Observable<ApiListResponse<NotificationItem>> {
    return this.http.get<ApiListResponse<NotificationItem>>(`${this.baseUrl}/notifications`, {
      params: this.buildParams({ unreadOnly }),
    });
  }

  markNotificationRead(id: string): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/${id}/read`, {});
  }

  markAllNotificationsRead(): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/notifications/read-all`, {});
  }

  deleteNotification(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/notifications/${id}`);
  }

  getStats(): Observable<ApiValueResponse<Record<string, number>>> {
    return this.http.get<ApiValueResponse<Record<string, number>>>(`${this.baseUrl}/stats`);
  }

  getMyProperties(): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/properties/my-properties`);
  }

  createProperty(payload: Record<string, unknown>): Observable<Property> {
    return this.http.post<Property>(`${this.baseUrl}/properties/json`, payload);
  }

  uploadPropertyImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return this.http.post<void>(`${this.baseUrl}/properties/${id}/images`, formData);
  }

  // --- KYC Verification ---

  getKycStatus(): Observable<
    ApiValueResponse<{
      status: string;
      rejectionReason?: string;
      verifiedAt?: string;
      submittedAt?: string;
    }>
  > {
    return this.http.get<ApiValueResponse<any>>(`${this.baseUrl}/kyc/status`);
  }

  createKycSession(): Observable<{
    sessionId: string;
    clientSecret: string;
    publishableKey: string;
  }> {
    return this.http.post<any>(`${this.baseUrl}/kyc/session`, {});
  }

  // --- Stripe Connect & Payouts ---

  getPayoutSummary(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/payouts/summary`);
  }

  onboardConnectAccount(refreshUrl: string, returnUrl: string): Observable<{ url: string }> {
    return this.http.post<any>(`${this.baseUrl}/payouts/connect/onboard`, {
      refreshUrl,
      returnUrl,
    });
  }

  // --- Property Availability ---

  getPropertyAvailability(
    propertyId: string,
    start: string,
    end: string,
  ): Observable<
    ApiListResponse<{
      date: string;
      status: string;
      bookingId?: string;
    }>
  > {
    return this.http.get<ApiListResponse<any>>(
      `${this.baseUrl}/properties/${propertyId}/availability`,
      {
        params: this.buildParams({ start, end }),
      },
    );
  }

  blockPropertyRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${propertyId}/availability/block`, {
      start,
      end,
    });
  }

  unblockPropertyRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${propertyId}/availability/unblock`, {
      start,
      end,
    });
  }

  // --- Lease Management ---

  getMyLeases(): Observable<ApiListResponse<any>> {
    return this.http.get<ApiListResponse<any>>(`${this.baseUrl}/leases/my`);
  }

  getLeaseByBooking(bookingId: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/leases/booking/${bookingId}`);
  }

  generateLease(bookingId: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/leases/booking/${bookingId}/generate`, {});
  }

  signLease(leaseId: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/leases/${leaseId}/sign`, {});
  }

  uploadLeaseTemplate(propertyId: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.baseUrl}/leases/property/${propertyId}/template`, formData);
  }

  createVehicle(payload: Record<string, unknown>): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.baseUrl}/vehicles`, payload);
  }

  updateVehicle(id: string, payload: Record<string, unknown>): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.baseUrl}/vehicles/${id}`, payload);
  }

  deleteVehicle(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/vehicles/${id}`);
  }

  getMyVehicles(): Observable<ApiPageResponse<Vehicle>> {
    return this.http.get<ApiPageResponse<Vehicle>>(`${this.baseUrl}/vehicles/my-vehicles`);
  }

  uploadVehicleImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return this.http.post<void>(`${this.baseUrl}/vehicles/${id}/images`, formData);
  }

  // --- Insurance Marketplace ---

  getInsurancePlans(
    type: 'TENANT' | 'LANDLORD' | 'VEHICLE' = 'TENANT',
  ): Observable<InsurancePlan[]> {
    return this.http.get<InsurancePlan[]>(`${this.baseUrl}/insurance/plans`, {
      params: this.buildParams({ type }),
    });
  }

  purchaseInsurancePolicy(planId: string, bookingId: string): Observable<InsurancePolicy> {
    return this.http.post<InsurancePolicy>(`${this.baseUrl}/insurance/purchase`, null, {
      params: this.buildParams({ planId, bookingId }),
    });
  }

  // --- Finance & Receipts ---

  getMyReceipts(): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.baseUrl}/finance/receipts`);
  }

  // --- Disputes ---

  openDispute(bookingId: string, reason: string, description: string): Observable<Dispute> {
    return this.http.post<Dispute>(`${this.baseUrl}/disputes`, null, {
      params: this.buildParams({ bookingId, reason, description }),
    });
  }

  getAllDisputes(): Observable<Dispute[]> {
    return this.http.get<Dispute[]>(`${this.baseUrl}/disputes`);
  }

  resolveDispute(id: string, resolutionNotes: string): Observable<Dispute> {
    return this.http.patch<Dispute>(`${this.baseUrl}/disputes/${id}/resolve`, null, {
      params: this.buildParams({ resolutionNotes }),
    });
  }

  // --- Reviews ---

  createReview(request: ReviewCreateRequest): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews`, request);
  }

  getPropertyReviews(propertyId: string): Observable<ApiListResponse<Review>> {
    return this.http.get<ApiListResponse<Review>>(`${this.baseUrl}/reviews/property/${propertyId}`);
  }

  getAveragePropertyRating(propertyId: string): Observable<ApiValueResponse<number>> {
    return this.http.get<ApiValueResponse<number>>(
      `${this.baseUrl}/reviews/property/${propertyId}/average`,
    );
  }

  getTenantReviews(userId: string): Observable<ApiListResponse<Review>> {
    return this.http.get<ApiListResponse<Review>>(`${this.baseUrl}/reviews/tenant/${userId}`);
  }

  getAverageTenantRating(userId: string): Observable<ApiValueResponse<number>> {
    return this.http.get<ApiValueResponse<number>>(
      `${this.baseUrl}/reviews/tenant/${userId}/average`,
    );
  }

  deleteReview(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/reviews/${id}`);
  }

  // --- GDPR Compliance ---

  exportData(): Observable<Record<string, unknown>> {
    return this.http.get<Record<string, unknown>>(`${this.baseUrl}/gdpr/export`);
  }

  eraseData(): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/gdpr/erase`);
  }

  // --- Currencies ---

  getCurrencyRates(): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(`${this.baseUrl}/currencies/rates`);
  }

  convertCurrency(amount: number, from: string, to: string): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/currencies/convert`, {
      params: this.buildParams({ amount, from, to }),
    });
  }

  // --- AI Pricing ---

  getPricingRecommendation(propertyId: string): Observable<PricingRecommendation> {
    return this.http.get<PricingRecommendation>(
      `${this.baseUrl}/properties/${propertyId}/pricing/recommendation`,
    );
  }

  // --- Agencies ---

  getAllAgencies(): Observable<Agency[]> {
    return this.http.get<Agency[]>(`${this.baseUrl}/agencies`);
  }

  getAgency(id: string): Observable<Agency> {
    return this.http.get<Agency>(`${this.baseUrl}/agencies/${id}`);
  }

  verifyAgency(id: string): Observable<Agency> {
    return this.http.patch<Agency>(`${this.baseUrl}/agencies/${id}/verify`, {});
  }

  getVehicleConditionReports(id: string): Observable<ApiListResponse<any>> {
    return this.http.get<ApiListResponse<any>>(`${this.baseUrl}/vehicles/${id}/condition`);
  }

  getAdminAnalytics(): Observable<Analytics> {
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

  private buildParams(values: object): HttpParams {
    let params = new HttpParams();

    Object.entries(values as Record<string, unknown>).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        params = params.set(key, String(value));
      }
    });

    return params;
  }
}
