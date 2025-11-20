import { Component, EventEmitter, Input, Output } from "@angular/core";
import { Property } from "src/app/models/property.model";
import { IonicModule } from "@ionic/angular";
import { FavoriteService } from "src/app/core/services/favorite/favorite.service";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { TranslateModule } from "@ngx-translate/core";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-property-card",
  standalone: true,
  imports: [IonicModule, TranslateModule, CommonModule],
  templateUrl: "./property-card.component.html",
  styleUrls: ["./property-card.component.scss"],
})
export class PropertyCardComponent {
  @Input() property!: Property;
  @Input() showFavoriteButton = true;
  @Output() cardClick = new EventEmitter<Property>();
  @Output() favoriteToggled = new EventEmitter<void>();

  isFavorite = false;
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
      return;
    }

    if (this.isFavorite) {
      this.favoriteService.removeFromFavorites(this.property.id).subscribe({
        next: () => {
          this.isFavorite = false;
          this.favoriteToggled.emit();
        },
      });
    } else {
      this.favoriteService.addToFavorites(this.property.id).subscribe({
        next: () => {
          this.isFavorite = true;
          this.favoriteToggled.emit();
        },
      });
    }
  }

  onCardClick(event: Event): void {
    this.cardClick.emit(this.property);
  }

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
