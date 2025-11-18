import { Component, OnInit, ViewChild } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { IonInfiniteScroll, IonicModule } from "@ionic/angular";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import {
  PagedResponse,
  PropertyService,
} from "src/app/core/services/property/property.service";
import {
  ListingType,
  Property,
  PropertySearchParams,
  PropertyType,
} from "src/app/models/property.model";
import { PropertyCardComponent } from "../property-card/property-card.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { PropertyFiltersComponent } from "../property-filters/property-filters.component";

@Component({
  selector: "app-property-list",
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    ReactiveFormsModule, // Needed for [formGroup]
    TranslateModule,
    FormsModule,
  ],
  templateUrl: "./property-list.component.html",
  styleUrl: "./property-list.component.scss",
})
export class PropertyListComponent implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll!: IonInfiniteScroll;

  properties: Property[] = [];
  loading = false;
  viewMode: "grid" | "list" = "grid";

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  // Filters
  searchParams: PropertySearchParams = {
    page: 0,
    size: 20,
    sortBy: "createdAt",
    sortDirection: "desc",
  };

  showFilters = false;

  propertyTypes = Object.values(PropertyType);
  listingTypes = Object.values(ListingType);

  // Filter options
  bedroomOptions = [
    { label: "Studio", value: 0 },
    { label: "1+", value: 1 },
    { label: "2+", value: 2 },
    { label: "3+", value: 3 },
    { label: "4+", value: 4 },
  ];

  priceRanges = [
    { label: "Under 100K", min: 0, max: 100000 },
    { label: "100K - 200K", min: 100000, max: 200000 },
    { label: "200K - 500K", min: 200000, max: 500000 },
    { label: "500K+", min: 500000, max: null },
  ];

  constructor(
    private propertyService: PropertyService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get query params from URL
    this.route.queryParams.subscribe((params) => {
      this.searchParams = {
        ...this.searchParams,
        ...params,
        page: 0, // Reset to first page on new search
      };
      this.loadProperties();
    });
  }

  loadProperties(append: boolean = false): void {
    if (!append) {
      this.loading = true;
      this.properties = [];
      this.searchParams.page = 0;
    }

    this.propertyService.searchProperties(this.searchParams).subscribe({
      next: (response: PagedResponse<Property>) => {
        if (append) {
          this.properties = [...this.properties, ...response.content];
        } else {
          this.properties = response.content;
        }

        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.page;
        this.loading = false;

        if (this.infiniteScroll) {
          this.infiniteScroll.complete();
          if (this.currentPage >= this.totalPages - 1) {
            this.infiniteScroll.disabled = true;
          }
        }
      },
      error: (error) => {
        console.error("Error loading properties:", error);
        this.loading = false;
        if (this.infiniteScroll) {
          this.infiniteScroll.complete();
        }
      },
    });
  }

  loadMore(event: any): void {
    this.searchParams.page = (this.searchParams.page || 0) + 1;
    this.loadProperties(true);
  }

  toggleViewMode(): void {
    this.viewMode = this.viewMode === "grid" ? "list" : "grid";
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  applyFilters(): void {
    this.updateQueryParams();
    this.loadProperties();
    this.showFilters = false;
  }

  clearFilters(): void {
    this.searchParams = {
      page: 0,
      size: 20,
      sortBy: "createdAt",
      sortDirection: "desc",
    };
    this.updateQueryParams();
    this.loadProperties();
  }

  setSortBy(sortBy: string, sortDirection: "asc" | "desc" = "desc"): void {
    this.searchParams.sortBy = sortBy;
    this.searchParams.sortDirection = sortDirection;
    this.loadProperties();
  }

  private updateQueryParams(): void {
    const params: any = {};
    Object.entries(this.searchParams).forEach(([key, value]) => {
      if (
        value !== null &&
        value !== undefined &&
        value !== "" &&
        key !== "page" &&
        key !== "size"
      ) {
        params[key] = value;
      }
    });

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: "merge",
    });
  }

  navigateToPropertyDetail(propertyId: string): void {
    this.router.navigate(["/properties", propertyId]);
  }
}
