import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Agency } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class AgencyApi extends BaseApi {
  getAll(): Observable<Agency[]> {
    return this.http.get<Agency[]>(`${this.baseUrl}/agencies`);
  }

  getById(id: string): Observable<Agency> {
    return this.http.get<Agency>(`${this.baseUrl}/agencies/${id}`);
  }

  verify(id: string): Observable<Agency> {
    return this.http.patch<Agency>(`${this.baseUrl}/agencies/${id}/verify`, {});
  }
}
