import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Receipt } from '../../models/api.types';
import { BaseApi } from './base.api';

@Injectable({ providedIn: 'root' })
export class FinanceApi extends BaseApi {
  getMyReceipts(): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.baseUrl}/finance/receipts`);
  }
}
