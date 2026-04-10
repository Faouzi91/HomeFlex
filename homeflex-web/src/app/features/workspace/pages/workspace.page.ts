import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, of, switchMap, from } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { loadStripe } from '@stripe/stripe-js';
import { ApiClient } from '../../../core/api/api.client';
import {
  Analytics,
  Booking,
  ChatRoom,
  Message,
  NotificationItem,
  Property,
  ReportItem,
  VehicleBooking,
} from '../../../core/models/api.types';
import {
  formatCurrency,
  formatDate,
  formatDateTime,
  initials,
} from '../../../core/utils/formatters';
import { SessionStore } from '../../../core/state/session.store';

type WorkspaceTab =
  | 'overview'
  | 'favorites'
  | 'bookings'
  | 'messages'
  | 'notifications'
  | 'profile'
  | 'hosting'
  | 'admin';
type WorkspaceTabItem = {
  id: WorkspaceTab;
  label: string;
  description: string;
};

@Component({
  selector: 'app-workspace-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './workspace.page.html',
  styleUrl: './workspace.page.scss',
})
export class WorkspacePageComponent {
  private readonly api = inject(ApiClient);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected readonly session = inject(SessionStore);

  protected readonly activeTab = signal<WorkspaceTab>('overview');
  protected readonly favorites = signal<Property[]>([]);
  protected readonly propertyBookings = signal<Booking[]>([]);
  protected readonly vehicleBookings = signal<VehicleBooking[]>([]);
  protected readonly myLeases = signal<any[]>([]);
  protected readonly notifications = signal<NotificationItem[]>([]);
  protected readonly chatRooms = signal<ChatRoom[]>([]);
  protected readonly messages = signal<Message[]>([]);
  protected readonly myProperties = signal<Property[]>([]);
  protected readonly hostBookings = signal<Booking[]>([]);
  protected readonly analytics = signal<Analytics | null>(null);
  protected readonly pendingProperties = signal<Property[]>([]);
  protected readonly reports = signal<ReportItem[]>([]);
  protected readonly kycStatus = signal<any>(null);
  protected readonly payoutSummary = signal<any>(null);
  protected readonly selectedHostPropertyAvailability = signal<any[]>([]);
  protected readonly selectedRoomId = signal('');
  protected readonly selectedHostPropertyId = signal('');
  protected readonly profileMessage = signal('');
  protected readonly passwordMessage = signal('');
  protected readonly hostMessage = signal('');
  protected readonly propertyImages = signal<File[]>([]);

  protected readonly unreadNotifications = computed(
    () => this.notifications().filter((item) => !item.isRead).length,
  );

  protected readonly rangeForm = this.fb.group({
    start: ['', Validators.required],
    end: ['', Validators.required],
  });

  protected readonly profileForm = this.fb.group({
    firstName: [''],
    lastName: [''],
    phoneNumber: [''],
    languagePreference: ['en'],
  });

