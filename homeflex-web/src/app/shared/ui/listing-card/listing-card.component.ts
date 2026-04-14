import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Property, Vehicle } from '../../../core/models/api.types';
import { compactNumber, propertyImage, vehicleImage } from '../../../core/utils/formatters';
import { ConvertCurrencyPipe } from '../../../core/pipes/convert-currency/convert-currency.pipe';
import { SessionStore } from '../../../core/state/session.store';

@Component({
  selector: 'app-listing-card',
  imports: [RouterLink],
  templateUrl: './listing-card.component.html',
  styleUrl: './listing-card.component.scss',
})
export class ListingCardComponent {
  readonly variant = input.required<'property' | 'vehicle'>();
  readonly item = input.required<Property | Vehicle>();

  protected readonly session = inject(SessionStore);
  private readonly convertCurrencyPipe = inject(ConvertCurrencyPipe);

  protected href(): string[] {
    return this.variant() === 'property'
      ? ['/properties', (this.item() as Property).id]
      : ['/vehicles', (this.item() as Vehicle).id];
  }

  protected image(): string {
    return this.variant() === 'property'
      ? propertyImage(this.item() as Property)
      : vehicleImage(this.item() as Vehicle);
  }

  protected badge(): string {
    if (this.variant() === 'property') {
      const property = this.item() as Property;
      return `${property.listingType} / ${property.propertyType}`;
    }

    const vehicle = this.item() as Vehicle;
    return `${vehicle.transmission} / ${vehicle.fuelType}`;
  }

  protected title(): string {
    if (this.variant() === 'property') {
      return (this.item() as Property).title;
    }

    const vehicle = this.item() as Vehicle;
    return `${vehicle.brand} ${vehicle.model}`;
  }

  protected description(): string {
    return this.variant() === 'property'
      ? (this.item() as Property).description
      : ((this.item() as Vehicle).description ??
          'Designed for clean city pickups, longer escapes, and premium day rentals.');
  }

  protected location(): string {
    if (this.variant() === 'property') {
      const property = this.item() as Property;
      return `${property.city}, ${property.country}`;
    }

    const vehicle = this.item() as Vehicle;
    return `${vehicle.pickupCity ?? 'Flexible pickup'}, Cameroon`;
  }

  protected stats(): number {
    return this.variant() === 'property'
      ? (this.item() as Property).viewCount
      : (this.item() as Vehicle).viewCount;
  }

  protected price(): string {
    const pref = this.session.currencyPreference();
    if (this.variant() === 'property') {
      const prop = this.item() as Property;
      return this.convertCurrencyPipe.transform(prop.price, prop.currency, pref) || '';
    }
    const veh = this.item() as Vehicle;
    return this.convertCurrencyPipe.transform(veh.dailyPrice, veh.currency, pref) || '';
  }

  protected priceSuffix(): string {
    return this.variant() === 'property' ? 'monthly' : 'per day';
  }

  protected detail(): string {
    if (this.variant() === 'property') {
      const property = this.item() as Property;
      return `${property.bedrooms ?? 0} bd / ${property.bathrooms ?? 0} ba`;
    }

    const vehicle = this.item() as Vehicle;
    return `${vehicle.year} / ${vehicle.seats ?? 0} seats`;
  }

  protected chips(): string[] {
    if (this.variant() === 'property') {
      const property = this.item() as Property;
      return [
        `${property.areaSqm ?? '--'} sqm`,
        `${property.bedrooms ?? 0} bedrooms`,
        `${property.bathrooms ?? 0} baths`,
      ];
    }

    const vehicle = this.item() as Vehicle;
    return [`${vehicle.year}`, `${vehicle.seats ?? 0} seats`, `${vehicle.mileage ?? 0} km`];
  }

  protected compact(value: number): string {
    return compactNumber(value);
  }
}
