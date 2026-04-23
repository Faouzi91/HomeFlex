import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
  inject,
} from '@angular/core';
import * as L from 'leaflet';
import { TranslateService } from '@ngx-translate/core';
import { Property } from '../../../core/models/api.types';

@Component({
  selector: 'app-map',
  imports: [],
  template: `<div
    #mapContainer
    class="w-full h-full rounded-xl overflow-hidden shadow-inner border border-slate-200"
  ></div>`,
  styles: [
    `
      :host {
        display: block;
        width: 100%;
        height: 100%;
      }
    `,
  ],
})
export class MapComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef;
  @Input() properties: Property[] = [];
  @Input() center: [number, number] = [48.8566, 2.3522]; // Default to Paris
  @Input() zoom = 12;
  @Output() propertySelected = new EventEmitter<Property>();

  private readonly translate = inject(TranslateService);
  private map?: L.Map;
  private markers: L.Marker[] = [];

  ngOnInit(): void {
    this.initMap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['properties'] && this.map) {
      this.updateMarkers();
    }
    if (changes['center'] && this.map && !changes['center'].isFirstChange()) {
      this.map.setView(this.center, this.zoom);
    }
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView(this.center, this.zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
    }).addTo(this.map);

    this.updateMarkers();
  }

  private updateMarkers(): void {
    if (!this.map) return;

    // Clear existing markers
    this.markers.forEach((m) => m.remove());
    this.markers = [];

    const bounds = L.latLngBounds([]);

    this.properties.forEach((property) => {
      if (property.latitude && property.longitude) {
        const viewDetailsLabel = this.translate.instant('NAV.PROPERTIES'); // Or a better key if I had one
        const marker = L.circleMarker([property.latitude, property.longitude], {
          radius: 10,
          fillColor: '#6366f1', // brand-500
          color: '#fff',
          weight: 2,
          opacity: 1,
          fillOpacity: 0.8,
        })
          .addTo(this.map!)
          .bindPopup(
            `<div class="p-2 min-w-[150px]">
              <h4 class="font-bold text-slate-900 mb-1">${property.title}</h4>
              <p class="text-sm font-extrabold text-brand-600 mb-2">${property.price} ${property.currency}</p>
              <button class="w-full bg-slate-900 text-white py-1.5 rounded text-[10px] font-bold uppercase tracking-wider hover:bg-slate-800 transition-colors" id="popup-${property.id}">${viewDetailsLabel}</button>
            </div>`,
          );

        marker.on('popupopen', () => {
          setTimeout(() => {
            document.getElementById(`popup-${property.id}`)?.addEventListener('click', () => {
              this.propertySelected.emit(property);
            });
          }, 0);
        });

        this.markers.push(marker as unknown as L.Marker);
        bounds.extend([property.latitude, property.longitude]);
      }
    });

    if (this.markers.length > 0) {
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }
}
