import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { PropertyService } from "src/app/core/services/property/property.service";
import { Property, ListingType } from "src/app/models/property.model";
import { IonicModule } from "@ionic/angular";
import { PropertyCardComponent } from "../properties/property-card/property-card.component";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { TranslateModule } from "@ngx-translate/core";

@Component({
  selector: "app-landing",
  standalone: true,
  imports: [
    IonicModule,
    PropertyCardComponent,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TranslateModule,
  ],
  templateUrl: "./landing.component.html",
  styleUrl: "./landing.component.scss",
})
export class LandingComponent implements OnInit {
  featuredProperties: Property[] = [];
  searchCity = "";
  selectedType: ListingType = ListingType.RENT;

  stats = {
    properties: "10,000+",
    users: "50,000+",
    cities: "25+",
    transactions: "5,000+",
  };

  features = [
    {
      icon: "search",
      title: "Smart Search",
      description:
        "Find your perfect property with advanced filters and AI-powered recommendations",
    },
    {
      icon: "shield-checkmark",
      title: "Verified Listings",
      description:
        "All properties are verified by our team for authenticity and quality",
    },
    {
      icon: "chatbubbles",
      title: "Instant Chat",
      description:
        "Connect with landlords instantly through our real-time messaging system",
    },
    {
      icon: "heart",
      title: "Save Favorites",
      description:
        "Create collections of your favorite properties and get instant alerts",
    },
  ];

  constructor(
    private router: Router,
    private propertyService: PropertyService
  ) {}

  ngOnInit(): void {
    this.loadFeaturedProperties();
  }

  loadFeaturedProperties(): void {
    this.propertyService.getMyProperties().subscribe({
      next: (properties) => {
        this.featuredProperties = properties.content.slice(0, 4);
      },
      error: (error) => {
        console.error("Error loading featured properties:", error);
      },
    });
  }

  onSearch(): void {
    const queryParams: any = {};

    if (this.searchCity) {
      queryParams.city = this.searchCity;
    }

    queryParams.listingType = this.selectedType;

    this.router.navigate(["/properties"], { queryParams });
  }

  setListingType(type: ListingType): void {
    this.selectedType = type;
  }

  navigateToProperties(): void {
    this.router.navigate(["/properties"]);
  }

  navigateToPropertyDetail(propertyId: string): void {
    this.router.navigate(["/properties", propertyId]);
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency || "XAF",
      minimumFractionDigits: 0,
    }).format(price);
  }
}
