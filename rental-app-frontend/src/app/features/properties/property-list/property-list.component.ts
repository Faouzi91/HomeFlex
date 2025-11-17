import { Component, OnInit, ViewChild } from "@angular/core";
import { Router } from "@angular/router";
import { IonInfiniteScroll, IonicModule } from "@ionic/angular";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { PropertyService } from "src/app/core/services/property/property.service";
import { Property, PropertySearchParams } from "src/app/models/property.model";
import { PropertyCardComponent } from "../property-card/property-card.component";
import { ReactiveFormsModule } from "@angular/forms";
import { PropertyFiltersComponent } from "../property-filters/property-filters.component";

@Component({
  selector: "app-property-list",
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    ReactiveFormsModule, // Needed for [formGroup]
    TranslateModule, // <-- Add this to use 'translate' pipe],
    PropertyFiltersComponent,
  ],
  templateUrl: "./property-list.component.html",
  styleUrl: "./property-list.component.scss",
})
export class PropertyListComponent implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll?: IonInfiniteScroll;

  properties: Property[] = [];
  loading = false;
  searchParams: PropertySearchParams = {
    page: 0,
    size: 20,
    sortBy: "createdAt",
    sortDirection: "desc",
  };
  totalPages = 0;
  showFilters = false;

  // Filter options
  propertyTypes = ["APARTMENT", "HOUSE", "STUDIO", "VILLA", "ROOM", "OFFICE"];
  cities: string[] = [];

  constructor(
    private propertyService: PropertyService,
    private router: Router,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.loadProperties();
  }

  loadProperties(reset: boolean = false): void {
    if (reset) {
      this.searchParams.page = 0;
      this.properties = [];
    }

    this.loading = true;

    this.propertyService.searchProperties(this.searchParams).subscribe({
      next: (response) => {
        this.properties = [...this.properties, ...response.content];
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error) => {
        console.error("Error loading properties:", error);
        this.loading = false;
      },
    });
  }

  loadMore(event: any): void {
    if (this.searchParams.page! < this.totalPages - 1) {
      this.searchParams.page!++;
      this.propertyService.searchProperties(this.searchParams).subscribe({
        next: (response) => {
          this.properties = [...this.properties, ...response.content];
          event.target.complete();
        },
        error: () => {
          event.target.complete();
        },
      });
    } else {
      event.target.complete();
    }
  }

  onSearch(searchTerm: string): void {
    this.searchParams.city = searchTerm;
    this.loadProperties(true);
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  applyFilters(filters: PropertySearchParams): void {
    this.searchParams = { ...this.searchParams, ...filters, page: 0 };
    this.loadProperties(true);
    this.showFilters = false;
  }

  clearFilters(): void {
    this.searchParams = {
      page: 0,
      size: 20,
      sortBy: "createdAt",
      sortDirection: "desc",
    };
    this.loadProperties(true);
  }

  viewProperty(property: Property): void {
    this.router.navigate(["/properties", property.id]);
  }

  doRefresh(event: any): void {
    this.loadProperties(true);
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }
}
