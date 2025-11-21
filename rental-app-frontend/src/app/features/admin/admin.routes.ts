import { Routes } from "@angular/router";
import { AdminLayoutComponent } from "./layout/admin-layout/admin-layout.component";
import { AdminGuard } from "src/app/core/guards/admin.guard";

export const ADMIN_ROUTES: Routes = [
  {
    path: "",
    component: AdminLayoutComponent,
    canActivate: [AdminGuard],
    children: [
      {
        path: "",
        loadComponent: () =>
          import("./dashboard/admin-dashboard/admin-dashboard.component").then(
            (m) => m.AdminDashboardComponent
          ),
      },
      {
        path: "properties",
        loadComponent: () =>
          import(
            "./properties/admin-properties/admin-properties.component"
          ).then((m) => m.AdminPropertiesComponent),
      },
      {
        path: "users",
        loadComponent: () =>
          import("./users/admin-users/admin-users.component").then(
            (m) => m.AdminUsersComponent
          ),
      },
      {
        path: "reports",
        loadComponent: () =>
          import("./reports/admin-reports/admin-reports.component").then(
            (m) => m.AdminReportsComponent
          ),
      },
      {
        path: "analytics",
        loadComponent: () =>
          import("./dashboard/admin-dashboard/admin-dashboard.component").then(
            (m) => m.AdminDashboardComponent
          ),
      },
    ],
  },
];
