import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiListResponse, ApiValueResponse, Property } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class FavoriteApi extends BaseApi {
  getAll(): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/favorites`);
  }

  check(propertyId: string): Observable<ApiValueResponse<boolean>> {
    return this.http.get<ApiValueResponse<boolean>>(
      `${this.baseUrl}/favorites/check/${propertyId}`,
    );
  }

  add(propertyId: string): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/favorites/${propertyId}`, {});
  }

  remove(propertyId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/favorites/${propertyId}`);
  }
}
