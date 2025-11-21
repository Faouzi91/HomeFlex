// ====================================
// properties.routes.ts - Standalone routing
// ====================================
import { Routes } from "@angular/router";
import { AuthGuard } from "src/app/core/guards/auth.guard";
import { PublicAccessGuard } from "src/app/core/guards/public-access.guard";

export const PROPERTY_ROUTES: Routes = [
  {
    path: "",
    canActivate: [AuthGuard, PublicAccessGuard],
    loadComponent: () =>
      import("./property-list/property-list.component").then(
        (m) => m.PropertyListComponent
      ),
  },
  {
    path: ":id",
    canActivate: [AuthGuard, PublicAccessGuard],
    loadComponent: () =>
      import("./property-detail/property-detail.component").then(
        (m) => m.PropertyDetailComponent
      ),
  },
];
