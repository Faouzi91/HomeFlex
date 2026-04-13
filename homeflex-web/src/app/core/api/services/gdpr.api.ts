import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class GdprApi extends BaseApi {
  exportData(): Observable<Record<string, unknown>> {
    return this.http.get<Record<string, unknown>>(`${this.baseUrl}/gdpr/export`);
  }

  eraseData(): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/gdpr/erase`);
  }
}