  protected readonly passwordForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  protected readonly propertyForm = this.fb.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    propertyType: ['APARTMENT', Validators.required],
    listingType: ['RENT', Validators.required],
    price: [0, Validators.required],
    currency: ['XAF'],
    address: ['', Validators.required],
    city: ['', Validators.required],
    stateProvince: ['Littoral'],
    country: ['Cameroon', Validators.required],
    bedrooms: [1],
    bathrooms: [1],
    areaSqm: [60],
    availableFrom: [''],
    amenityIds: [[] as string[]],
  });

  protected readonly vehicleForm = this.fb.group({
    brand: ['', Validators.required],
    model: ['', Validators.required],
    year: [2024, Validators.required],
    transmission: ['AUTOMATIC', Validators.required],
    fuelType: ['GASOLINE', Validators.required],
    dailyPrice: [0, Validators.required],
    currency: ['XAF'],
    description: [''],
    mileage: [0],
    seats: [5],
    color: ['Black'],
    licensePlate: [''],
    pickupCity: ['Douala'],
    pickupAddress: [''],
  });

  protected readonly messageForm = this.fb.group({
    message: ['', Validators.required],
  });

  constructor() {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const chatId = params.get('chat');
      if (chatId) {
        this.activeTab.set('messages');
        this.openRoom(chatId);
      }
    });

    this.seedProfileForm();
    this.loadWorkspace();
  }

  protected tabs(): WorkspaceTabItem[] {
    const tabs: WorkspaceTabItem[] = [
      { id: 'overview', label: 'Overview', description: 'High-level activity and health' },
      { id: 'favorites', label: 'Favorites', description: 'Saved property shortlist' },
      { id: 'bookings', label: 'Bookings', description: 'Property and vehicle reservations' },
      { id: 'messages', label: 'Messages', description: 'Conversation threads and replies' },
      { id: 'notifications', label: 'Notifications', description: 'Alerts and system updates' },
      { id: 'profile', label: 'Profile', description: 'Account details and security' },
    ];

    if (this.session.isLandlord() || this.session.isAdmin()) {
      tabs.push({ id: 'hosting', label: 'Hosting', description: 'Listings and booking approvals' });
    }

    if (this.session.isAdmin()) {
      tabs.push({ id: 'admin', label: 'Admin', description: 'Approvals, analytics, and reports' });
    }

    return tabs;
  }

  protected userInitials(): string {
    const user = this.session.user();
    return initials(user?.firstName, user?.lastName);
  }

  protected selectedRoomTitle(): string {
    return (
      this.chatRooms().find((room) => room.id === this.selectedRoomId())?.propertyTitle ??
      'Conversation'
    );
  }

  protected loadWorkspace(): void {
    if (!this.session.isAuthenticated()) {
      return;
    }

    const user = this.session.user();
    if (!user) {
      return;
    }

    forkJoin({
      favorites: this.api.getFavorites(),
      propertyBookings:
        user.role === 'TENANT' ? this.api.getMyPropertyBookings() : of({ data: [] }),
      vehicleBookings: this.api.getMyVehicleBookings(),
      notifications: this.api.getNotifications(),
      chatRooms: this.api.getChatRooms(),
      myProperties:
        this.session.isLandlord() || this.session.isAdmin()
          ? this.api.getMyProperties()
          : of({ data: [] }),
      analytics: this.session.isAdmin() ? this.api.getAdminAnalytics() : of(null),
      pendingProperties: this.session.isAdmin()
        ? this.api.getPendingProperties()
        : of({ data: [], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
      reports: this.session.isAdmin()
        ? this.api.getReports()
        : of({ data: [], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
      myLeases: this.api.getMyLeases(),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        this.favorites.set(response.favorites.data);
        this.propertyBookings.set(response.propertyBookings.data);
        this.vehicleBookings.set(response.vehicleBookings.data);
        this.myLeases.set(response.myLeases.data);
        this.notifications.set(response.notifications.data);
        this.chatRooms.set(response.chatRooms.data);
        this.myProperties.set(response.myProperties.data);
        this.analytics.set(response.analytics);
        this.pendingProperties.set(response.pendingProperties.data);
        this.reports.set(response.reports.data);
      });

    if (this.session.isLandlord() || this.session.isAdmin()) {
      this.loadKycStatus();
      this.loadPayoutSummary();
    }
  }

  protected loadKycStatus(): void {
    this.api
      .getKycStatus()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.kycStatus.set(response.data));
  }

  protected startKyc(): void {
    this.api
      .createKycSession()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(async (response) => {
        this.hostMessage.set('Redirecting to Stripe Identity for verification...');
        const stripe = await loadStripe(response.publishableKey);
        if (stripe) {
          const { error } = await stripe.verifyIdentity(response.clientSecret);
          if (error) {
            this.hostMessage.set(`Verification failed: ${error.message}`);
          } else {
            this.hostMessage.set(
              'Identity verification submitted successfully. Waiting for results...',
            );
            this.loadKycStatus();
          }
        } else {
          this.hostMessage.set('Failed to initialize Stripe.');
        }
      });
  }

  protected loadPayoutSummary(): void {
    this.api
      .getPayoutSummary()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.payoutSummary.set(response));
  }

  protected onboardConnect(): void {
    this.api
      .onboardConnectAccount()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        if (response.url) {
          window.location.href = response.url;
        }
      });
  }

  protected onFileSelected(event: any): void {
    const files = event.target.files;
    if (files) {
      this.propertyImages.set(Array.from(files));
    }
  }

  protected createProperty(): void {
    if (this.propertyForm.invalid) {
      return;
    }

    this.api
      .createProperty(this.propertyForm.getRawValue())
      .pipe(
        switchMap((prop) => {
          if (this.propertyImages().length > 0) {
            return this.api.uploadPropertyImages(prop.id, this.propertyImages());
          }
          return of(null);
        }),
        switchMap(() => this.api.getMyProperties()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => {
        this.myProperties.set(response.data);
        this.hostMessage.set('Property created successfully with images.');
        this.propertyForm.patchValue({
          title: '',
          description: '',
          address: '',
          city: '',
        });
        this.propertyImages.set([]);
      });
  }

  protected loadAvailability(propertyId: string): void {
    this.selectedHostPropertyId.set(propertyId);
    const start = new Date().toISOString().split('T')[0];
    const end = new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

    this.api
      .getPropertyAvailability(propertyId, start, end)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        this.selectedHostPropertyAvailability.set(response.data);
      });
  }

  protected blockRange(): void {
    if (this.rangeForm.invalid || !this.selectedHostPropertyId()) return;
    const { start, end } = this.rangeForm.getRawValue();
    this.api
      .blockPropertyRange(this.selectedHostPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.loadAvailability(this.selectedHostPropertyId());
        this.hostMessage.set('Dates blocked successfully.');
      });
  }

  protected unblockRange(): void {
    if (this.rangeForm.invalid || !this.selectedHostPropertyId()) return;
    const { start, end } = this.rangeForm.getRawValue();
    this.api
      .unblockPropertyRange(this.selectedHostPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.loadAvailability(this.selectedHostPropertyId());
        this.hostMessage.set('Dates unblocked successfully.');
      });
  }

  protected generateLease(bookingId: string): void {
    this.api
      .generateLease(bookingId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.hostMessage.set('Lease generated successfully.');
        this.loadLeases();
      });
  }

  protected signLease(leaseId: string): void {
    this.api
      .signLease(leaseId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.loadLeases();
      });
  }

  protected loadLeases(): void {
    this.api
      .getMyLeases()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.myLeases.set(response.data));
  }

  protected saveProfile(): void {
    const value = this.profileForm.getRawValue();
    this.session
      .updateProfile({
        firstName: value.firstName ?? undefined,
        lastName: value.lastName ?? undefined,
        phoneNumber: value.phoneNumber ?? null,
        languagePreference: value.languagePreference ?? undefined,
      })
      .subscribe(() => {
        this.profileMessage.set('Profile updated successfully.');
        this.seedProfileForm();
      });
  }

  protected savePassword(): void {
    if (this.passwordForm.invalid) {
      return;
    }

    this.session
      .changePassword(
        this.passwordForm.getRawValue() as { currentPassword: string; newPassword: string },
      )
      .subscribe((message) => {
        this.passwordMessage.set(message);
        this.passwordForm.reset();
      });
  }

  protected createVehicle(): void {
    if (this.vehicleForm.invalid) {
      return;
    }

    this.api
      .createVehicle(this.vehicleForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.hostMessage.set('Vehicle created successfully.');
      });
  }

  protected loadHostBookings(propertyId: string): void {
    this.selectedHostPropertyId.set(propertyId);
    this.api
      .getPropertyBookings(propertyId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        this.hostBookings.set(response.data);
      });
  }

  protected approveHostBooking(bookingId: string): void {
    this.api
      .approvePropertyBooking(bookingId, 'Approved from HomeFlex web workspace')
      .pipe(
        switchMap(() => this.refreshHostBookings()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

  protected rejectHostBooking(bookingId: string): void {
    this.api
      .rejectPropertyBooking(bookingId, 'Declined from HomeFlex web workspace')
      .pipe(
        switchMap(() => this.refreshHostBookings()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();
  }

  protected openRoom(roomId: string): void {
    this.selectedRoomId.set(roomId);
    this.api
      .getChatMessages(roomId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.messages.set(response.data));
  }

  protected sendMessage(): void {
    if (!this.selectedRoomId() || this.messageForm.invalid) {
      return;
    }

    this.api
      .sendMessage(this.selectedRoomId(), this.messageForm.getRawValue().message ?? '')
      .pipe(
        switchMap(() => this.api.getChatMessages(this.selectedRoomId())),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => {
        this.messages.set(response.data);
        this.messageForm.patchValue({ message: '' });
      });
  }

  protected markNotificationRead(id: string): void {
    this.api
      .markNotificationRead(id)
      .pipe(
        switchMap(() => this.api.getNotifications()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => this.notifications.set(response.data));
  }

  protected markAllNotificationsRead(): void {
    this.api
      .markAllNotificationsRead()
      .pipe(
        switchMap(() => this.api.getNotifications()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => this.notifications.set(response.data));
  }

  protected approvePendingProperty(id: string): void {
    this.api
      .approveProperty(id)
      .pipe(
        switchMap(() => this.api.getPendingProperties()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => this.pendingProperties.set(response.data));
  }

  protected rejectPendingProperty(id: string): void {
    this.api
      .rejectProperty(id, 'Rejected from HomeFlex web workspace')
      .pipe(
        switchMap(() => this.api.getPendingProperties()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => this.pendingProperties.set(response.data));
  }

  protected logout(): void {
    this.session.logout().subscribe(() => {
      this.router.navigateByUrl('/');
    });
  }

  protected price(value: number | null, currency: string): string {
    return formatCurrency(value, currency);
  }

  protected date(value: string | null): string {
    return formatDate(value);
  }

  protected dateTime(value: string | null): string {
    return formatDateTime(value);
  }

  private seedProfileForm(): void {
    const user = this.session.user();
    if (!user) {
      return;
    }

    this.profileForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      phoneNumber: user.phoneNumber ?? '',
      languagePreference: user.languagePreference ?? 'en',
    });
  }

  private refreshHostBookings() {
    const propertyId = this.selectedHostPropertyId();
    return propertyId
      ? this.api.getPropertyBookings(propertyId).pipe(
          switchMap((response) => {
            this.hostBookings.set(response.data);
            return of(response);
          }),
        )
      : of({ data: [] });
  }
}
