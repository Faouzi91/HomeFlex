import { Routes } from "@angular/router";
import { AuthGuard } from "./core/guards/auth.guard";
import { LandingComponent } from "./features/landing/landing.component";
import { PublicAccessGuard } from "./core/guards/public-access.guard";

export const APP_ROUTES: Routes = [
  {
    path: "",
    canActivate: [PublicAccessGuard],
    component: LandingComponent,
  },
  {
    path: "auth",
    loadChildren: () =>
      import("./features/auth/auth.module").then((m) => m.AuthModule),
  },
  {
    path: "properties",
    loadChildren: () =>
      import("./features/properties/properties.routes").then(
        (m) => m.PROPERTY_ROUTES
      ),
  },
  {
    path: "bookings",
    loadChildren: () =>
      import("./bookings/bookings.module").then((m) => m.BookingsModule),
    canActivate: [AuthGuard, PublicAccessGuard],
  },
  {
    path: "chat",
    loadChildren: () => import("./chat/chat.module").then((m) => m.ChatModule),
    canActivate: [AuthGuard],
  },
  {
    path: "favorites",
    loadChildren: () =>
      import("./favorites/favorites.module").then((m) => m.FavoritesModule),
    canActivate: [AuthGuard, PublicAccessGuard],
  },
  {
    path: "profile",
    loadChildren: () =>
      import("./profile/profile.module").then((m) => m.ProfileModule),
    canActivate: [AuthGuard],
  },
  {
    path: "admin",
    loadChildren: () =>
      import("./features/admin/admin.routes").then((m) => m.ADMIN_ROUTES),
    canActivate: [AuthGuard],
  },
  {
    path: "**",
    redirectTo: "",
  },
];
