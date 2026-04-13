import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApiListResponse,
  ApiValueResponse,
  Review,
  ReviewCreateRequest,
} from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class ReviewApi extends BaseApi {
  create(request: ReviewCreateRequest): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews`, request);
  }

  getByProperty(propertyId: string): Observable<ApiListResponse<Review>> {
    return this.http.get<ApiListResponse<Review>>(`${this.baseUrl}/reviews/property/${propertyId}`);
  }

  getPropertyAverage(propertyId: string): Observable<ApiValueResponse<number>> {
    return this.http.get<ApiValueResponse<number>>(
      `${this.baseUrl}/reviews/property/${propertyId}/average`,
    );
  }

  getByTenant(userId: string): Observable<ApiListResponse<Review>> {
    return this.http.get<ApiListResponse<Review>>(`${this.baseUrl}/reviews/tenant/${userId}`);
  }

  getTenantAverage(userId: string): Observable<ApiValueResponse<number>> {
    return this.http.get<ApiValueResponse<number>>(
      `${this.baseUrl}/reviews/tenant/${userId}/average`,
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/reviews/${id}`);
  }

  reply(id: string, reply: string): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews/${id}/reply`, null, {
      params: this.buildParams({ reply }),
    });
  }
}
