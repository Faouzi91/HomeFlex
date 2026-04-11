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
  isActive: boolean;
  isVerified: boolean;
  languagePreference: string | null;
  agencyId?: string | null;
  agencyRole?: string | null;
  trustScore?: number;
  createdAt: string;
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
  isAvailable: boolean;
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
  stripePaymentIntentId: string | null;
  paymentConfirmedAt: string | null;
  escrowReleasedAt: string | null;
  landlordResponse: string | null;
  proposedStartDate: string | null;
  proposedEndDate: string | null;
  modificationReason: string | null;
  respondedAt: string | null;
  createdAt: string;
}

export interface BookingModificationRequest {
  startDate: string;
  endDate: string;
  reason?: string;
}

export interface VehicleBooking {
  id: string;
  vehicleId: string;
  tenantId: string;
  startDate: string;
  endDate: string;
  totalPrice: number;
  currency: string;
  status: string;
  platformFee: number;
  message: string | null;
  createdAt: string;
}

export type ReviewType = 'PROPERTY' | 'TENANT';

export interface Review {
  id: string;
  type: ReviewType;
  propertyId?: string;
  targetUser?: User;
  reviewer: User;
  rating: number;
  comment: string | null;
  createdAt: string;
}

export interface ReviewCreateRequest {
  propertyId?: string;
  targetUserId?: string;
  rating: number;
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
  bookingId: string;
  tenantId: string;
  landlordId: string;
  content: string;
  status: 'PENDING' | 'SIGNED' | 'EXPIRED' | 'CANCELLED';
  signedAt?: string;
  blockchainTxHash?: string;
  onChainStatus: 'NOT_MINTED' | 'PENDING' | 'SUCCESS' | 'FAILED';
  contractAddress?: string;
  tokenId?: string;
  createdAt: string;
  updatedAt: string;
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
