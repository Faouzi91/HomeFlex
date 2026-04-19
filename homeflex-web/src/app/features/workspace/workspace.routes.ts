import { Routes } from '@angular/router';
import { landlordGuard } from '../../core/guards/landlord.guard';
import { WorkspaceLayoutComponent } from './layout/workspace-layout.component';
import { OverviewTabComponent } from './tabs/overview/overview-tab.component';
import { FavoritesTabComponent } from './tabs/favorites/favorites-tab.component';
import { BookingsTabComponent } from './tabs/bookings/bookings-tab.component';
import { MessagesTabComponent } from './tabs/messages/messages-tab.component';
import { NotificationsTabComponent } from './tabs/notifications/notifications-tab.component';
import { ProfileTabComponent } from './tabs/profile/profile-tab.component';
import { HostingTabComponent } from './tabs/hosting/hosting-tab.component';
import { PropertyFormComponent } from './tabs/hosting/property-form/property-form.component';
import { VehicleFormComponent } from './tabs/hosting/vehicle-form/vehicle-form.component';
import { MaintenanceTabComponent } from './tabs/maintenance/maintenance-tab.component';
import { FinanceTabComponent } from './tabs/finance/finance-tab.component';
import { DisputesTabComponent } from './tabs/disputes/disputes-tab.component';
import { InsuranceTabComponent } from './tabs/insurance/insurance-tab.component';

export const workspaceRoutes: Routes = [
  {
    path: '',
    component: WorkspaceLayoutComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: OverviewTabComponent, title: 'Overview | Workspace' },
      { path: 'favorites', component: FavoritesTabComponent, title: 'Favorites | Workspace' },
      { path: 'bookings', component: BookingsTabComponent, title: 'Bookings | Workspace' },
      { path: 'messages', component: MessagesTabComponent, title: 'Messages | Workspace' },
      {
        path: 'notifications',
        component: NotificationsTabComponent,
        title: 'Notifications | Workspace',
      },
      { path: 'profile', component: ProfileTabComponent, title: 'Settings | Workspace' },
      {
        path: 'hosting',
        canActivate: [landlordGuard],
        children: [
          { path: '', component: HostingTabComponent, title: 'Hosting | Workspace' },
          {
            path: 'new-property',
            component: PropertyFormComponent,
            title: 'New Property | Workspace',
          },
          {
            path: 'edit-property/:id',
            component: PropertyFormComponent,
            title: 'Edit Property | Workspace',
          },
          {
            path: 'new-vehicle',
            component: VehicleFormComponent,
            title: 'New Vehicle | Workspace',
          },
          {
            path: 'edit-vehicle/:id',
            component: VehicleFormComponent,
            title: 'Edit Vehicle | Workspace',
          },
        ],
      },
      {
        path: 'maintenance',
        component: MaintenanceTabComponent,
        title: 'Work Orders | Workspace',
      },
      { path: 'finance', component: FinanceTabComponent, title: 'Receipts | Workspace' },
      { path: 'disputes', component: DisputesTabComponent, title: 'Disputes | Workspace' },
      { path: 'insurance', component: InsuranceTabComponent, title: 'Insurance | Workspace' },
    ],
  },
];
