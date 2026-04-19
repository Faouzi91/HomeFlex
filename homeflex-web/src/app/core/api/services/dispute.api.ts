import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dispute, DisputeEvidence } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class DisputeApi extends BaseApi {
  open(bookingId: string, reason: string, description: string): Observable<Dispute> {
    return this.http.post<Dispute>(`${this.baseUrl}/disputes`, null, {
      params: this.buildParams({ bookingId, reason, description }),
    });
  }

  getMine(): Observable<Dispute[]> {
    return this.http.get<Dispute[]>(`${this.baseUrl}/disputes/mine`);
  }

  getAll(): Observable<Dispute[]> {
    return this.http.get<Dispute[]>(`${this.baseUrl}/disputes`);
  }

  resolve(id: string, resolutionNotes: string): Observable<Dispute> {
    return this.http.patch<Dispute>(`${this.baseUrl}/disputes/${id}/resolve`, null, {
      params: this.buildParams({ resolutionNotes }),
    });
  }

  uploadEvidence(id: string, file: File, description?: string): Observable<DisputeEvidence> {
    const formData = new FormData();
    formData.append('file', file);
    if (description) {
      formData.append('description', description);
    }
    return this.http.post<DisputeEvidence>(`${this.baseUrl}/disputes/${id}/evidence`, formData);
  }

  getEvidence(id: string): Observable<DisputeEvidence[]> {
    return this.http.get<DisputeEvidence[]>(`${this.baseUrl}/disputes/${id}/evidence`);
  }
}
