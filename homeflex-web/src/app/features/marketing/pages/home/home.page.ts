import { Component, DestroyRef, inject, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PropertyApi } from '../../../../core/api/services/property.api';
import { VehicleApi } from '../../../../core/api/services/vehicle.api';
import { StatsApi } from '../../../../core/api/services/stats.api';
import { Property, Vehicle } from '../../../../core/models/api.types';
import { compactNumber } from '../../../../core/utils/formatters';
import { ListingCardComponent } from '../../../../shared/ui/listing-card/listing-card.component';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink, ListingCardComponent, ReactiveFormsModule],
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss',
})
export class HomePageComponent {
  private readonly propertyApi = inject(PropertyApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly statsApi = inject(StatsApi);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly searchTab = signal<'properties' | 'vehicles'>('properties');

  protected readonly propertyForm = this.fb.group({
    destination: [''],
    checkIn: [''],
    checkOut: [''],
    guests: [1],
    propertyType: [''],
  });

  protected readonly vehicleForm = this.fb.group({
    pickupCity: [''],
    pickupDate: [''],
    returnDate: [''],
    transmission: [''],
  });

  protected readonly citySuggestions = signal<string[]>([]);
  protected readonly showCitySuggestions = signal(false);
  protected readonly properties = signal<Property[]>([]);
  protected readonly vehicles = signal<Vehicle[]>([]);
  protected readonly stats = signal<Record<string, number>>({});

  constructor() {
    forkJoin({
      properties: this.propertyApi.search({ size: 4 }),
      vehicles: this.vehicleApi.search({ size: 4 }),
      stats: this.statsApi.get(),
      cities: this.propertyApi.getCities(),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ({ properties, vehicles, stats, cities }) => {
          this.properties.set(properties.data);
          this.vehicles.set(vehicles.data);
          this.stats.set(stats.data);
          this.citySuggestions.set(cities);
        },
        error: () => {},
      });
  }

  protected heroStats(): Array<{ label: string; value: string }> {
    const stats = this.stats();
    return [
      {
        label: 'Live listings',
        value: compactNumber((stats['properties'] ?? 0) + (stats['vehicles'] ?? 0)),
      },
      { label: 'Confirmed bookings', value: compactNumber(stats['bookings'] ?? 0) },
      { label: 'Active members', value: compactNumber(stats['users'] ?? 0) },
    ];
  }

  protected compact(value: number): string {
    return compactNumber(value);
  }

  protected filteredCities(): string[] {
    const query = this.propertyForm.get('destination')?.value?.toLowerCase() || '';
    if (!query) return this.citySuggestions().slice(0, 6);
    return this.citySuggestions()
      .filter((c) => c.toLowerCase().includes(query))
      .slice(0, 6);
  }

  protected filteredVehicleCities(): string[] {
    const query = this.vehicleForm.get('pickupCity')?.value?.toLowerCase() || '';
    if (!query) return this.citySuggestions().slice(0, 6);
    return this.citySuggestions()
      .filter((c) => c.toLowerCase().includes(query))
      .slice(0, 6);
  }

  protected selectCity(city: string): void {
    this.propertyForm.patchValue({ destination: city });
    this.showCitySuggestions.set(false);
  }

  protected selectVehicleCity(city: string): void {
    this.vehicleForm.patchValue({ pickupCity: city });
    this.showCitySuggestions.set(false);
  }

  protected onPropertySearch(): void {
    const f = this.propertyForm.getRawValue();
    const queryParams: Record<string, string> = {};
    if (f.destination) queryParams['query'] = f.destination;
    if (f.checkIn) queryParams['startDate'] = f.checkIn;
    if (f.checkOut) queryParams['endDate'] = f.checkOut;
    if (f.propertyType) queryParams['propertyType'] = f.propertyType;
    if (f.guests && f.guests > 1) queryParams['guests'] = String(f.guests);
    this.router.navigate(['/properties'], { queryParams });
  }

  protected onVehicleSearch(): void {
    const f = this.vehicleForm.getRawValue();
    const queryParams: Record<string, string> = {};
    if (f.pickupCity) queryParams['city'] = f.pickupCity;
    if (f.pickupDate) queryParams['startDate'] = f.pickupDate;
    if (f.returnDate) queryParams['endDate'] = f.returnDate;
    if (f.transmission) queryParams['transmission'] = f.transmission;
    this.router.navigate(['/vehicles'], { queryParams });
  }

  protected incrementGuests(): void {
    const current = this.propertyForm.get('guests')?.value ?? 1;
    if (current < 20) this.propertyForm.patchValue({ guests: current + 1 });
  }

  protected decrementGuests(): void {
    const current = this.propertyForm.get('guests')?.value ?? 1;
    if (current > 1) this.propertyForm.patchValue({ guests: current - 1 });
  }

  protected todayDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  protected tomorrowDate(): string {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  }

  // ── Static marketing data ──────────────────────────────────────

  protected readonly trustBadges = [
    { label: 'Secure payments', icon: 'M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285Z' },
    { label: 'Verified hosts', icon: 'M9 12.75 11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 0 1-1.043 3.296 3.745 3.745 0 0 1-3.296 1.043A3.745 3.745 0 0 1 12 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 0 1-3.296-1.043 3.745 3.745 0 0 1-1.043-3.296A3.745 3.745 0 0 1 3 12c0-1.268.63-2.39 1.593-3.068a3.745 3.745 0 0 1 1.043-3.296 3.746 3.746 0 0 1 3.296-1.043A3.746 3.746 0 0 1 12 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 0 1 3.296 1.043 3.746 3.746 0 0 1 1.043 3.296A3.745 3.745 0 0 1 21 12Z' },
    { label: '24/7 support', icon: 'M20.25 8.511c.884.284 1.5 1.128 1.5 2.097v4.286c0 1.136-.847 2.1-1.98 2.193-.34.027-.68.052-1.02.072v3.091l-3-3c-1.354 0-2.694-.055-4.02-.163a2.115 2.115 0 0 1-.825-.242m9.345-8.334a2.126 2.126 0 0 0-.476-.095 48.64 48.64 0 0 0-8.048 0c-1.131.094-1.976 1.057-1.976 2.192v4.286c0 .837.46 1.58 1.155 1.951m9.345-8.334V6.637c0-1.621-1.152-3.026-2.76-3.235A48.455 48.455 0 0 0 11.25 3c-2.115 0-4.198.137-6.24.402-1.608.209-2.76 1.614-2.76 3.235v6.226c0 1.621 1.152 3.026 2.76 3.235.577.075 1.157.14 1.74.194V21l4.155-4.155' },
    { label: 'No hidden fees', icon: 'M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178ZM15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z' },
  ];

  protected readonly howItWorks = [
    {
      step: 1,
      title: 'Search & discover',
      description: 'Browse thousands of properties and vehicles with smart filters, city suggestions, and real-time availability.',
      icon: 'm21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z',
    },
    {
      step: 2,
      title: 'Book instantly',
      description: 'Reserve your stay or rental in seconds with secure Stripe payments and instant host confirmation.',
      icon: 'M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5',
    },
    {
      step: 3,
      title: 'Move in or drive off',
      description: 'Get your digital lease, pick up the keys, and enjoy your new space or ride. It is that simple.',
      icon: 'M15.75 5.25a3 3 0 0 1 3 3m3 0a6 6 0 0 1-7.029 5.912c-.563-.097-1.159.026-1.563.43L10.5 17.25H8.25v2.25H6v2.25H2.25v-2.818c0-.597.237-1.17.659-1.591l6.499-6.499c.404-.404.527-1 .43-1.563A6 6 0 1 1 21.75 8.25Z',
    },
  ];

  protected readonly destinations = [
    {
      city: 'Douala',
      tagline: 'Economic capital, vibrant nightlife',
      image: 'https://images.unsplash.com/photo-1591122947157-26bad3a117d2?auto=format&fit=crop&w=600&q=80',
    },
    {
      city: 'Yaoundé',
      tagline: 'Political capital, green hills',
      image: 'https://images.unsplash.com/photo-1590846406792-0adc7f938f1d?auto=format&fit=crop&w=600&q=80',
    },
    {
      city: 'Kribi',
      tagline: 'Beach paradise, seafood heaven',
      image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80',
    },
    {
      city: 'Limbe',
      tagline: 'Volcanic sands, botanical gardens',
      image: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=600&q=80',
    },
  ];

  protected readonly whyFeatures = [
    {
      title: 'Stripe-powered escrow',
      description: 'Funds held securely until check-in confirms. Both parties stay protected.',
      icon: 'M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Z',
    },
    {
      title: 'KYC-verified landlords',
      description: 'Every host passes Stripe Identity verification before publishing.',
      icon: 'M15 9h3.75M15 12h3.75M15 15h3.75M4.5 19.5h15a2.25 2.25 0 0 0 2.25-2.25V6.75A2.25 2.25 0 0 0 19.5 4.5h-15a2.25 2.25 0 0 0-2.25 2.25v10.5A2.25 2.25 0 0 0 4.5 19.5Zm6-10.125a1.875 1.875 0 1 1-3.75 0 1.875 1.875 0 0 1 3.75 0Zm1.294 6.336a6.721 6.721 0 0 1-3.17.789 6.721 6.721 0 0 1-3.168-.789 3.376 3.376 0 0 1 6.338 0Z',
    },
    {
      title: 'Real-time messaging',
      description: 'Chat with your host or tenant directly inside the platform.',
      icon: 'M8.625 12a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H8.25m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0H12m4.125 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25Z',
    },
    {
      title: 'Multi-currency pricing',
      description: 'See prices in XAF, USD, EUR, or GBP with live conversion rates.',
      icon: 'M12 6v12m-3-2.818.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
    },
  ];

  protected readonly testimonials = [
    {
      name: 'Marie Ngo Ngono',
      role: 'Tenant in Douala',
      initials: 'MN',
      quote: 'I found a beautiful apartment in Bonanjo within two days. The booking process was seamless and the host was verified. Highly recommend.',
    },
    {
      name: 'Jean-Pierre Fotso',
      role: 'Property host',
      initials: 'JF',
      quote: 'As a landlord, HomeFlex gives me visibility I never had before. The admin panel, messaging, and payment system are all top-notch.',
    },
    {
      name: 'Aisha Bello',
      role: 'Vehicle renter',
      initials: 'AB',
      quote: 'Renting a car for my trip to Kribi was incredibly easy. Fair pricing, clean vehicle, and the whole process was handled through the app.',
    },
  ];
}
