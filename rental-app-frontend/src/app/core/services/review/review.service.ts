// ====================================
// features/reviews/services/review.service.ts
// ====================================
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/app/environments/environment';
import { ApiListResponse, ApiValueResponse } from 'src/app/types/api.types';

export interface Review {
  id: string;
  propertyId: string;
  reviewer: any;
  rating: number;
  comment: string;
  createdAt: Date;
}

export interface ReviewCreateRequest {
  propertyId: string;
  rating: number;
  comment: string;
}

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  createReview(request: ReviewCreateRequest): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, request);
  }

  getPropertyReviews(propertyId: string): Observable<Review[]> {
    return this.http
      .get<ApiListResponse<Review>>(`${this.apiUrl}/property/${propertyId}`)
      .pipe(map((r: ApiListResponse<Review>) => r.data));
  }

  getAverageRating(propertyId: string): Observable<number> {
    return this.http
      .get<ApiValueResponse<number>>(`${this.apiUrl}/property/${propertyId}/average`)
      .pipe(map((r: ApiValueResponse<number>) => r.data));
  }

  deleteReview(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
