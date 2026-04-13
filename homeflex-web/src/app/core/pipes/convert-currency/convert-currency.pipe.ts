import { Pipe, PipeTransform, inject, Injectable, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { CurrencyApi } from '../../api/services/currency.api';

@Pipe({
  name: 'convertCurrency',
  standalone: true,
})
@Injectable({ providedIn: 'root' })
export class ConvertCurrencyPipe implements PipeTransform {
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly currencyApi = inject(CurrencyApi);

  private readonly rates = signal<Record<string, number>>({});
  private loaded = false;

  constructor() {
    this.loadRates();
  }

  private loadRates(): void {
    if (this.loaded) return;
    this.loaded = true;

    this.currencyApi.getRates().subscribe({
      next: (rates) => this.rates.set(rates),
      error: () => {
        // Fallback rates if API is unreachable
        this.rates.set({
          USD: 1,
          EUR: 0.92,
          GBP: 0.79,
          XAF: 605,
          AED: 3.67,
          SAR: 3.75,
        });
      },
    });
  }

  transform(
    value: number | undefined | null,
    sourceCurrency: string,
    targetCurrency: string,
  ): string {
    if (value == null) return '';

    const source = sourceCurrency?.toUpperCase() || 'XAF';
    const target = targetCurrency?.toUpperCase() || 'XAF';

    if (source === target) {
      return this.currencyPipe.transform(value, source, 'symbol', '1.0-0') || '';
    }

    const rateMap = this.rates();
    const fromRate = rateMap[source];
    const toRate = rateMap[target];

    // If rates not loaded yet, show source currency formatted
    if (!fromRate || !toRate) {
      return this.currencyPipe.transform(value, source, 'symbol', '1.0-0') || '';
    }

    // Convert: source -> USD (base) -> target
    const valueInBase = value / fromRate;
    const finalValue = valueInBase * toRate;

    return this.currencyPipe.transform(finalValue, target, 'symbol', '1.0-0') || '';
  }
}
