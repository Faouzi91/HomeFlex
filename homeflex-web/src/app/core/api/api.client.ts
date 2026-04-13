import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Agency,
  Amenity,
  Analytics,
  ApiListResponse,
  ApiPageResponse,
  ApiValueResponse,
  AuthResponse,
  Booking,
  BookingModificationRequest,
  ChatRoom,
  ConditionReport,
  Dispute,
  DisputeEvidence,
  InsurancePlan,
  InsurancePolicy,
  MaintenanceRequest,
  MaintenanceRequestCreateRequest,
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
  SystemConfig,
  User,
  Vehicle,
  VehicleBooking,
  VehicleSearchParams,
} from '../models/api.types';
import {
  AdminApi,
  AgencyApi,
  AuthApi,
  BookingApi,
  ChatApi,
  CurrencyApi,
  DisputeApi,
  FavoriteApi,
  FinanceApi,
  GdprApi,
  InsuranceApi,
  KycApi,
  LeaseApi,
  MaintenanceApi,
  NotificationApi,
  PayoutApi,
  PropertyApi,
  ReviewApi,
  StatsApi,
  UserApi,
  VehicleApi,
} from './services';

/**
 * Backward-compatible facade that delegates to domain-specific API services.
 *
 * New code should inject the domain services directly (e.g. PropertyApi, BookingApi).
 * This class exists only to avoid a big-bang migration of every consumer at once.
 *
 * @deprecated Import domain services from `@core/api/services` instead.
 */
@Injectable({ providedIn: 'root' })
export class ApiClient {
  private readonly auth = inject(AuthApi);
  private readonly users = inject(UserApi);
  private readonly properties = inject(PropertyApi);
  private readonly vehicles = inject(VehicleApi);
  private readonly bookings = inject(BookingApi);
  private readonly favorites = inject(FavoriteApi);
  private readonly reviews = inject(ReviewApi);
  private readonly chat = inject(ChatApi);
  private readonly notifs = inject(NotificationApi);
  private readonly maintenance = inject(MaintenanceApi);
  private readonly disputes = inject(DisputeApi);
  private readonly leases = inject(LeaseApi);
  private readonly insurance = inject(InsuranceApi);
  private readonly finance = inject(FinanceApi);
  private readonly kyc = inject(KycApi);
  private readonly payouts = inject(PayoutApi);
  private readonly admin = inject(AdminApi);
  private readonly agencies = inject(AgencyApi);
  private readonly currencies = inject(CurrencyApi);
  private readonly gdpr = inject(GdprApi);
  private readonly stats = inject(StatsApi);

