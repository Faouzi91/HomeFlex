import { Component, OnInit } from "@angular/core";
import {
  Analytics,
  AdminService,
} from "src/app/core/services/admin/admin.service";
import { IonicModule } from "@ionic/angular";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { TranslateModule } from "@ngx-translate/core";
import { Chart, registerables } from "chart.js";
Chart.register(...registerables);

@Component({
  selector: "app-admin-dashboard",
  standalone: true,
  imports: [IonicModule, CommonModule, TranslateModule, RouterModule],
  templateUrl: "./admin-dashboard.component.html",
  styleUrls: ["./admin-dashboard.component.scss"],
})
export class AdminDashboardComponent implements OnInit {
  analytics: Analytics | null = null;
  loading = true;

  bookingsChart: any;
  propertiesCityChart: any;
  propertiesTypeChart: any;

  stats = [
    {
      title: "admin.stats.totalProperties",
      value: 0,
      icon: "home",
      color: "blue",
      key: "totalProperties",
    },
    {
      title: "admin.stats.totalUsers",
      value: 0,
      icon: "people",
      color: "green",
      key: "totalUsers",
    },
    {
      title: "admin.stats.totalBookings",
      value: 0,
      icon: "calendar",
      color: "purple",
      key: "totalBookings",
    },
    {
      title: "admin.stats.pendingApprovals",
      value: 0,
      icon: "time",
      color: "orange",
      key: "pendingProperties",
    },
  ];

  managementCards = [
    {
      title: "admin.management.pendingProperties.title",
      subtitle: "admin.management.pendingProperties.subtitle",
      icon: "alert-circle",
      color: "orange",
      route: "/admin/properties",
    },
    {
      title: "admin.management.userManagement.title",
      subtitle: "admin.management.userManagement.subtitle",
      icon: "people-circle",
      color: "blue",
      route: "/admin/users",
    },
    {
      title: "admin.management.reports.title",
      subtitle: "admin.management.reports.subtitle",
      icon: "flag",
      color: "red",
      route: "/admin/reports",
    },
    {
      title: "admin.management.analytics.title",
      subtitle: "admin.management.analytics.subtitle",
      icon: "bar-chart",
      color: "green",
      route: "/admin/analytics",
    },
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadAnalytics();
  }

  loadAnalytics() {
    this.loading = true;
    this.adminService.getAnalytics().subscribe({
      next: (res) => {
        this.analytics = res;
        this.updateStats(res);
        this.buildCharts(res);
        console.log("Analytics", res);

        this.loading = false;
      },
      error: (err) => {
        console.error("Failed to load analytics", err);
        this.loading = false;
      },
    });
  }

  updateStats(analytics: Analytics) {
    this.stats = this.stats.map((stat) => ({
      ...stat,
      value: analytics[stat.key] || 0,
    }));
  }

  getColorClass(color: string): string {
    const colors: any = {
      blue: "bg-blue-50 text-blue-600",
      green: "bg-green-50 text-green-600",
      purple: "bg-purple-50 text-purple-600",
      orange: "bg-orange-50 text-orange-600",
      red: "bg-red-50 text-red-600",
    };
    return colors[color] || "bg-gray-50 text-gray-600";
  }

  buildCharts(analytics: Analytics) {
    // BOOKING STATUS CHART
    if (this.bookingsChart) this.bookingsChart.destroy();
    this.bookingsChart = new Chart("bookingsChart", {
      type: "pie",
      data: {
        labels: Object.keys(analytics.bookingsByStatus),
        datasets: [
          {
            data: Object.values(analytics.bookingsByStatus),
          },
        ],
      },
    });

    // PROPERTIES BY CITY CHART
    if (this.propertiesCityChart) this.propertiesCityChart.destroy();
    this.propertiesCityChart = new Chart("propertiesCityChart", {
      type: "bar",
      data: {
        labels: Object.keys(analytics.propertiesByCity),
        datasets: [
          {
            data: Object.values(analytics.propertiesByCity),
          },
        ],
      },
    });

    // PROPERTIES BY TYPE CHART
    if (this.propertiesTypeChart) this.propertiesTypeChart.destroy();
    this.propertiesTypeChart = new Chart("propertiesTypeChart", {
      type: "doughnut",
      data: {
        labels: Object.keys(analytics.propertiesByType),
        datasets: [
          {
            data: Object.values(analytics.propertiesByType),
          },
        ],
      },
    });
  }
}
