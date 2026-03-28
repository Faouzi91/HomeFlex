import { Injectable } from '@angular/core';
import { Preferences } from '@capacitor/preferences';
import { CapacitorService } from '../capacitor/capacitor.service';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private readonly ACCESS_TOKEN_KEY = 'auth_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'current_user';

  // In-memory cache to keep synchronous API while persisting async on native
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private currentUser: any | null = null;

  constructor(private capacitorService: CapacitorService) {
    // Load persisted values (async) into memory on startup if running on native
    if (this.capacitorService.isNativePlatform()) {
      this.loadFromPreferences();
    } else {
      // Web: read from localStorage to initialize cache
      this.accessToken = localStorage.getItem(this.ACCESS_TOKEN_KEY);
      this.refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
      const json = localStorage.getItem(this.USER_KEY);
      if (json) {
        try {
          this.currentUser = JSON.parse(json);
        } catch (e) {
          console.error('Failed to parse stored user', e);
        }
      }
    }
  }

  private async loadFromPreferences(): Promise<void> {
    try {
      const at = await Preferences.get({ key: this.ACCESS_TOKEN_KEY });
      const rt = await Preferences.get({ key: this.REFRESH_TOKEN_KEY });
      const u = await Preferences.get({ key: this.USER_KEY });
      this.accessToken = at.value ?? null;
      this.refreshToken = rt.value ?? null;
      if (u.value) {
        try {
          this.currentUser = JSON.parse(u.value);
        } catch (e) {
          console.error('Failed to parse stored user from preferences', e);
          this.currentUser = null;
        }
      }
    } catch (error) {
      console.error('Error loading tokens from Preferences', error);
    }
  }

  // Access token
  setAccessToken(token: string | null): void {
    this.accessToken = token ?? null;
    if (this.capacitorService.isNativePlatform()) {
      if (token === null || token === undefined) {
        Preferences.remove({ key: this.ACCESS_TOKEN_KEY });
      } else {
        Preferences.set({ key: this.ACCESS_TOKEN_KEY, value: token });
      }
    } else {
      if (token === null || token === undefined) {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
      } else {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
      }
    }
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  // Refresh token
  setRefreshToken(token: string | null): void {
    this.refreshToken = token ?? null;
    if (this.capacitorService.isNativePlatform()) {
      if (token === null || token === undefined) {
        Preferences.remove({ key: this.REFRESH_TOKEN_KEY });
      } else {
        Preferences.set({ key: this.REFRESH_TOKEN_KEY, value: token });
      }
    } else {
      if (token === null || token === undefined) {
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
      } else {
        localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
      }
    }
  }

  getRefreshToken(): string | null {
    return this.refreshToken;
  }

  // Current user
  setCurrentUser(user: any | null): void {
    this.currentUser = user ?? null;
    const value = user === null || user === undefined ? null : JSON.stringify(user);
    if (this.capacitorService.isNativePlatform()) {
      if (value === null) {
        Preferences.remove({ key: this.USER_KEY });
      } else {
        Preferences.set({ key: this.USER_KEY, value });
      }
    } else {
      if (value === null) {
        localStorage.removeItem(this.USER_KEY);
      } else {
        try {
          localStorage.setItem(this.USER_KEY, value);
        } catch (e) {
          console.error('Failed to serialize user for storage', e);
        }
      }
    }
  }

  getCurrentUser(): any | null {
    return this.currentUser;
  }

  clear(): void {
    this.accessToken = null;
    this.refreshToken = null;
    this.currentUser = null;
    if (this.capacitorService.isNativePlatform()) {
      Preferences.remove({ key: this.ACCESS_TOKEN_KEY });
      Preferences.remove({ key: this.REFRESH_TOKEN_KEY });
      Preferences.remove({ key: this.USER_KEY });
    } else {
      localStorage.removeItem(this.ACCESS_TOKEN_KEY);
      localStorage.removeItem(this.REFRESH_TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
  }
}
