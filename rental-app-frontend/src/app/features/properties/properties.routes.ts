// ====================================
// properties.routes.ts - Standalone routing
// ====================================
import { Routes } from "@angular/router";

export const PROPERTY_ROUTES: Routes = [
  {
    path: "",
    loadComponent: () =>
      import("./property-list/property-list.component").then(
        (m) => m.PropertyListComponent
      ),
  },
  {
    path: ":id",
    loadComponent: () =>
      import("./property-detail/property-detail.component").then(
        (m) => m.PropertyDetailComponent
      ),
  },
];
