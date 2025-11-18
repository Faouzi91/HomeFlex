// ====================================
// app-routing.module.ts
// ====================================
import { NgModule } from "@angular/core";
import { PreloadAllModules, RouterModule, Routes } from "@angular/router";
import { AuthGuard } from "./core/guards/auth.guard";
import { LandingComponent } from "./features/landing/landing.component";

const routes: Routes = [
  {
    path: "",
    component: LandingComponent, // New landing page
  },
  {
    path: "auth",
    loadChildren: () =>
      import("./features/auth/auth.module").then((m) => m.AuthModule),
  },
  {
    path: "properties",
    loadChildren: () =>
      import("./features/properties/properties.module").then(
        (m) => m.PropertiesModule
      ),
  },
  {
    path: "bookings",
    loadChildren: () =>
      import("./bookings/bookings.module").then((m) => m.BookingsModule),
    canActivate: [AuthGuard],
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
    canActivate: [AuthGuard],
  },
  {
    path: "profile",
    loadChildren: () =>
      import("./profile/profile.module").then((m) => m.ProfileModule),
    canActivate: [AuthGuard],
  },
  // {
  //   path: "admin",
  //   loadChildren: () =>
  //     // @ts-ignore: Admin module may be absent in some builds; keep route for future/optional feature
  //     import("./features/admin/admin.module").then((m) => m.AdminModule),
  //   canActivate: [AuthGuard],
  // },
  {
    path: "**",
    redirectTo: "",
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules,
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
