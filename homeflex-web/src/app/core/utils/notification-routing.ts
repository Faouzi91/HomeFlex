import { NavigationExtras } from '@angular/router';
import { NotificationItem } from '../models/api.types';

export interface NotificationNavigationTarget {
  path: (string | number)[];
  extras?: NavigationExtras;
}

export function getNotificationNavigationTarget(
  notification: NotificationItem,
): NotificationNavigationTarget | null {
  const type = (notification.type ?? '').toUpperCase();
  const relType = (notification.relatedEntityType ?? '').toUpperCase();
  const relId = notification.relatedEntityId;

  if (type === 'MESSAGE' || relType === 'CHAT_ROOM' || relType === 'MESSAGE') {
    return {
      path: ['/workspace/messages'],
      extras: { queryParams: relId ? { room: relId } : {} },
    };
  }

  if (type === 'BOOKING' || relType === 'BOOKING' || relType === 'VEHICLE_BOOKING') {
    return {
      path: ['/workspace/bookings'],
      extras: { queryParams: relId ? { booking: relId } : {} },
    };
  }

  if (type === 'PAYMENT' || relType === 'PAYMENT') {
    return { path: ['/workspace/bookings'] };
  }

  if (type === 'DISPUTE' || relType === 'DISPUTE') {
    return { path: ['/workspace/disputes'] };
  }

  if (relType === 'MAINTENANCE_REQUEST' || relType === 'MAINTENANCE') {
    return { path: ['/workspace/maintenance'] };
  }

  if (relType === 'LEASE' || relType === 'PROPERTY_LEASE') {
    return { path: ['/workspace/bookings'] };
  }

  if (relType === 'PROPERTY' && relId) {
    return { path: ['/properties', relId] };
  }

  if (relType === 'VEHICLE' && relId) {
    return { path: ['/vehicles', relId] };
  }

  return null;
}
