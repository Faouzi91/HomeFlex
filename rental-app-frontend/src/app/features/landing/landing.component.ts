import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PropertyService } from 'src/app/core/services/property/property.service';
import { Property, ListingType } from 'src/app/models/property.model';
import { IonicModule } from '@ionic/angular';
import { PropertyCardComponent } from '../properties/property-card/property-card.component';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TranslateModule,
    SharedModule,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent implements OnInit {
  featuredProperties: Property[] = [];
  isLoading = true;
  searchCity = '';
  selectedType: ListingType = ListingType.RENT;
  searchType: 'buy' | 'rent' = 'rent';

  stats: {
    properties?: number;
    users?: number;
    cities?: number;
    transactions?: number;
  } = {
    properties: 0,
    users: 0,
    cities: 0,
    transactions: 0,
  };

  constructor(
    private router: Router,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    this.loadFeaturedProperties();
    this.loadStats();
  }

  loadFeaturedProperties(): void {
    this.isLoading = true; // Start loading
    this.propertyService.getFeaturedProperties().subscribe({
      next: (properties) => {
        this.featuredProperties = properties;
        this.isLoading = false; // Stop loading
      },
      error: (err) => {
        console.error('Error fetching featured properties:', err);
        this.isLoading = false; // Stop loading even on error
      },
    });
  }

  // Add logic for the chips
  applyQuickFilter(type: string) {
    const queryParams: any = {};
    if (type === 'bedrooms') queryParams.bedrooms = 2; // Example default
    if (type === 'price') queryParams.sort = 'price_asc';
    // Navigate to search page with these params
    this.router.navigate(['/properties'], { queryParams });
  }

  loadStats(): void {
    this.propertyService.getStats().subscribe({
      next: (res) => (this.stats = res),
      error: (err) => console.error('Error fetching stats', err),
    });
  }

  onSearch(): void {
    const queryParams: any = {};
    if (this.searchCity) queryParams.city = this.searchCity;

    this.propertyService.searchProperties(queryParams).subscribe({
      next: (res) => (this.featuredProperties = res.data),
      error: (err) => console.error('Search error', err),
    });
  }

  setSearchType(type: 'buy' | 'rent'): void {
    this.searchType = type;
    // Optional: You can pass this type to your search parameters later
  }

  setListingType(type: ListingType): void {
    this.selectedType = type;
  }

  navigate(path: string): void {
    this.router.navigate([path]);
  }

  navigateToProperties(): void {
    this.router.navigate(['/properties']);
  }

  navigateToPropertyDetail(propertyId: string): void {
    this.router.navigate(['/properties', propertyId]);
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'XAF',
      minimumFractionDigits: 0,
    }).format(price);
  }
}
