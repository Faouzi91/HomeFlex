import { CurrencyPipe, DecimalPipe, LowerCasePipe, SlicePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, of, switchMap, from } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { loadStripe } from '@stripe/stripe-js';
import { TranslateModule } from '@ngx-translate/core';
import { ApiClient } from '../../../core/api/api.client';
import {
  Agency,
  Analytics,
  Booking,
  ChatRoom,
  Dispute,
  InsurancePolicy,
  MaintenanceRequest,
  MaintenanceStatus,
  Message,
  NotificationItem,
  Property,
  Receipt,
  ReportItem,
  User,
  Vehicle,
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
  | 'maintenance'
  | 'hosting'
  | 'admin';
type WorkspaceTabItem = {
  id: WorkspaceTab;
  label: string;
  description: string;
};

@Component({
  selector: 'app-workspace-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CurrencyPipe,
    DecimalPipe,
    SlicePipe,
    LowerCasePipe,
    TranslateModule,
  ],
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
  protected readonly myMaintenanceRequests = signal<MaintenanceRequest[]>([]);
  protected readonly landlordMaintenanceRequests = signal<MaintenanceRequest[]>([]);
  protected readonly notifications = signal<NotificationItem[]>([]);
  protected readonly chatRooms = signal<ChatRoom[]>([]);
  protected readonly messages = signal<Message[]>([]);
  protected readonly myProperties = signal<Property[]>([]);
  protected readonly myVehicles = signal<Vehicle[]>([]);
  protected readonly hostBookings = signal<Booking[]>([]);
  protected readonly analytics = signal<Analytics | null>(null);
  protected readonly pendingProperties = signal<Property[]>([]);
  protected readonly reports = signal<ReportItem[]>([]);
  protected readonly disputes = signal<Dispute[]>([]);
  protected readonly agencies = signal<Agency[]>([]);
  protected readonly allReceipts = signal<Receipt[]>([]);
  protected readonly allInsurancePolicies = signal<InsurancePolicy[]>([]);
  protected readonly kycStatus = signal<any>(null);
  protected readonly payoutSummary = signal<any>(null);
  protected readonly selectedHostPropertyAvailability = signal<any[]>([]);
  protected readonly selectedHostVehicleConditionReports = signal<any[]>([]);
  protected readonly selectedRoomId = signal('');
  protected readonly selectedHostPropertyId = signal('');
  protected readonly selectedHostVehicleId = signal('');
  protected readonly selectedMaintenanceRequestId = signal('');
  protected readonly maintenanceDetail = signal<MaintenanceRequest | null>(null);
  protected readonly profileMessage = signal('');
  protected readonly passwordMessage = signal('');
  protected readonly hostMessage = signal('');
  protected readonly propertyImages = signal<File[]>([]);
  protected readonly vehicleImages = signal<File[]>([]);
  protected readonly maintenanceImages = signal<File[]>([]);

  protected readonly unreadNotifications = computed(
    () => this.notifications().filter((item) => !item.isRead).length,
  );

  protected readonly tabs = computed<WorkspaceTabItem[]>(() => {
    const items: WorkspaceTabItem[] = [
      { id: 'overview', label: 'Overview', description: 'At a glance summary' },
      { id: 'favorites', label: 'Favorites', description: 'Saved properties' },
      { id: 'bookings', label: 'Bookings', description: 'Your active stays' },
      { id: 'messages', label: 'Messages', description: 'Chat with hosts' },
      { id: 'notifications', label: 'Alerts', description: 'Recent activity' },
      { id: 'profile', label: 'Settings', description: 'Account and security' },
    ];

    if (this.session.isLandlord() || this.session.isAdmin()) {
      items.push({ id: 'hosting', label: 'Hosting', description: 'Manage your listings' });
      items.push({ id: 'maintenance', label: 'Work Orders', description: 'Property repairs' });
    }

    if (this.session.isAdmin()) {
      items.push({ id: 'admin', label: 'Admin Console', description: 'Platform management' });
    }

    return items;
  });

  protected readonly selectedRoomTitle = computed(() => {
    const roomId = this.selectedRoomId();
    const room = this.chatRooms().find((r) => r.id === roomId);
    return room ? room.propertyTitle : 'Select a conversation';
  });

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
    year: [new Date().getFullYear(), Validators.required],
    dailyPrice: [0, Validators.required],
    currency: ['XAF'],
    transmission: ['AUTOMATIC', Validators.required],
    fuelType: ['PETROL', Validators.required],
    seats: [5, Validators.required],
    category: ['CAR', Validators.required],
    description: ['', Validators.required],
  });

  protected readonly maintenanceForm = this.fb.group({
    propertyId: ['', Validators.required],
    title: ['', Validators.required],
    description: ['', Validators.required],
    category: ['OTHER', Validators.required],
    priority: ['MEDIUM', Validators.required],
  });

  protected readonly maintenanceStatusForm = this.fb.group({
    status: ['REPORTED' as MaintenanceStatus, Validators.required],
    resolutionNotes: [''],
  });

  protected readonly messageForm = this.fb.group({
    text: ['', Validators.required],
  });

  constructor() {
    this.route.queryParams.pipe(takeUntilDestroyed()).subscribe((params) => {
      if (params['tab']) {
        this.activeTab.set(params['tab'] as WorkspaceTab);
      }
    });

    this.loadWorkspace();
  }

  protected loadWorkspace(): void {
    const user = this.session.user();
    if (!user) return;

    forkJoin({
      favorites: this.api.getFavorites(),
      propertyBookings: this.api.getMyPropertyBookings(),
      vehicleBookings: this.api.getMyVehicleBookings(),
      notifications: this.api.getNotifications(),
      chatRooms: this.api.getChatRooms(),
      myProperties:
        this.session.isLandlord() || this.session.isAdmin()
          ? this.api.getMyProperties()
          : of({ data: [] }),
      myVehicles:
        this.session.isLandlord() || this.session.isAdmin()
          ? this.api.getMyVehicles()
          : of({ data: [], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
      analytics: this.session.isAdmin() ? this.api.getAdminAnalytics() : of(null),
      pendingProperties: this.session.isAdmin()
        ? this.api.getPendingProperties()
        : of({ data: [], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
      reports: this.session.isAdmin()
        ? this.api.getReports()
        : of({ data: [], page: 0, size: 0, totalElements: 0, totalPages: 0 }),
      disputes: this.session.isAdmin() ? this.api.getAllDisputes() : of([]),
      agencies: this.session.isAdmin() ? this.api.getAllAgencies() : of([]),
      myLeases: this.api.getMyLeases(),
      myReceipts: this.api.getMyReceipts(),
      myInsurance: this.api.getInsurancePlans('TENANT'),
      myMaintenance: this.api.getMyMaintenanceRequests(),
      landlordMaintenance:
        this.session.isLandlord() || this.session.isAdmin()
          ? this.api.getLandlordMaintenanceRequests()
          : of([]),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        this.favorites.set(response.favorites.data);
        this.propertyBookings.set(response.propertyBookings.data);
        this.vehicleBookings.set(response.vehicleBookings.data);
        this.notifications.set(response.notifications.data);
        this.chatRooms.set(response.chatRooms.data);
        this.myProperties.set(response.myProperties.data);
        this.myVehicles.set(response.myVehicles.data);
        this.analytics.set(response.analytics);
        this.pendingProperties.set(response.pendingProperties.data);
        this.reports.set(response.reports.data);
        if (Array.isArray(response.disputes)) {
          this.disputes.set(response.disputes);
        }
        if (Array.isArray(response.agencies)) {
          this.agencies.set(response.agencies);
        }
        this.myLeases.set(response.myLeases.data);
        this.allReceipts.set(response.myReceipts);
        this.myMaintenanceRequests.set(response.myMaintenance);
        this.landlordMaintenanceRequests.set(response.landlordMaintenance);

        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          phoneNumber: user.phoneNumber,
          languagePreference: user.languagePreference || 'en',
        });
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
            this.hostMessage.set('Verification submitted!');
            this.loadKycStatus();
          }
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
    const currentUrl = window.location.href;
    this.api
      .onboardConnectAccount(currentUrl, currentUrl)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => {
        window.location.href = response.url;
      });
  }

  protected saveProfile(): void {
    this.api
      .updateProfile(this.profileForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (user) => {
          this.session.user.set(user);
          this.profileMessage.set('Profile updated successfully!');
          setTimeout(() => this.profileMessage.set(''), 3000);
        },
        error: () => this.profileMessage.set('Failed to update profile.'),
      });
  }

  protected savePassword(): void {
    if (this.passwordForm.invalid) return;
    this.api
      .changePassword(this.passwordForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.passwordMessage.set('Password changed successfully!');
          this.passwordForm.reset();
          setTimeout(() => this.passwordMessage.set(''), 3000);
        },
        error: (err) => this.passwordMessage.set(err.error?.message || 'Failed to change.'),
      });
  }

  protected openRoom(id: string): void {
    this.selectedRoomId.set(id);
    this.api
      .getChatMessages(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((response) => this.messages.set(response.data));
  }

  protected sendMessage(): void {
    if (this.messageForm.invalid || !this.selectedRoomId()) return;
    this.api
      .sendMessage(this.selectedRoomId(), this.messageForm.value.text!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((message) => {
        this.messages.update((prev) => [...prev, message]);
        this.messageForm.reset();
      });
  }

  protected markNotificationRead(id: string): void {
    this.api
      .markNotificationRead(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === id ? { ...item, isRead: true } : item)),
        );
      });
  }

  protected markAllNotificationsRead(): void {
    this.api
      .markAllNotificationsRead()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.notifications.update((items) => items.map((item) => ({ ...item, isRead: true })));
      });
  }

  protected deleteNotification(id: string): void {
    this.api
      .deleteNotification(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.notifications.update((items) => items.filter((item) => item.id !== id));
      });
  }

  protected approvePropertyBooking(id: string): void {
    this.api
      .approvePropertyBooking(id, 'Approved from workspace')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected rejectPropertyBooking(id: string): void {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;
    this.api
      .rejectPropertyBooking(id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected loadAvailability(id: string): void {
    this.selectedHostPropertyId.set(id);
    const start = new Date().toISOString().split('T')[0];
    const end = new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    this.api
      .getPropertyAvailability(id, start, end)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => this.selectedHostPropertyAvailability.set(res.data));
  }

  protected blockRange(): void {
    if (this.rangeForm.invalid || !this.selectedHostPropertyId()) return;
    const { start, end } = this.rangeForm.value;
    this.api
      .blockPropertyRange(this.selectedHostPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadAvailability(this.selectedHostPropertyId()));
  }

  protected unblockRange(): void {
    if (this.rangeForm.invalid || !this.selectedHostPropertyId()) return;
    const { start, end } = this.rangeForm.value;
    this.api
      .unblockPropertyRange(this.selectedHostPropertyId(), start!, end!)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadAvailability(this.selectedHostPropertyId()));
  }

  protected openMaintenanceDetail(id: string): void {
    this.selectedMaintenanceRequestId.set(id);
    this.api
      .getMaintenanceRequest(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => {
        this.maintenanceDetail.set(res);
        this.maintenanceStatusForm.patchValue({
          status: res.status,
          resolutionNotes: res.resolutionNotes,
        });
      });
  }

  protected updateMaintenanceStatus(): void {
    if (this.maintenanceStatusForm.invalid || !this.selectedMaintenanceRequestId()) return;
    this.api
      .updateMaintenanceStatus(
        this.selectedMaintenanceRequestId(),
        this.maintenanceStatusForm.value as any,
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res) => {
        this.maintenanceDetail.set(res);
        this.loadWorkspace();
      });
  }

  protected onPropertyImagesSelected(event: any): void {
    this.propertyImages.set(Array.from(event.target.files));
  }

  protected createProperty(): void {
    if (this.propertyForm.invalid) return;
    this.api
      .createProperty(this.propertyForm.value as any)
      .pipe(
        switchMap((prop) => {
          if (this.propertyImages().length > 0) {
            return this.api
              .uploadPropertyImages(prop.id, this.propertyImages())
              .pipe(switchMap(() => of(prop)));
          }
          return of(prop);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        this.propertyForm.reset();
        this.propertyImages.set([]);
        this.loadWorkspace();
      });
  }

  protected onVehicleImagesSelected(event: any): void {
    this.vehicleImages.set(Array.from(event.target.files));
  }

  protected onMaintenanceImageSelected(event: any): void {
    this.maintenanceImages.set(Array.from(event.target.files));
  }

  protected submitMaintenanceRequest(): void {
    if (this.maintenanceForm.invalid) return;
    this.api
      .createMaintenanceRequest(this.maintenanceForm.value as any)
      .pipe(
        switchMap((req) => {
          if (this.maintenanceImages().length > 0) {
            return this.api
              .uploadMaintenanceImages(req.id, this.maintenanceImages())
              .pipe(switchMap(() => of(req)));
          }
          return of(req);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        this.maintenanceForm.reset();
        this.maintenanceImages.set([]);
        this.loadWorkspace();
      });
  }

  protected createVehicle(): void {
    if (this.vehicleForm.invalid) return;
    this.api
      .createVehicle(this.vehicleForm.value as any)
      .pipe(
        switchMap((v) => {
          if (this.vehicleImages().length > 0) {
            return this.api
              .uploadVehicleImages(v.id, this.vehicleImages())
              .pipe(switchMap(() => of(v)));
          }
          return of(v);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        this.vehicleForm.reset();
        this.vehicleImages.set([]);
        this.loadWorkspace();
      });
  }

  protected editVehicle(vehicle: Vehicle): void {
    this.selectedHostVehicleId.set(vehicle.id);
    this.vehicleForm.patchValue({
      brand: vehicle.brand,
      model: vehicle.model,
      year: vehicle.year,
      dailyPrice: vehicle.dailyPrice,
      currency: vehicle.currency,
      transmission: vehicle.transmission,
      fuelType: vehicle.fuelType,
      seats: vehicle.seats,
      category: vehicle.category,
      description: vehicle.description,
    });
  }

  protected updateVehicle(): void {
    if (this.vehicleForm.invalid || !this.selectedHostVehicleId()) return;
    this.api
      .updateVehicle(this.selectedHostVehicleId(), this.vehicleForm.value as any)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.selectedHostVehicleId.set('');
        this.vehicleForm.reset();
        this.loadWorkspace();
      });
  }

  protected deleteVehicle(id: string): void {
    if (!confirm('Are you sure you want to delete this vehicle?')) return;
    this.api
      .deleteVehicle(id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
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
      .rejectProperty(id, 'Rejected')
      .pipe(
        switchMap(() => this.api.getPendingProperties()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((response) => this.pendingProperties.set(response.data));
  }

  protected resolveDispute(id: string): void {
    const notes = prompt('Enter resolution notes:');
    if (!notes) return;
    this.api
      .resolveDispute(id, notes)
      .pipe(
        switchMap(() => this.api.getAllDisputes()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((res) => this.disputes.set(res));
  }

  protected openDispute(bookingId: string): void {
    const reason = prompt('Enter dispute reason (e.g., DAMAGE, DEPOSIT_RETURN):');
    const description = prompt('Enter dispute description:');
    if (!reason || !description) return;

    this.api
      .openDispute(bookingId, reason, description)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        alert('Dispute opened successfully. An admin will review it.');
        this.loadWorkspace();
      });
  }

  protected generateLease(bookingId: string): void {
    this.api
      .generateLease(bookingId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected signLease(leaseId: string): void {
    this.api
      .signLease(leaseId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected approveHostBooking(id: string): void {
    this.api
      .approvePropertyBooking(id, 'Approved')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected rejectHostBooking(id: string): void {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;
    this.api
      .rejectPropertyBooking(id, reason)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadWorkspace());
  }

  protected logout(): void {
    this.session.logout().subscribe(() => {
      this.router.navigateByUrl('/');
    });
  }

  protected exportMyData(): void {
    this.api
      .exportData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data) => {
        const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `homeflex-data-export-${new Date().toISOString()}.json`;
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  protected eraseMyData(): void {
    const confirmation = prompt(
      'WARNING: This will permanently delete your account and all associated data. Type "DELETE" to confirm:',
    );
    if (confirmation !== 'DELETE') return;

    this.api
      .eraseData()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        alert('Your data has been erased. You will now be logged out.');
        this.logout();
      });
  }

  protected date(val: string | null): string {
    return val ? formatDate(val) : '-';
  }

  protected dateTime(val: string): string {
    return formatDateTime(val);
  }

  protected currency(val: number, cur = 'XAF'): string {
    return formatCurrency(val, cur);
  }

  protected price(val: number | null | undefined, cur = 'XAF'): string {
    return val !== null && val !== undefined ? formatCurrency(val, cur) : '-';
  }

  protected userInitials(user: User | null): string {
    if (!user) return '?';
    return initials(user.firstName, user.lastName);
  }

  protected loadVehicleConditionReports(id: string): void {
    this.selectedHostVehicleId.set(id);
    this.api.getVehicleConditionReports(id).subscribe((res) => {
      this.selectedHostVehicleConditionReports.set(res.data);
    });
  }

  protected loadHostBookings(propertyId: string): void {
    this.selectedHostPropertyId.set(propertyId);
    this.api
      .getPropertyBookings(propertyId)
      .pipe(
        switchMap((response) => {
          this.hostBookings.set(response.data);
          return of(response);
        }),
      )
      .subscribe();
  }
}
