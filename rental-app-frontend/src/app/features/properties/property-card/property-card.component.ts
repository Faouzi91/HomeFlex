import { Component, EventEmitter, Input, Output } from "@angular/core";
import { Property } from "src/app/models/property.model";
import { IonicModule } from "@ionic/angular";
import { FavoriteService } from "src/app/core/services/favorite/favorite.service";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { TranslateModule } from "@ngx-translate/core";

@Component({
  selector: "app-property-card",
  standalone: true,
  imports: [IonicModule, TranslateModule],
  templateUrl: "./property-card.component.html",
  styleUrl: "./property-card.component.scss",
})
export class PropertyCardComponent {
  @Input() property!: Property;
  @Input() isFavorite = false;
  @Output() favoriteToggled = new EventEmitter<void>();

  onFavoriteClick(event: Event): void {
    event.stopPropagation();
    this.favoriteToggled.emit();
  }

  isAuthenticated = false;

  constructor(
    private favoriteService: FavoriteService,
    private authService: AuthService
  ) {
    this.isAuthenticated = this.authService.isAuthenticated();
  }

  ngOnInit(): void {
    if (this.isAuthenticated) {
      this.checkIfFavorite();
    }
  }

  checkIfFavorite(): void {
    this.favoriteService.isFavorite(this.property.id).subscribe({
      next: (result) => {
        this.isFavorite = result;
      },
    });
  }

  toggleFavorite(event: Event): void {
    event.stopPropagation();

    if (!this.isAuthenticated) {
      // Navigate to login
      return;
    }

    if (this.isFavorite) {
      this.favoriteService.removeFromFavorites(this.property.id).subscribe({
        next: () => {
          this.isFavorite = false;
        },
      });
    } else {
      this.favoriteService.addToFavorites(this.property.id).subscribe({
        next: () => {
          this.isFavorite = true;
        },
      });
    }
  }

  // onCardClick(): void {
  //   this.favoriteToggled.emit(this.property);
  // }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: currency || "XAF",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  }

  getPropertyTypeLabel(type: string): string {
    const labels: any = {
      APARTMENT: "Apartment",
      HOUSE: "House",
      STUDIO: "Studio",
      VILLA: "Villa",
      ROOM: "Room",
      OFFICE: "Office",
      LAND: "Land",
    };
    return labels[type] || type;
  }

  getPrimaryImage(): string {
    const primary = this.property.images?.find((img) => img.isPrimary);
    return (
      primary?.imageUrl ||
      this.property.images?.[0]?.imageUrl ||
      "/assets/images/placeholder-property.jpg"
    );
  }
}
