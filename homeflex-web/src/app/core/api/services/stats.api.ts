import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiValueResponse } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class StatsApi extends BaseApi {
  get(): Observable<ApiValueResponse<Record<string, number>>> {
    return this.http.get<ApiValueResponse<Record<string, number>>>(`${this.baseUrl}/stats`);
  }
}
