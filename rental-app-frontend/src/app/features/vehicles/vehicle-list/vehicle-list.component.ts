import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VehicleCardComponent } from '../vehicle-card/vehicle-card.component';
import { VehicleService } from 'src/app/core/services/vehicle/vehicle.service';
import { Vehicle, VehicleSearchParams } from 'src/app/models/vehicle.model';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule, VehicleCardComponent],
  templateUrl: './vehicle-list.component.html',
  styleUrls: ['./vehicle-list.component.scss'],
})
export class VehicleListComponent implements OnInit {
  vehicles: Vehicle[] = [];
  loading = false;
  totalElements = 0;
  currentPage = 0;
  hasMore = true;
  showFilters = false;

  filters: VehicleSearchParams = {};

  transmissionOptions = ['AUTOMATIC', 'MANUAL'];
  fuelTypeOptions = ['PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'];

  constructor(
    private vehicleService: VehicleService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.filters = {
        brand: params['brand'] || undefined,
        city: params['city'] || undefined,
        transmission: params['transmission'] || undefined,
        fuelType: params['fuelType'] || undefined,
        minPrice: params['minPrice'] ? Number(params['minPrice']) : undefined,
        maxPrice: params['maxPrice'] ? Number(params['maxPrice']) : undefined,
      };
      this.vehicles = [];
      this.currentPage = 0;
      this.hasMore = true;
      this.loadVehicles();
    });
  }

  loadVehicles(): void {
    if (this.loading) return;
    this.loading = true;

    const params: VehicleSearchParams = {
      ...this.filters,
      page: this.currentPage,
      size: 20,
    };

    this.vehicleService.search(params).subscribe({
      next: (res) => {
        this.vehicles = [...this.vehicles, ...res.data];
        this.totalElements = res.totalElements;
        this.hasMore = this.currentPage < res.totalPages - 1;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  loadMore(event?: any): void {
    if (!this.hasMore) {
      event?.target?.complete();
      return;
    }
    this.currentPage++;
    this.vehicleService.search({ ...this.filters, page: this.currentPage, size: 20 }).subscribe({
      next: (res) => {
        this.vehicles = [...this.vehicles, ...res.data];
        this.hasMore = this.currentPage < res.totalPages - 1;
        event?.target?.complete();
      },
      error: () => {
        event?.target?.complete();
      },
    });
  }

  applyFilters(): void {
    const params: Record<string, string | number> = {};
    for (const [key, value] of Object.entries(this.filters)) {
      if (value !== null && value !== undefined && value !== '') {
        params[key] = value as string | number;
      }
    }
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
    });
    this.showFilters = false;
  }

  clearFilters(): void {
    this.filters = {};
    this.router.navigate([], { relativeTo: this.route, queryParams: {} });
    this.showFilters = false;
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  navigateToDetail(vehicle: Vehicle): void {
    this.router.navigate(['/vehicles', vehicle.id]);
  }

  trackById(_index: number, item: Vehicle): string {
    return item.id;
  }
}
