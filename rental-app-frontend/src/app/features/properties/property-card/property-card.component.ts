import { Component, EventEmitter, Input, Output } from "@angular/core";
import { Property } from "src/app/models/property.model";
import { IonicModule } from "@ionic/angular";

@Component({
  selector: "app-property-card",
  standalone: true,
  imports: [IonicModule],
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
}
