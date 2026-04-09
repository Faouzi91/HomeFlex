import { Routes } from '@angular/router';
import { HomePageComponent } from './features/marketing/pages/home.page';
import { LoginPageComponent } from './features/auth/pages/login/login.page';
import { PasswordResetPageComponent } from './features/auth/pages/password-reset/password-reset.page';
import { RegisterPageComponent } from './features/auth/pages/register/register.page';
import { PropertiesPageComponent } from './features/properties/pages/properties.page';
import { PropertyDetailPageComponent } from './features/properties/pages/property-detail.page';
import { VehicleDetailPageComponent } from './features/vehicles/pages/vehicle-detail.page';
import { VehiclesPageComponent } from './features/vehicles/pages/vehicles.page';
import { WorkspacePageComponent } from './features/workspace/pages/workspace.page';

export const routes: Routes = [
  { path: '', component: HomePageComponent, title: 'HomeFlex | Premium rentals' },
  { path: 'properties', component: PropertiesPageComponent, title: 'Properties | HomeFlex' },
  {
    path: 'properties/:id',
    component: PropertyDetailPageComponent,
    title: 'Property detail | HomeFlex',
  },
  { path: 'vehicles', component: VehiclesPageComponent, title: 'Vehicles | HomeFlex' },
  {
    path: 'vehicles/:id',
    component: VehicleDetailPageComponent,
    title: 'Vehicle detail | HomeFlex',
  },
  { path: 'login', component: LoginPageComponent, title: 'Login | HomeFlex' },
  { path: 'register', component: RegisterPageComponent, title: 'Register | HomeFlex' },
  {
    path: 'password-reset',
    component: PasswordResetPageComponent,
    title: 'Reset password | HomeFlex',
  },
  { path: 'auth', redirectTo: 'login', pathMatch: 'full' },
  { path: 'workspace', component: WorkspacePageComponent, title: 'Workspace | HomeFlex' },
  { path: '**', redirectTo: '' },
];
