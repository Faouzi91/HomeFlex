// ====================================
// favorite.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Property } from "../../../models/property.model";

@Injectable({
  providedIn: "root",
})
export class FavoriteService {
  private apiUrl = `${environment.apiUrl}/favorites`;

  constructor(private http: HttpClient) {}

  addToFavorites(propertyId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${propertyId}`, {});
  }

  removeFromFavorites(propertyId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${propertyId}`);
  }

  getMyFavorites(): Observable<Property[]> {
    return this.http.get<Property[]>(this.apiUrl);
  }

  isFavorite(propertyId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check/${propertyId}`);
  }
}
