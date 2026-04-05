import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { Vehicle } from 'src/app/models/vehicle.model';

@Component({
  selector: 'app-vehicle-card',
  standalone: true,
  imports: [IonicModule, CommonModule],
  templateUrl: './vehicle-card.component.html',
  styleUrls: ['./vehicle-card.component.scss'],
})
export class VehicleCardComponent {
  @Input() vehicle!: Vehicle;
  @Output() cardClick = new EventEmitter<Vehicle>();

  onCardClick(): void {
    this.cardClick.emit(this.vehicle);
  }

  getPrimaryImage(): string {
    return this.vehicle.images?.[0]?.imageUrl || '/assets/images/placeholder-vehicle.jpg';
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'XAF',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  }
}
