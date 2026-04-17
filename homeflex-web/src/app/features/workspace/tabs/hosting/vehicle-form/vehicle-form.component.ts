import { Component, DestroyRef, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass, TitleCasePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { switchMap, catchError, of } from 'rxjs';
import { VehicleApi } from '../../../../../core/api/services/vehicle.api';
import { WorkspaceStore } from '../../../workspace.store';

const TRANSMISSIONS = ['AUTOMATIC', 'MANUAL'];
const FUEL_TYPES = ['PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID'];
const CURRENCIES = ['XAF', 'USD', 'EUR'];

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, TitleCasePipe],
  templateUrl: './vehicle-form.component.html',
})
export class VehicleFormComponent implements OnInit {
  private readonly vehicleApi = inject(VehicleApi);
  private readonly store = inject(WorkspaceStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly editId = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly pendingImages = signal<File[]>([]);
  protected readonly previewUrls = signal<string[]>([]);

  protected readonly transmissions = TRANSMISSIONS;
  protected readonly fuelTypes = FUEL_TYPES;
  protected readonly currencies = CURRENCIES;

  protected readonly form = this.fb.group({
    brand: ['', Validators.required],
    model: ['', Validators.required],
    year: [
      new Date().getFullYear(),
      [Validators.required, Validators.min(1990), Validators.max(new Date().getFullYear() + 1)],
    ],
    transmission: ['AUTOMATIC', Validators.required],
    fuelType: ['PETROL', Validators.required],
    dailyPrice: [null as number | null, [Validators.required, Validators.min(1)]],
    currency: ['XAF', Validators.required],
    description: [''],
    mileage: [null as number | null],
    seats: [null as number | null, [Validators.min(1), Validators.max(20)]],
    color: [''],
    licensePlate: [''],
    pickupCity: [''],
    pickupAddress: [''],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId.set(id);
      this.loading.set(true);
      this.vehicleApi
        .getById(id)
        .pipe(
          catchError(() => of(null)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((v) => {
          this.loading.set(false);
          if (!v) {
            this.errorMessage.set('Vehicle not found.');
            return;
          }
          this.form.patchValue({
            brand: v.brand,
            model: v.model,
            year: v.year,
            transmission: v.transmission,
            fuelType: v.fuelType,
            dailyPrice: v.dailyPrice,
            currency: v.currency,
            description: v.description ?? '',
            mileage: v.mileage,
            seats: v.seats,
            color: v.color ?? '',
            licensePlate: v.licensePlate ?? '',
            pickupCity: v.pickupCity ?? '',
            pickupAddress: v.pickupAddress ?? '',
          });
        });
    }
  }

  protected onImagesSelected(event: Event): void {
    const files = Array.from((event.target as HTMLInputElement).files ?? []);
    const combined = [...this.pendingImages(), ...files].slice(0, 10);
    this.pendingImages.set(combined);
    this.previewUrls.set(combined.map((f) => URL.createObjectURL(f)));
  }

  protected removeImage(index: number): void {
    const imgs = [...this.pendingImages()];
    imgs.splice(index, 1);
    this.pendingImages.set(imgs);
    this.previewUrls.set(imgs.map((f) => URL.createObjectURL(f)));
  }

  protected submit(): void {
    if (this.form.invalid || this.submitting()) return;
    this.submitting.set(true);
    this.errorMessage.set('');

    const payload = this.buildPayload();
    const id = this.editId();
    const save$ = id ? this.vehicleApi.update(id, payload) : this.vehicleApi.create(payload);

    save$
      .pipe(
        switchMap((vehicle) => {
          if (this.pendingImages().length > 0) {
            return this.vehicleApi
              .uploadImages(vehicle.id, this.pendingImages())
              .pipe(catchError(() => of(null)));
          }
          return of(null);
        }),
        catchError((err) => {
          this.errorMessage.set(err.error?.message ?? 'Failed to save vehicle.');
          this.submitting.set(false);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        if (!this.errorMessage()) {
          this.store.refreshVehicles();
          this.router.navigate(['/workspace/hosting']);
        }
        this.submitting.set(false);
      });
  }

  protected cancel(): void {
    this.router.navigate(['/workspace/hosting']);
  }

  private buildPayload(): Record<string, unknown> {
    const v = this.form.value;
    return {
      brand: v.brand,
      model: v.model,
      year: v.year,
      transmission: v.transmission,
      fuelType: v.fuelType,
      dailyPrice: v.dailyPrice,
      currency: v.currency,
      description: v.description || null,
      mileage: v.mileage ?? null,
      seats: v.seats ?? null,
      color: v.color || null,
      licensePlate: v.licensePlate || null,
      pickupCity: v.pickupCity || null,
      pickupAddress: v.pickupAddress || null,
    };
  }

  protected isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }
}
