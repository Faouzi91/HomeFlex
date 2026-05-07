import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ApiListResponse,
  ApiPageResponse,
  OccupancyData,
  OccupancySummary,
  PricingRecommendation,
  Property,
  PropertySearchParams,
  PropertyUnit,
  PropertyUnitRequest,
  ReportItem,
  RoomType,
  RoomTypeCreateRequest,
} from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class PropertyApi extends BaseApi {
  getCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/properties/cities`);
  }

  search(params: PropertySearchParams): Observable<ApiPageResponse<Property>> {
    return this.http.get<ApiPageResponse<Property>>(`${this.baseUrl}/properties/search`, {
      params: this.buildParams(params),
    });
  }

  getById(id: string): Observable<Property> {
    return this.http.get<Property>(`${this.baseUrl}/properties/${id}`);
  }

  trackView(id: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${id}/view`, {});
  }

  getSimilar(id: string): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/properties/${id}/similar`);
  }

  getMine(): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/properties/my-properties`);
  }

  create(payload: Record<string, unknown>): Observable<Property> {
    return this.http.post<Property>(`${this.baseUrl}/properties/json`, payload);
  }

  uploadImages(id: string, files: File[]): Observable<void> {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return this.http.post<void>(`${this.baseUrl}/properties/${id}/images`, formData);
  }

  update(id: string, payload: Record<string, unknown>): Observable<Property> {
    return this.http.put<Property>(`${this.baseUrl}/properties/${id}/json`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/properties/${id}`);
  }

  compare(ids: string[]): Observable<ApiListResponse<Property>> {
    return this.http.get<ApiListResponse<Property>>(`${this.baseUrl}/properties/compare`, {
      params: this.buildParams({ ids: ids.join(',') }),
    });
  }

  getReports(propertyId: string): Observable<ApiListResponse<ReportItem>> {
    return this.http.get<ApiListResponse<ReportItem>>(
      `${this.baseUrl}/properties/${propertyId}/reports`,
    );
  }

  report(payload: {
    propertyId: string;
    reason: string;
    description: string;
  }): Observable<ReportItem> {
    return this.http.post<ReportItem>(`${this.baseUrl}/properties/${payload.propertyId}/report`, {
      reason: payload.reason,
      description: payload.description,
    });
  }

  getAvailability(
    propertyId: string,
    start: string,
    end: string,
  ): Observable<
    ApiListResponse<{
      date: string;
      status: string;
      bookingId?: string;
    }>
  > {
    return this.http.get<ApiListResponse<any>>(
      `${this.baseUrl}/properties/${propertyId}/availability`,
      {
        params: this.buildParams({ start, end }),
      },
    );
  }

  blockRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${propertyId}/availability/block`, {
      start,
      end,
    });
  }

  unblockRange(propertyId: string, start: string, end: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/properties/${propertyId}/availability/unblock`, {
      start,
      end,
    });
  }

  getPricingRecommendation(propertyId: string): Observable<PricingRecommendation> {
    return this.http.get<PricingRecommendation>(
      `${this.baseUrl}/properties/${propertyId}/pricing/recommendation`,
    );
  }

  getPricingRules(
    propertyId: string,
  ): Observable<import('../../../core/models/api.types').PricingRule[]> {
    return this.http.get<import('../../../core/models/api.types').PricingRule[]>(
      `${this.baseUrl}/properties/${propertyId}/pricing/rules`,
    );
  }

  createPricingRule(
    propertyId: string,
    body: import('../../../core/models/api.types').PricingRuleCreateRequest,
  ): Observable<import('../../../core/models/api.types').PricingRule> {
    return this.http.post<import('../../../core/models/api.types').PricingRule>(
      `${this.baseUrl}/properties/${propertyId}/pricing/rules`,
      body,
    );
  }

  deletePricingRule(propertyId: string, ruleId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/properties/${propertyId}/pricing/rules/${ruleId}`,
    );
  }

  // ── Room Types ──────────────────────────────────────────────────────────────

  getRoomTypes(propertyId: string): Observable<ApiListResponse<RoomType>> {
    return this.http.get<ApiListResponse<RoomType>>(
      `${this.baseUrl}/properties/${propertyId}/room-types`,
    );
  }

  createRoomType(propertyId: string, body: RoomTypeCreateRequest): Observable<RoomType> {
    return this.http.post<RoomType>(`${this.baseUrl}/properties/${propertyId}/room-types`, body);
  }

  updateRoomType(
    propertyId: string,
    roomTypeId: string,
    body: RoomTypeCreateRequest,
  ): Observable<RoomType> {
    return this.http.put<RoomType>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}`,
      body,
    );
  }

  deleteRoomType(propertyId: string, roomTypeId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}`,
    );
  }

  // ── Property Units (per-unit identity) ──────────────────────────────────────

  getUnits(propertyId: string, roomTypeId: string): Observable<ApiListResponse<PropertyUnit>> {
    return this.http.get<ApiListResponse<PropertyUnit>>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}/units`,
    );
  }

  getAvailableUnits(
    propertyId: string,
    roomTypeId: string,
    startDate: string,
    endDate: string,
  ): Observable<ApiListResponse<PropertyUnit>> {
    return this.http.get<ApiListResponse<PropertyUnit>>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}/units/available`,
      { params: this.buildParams({ startDate, endDate }) },
    );
  }

  createUnit(
    propertyId: string,
    roomTypeId: string,
    body: PropertyUnitRequest,
  ): Observable<PropertyUnit> {
    return this.http.post<PropertyUnit>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}/units`,
      body,
    );
  }

  updateUnit(
    propertyId: string,
    roomTypeId: string,
    unitId: string,
    body: PropertyUnitRequest,
  ): Observable<PropertyUnit> {
    return this.http.put<PropertyUnit>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}/units/${unitId}`,
      body,
    );
  }

  deleteUnit(propertyId: string, roomTypeId: string, unitId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/properties/${propertyId}/room-types/${roomTypeId}/units/${unitId}`,
    );
  }

  // ── Occupancy ───────────────────────────────────────────────────────────────

  getOccupancy(propertyId: string, from: string, to: string): Observable<OccupancyData> {
    return this.http.get<OccupancyData>(`${this.baseUrl}/properties/${propertyId}/occupancy`, {
      params: this.buildParams({ from, to }),
    });
  }

  getOccupancySummary(propertyId: string, from: string, to: string): Observable<OccupancySummary> {
    return this.http.get<OccupancySummary>(
      `${this.baseUrl}/properties/${propertyId}/occupancy/summary`,
      { params: this.buildParams({ from, to }) },
    );
  }

  submitForReview(propertyId: string): Observable<Property> {
    return this.http.post<Property>(`${this.baseUrl}/properties/${propertyId}/submit`, {});
  }
}
