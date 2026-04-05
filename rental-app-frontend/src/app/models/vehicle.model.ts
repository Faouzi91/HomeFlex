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
  description: string;
  mileage: number;
  seats: number;
  color: string;
  licensePlate: string;
  pickupCity: string;
  pickupAddress: string;
  viewCount: number;
  images: VehicleImage[];
  createdAt: string;
  updatedAt: string;
}

export interface VehicleImage {
  id: string;
  imageUrl: string;
  displayOrder: number;
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
  message: string;
  createdAt: string;
}

export interface VehicleBookingCreateRequest {
  vehicleId: string;
  startDate: string;
  endDate: string;
  message?: string;
}

export interface VehicleSearchParams {
  brand?: string;
  model?: string;
  city?: string;
  transmission?: string;
  fuelType?: string;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}
