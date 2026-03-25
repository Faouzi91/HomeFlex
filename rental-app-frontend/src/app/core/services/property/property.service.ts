// ====================================
// features/properties/services/property.service.ts
// ====================================
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { environment } from 'src/app/environments/environment';
import { Property, PropertyReport, PropertySearchParams } from 'src/app/models/property.model';
import { PropertyState } from '../../state/property.state';
import { ApiListResponse, ApiPageResponse, ApiValueResponse } from 'src/app/types/api.types';

/** @deprecated Use ApiPageResponse — kept for templates importing PagedResponse */
export type PagedResponse<T> = ApiPageResponse<T>;

export interface Stats {
  properties: number;
  users: number;
  cities: number;
  transactions: number;
}

@Injectable({
  providedIn: 'root',
})
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/properties`;

  // State management
  private propertiesSubject = new BehaviorSubject<Property[]>([]);
  public properties$ = this.propertiesSubject.asObservable();

  constructor(
    private http: HttpClient,
    private propertyState: PropertyState
  ) {}

  searchProperties(params: PropertySearchParams): Observable<ApiPageResponse<Property>> {
    let httpParams = new HttpParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== null && value !== undefined && value !== '') {
        httpParams = httpParams.append(key, value.toString());
      }
    });

    return this.http
      .get<ApiPageResponse<Property>>(`${this.apiUrl}/search`, {
        params: httpParams,
      })
      .pipe(
        tap((response) => {
          if (params.page === 0) {
            this.propertiesSubject.next(response.data);
            this.propertyState.setProperties(response.data);
          } else {
            const current = this.propertiesSubject.value;
            this.propertiesSubject.next([...current, ...response.data]);
            this.propertyState.setProperties([...current, ...response.data]);
          }
        })
      );
  }

  getPropertyById(id: string): Observable<Property> {
    return this.http.get<Property>(`${this.apiUrl}/${id}`);
  }

  getFeaturedProperties(limit: number = 8): Observable<Property[]> {
    let params = new HttpParams()
      .set('page', '0')
      .set('size', limit.toString())
      .set('sortBy', 'viewCount')
      .set('sortDirection', 'DESC');

    return this.http
      .get<ApiPageResponse<Property>>(`${this.apiUrl}/search`, { params })
      .pipe(map((response: ApiPageResponse<Property>) => response.data));
  }

  getMyProperties(): Observable<Property[]> {
    return this.http
      .get<ApiListResponse<Property>>(`${this.apiUrl}/my-properties`)
      .pipe(map((r: ApiListResponse<Property>) => r.data));
  }

  createProperty(property: Partial<Property>): Observable<Property> {
    return this.http.post<Property>(this.apiUrl, property);
  }

  updateProperty(id: string, property: Partial<Property>): Observable<Property> {
    return this.http.put<Property>(`${this.apiUrl}/${id}`, property);
  }

  deleteProperty(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  incrementViewCount(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/view`, {});
  }

  getStats(): Observable<Stats> {
    return this.http
      .get<ApiValueResponse<Stats>>(`${environment.apiUrl}/stats`)
      .pipe(map((r: ApiValueResponse<Stats>) => r.data));
  }

  getSimilarProperties(id: string): Observable<Property[]> {
    return this.http
      .get<ApiListResponse<Property>>(`${this.apiUrl}/${id}/similar`)
      .pipe(map((r: ApiListResponse<Property>) => r.data));
  }

  reportProperty(
    propertyId: string,
    payload: { reason: string; description?: string }
  ): Observable<PropertyReport> {
    return this.http.post<PropertyReport>(`${this.apiUrl}/${propertyId}/report`, payload);
  }

  getPropertyReports(propertyId: string): Observable<PropertyReport[]> {
    return this.http
      .get<ApiListResponse<PropertyReport>>(`${this.apiUrl}/${propertyId}/reports`)
      .pipe(map((r: ApiListResponse<PropertyReport>) => r.data));
  }

  uploadImage(propertyId: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<{ imageUrl: string }>(`${this.apiUrl}/${propertyId}/images`, formData)
      .pipe(map((response: { imageUrl: string }) => response.imageUrl));
  }

  deleteImage(propertyId: string, imageId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${propertyId}/images/${imageId}`);
  }
}
