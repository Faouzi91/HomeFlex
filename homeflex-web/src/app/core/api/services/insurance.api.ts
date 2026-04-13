import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InsurancePlan, InsurancePolicy } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class InsuranceApi extends BaseApi {
  getPlans(type: 'TENANT' | 'LANDLORD' | 'VEHICLE' = 'TENANT'): Observable<InsurancePlan[]> {
    return this.http.get<InsurancePlan[]>(`${this.baseUrl}/insurance/plans`, {
      params: this.buildParams({ type }),
    });
  }

  purchase(planId: string, bookingId: string): Observable<InsurancePolicy> {
    return this.http.post<InsurancePolicy>(`${this.baseUrl}/insurance/purchase`, null, {
      params: this.buildParams({ planId, bookingId }),
    });
  }
}
