import { Component, DestroyRef, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PropertyApi } from '../../../core/api/services/property.api';
import { VehicleApi } from '../../../core/api/services/vehicle.api';
import { StatsApi } from '../../../core/api/services/stats.api';
import { Property, Vehicle } from '../../../core/models/api.types';
import { compactNumber } from '../../../core/utils/formatters';
import { ListingCardComponent } from '../../../shared/ui/listing-card/listing-card.component';

type PromiseCard = {
  value: string;
  title: string;
  copy: string;
};

type StoryCard = {
  eyebrow: string;
  title: string;
  copy: string;
  link: string;
  linkLabel: string;
};

type JourneyStep = {
  step: string;
  title: string;
  copy: string;
};

@Component({
  selector: 'app-home-page',
  imports: [RouterLink, ListingCardComponent],
  templateUrl: './home.page.html',
  styleUrl: './home.page.scss',
})
export class HomePageComponent {
  private readonly propertyApi = inject(PropertyApi);
  private readonly vehicleApi = inject(VehicleApi);
  private readonly statsApi = inject(StatsApi);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly properties = signal<Property[]>([]);
  protected readonly vehicles = signal<Vehicle[]>([]);
  protected readonly stats = signal<Record<string, number>>({});

  protected readonly promiseCards: PromiseCard[] = [
    {
      value: '01',
      title: 'Editorial discovery',
      copy: 'A polished search experience for premium homes and vehicles with consistent visual language across both categories.',
    },
    {
      value: '02',
      title: 'Operational confidence',
      copy: 'Messaging, bookings, favorites, notifications, and account management stay in one connected workspace.',
    },
    {
      value: '03',
      title: 'Marketplace trust',
      copy: 'Clear pricing, strong hierarchy, and image-first browsing create the kind of credibility users expect immediately.',
    },
  ];

  protected readonly storyCards: StoryCard[] = [
    {
      eyebrow: 'Property collection',
      title: 'Residential search that feels premium from the first scroll.',
      copy: 'Browse long-term homes, short stays, and furnished listings with a calmer rhythm and clearer prioritization.',
      link: '/properties',
      linkLabel: 'Explore homes',
    },
    {
      eyebrow: 'Vehicle collection',
      title: 'Vehicle discovery deserves the same design quality as homes.',
      copy: 'Bring car photos, availability, and pickup context into the same elevated marketplace rather than a separate experience.',
      link: '/vehicles',
      linkLabel: 'Browse vehicles',
    },
    {
      eyebrow: 'Workspace',
      title: 'A control room built for renters, hosts, and admins.',
      copy: 'Manage bookings, conversations, approvals, and profile actions without the UI collapsing into noise.',
      link: '/workspace',
      linkLabel: 'Open workspace',
    },
  ];

  protected readonly journeySteps: JourneyStep[] = [
    {
      step: 'Step 1',
      title: 'Search with clarity',
      copy: 'Start with high-quality cards, filters that make sense, and a layout that guides attention instead of competing for it.',
    },
    {
      step: 'Step 2',
      title: 'Compare with confidence',
      copy: 'Property and vehicle details surface the information people actually need to decide quickly and comfortably.',
    },
    {
      step: 'Step 3',
      title: 'Manage the relationship',
      copy: 'Favorites, requests, messages, and post-booking management all live inside the same HomeFlex account.',
    },
  ];

  constructor() {
    forkJoin({
      properties: this.propertyApi.search({ size: 4 }),
      vehicles: this.vehicleApi.search({ size: 4 }),
      stats: this.statsApi.get(),
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(({ properties, vehicles, stats }) => {
        this.properties.set(properties.data);
        this.vehicles.set(vehicles.data);
        this.stats.set(stats.data);
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
}
