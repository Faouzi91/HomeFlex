export interface ApiPageResponse<T> {
  data: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiListResponse<T> {
  data: T[];
}

export interface ApiValueResponse<T> {
  data: T;
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
  profilePictureUrl: string | null;
  role: 'TENANT' | 'LANDLORD' | 'ADMIN' | string;
  roles?: string[];
  permissions?: string[];
  isActive: boolean;
  isVerified: boolean;
  languagePreference: string | null;
  agencyId?: string | null;
  agencyRole?: string | null;
  trustScore?: number;
  emailNotificationsEnabled?: boolean;
  pushNotificationsEnabled?: boolean;
  smsNotificationsEnabled?: boolean;
  profileCompleteness?: number;
  createdAt: string;
  stripeConnected?: boolean;
  stripeAccountId?: string | null;
}

export interface PayoutSummary {
  availableBalance: number;
  pendingBalance: number;
  escrowHeld: number;
  totalEarnings: number;
  stripeAccountConnected: boolean;
}

export interface ConnectOnboardingResponse {
  stripeAccountId: string;
  onboardingUrl: string;
}

export interface AuthResponse {
  user: User;
}

export interface PropertyImage {
  id: string;
  imageUrl: string;
  thumbnailUrl: string | null;
  displayOrder: number;
  isPrimary: boolean;
}

export interface Amenity {
  id: string;
  name: string;
  nameFr: string;
  icon: string;
  category: string;
}

export interface Property {
  id: string;
  title: string;
  description: string;
  propertyType: string;
  listingType: string;
  price: number;
  currency: string;
  address: string;
  city: string;
  stateProvince: string | null;
  country: string;
  postalCode: string | null;
  latitude: number | null;
  longitude: number | null;
  bedrooms: number | null;
  bathrooms: number | null;
  areaSqm: number | null;
  floorNumber: number | null;
  totalFloors: number | null;
  instantBookEnabled?: boolean;
  cleaningFee?: number | null;
  securityDeposit?: number | null;
  cancellationPolicy?: string | null;
  isAvailable: boolean;
  checkInTime?: string;
  checkOutTime?: string;
  starRating?: number | null;
  petsAllowed?: boolean;
  smokingAllowed?: boolean;
  childrenAllowed?: boolean;
  minStayNights?: number;
  maxStayNights?: number | null;
  houseRules?: string | null;
  rejectionReason?: string | null;
  submittedAt?: string | null;
  approvedAt?: string | null;
  availableFrom: string | null;
  status: string;
  viewCount: number;
  favoriteCount: number;
  images: PropertyImage[];
  videos: unknown[];
  amenities: Amenity[];
  landlord: User;
  createdAt: string;
  updatedAt: string;
}

export interface PricingRule {
  id: string;
  propertyId: string;
  ruleType: 'WEEKEND' | 'SEASONAL' | 'LONG_STAY';
  label: string | null;
  multiplier: number;
  minStayDays: number | null;
  startDate: string | null;
  endDate: string | null;
}

export interface PricingRuleCreateRequest {
  ruleType: string;
  label?: string;
  multiplier: number;
  minStayDays?: number;
  startDate?: string;
  endDate?: string;
}

export interface PropertySearchParams {
  q?: string;
  city?: string;
  minPrice?: number | null;
  maxPrice?: number | null;
  propertyType?: string;
  bedrooms?: number | null;
  bathrooms?: number | null;
  amenityIds?: string[];
  lat?: number | null;
  lng?: number | null;
  page?: number;
  size?: number;
}

export interface VehicleImage {
  id: string;
  imageUrl: string;
  displayOrder: number;
  isPrimary: boolean;
}

export interface Vehicle {
  id: string;
  ownerId: string;
  brand: string;
  model: string;
  year: number;
  transmission: string;
  fuelType: string;
  dailyPrice: number;
  currency: string;
  status: string;
  description: string | null;
  mileage: number | null;
  seats: number | null;
  color: string | null;
  licensePlate: string | null;
  pickupCity: string | null;
  pickupAddress: string | null;
  viewCount: number;
  images: VehicleImage[];
  owner: User | null;
  createdAt: string;
  updatedAt: string;
}

export interface VehicleSearchParams {
  brand?: string;
  model?: string;
  city?: string;
  transmission?: string;
  fuelType?: string;
  status?: string;
  minPrice?: number | null;
  maxPrice?: number | null;
  page?: number;
  size?: number;
}

export interface Booking {
  id: string;
  property: Property;
  tenant: User;
  bookingType: string;
  requestedDate: string | null;
  startDate: string | null;
  endDate: string | null;
  status: string;
  message: string | null;
  numberOfOccupants: number | null;
  totalPrice: number | null;
  platformFee: number | null;
  cleaningFee: number | null;
  taxAmount: number | null;
  stripePaymentIntentId: string | null;
  stripeClientSecret: string | null;
  paymentStatus: string | null;
  paymentFailureReason: string | null;
  paymentConfirmedAt: string | null;
  escrowReleasedAt: string | null;
  landlordResponse: string | null;
  proposedStartDate: string | null;
  proposedEndDate: string | null;
  modificationReason: string | null;
  respondedAt: string | null;
  createdAt: string;
  // Hotel room fields
  roomTypeId?: string | null;
  roomTypeName?: string | null;
  numberOfRooms?: number;
  unitId?: string | null;
  unitNumber?: string | null;
}

export type RentalPhase = 'UPCOMING' | 'ACTIVE' | 'PAST';

export interface BookingModificationRequest {
  startDate: string;
  endDate: string;
  reason?: string;
}

export interface VehicleBooking {
  id: string;
  vehicleId: string;
  vehicle: Vehicle;
  tenantId: string;
  tenant: User;
  startDate: string;
  endDate: string;
  totalPrice: number;
  currency: string;
  status: string;
  platformFee?: number;
  message?: string;
  rejectionReason?: string;
  createdAt: string;
}

export type ReviewType = 'PROPERTY' | 'TENANT';

export interface Review {
  id: string;
  type: ReviewType;
  propertyId?: string;
  propertyTitle?: string;
  targetUser?: User;
  reviewer: User;
  rating: number;
  cleanlinessRating?: number | null;
  accuracyRating?: number | null;
  communicationRating?: number | null;
  locationRating?: number | null;
  checkinRating?: number | null;
  valueRating?: number | null;
  comment: string | null;
  reply?: string | null;
  repliedAt?: string | null;
  createdAt: string;
}

export interface ReviewCreateRequest {
  propertyId?: string;
  targetUserId?: string;
  rating: number;
  cleanlinessRating?: number;
  accuracyRating?: number;
  communicationRating?: number;
  locationRating?: number;
  checkinRating?: number;
  valueRating?: number;
  comment: string;
}

export interface ChatRoom {
  id: string;
  propertyId: string;
  propertyTitle: string;
  tenant: User;
  landlord: User;
  lastMessageAt: string | null;
  unreadCount: number | null;
}

export interface Message {
  id: string;
  chatRoomId: string;
  senderId: string;
  senderName: string;
  messageText: string;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: string;
  relatedEntityType: string | null;
  relatedEntityId: string | null;
  isRead: boolean;
  createdAt: string;
}

export interface Analytics {
  totalUsers: number;
  totalTenants: number;
  totalLandlords: number;
  totalProperties: number;
  pendingProperties: number;
  approvedProperties: number;
  totalBookings: number;
  pendingBookings: number;
  approvedBookings: number;
  totalMessages: number;
  propertiesByType: Record<string, number>;
  propertiesByCity: Record<string, number>;
  bookingsByStatus: Record<string, number>;
  topViewedProperties: TopProperty[];
  topFavoritedProperties: TopProperty[];
}

export interface TopProperty {
  propertyId: string;
  title: string;
  value: number;
}

export interface ReportItem {
  id: string;
  propertyId: string;
  propertyTitle: string;
  reporter: User;
  reason: string;
  description: string | null;
  status: string;
  createdAt: string;
  resolvedAt: string | null;
  resolvedBy: User | null;
}

export type MaintenanceCategory = 'PLUMBING' | 'ELECTRICAL' | 'APPLIANCE' | 'STRUCTURAL' | 'OTHER';
export type MaintenancePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type MaintenanceStatus = 'REPORTED' | 'IN_PROGRESS' | 'RESOLVED' | 'CANCELLED';

export interface MaintenanceRequest {
  id: string;
  propertyId: string;
  propertyTitle: string;
  tenantId: string;
  tenantName: string;
  title: string;
  description: string;
  category: MaintenanceCategory;
  priority: MaintenancePriority;
  status: MaintenanceStatus;
  resolutionNotes: string | null;
  resolvedAt: string | null;
  imageUrls: string[];
  createdAt: string;
  updatedAt: string;
}

export interface MaintenanceRequestCreateRequest {
  propertyId: string;
  title: string;
  description: string;
  category: MaintenanceCategory;
  priority: MaintenancePriority;
}

export interface MaintenanceStatusUpdateRequest {
  status: MaintenanceStatus;
  resolutionNotes?: string;
}

export interface InsurancePlan {
  id: string;
  providerName: string;
  name: string;
  type: 'TENANT' | 'LANDLORD' | 'VEHICLE';
  description: string;
  coverageDetails: string;
  dailyPremium: number;
  maxCoverageAmount: number;
}

export interface InsurancePolicy {
  id: string;
  plan: InsurancePlan;
  user: User;
  booking?: Booking;
  policyNumber: string;
  status: string;
  startDate: string;
  endDate: string;
  totalPremium: number;
  certificateUrl: string;
  createdAt: string;
}

export interface Agency {
  id: string;
  name: string;
  description?: string;
  logoUrl?: string;
  websiteUrl?: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  isVerified: boolean;
  customDomain?: string;
  themePrimaryColor: string;
  createdAt: string;
}

export interface PropertyLease {
  id: string;
  propertyId: string;
  bookingId: string;
  landlordId: string;
  tenantId: string;
  leaseUrl: string;
  status: 'PENDING' | 'SIGNED' | 'EXPIRED' | 'CANCELLED';
  signedAt: string | null;
  createdAt: string;
}

export interface Receipt {
  id: string;
  bookingId?: string;
  userId: string;
  receiptNumber: string;
  amount: number;
  currency: string;
  status: string;
  receiptUrl?: string;
  issuedAt: string;
  createdAt: string;
}

export interface Dispute {
  id: string;
  bookingId: string;
  initiatorId: string;
  reason: string;
  description?: string;
  status: 'OPEN' | 'UNDER_REVIEW' | 'RESOLVED' | 'CLOSED';
  resolutionNotes?: string;
  resolvedAt?: string;
  resolvedById?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PricingRecommendation {
  propertyId: string;
  currentPrice: number;
  recommendedPrice: number;
  confidenceLevel: 'LOW' | 'MEDIUM' | 'HIGH';
  reasoning: string;
}

export type BedType = 'SINGLE' | 'DOUBLE' | 'TWIN' | 'QUEEN' | 'KING' | 'BUNK' | 'SOFA';

export interface RoomTypeImage {
  id: string;
  imageUrl: string;
  displayOrder: number;
  isPrimary: boolean;
}

export interface RoomType {
  id: string;
  propertyId: string;
  name: string;
  description: string | null;
  bedType: BedType;
  numBeds: number;
  maxOccupancy: number;
  pricePerNight: number;
  currency: string;
  totalRooms: number;
  sizeSqm: number | null;
  isActive: boolean;
  images: RoomTypeImage[];
  amenities: Amenity[];
  createdAt: string;
}

export interface RoomTypeCreateRequest {
  name: string;
  description?: string;
  bedType: BedType;
  numBeds: number;
  maxOccupancy: number;
  pricePerNight: number;
  currency?: string;
  totalRooms: number;
  sizeSqm?: number;
  amenityIds?: string[];
}

export type UnitStatus = 'AVAILABLE' | 'OUT_OF_SERVICE' | 'UNDER_MAINTENANCE';

export interface PropertyUnit {
  id: string;
  roomTypeId: string;
  unitNumber: string;
  floor: number | null;
  status: UnitStatus;
  notes: string | null;
  createdAt: string;
}

export interface PropertyUnitRequest {
  unitNumber: string;
  floor?: number | null;
  status?: UnitStatus;
  notes?: string | null;
}

// Occupancy
export interface OccupancyDay {
  date: string;
  status: 'AVAILABLE' | 'BOOKED' | 'BLOCKED';
  bookingId?: string;
}

export interface RoomDay {
  date: string;
  bookedRooms: number;
  availableRooms: number;
}

export interface RoomTypeOccupancy {
  roomTypeId: string;
  roomTypeName: string;
  totalRooms: number;
  days: RoomDay[];
}

export interface StandaloneOccupancy {
  type: 'STANDALONE';
  from: string;
  to: string;
  days: OccupancyDay[];
}

export interface HotelOccupancy {
  type: 'HOTEL';
  from: string;
  to: string;
  roomTypes: RoomTypeOccupancy[];
}

export type OccupancyData = StandaloneOccupancy | HotelOccupancy;

export interface OccupancySummary {
  propertyId: string;
  propertyType: string;
  from: string;
  to: string;
  totalDays: number;
  occupiedDays: number;
  occupancyRate: number;
  totalRoomNights: number;
  bookedRoomNights: number;
}

export interface SystemConfig {
  id: string;
  configKey: string;
  configValue: string;
  description: string | null;
}

export interface AdminPricingRule {
  id: string;
  propertyId: string | null;
  propertyTitle: string | null;
  ruleType: 'WEEKEND' | 'SEASONAL' | 'LONG_STAY' | string;
  label: string | null;
  multiplier: number;
  minStayDays: number | null;
  startDate: string | null;
  endDate: string | null;
  createdAt: string;
}

export interface CancellationPolicy {
  id: string;
  code: string;
  name: string;
  description: string | null;
  refundPercentage: number;
  hoursBeforeCheckin: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CancellationPolicyRequest {
  code: string;
  name: string;
  description?: string | null;
  refundPercentage: number;
  hoursBeforeCheckin: number;
  isActive?: boolean;
}

export interface DisputeEvidence {
  id: string;
  disputeId: string;
  fileUrl: string;
  description: string | null;
  uploadedById: string;
  createdAt: string;
}

export interface ConditionReport {
  id: string;
  vehicleId: string;
  reportedById: string;
  exteriorCondition: string | null;
  interiorCondition: string | null;
  mileageAtReport: number | null;
  fuelLevel: string | null;
  notes: string | null;
  imageUrls: string[];
  createdAt: string;
}
