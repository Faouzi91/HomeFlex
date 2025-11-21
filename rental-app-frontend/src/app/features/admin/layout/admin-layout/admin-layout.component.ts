import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";

@Component({
  selector: "app-admin-layout",
  standalone: true,
  imports: [CommonModule, RouterModule, IonicModule],
  templateUrl: "./admin-layout.component.html",
  styleUrls: ["./admin-layout.component.scss"],
})
export class AdminLayoutComponent {
  sidebarOpen = true; // default for desktop

  ngOnInit() {
    // default: sidebar visible only on desktop
    this.sidebarOpen = window.innerWidth >= 1024;
    window.addEventListener("resize", () => {
      // close sidebar on small screens
      this.sidebarOpen = window.innerWidth >= 1024;
    });
  }

  menuItems = [
    { label: "Dashboard", route: "/admin", icon: "grid" },
    { label: "Properties", route: "/admin/properties", icon: "home" },
    { label: "Users", route: "/admin/users", icon: "people" },
    { label: "Reports", route: "/admin/reports", icon: "flag" },
    { label: "Analytics", route: "/admin/analytics", icon: "bar-chart" },
  ];

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }
}
