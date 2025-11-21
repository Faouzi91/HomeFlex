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
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-property-list",
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    CommonModule,
  ],
  templateUrl: "./property-list.component.html",
  styleUrls: ["./property-list.component.scss"],
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
    { key: "studioBeds", value: 0 }, // maps to filters.quickFilters.studioBeds
    { key: "onePlusBeds", value: 1 }, // maps to filters.quickFilters.onePlusBeds
    { key: "twoPlusBeds", value: 2 },
    { key: "threePlusBeds", value: 3 },
    { key: "fourPlusBeds", value: 4 },
  ];

  priceRanges = [
    { key: "under100k", min: 0, max: 100000 },
    { key: "100to200k", min: 100000, max: 200000 },
    { key: "200to500k", min: 200000, max: 500000 },
    { key: "over500k", min: 500000, max: null },
  ];

  constructor(
    private propertyService: PropertyService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to query params
    this.route.queryParams.subscribe((params) => {
      // Merge query params with default search params
      this.searchParams = {
        page: 0,
        size: 20,
        sortBy: params["sortBy"] || "createdAt",
        sortDirection: params["sortDirection"] || "desc",
        city: params["city"] || undefined,
        propertyType: params["propertyType"] || undefined,
        listingType: params["listingType"] || undefined,
        minPrice: params["minPrice"] ? Number(params["minPrice"]) : undefined,
        maxPrice: params["maxPrice"] ? Number(params["maxPrice"]) : undefined,
        bedrooms: params["bedrooms"] ? Number(params["bedrooms"]) : undefined,
        bathrooms: params["bathrooms"]
          ? Number(params["bathrooms"])
          : undefined,
      };

      // Always load properties when params change (or on init)
      this.loadProperties();
    });
  }

  loadProperties(append: boolean = false): void {
    if (!append) {
      this.loading = true;
      this.properties = [];
      this.searchParams.page = 0;
    }

    console.log("Loading properties with params:", this.searchParams);

    this.propertyService.searchProperties(this.searchParams).subscribe({
      next: (response: PagedResponse<Property>) => {
        console.log("Properties loaded:", response);

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
    console.log("View mode changed to:", this.viewMode);
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  applyFilters(): void {
    this.updateQueryParams();
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
  }

  setSortBy(sortBy: string, sortDirection: "asc" | "desc" = "desc"): void {
    this.searchParams.sortBy = sortBy;
    this.searchParams.sortDirection = sortDirection;
    this.loadProperties();
  }

  /**
   * Return the translation key for the listing type shown in the header.
   * The template pipes the result to | translate, so we return a translation key.
   */
  getListingTypeKey(): string {
    const lt = this.searchParams?.listingType;
    if (!lt) {
      return "property.forRent"; // default fallback
    }

    // Normalize common enum values and map to the translation keys in your JSON
    const normalized = String(lt).toUpperCase();

    switch (normalized) {
      case "RENT":
      case "SHORT_TERM":
        return "property.forRent";
      case "SALE":
      case "SELL":
        return "property.forSale";
      default:
        // If your listingType already contains a translation key, return it directly.
        // Fallback to forRent if unknown.
        return "property.forRent";
    }
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
