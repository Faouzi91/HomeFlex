import { getNotificationNavigationTarget } from './notification-routing';
import { NotificationItem } from '../models/api.types';

function notification(overrides: Partial<NotificationItem>): NotificationItem {
  return {
    id: 'notif-1',
    type: 'SYSTEM',
    title: 'Test',
    message: 'Message',
    isRead: false,
    createdAt: new Date().toISOString(),
    ...overrides,
  } as NotificationItem;
}

describe('getNotificationNavigationTarget', () => {
  it('routes booking notifications to the booking panel', () => {
    expect(
      getNotificationNavigationTarget(
        notification({ type: 'BOOKING', relatedEntityId: 'booking-1' }),
      ),
    ).toEqual({
      path: ['/workspace/bookings'],
      extras: { queryParams: { booking: 'booking-1' } },
    });
  });

  it('routes disputes to the disputes tab', () => {
    expect(
      getNotificationNavigationTarget(
        notification({ type: 'DISPUTE', relatedEntityType: 'DISPUTE' }),
      ),
    ).toEqual({
      path: ['/workspace/disputes'],
    });
  });

  it('routes maintenance updates to the maintenance tab', () => {
    expect(
      getNotificationNavigationTarget(
        notification({ relatedEntityType: 'MAINTENANCE_REQUEST', relatedEntityId: 'req-1' }),
      ),
    ).toEqual({
      path: ['/workspace/maintenance'],
    });
  });
});
