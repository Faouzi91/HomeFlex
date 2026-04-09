import { Property, Vehicle } from '../models/api.types';

const money = new Intl.NumberFormat('fr-CM', {
  maximumFractionDigits: 0,
});

const compact = new Intl.NumberFormat('en', {
  notation: 'compact',
  maximumFractionDigits: 1,
});

export function formatCurrency(value: number | null | undefined, currency = 'XAF'): string {
  if (value == null) {
    return '--';
  }

  return `${money.format(value)} ${currency}`;
}

export function formatDate(value: string | null | undefined): string {
  if (!value) {
    return '--';
  }

  return new Intl.DateTimeFormat('en', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(value));
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) {
    return '--';
  }

  return new Intl.DateTimeFormat('en', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  }).format(new Date(value));
}

export function compactNumber(value: number | null | undefined): string {
  if (value == null) {
    return '0';
  }

  return compact.format(value);
}

export function initials(firstName?: string | null, lastName?: string | null): string {
  return `${firstName?.[0] ?? ''}${lastName?.[0] ?? ''}`.toUpperCase() || 'HF';
}

export function propertyImage(property: Property): string {
  return (
    property.images.find((image) => image.isPrimary)?.imageUrl ??
    property.images[0]?.imageUrl ??
    'https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=1200&q=80'
  );
}

export function vehicleImage(vehicle: Vehicle): string {
  return (
    vehicle.images.find((image) => image.isPrimary)?.imageUrl ??
    vehicle.images[0]?.imageUrl ??
    'https://images.unsplash.com/photo-1549924231-f129b911e442?auto=format&fit=crop&w=1200&q=80'
  );
}
