// ====================================
// models/property.model.ts

import { User } from "./user.model";

// ====================================
export enum PropertyType {
  APARTMENT = "APARTMENT",
  HOUSE = "HOUSE",
  STUDIO = "STUDIO",
  VILLA = "VILLA",
  ROOM = "ROOM",
  OFFICE = "OFFICE",
  LAND = "LAND",
}

export enum ListingType {
  RENT = "RENT",
  SALE = "SALE",
  SHORT_TERM = "SHORT_TERM",
}

export enum PropertyStatus {
  PENDING = "PENDING",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
  INACTIVE = "INACTIVE",
}

export interface Property {
  id: string;
  landlord: User;
  title: string;
  description: string;
  propertyType: PropertyType;
  listingType: ListingType;
  price: number;
  currency: string;

  // Location
  address: string;
  city: string;
  stateProvince?: string;
  country: string;
  postalCode?: string;
  latitude?: number;
  longitude?: number;

  // Details
  bedrooms?: number;
  bathrooms?: number;
  areaSqm?: number;
  floorNumber?: number;
  totalFloors?: number;

  // Availability
  isAvailable: boolean;
  availableFrom?: Date;
  status: PropertyStatus;
  viewCount: number;
  favoriteCount: number;

  images: PropertyImage[];
  videos: PropertyVideo[];
  amenities: Amenity[];

  createdAt: Date;
  updatedAt: Date;
}

export interface PropertyImage {
  id: string;
  imageUrl: string;
  thumbnailUrl?: string;
  displayOrder: number;
  isPrimary: boolean;
}

export interface PropertyVideo {
  id: string;
  videoUrl: string;
  thumbnailUrl?: string;
  durationSeconds?: number;
}

export interface Amenity {
  id: string;
  name: string;
  nameFr?: string;
  icon?: string;
  category: string;
}

export interface PropertySearchParams {
  [x: string]: any;
  city?: string;
  minPrice?: number;
  maxPrice?: number;
  propertyType?: PropertyType;
  bedrooms?: number;
  bathrooms?: number;
  amenities?: string[];
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: "asc" | "desc";
}
