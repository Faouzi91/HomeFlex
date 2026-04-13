import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiValueResponse } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class KycApi extends BaseApi {
  getStatus(): Observable<
    ApiValueResponse<{
      status: string;
      rejectionReason?: string;
      verifiedAt?: string;
      submittedAt?: string;
    }>
  > {
    return this.http.get<ApiValueResponse<any>>(`${this.baseUrl}/kyc/status`);
  }

  createSession(): Observable<{
    sessionId: string;
    clientSecret: string;
    publishableKey: string;
  }> {
    return this.http.post<any>(`${this.baseUrl}/kyc/session`, {});
  }
}
