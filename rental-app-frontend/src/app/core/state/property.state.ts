import { Injectable, signal } from "@angular/core";
import { Property } from "../../models/property.model";

@Injectable({ providedIn: "root" })
export class PropertyState {
  readonly properties = signal<Property[]>([]);
  readonly loading = signal<boolean>(false);

  setProperties(properties: Property[]): void {
    this.properties.set(properties);
  }

  setLoading(loading: boolean): void {
    this.loading.set(loading);
  }

  clear(): void {
    this.properties.set([]);
    this.loading.set(false);
  }
}