  // --- Auth ---
  login(payload: { email: string; password: string }): Observable<AuthResponse> {
    return this.auth.login(payload);
  }
  socialLogin(provider: string, token: string): Observable<AuthResponse> {
    return this.auth.socialLogin(provider, token);
  }
  register(payload: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phoneNumber?: string | null;
    role: string;
  }): Observable<AuthResponse> {
    return this.auth.register(payload);
  }
  forgotPassword(email: string): Observable<ApiValueResponse<string>> {
    return this.auth.forgotPassword(email);
  }
  resetPassword(token: string, newPassword: string): Observable<ApiValueResponse<string>> {
    return this.auth.resetPassword(token, newPassword);
  }
  sendOtp(phoneNumber: string): Observable<ApiValueResponse<string>> {
    return this.auth.sendOtp(phoneNumber);
  }
  verifyOtp(phoneNumber: string, otp: string): Observable<ApiValueResponse<boolean>> {
    return this.auth.verifyOtp(phoneNumber, otp);
  }
  refresh(): Observable<AuthResponse> {
    return this.auth.refresh();
  }
  logout(): Observable<void> {
    return this.auth.logout();
  }

  // --- User ---
  getMe(): Observable<User> {
    return this.users.getMe();
  }
  updateProfile(payload: {
    firstName?: string;
    lastName?: string;
    phoneNumber?: string | null;
    languagePreference?: string;
  }): Observable<User> {
    return this.users.updateProfile(payload);
  }
  changePassword(payload: {
    currentPassword: string;
    newPassword: string;
  }): Observable<ApiValueResponse<string>> {
    return this.users.changePassword(payload);
  }
  uploadAvatar(file: File): Observable<User> {
    return this.users.uploadAvatar(file);
  }
  getUserById(id: string): Observable<User> {
    return this.users.getUserById(id);
  }

  // --- Properties ---
  searchProperties(params: PropertySearchParams): Observable<ApiPageResponse<Property>> {
    return this.properties.search(params);
  }
  getProperty(id: string): Observable<Property> {
    return this.properties.getById(id);
  }
  trackPropertyView(id: string): Observable<void> {
    return this.properties.trackView(id);
  }
  getSimilarProperties(id: string): Observable<ApiListResponse<Property>> {
    return this.properties.getSimilar(id);
  }
  getMyProperties(): Observable<ApiListResponse<Property>> {
    return this.properties.getMine();
  }
  createProperty(payload: Record<string, unknown>): Observable<Property> {
    return this.properties.create(payload);
  }
  uploadPropertyImages(id: string, files: File[]): Observable<void> {
    return this.properties.uploadImages(id, files);
  }
  updateProperty(id: string, payload: Record<string, unknown>): Observable<Property> {
    return this.properties.update(id, payload);
  }
  deleteProperty(id: string): Observable<void> {
    return this.properties.delete(id);
  }
  compareProperties(ids: string[]): Observable<ApiListResponse<Property>> {
    return this.properties.compare(ids);
  }
  getPropertyReports(propertyId: string): Observable<ApiListResponse<ReportItem>> {
    return this.properties.getReports(propertyId);
  }
  reportProperty(payload: {
    propertyId: string;
    reason: string;
    description: string;
  }): Observable<ReportItem> {
    return this.properties.report(payload);
  }
  getPropertyAvailability(
    propertyId: string,
    start: string,
    end: string,
  ): Observable<ApiListResponse<{ date: string; status: string; bookingId?: string }>> {
    return this.properties.getAvailability(propertyId, start, end);
  }
  blockPropertyRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.properties.blockRange(propertyId, start, end);
  }
  unblockPropertyRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.properties.unblockRange(propertyId, start, end);
  }
  getPricingRecommendation(propertyId: string): Observable<PricingRecommendation> {
    return this.properties.getPricingRecommendation(propertyId);
  }

  // --- Vehicles ---
  searchVehicles(params: VehicleSearchParams): Observable<ApiPageResponse<Vehicle>> {
    return this.vehicles.search(params);
  }
  getVehicle(id: string): Observable<Vehicle> {
    return this.vehicles.getById(id);
  }
  trackVehicleView(id: string): Observable<void> {
    return this.vehicles.trackView(id);
  }
  getVehicleAvailability(id: string, startDate: string, endDate: string): Observable<boolean> {
    return this.vehicles.getAvailability(id, startDate, endDate);
  }
  createVehicleBooking(payload: {
    vehicleId: string;
    startDate: string;
    endDate: string;
    message?: string | null;
  }): Observable<VehicleBooking> {
    return this.vehicles.createBooking(payload);
  }
  getMyVehicleBookings(): Observable<ApiListResponse<VehicleBooking>> {
    return this.vehicles.getMyBookings();
  }
  createVehicle(payload: Record<string, unknown>): Observable<Vehicle> {
    return this.vehicles.create(payload);
  }
  updateVehicle(id: string, payload: Record<string, unknown>): Observable<Vehicle> {
    return this.vehicles.update(id, payload);
  }
  deleteVehicle(id: string): Observable<void> {
    return this.vehicles.delete(id);
  }
  getMyVehicles(): Observable<ApiPageResponse<Vehicle>> {
    return this.vehicles.getMine();
  }
  uploadVehicleImages(id: string, files: File[]): Observable<void> {
    return this.vehicles.uploadImages(id, files);
  }
  getVehicleConditionReports(id: string): Observable<ApiListResponse<ConditionReport>> {
    return this.vehicles.getConditionReports(id);
  }
  createVehicleConditionReport(
    id: string,
    report: Record<string, unknown>,
  ): Observable<ConditionReport> {
    return this.vehicles.createConditionReport(id, report);
  }
  getVehicleActiveBookings(id: string): Observable<ApiListResponse<VehicleBooking>> {
    return this.vehicles.getActiveBookings(id);
  }

  // --- Bookings ---
  createPropertyBooking(payload: {
    propertyId: string;
    bookingType: string;
    requestedDate?: string | null;
    startDate?: string | null;
    endDate?: string | null;
    message?: string | null;
    numberOfOccupants?: number | null;
  }): Observable<Booking> {
    return this.bookings.create(payload);
  }
  getMyPropertyBookings(): Observable<ApiListResponse<Booking>> {
    return this.bookings.getMine();
  }
  getPropertyBookings(propertyId: string): Observable<ApiListResponse<Booking>> {
    return this.bookings.getByProperty(propertyId);
  }
  approvePropertyBooking(id: string, message?: string): Observable<Booking> {
    return this.bookings.approve(id, message);
  }
  rejectPropertyBooking(id: string, message: string): Observable<Booking> {
    return this.bookings.reject(id, message);
  }
  cancelPropertyBooking(id: string): Observable<Booking> {
    return this.bookings.cancel(id);
  }
  getBookingById(id: string): Observable<Booking> {
    return this.bookings.getById(id);
  }
  requestBookingModification(id: string, request: BookingModificationRequest): Observable<Booking> {
    return this.bookings.requestModification(id, request);
  }
  approveBookingModification(id: string): Observable<Booking> {
    return this.bookings.approveModification(id);
  }
  rejectBookingModification(id: string, reason?: string): Observable<Booking> {
    return this.bookings.rejectModification(id, reason);
  }

  // --- Favorites ---
  getFavorites(): Observable<ApiListResponse<Property>> {
    return this.favorites.getAll();
  }
  isFavorite(propertyId: string): Observable<ApiValueResponse<boolean>> {
    return this.favorites.check(propertyId);
  }
  addFavorite(propertyId: string): Observable<unknown> {
    return this.favorites.add(propertyId);
  }
  removeFavorite(propertyId: string): Observable<void> {
    return this.favorites.remove(propertyId);
  }

  // --- Reviews ---
  createReview(request: ReviewCreateRequest): Observable<Review> {
    return this.reviews.create(request);
  }
  getReviews(propertyId: string): Observable<ApiListResponse<Review>> {
    return this.reviews.getByProperty(propertyId);
  }
  getAverageRating(propertyId: string): Observable<ApiValueResponse<number>> {
    return this.reviews.getPropertyAverage(propertyId);
  }
  getPropertyReviews(propertyId: string): Observable<ApiListResponse<Review>> {
    return this.reviews.getByProperty(propertyId);
  }
  getAveragePropertyRating(propertyId: string): Observable<ApiValueResponse<number>> {
    return this.reviews.getPropertyAverage(propertyId);
  }
  getTenantReviews(userId: string): Observable<ApiListResponse<Review>> {
    return this.reviews.getByTenant(userId);
  }
  getAverageTenantRating(userId: string): Observable<ApiValueResponse<number>> {
    return this.reviews.getTenantAverage(userId);
  }
  deleteReview(id: string): Observable<void> {
    return this.reviews.delete(id);
  }
  replyToReview(id: string, reply: string): Observable<Review> {
    return this.reviews.reply(id, reply);
  }

  // --- Chat ---
  createChatRoom(payload: {
    propertyId: string;
    tenantId: string;
    landlordId: string;
  }): Observable<ChatRoom> {
    return this.chat.createRoom(payload);
  }
  getChatRooms(): Observable<ApiListResponse<ChatRoom>> {
    return this.chat.getRooms();
  }
  getChatMessages(roomId: string): Observable<ApiListResponse<Message>> {
    return this.chat.getMessages(roomId);
  }
  sendMessage(roomId: string, message: string): Observable<Message> {
    return this.chat.sendMessage(roomId, message);
  }
  markMessageAsRead(messageId: string): Observable<void> {
    return this.chat.markMessageAsRead(messageId);
  }
  markChatRoomAsRead(roomId: string): Observable<void> {
    return this.chat.markRoomAsRead(roomId);
  }

  // --- Notifications ---
  getNotifications(unreadOnly = false): Observable<ApiListResponse<NotificationItem>> {
    return this.notifs.getAll(unreadOnly);
  }
  markNotificationRead(id: string): Observable<void> {
    return this.notifs.markRead(id);
  }
  markAllNotificationsRead(): Observable<void> {
    return this.notifs.markAllRead();
  }
  deleteNotification(id: string): Observable<void> {
    return this.notifs.delete(id);
  }
  registerFcmToken(token: string): Observable<ApiValueResponse<string>> {
    return this.notifs.registerFcmToken(token);
  }

  // --- Maintenance ---
  createMaintenanceRequest(
    payload: MaintenanceRequestCreateRequest,
  ): Observable<MaintenanceRequest> {
    return this.maintenance.create(payload);
  }
  uploadMaintenanceImages(id: string, files: File[]): Observable<void> {
    return this.maintenance.uploadImages(id, files);
  }
  updateMaintenanceStatus(
    id: string,
    payload: MaintenanceStatusUpdateRequest,
  ): Observable<MaintenanceRequest> {
    return this.maintenance.updateStatus(id, payload);
  }
  getMyMaintenanceRequests(): Observable<MaintenanceRequest[]> {
    return this.maintenance.getMine();
  }
  getLandlordMaintenanceRequests(): Observable<MaintenanceRequest[]> {
    return this.maintenance.getLandlord();
  }
  getMaintenanceRequest(id: string): Observable<MaintenanceRequest> {
    return this.maintenance.getById(id);
  }

  // --- Disputes ---
  openDispute(bookingId: string, reason: string, description: string): Observable<Dispute> {
    return this.disputes.open(bookingId, reason, description);
  }
  getAllDisputes(): Observable<Dispute[]> {
    return this.disputes.getAll();
  }
  resolveDispute(id: string, resolutionNotes: string): Observable<Dispute> {
    return this.disputes.resolve(id, resolutionNotes);
  }
  uploadDisputeEvidence(id: string, file: File, description?: string): Observable<DisputeEvidence> {
    return this.disputes.uploadEvidence(id, file, description);
  }
  getDisputeEvidence(id: string): Observable<DisputeEvidence[]> {
    return this.disputes.getEvidence(id);
  }

  // --- Leases ---
  getMyLeases(): Observable<ApiListResponse<any>> {
    return this.leases.getMine();
  }
  getLeaseByBooking(bookingId: string): Observable<any> {
    return this.leases.getByBooking(bookingId);
  }
  generateLease(bookingId: string): Observable<any> {
    return this.leases.generate(bookingId);
  }
  signLease(leaseId: string): Observable<any> {
    return this.leases.sign(leaseId);
  }
  uploadLeaseTemplate(propertyId: string, file: File): Observable<any> {
    return this.leases.uploadTemplate(propertyId, file);
  }

  // --- Insurance ---
  getInsurancePlans(
    type: 'TENANT' | 'LANDLORD' | 'VEHICLE' = 'TENANT',
  ): Observable<InsurancePlan[]> {
    return this.insurance.getPlans(type);
  }
  purchaseInsurancePolicy(planId: string, bookingId: string): Observable<InsurancePolicy> {
    return this.insurance.purchase(planId, bookingId);
  }

  // --- Finance ---
  getMyReceipts(): Observable<Receipt[]> {
    return this.finance.getMyReceipts();
  }

  // --- KYC ---
  getKycStatus(): Observable<
    ApiValueResponse<{
      status: string;
      rejectionReason?: string;
      verifiedAt?: string;
      submittedAt?: string;
    }>
  > {
    return this.kyc.getStatus();
  }
  createKycSession(): Observable<{
    sessionId: string;
    clientSecret: string;
    publishableKey: string;
  }> {
    return this.kyc.createSession();
  }

  // --- Payouts ---
  getPayoutSummary(): Observable<any> {
    return this.payouts.getSummary();
  }
  onboardConnectAccount(refreshUrl: string, returnUrl: string): Observable<{ url: string }> {
    return this.payouts.onboardConnectAccount(refreshUrl, returnUrl);
  }

  // --- Admin ---
  getAdminAnalytics(): Observable<Analytics> {
    return this.admin.getAnalytics();
  }
  getPendingProperties(page = 0, size = 6): Observable<ApiPageResponse<Property>> {
    return this.admin.getPendingProperties(page, size);
  }
  approveProperty(id: string): Observable<Property> {
    return this.admin.approveProperty(id);
  }
  rejectProperty(id: string, reason: string): Observable<Property> {
    return this.admin.rejectProperty(id, reason);
  }
  getReports(page = 0, size = 20): Observable<ApiPageResponse<ReportItem>> {
    return this.admin.getReports(page, size);
  }
  resolveReport(id: string, reason?: string): Observable<ReportItem> {
    return this.admin.resolveReport(id, reason);
  }
  getAdminUsers(page = 0, size = 20): Observable<ApiPageResponse<User>> {
    return this.admin.getUsers(page, size);
  }
  suspendUser(id: string): Observable<User> {
    return this.admin.suspendUser(id);
  }
  activateUser(id: string): Observable<User> {
    return this.admin.activateUser(id);
  }
  getSystemConfigs(): Observable<SystemConfig[]> {
    return this.admin.getSystemConfigs();
  }
  updateSystemConfig(key: string, value: string): Observable<SystemConfig> {
    return this.admin.updateSystemConfig(key, value);
  }
  createAmenity(amenity: {
    name: string;
    nameFr: string;
    icon: string;
    category: string;
  }): Observable<Amenity> {
    return this.admin.createAmenity(amenity);
  }
  deleteAmenity(id: string): Observable<void> {
    return this.admin.deleteAmenity(id);
  }

  // --- Agencies ---
  getAllAgencies(): Observable<Agency[]> {
    return this.agencies.getAll();
  }
  getAgency(id: string): Observable<Agency> {
    return this.agencies.getById(id);
  }
  verifyAgency(id: string): Observable<Agency> {
    return this.agencies.verify(id);
  }

  // --- Currencies ---
  getCurrencyRates(): Observable<Record<string, number>> {
    return this.currencies.getRates();
  }
  convertCurrency(amount: number, from: string, to: string): Observable<number> {
    return this.currencies.convert(amount, from, to);
  }

  // --- Stats ---
  getStats(): Observable<ApiValueResponse<Record<string, number>>> {
    return this.stats.get();
  }

  // --- GDPR ---
  exportData(): Observable<Record<string, unknown>> {
    return this.gdpr.exportData();
  }
  eraseData(): Observable<void> {
    return this.gdpr.eraseData();
  }
}
