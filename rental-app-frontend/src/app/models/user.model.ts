// ====================================
// models/user.model.ts
// ====================================
export enum UserRole {
  TENANT = 'TENANT',
  LANDLORD = 'LANDLORD',
  ADMIN = 'ADMIN',
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  profilePictureUrl?: string;
  role: UserRole;
  avatarUrl?: string; // Avatar image URL
  language?: string; // User's preferred language
  isActive: boolean;
  isVerified: boolean;
  languagePreference: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  phoneNumber?: string;
}

/**
 * Matches backend AuthResponse record: only contains user data.
 * Tokens are delivered via httpOnly cookies — never exposed to JS.
 */
export interface AuthResponse {
  user: User;
}
