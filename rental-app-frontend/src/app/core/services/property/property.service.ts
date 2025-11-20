// ====================================
// features/properties/services/property.service.ts
// ====================================
import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable, BehaviorSubject } from "rxjs";
import { tap, map } from "rxjs/operators";
import { environment } from "src/app/environments/environment";
import { Property, PropertySearchParams } from "src/app/models/property.model";

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface Stats {
  properties: number;
  users: number;
  cities: number;
  transactions: number;
}

@Injectable({
  providedIn: "root",
})
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/properties`;

  // State management
  private propertiesSubject = new BehaviorSubject<Property[]>([]);
  public properties$ = this.propertiesSubject.asObservable();

  constructor(private http: HttpClient) {}

  searchProperties(
    params: PropertySearchParams
  ): Observable<PagedResponse<Property>> {
    let httpParams = new HttpParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== "") {
        httpParams = httpParams.append(key, value.toString());
      }
    });

    return this.http
      .get<PagedResponse<Property>>(`${this.apiUrl}/search`, {
        params: httpParams,
      })
      .pipe(
        tap((response) => {
          if (params.page === 0) {
            this.propertiesSubject.next(response.content);
          } else {
            const current = this.propertiesSubject.value;
            this.propertiesSubject.next([...current, ...response.content]);
          }
        })
      );
  }

  getPropertyById(id: string): Observable<Property> {
    return this.http.get<Property>(`${this.apiUrl}/${id}`);
  }

  getFeaturedProperties(limit: number = 8): Observable<Property[]> {
    let params = new HttpParams()
      .set("page", "0")
      .set("size", limit.toString())
      .set("sortBy", "viewCount") // Correct parameter
      .set("sortDirection", "DESC");

    return this.http
      .get<PagedResponse<Property>>(`${this.apiUrl}/search`, { params })
      .pipe(map((response) => response.content));
  }

  getMyProperties(
    page: number = 0,
    size: number = 20
  ): Observable<PagedResponse<Property>> {
    const params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());

    return this.http.get<PagedResponse<Property>>(
      `${this.apiUrl}/my-properties`,
      { params }
    );
  }

  createProperty(property: Partial<Property>): Observable<Property> {
    return this.http.post<Property>(this.apiUrl, property);
  }

  updateProperty(
    id: string,
    property: Partial<Property>
  ): Observable<Property> {
    return this.http.put<Property>(`${this.apiUrl}/${id}`, property);
  }

  deleteProperty(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  incrementViewCount(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/view`, {});
  }

  getStats(): Observable<Stats> {
    return this.http.get<Stats>(`${environment.apiUrl}/stats`);
  }

  getSimilarProperties(id: string): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.apiUrl}/${id}/similar`);
  }

  // getFeaturedProperties() {
  //   return this.http.get<Property[]>("/api/properties/search?featured=true");
  // }

  uploadImage(propertyId: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append("file", file);

    return this.http
      .post<{ imageUrl: string }>(
        `${this.apiUrl}/${propertyId}/images`,
        formData
      )
      .pipe(map((response) => response.imageUrl));
  }

  deleteImage(propertyId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${propertyId}/images/${imageId}`
    );
  }
}
