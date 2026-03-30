import { computed, inject } from '@angular/core';
import { signalStore, withState, withComputed, withMethods } from '@ngrx/signals';
import { patchState } from '@ngrx/signals';
import { User, UserRole } from '../../models/user.model';
import { AuthService } from '../services/auth/auth.service';

interface AuthStoreState {
  currentUser: User | null;
}

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState<AuthStoreState>({
    currentUser: null,
  }),
  withComputed((store) => ({
    isAuthenticated: computed(() => store.currentUser() !== null),
    userRole: computed(() => store.currentUser()?.role ?? null),
    isAdmin: computed(() => store.currentUser()?.role === UserRole.ADMIN),
    isLandlord: computed(() => store.currentUser()?.role === UserRole.LANDLORD),
    fullName: computed(() => {
      const u = store.currentUser();
      return u ? `${u.firstName} ${u.lastName}` : '';
    }),
  })),
  withMethods((store, authService = inject(AuthService)) => ({
    /** Rehydrate from localStorage / AuthService on startup. */
    hydrate(): void {
      const user = authService.getCurrentUser();
      if (user && authService.isAuthenticated()) {
        patchState(store, { currentUser: user });
      }
    },

    setUser(user: User): void {
      patchState(store, { currentUser: user });
    },

    clear(): void {
      patchState(store, { currentUser: null });
    },
  }))
);
