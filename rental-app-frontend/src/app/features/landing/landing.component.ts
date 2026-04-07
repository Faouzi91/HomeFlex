import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PropertyService } from 'src/app/core/services/property/property.service';
import { Property, ListingType } from 'src/app/models/property.model';
import { IonicModule } from '@ionic/angular';
import { PropertyCardComponent } from '../properties/property-card/property-card.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { FooterComponent } from 'src/app/shared/components/footer/footer.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    CommonModule,
    FormsModule,
    TranslateModule,
    FooterComponent,
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent implements OnInit {
  featuredProperties: Property[] = [];
  isLoading = true;
  searchCity = '';

  stats = { properties: 0, users: 0, cities: 0, transactions: 0 };

  constructor(
    private router: Router,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    this.loadFeaturedProperties();
    this.loadStats();
  }

  loadFeaturedProperties(): void {
    this.isLoading = true;
    this.propertyService.getFeaturedProperties(8).subscribe({
      next: (props) => {
        this.featuredProperties = props;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  loadStats(): void {
    this.propertyService.getStats().subscribe({
      next: (s) => (this.stats = s),
      error: () => {},
    });
  }

  onSearch(): void {
    const q: any = {};
    if (this.searchCity.trim()) q.city = this.searchCity.trim();
    this.router.navigate(['/properties'], { queryParams: q });
  }

  navigate(path: string): void {
    this.router.navigateByUrl(path);
  }

  navigateToProperties(): void {
    this.router.navigate(['/properties']);
  }

  navigateToPropertyDetail(id: string): void {
    this.router.navigate(['/properties', id]);
  }
}
