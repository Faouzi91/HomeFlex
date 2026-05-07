import { Component, DestroyRef, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass, TitleCasePipe } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { switchMap, catchError, of } from 'rxjs';
import { PropertyApi } from '../../../../../core/api/services/property.api';
import { WorkspaceStore } from '../../../workspace.store';

const PROPERTY_TYPES = [
  'APARTMENT',
  'HOUSE',
  'STUDIO',
  'VILLA',
  'ROOM',
  'OFFICE',
  'LAND',
  'HOTEL',
  'GUESTHOUSE',
  'HOSTEL',
  'RESORT',
];
const LISTING_TYPES = ['RENT', 'SALE', 'SHORT_TERM', 'NIGHTLY'];
const CURRENCIES = ['XAF', 'USD', 'EUR'];
const CANCELLATION_POLICIES = ['FLEXIBLE', 'MODERATE', 'STRICT', 'NON_REFUNDABLE'];

@Component({
  selector: 'app-property-form',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, TitleCasePipe],
  templateUrl: './property-form.component.html',
})
export class PropertyFormComponent implements OnInit {
  private readonly propertyApi = inject(PropertyApi);
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

  protected readonly propertyTypes = PROPERTY_TYPES;
  protected readonly listingTypes = LISTING_TYPES;
  protected readonly currencies = CURRENCIES;
  protected readonly cancellationPolicies = CANCELLATION_POLICIES;

  protected readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(5)]],
    description: ['', [Validators.required, Validators.minLength(20)]],
    propertyType: ['APARTMENT', Validators.required],
    listingType: ['RENT', Validators.required],
    price: [null as number | null, [Validators.required, Validators.min(1)]],
    currency: ['XAF', Validators.required],
    address: ['', Validators.required],
    city: ['', Validators.required],
    stateProvince: [''],
    country: ['', Validators.required],
    postalCode: [''],
    bedrooms: [null as number | null],
    bathrooms: [null as number | null],
    areaSqm: [null as number | null],
    floorNumber: [null as number | null],
    totalFloors: [null as number | null],
    availableFrom: [''],
    // Booking & policy fields (PropertyCreateRequest v5)
    cancellationPolicy: ['FLEXIBLE'],
    cleaningFee: [null as number | null, [Validators.min(0)]],
    securityDeposit: [null as number | null, [Validators.min(0)]],
    instantBookEnabled: [false],
    checkInTime: [''],
    checkOutTime: [''],
    starRating: [null as number | null, [Validators.min(1), Validators.max(5)]],
    petsAllowed: [false],
    smokingAllowed: [false],
    childrenAllowed: [true],
    minStayNights: [1, [Validators.min(1)]],
    maxStayNights: [null as number | null],
    houseRules: [''],
    submitAsDraft: [false],
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editId.set(id);
      this.loading.set(true);
      this.propertyApi
        .getById(id)
        .pipe(
          catchError(() => of(null)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((p) => {
          this.loading.set(false);
          if (!p) {
            this.errorMessage.set('Property not found.');
            return;
          }
          this.form.patchValue({
            title: p.title,
            description: p.description,
            propertyType: p.propertyType,
            listingType: p.listingType,
            price: p.price,
            currency: p.currency,
            address: p.address,
            city: p.city,
            stateProvince: p.stateProvince ?? '',
            country: p.country,
            postalCode: p.postalCode ?? '',
            bedrooms: p.bedrooms,
            bathrooms: p.bathrooms,
            areaSqm: p.areaSqm,
            floorNumber: p.floorNumber,
            totalFloors: p.totalFloors,
            availableFrom: p.availableFrom ?? '',
            cancellationPolicy: p.cancellationPolicy ?? 'FLEXIBLE',
            cleaningFee: p.cleaningFee ?? null,
            securityDeposit: p.securityDeposit ?? null,
            instantBookEnabled: p.instantBookEnabled ?? false,
            checkInTime: p.checkInTime ?? '',
            checkOutTime: p.checkOutTime ?? '',
            starRating: p.starRating ?? null,
            petsAllowed: p.petsAllowed ?? false,
            smokingAllowed: p.smokingAllowed ?? false,
            childrenAllowed: p.childrenAllowed ?? true,
            minStayNights: p.minStayNights ?? 1,
            maxStayNights: p.maxStayNights ?? null,
            houseRules: p.houseRules ?? '',
          });
        });
    }
  }

  protected onImagesSelected(event: Event): void {
    const files = Array.from((event.target as HTMLInputElement).files ?? []);
    const combined = [...this.pendingImages(), ...files].slice(0, 10);
    this.pendingImages.set(combined);
    const urls = combined.map((f) => URL.createObjectURL(f));
    this.previewUrls.set(urls);
  }

  protected removeImage(index: number): void {
    const imgs = [...this.pendingImages()];
    imgs.splice(index, 1);
    this.pendingImages.set(imgs);
    const urls = imgs.map((f) => URL.createObjectURL(f));
    this.previewUrls.set(urls);
  }

  protected submit(): void {
    if (this.form.invalid || this.submitting()) return;
    this.submitting.set(true);
    this.errorMessage.set('');

    const payload = this.buildPayload();
    const id = this.editId();
    const save$ = id ? this.propertyApi.update(id, payload) : this.propertyApi.create(payload);

    save$
      .pipe(
        switchMap((property) => {
          if (this.pendingImages().length > 0) {
            return this.propertyApi
              .uploadImages(property.id, this.pendingImages())
              .pipe(catchError(() => of(null)));
          }
          return of(null);
        }),
        catchError((err) => {
          this.errorMessage.set(err.error?.message ?? 'Failed to save property.');
          this.submitting.set(false);
          return of(null);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((result) => {
        if (result !== null || !this.errorMessage()) {
          this.store.refreshProperties();
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
      title: v.title,
      description: v.description,
      propertyType: v.propertyType,
      listingType: v.listingType,
      price: v.price,
      currency: v.currency,
      address: v.address,
      city: v.city,
      stateProvince: v.stateProvince || null,
      country: v.country,
      postalCode: v.postalCode || null,
      bedrooms: v.bedrooms ?? null,
      bathrooms: v.bathrooms ?? null,
      areaSqm: v.areaSqm ?? null,
      floorNumber: v.floorNumber ?? null,
      totalFloors: v.totalFloors ?? null,
      availableFrom: v.availableFrom || null,
      cancellationPolicy: v.cancellationPolicy || 'FLEXIBLE',
      cleaningFee: v.cleaningFee ?? 0,
      securityDeposit: v.securityDeposit ?? 0,
      instantBookEnabled: v.instantBookEnabled ?? false,
      checkInTime: v.checkInTime || null,
      checkOutTime: v.checkOutTime || null,
      starRating: v.starRating ?? null,
      petsAllowed: v.petsAllowed ?? false,
      smokingAllowed: v.smokingAllowed ?? false,
      childrenAllowed: v.childrenAllowed ?? true,
      minStayNights: v.minStayNights ?? 1,
      maxStayNights: v.maxStayNights ?? null,
      houseRules: v.houseRules || null,
      submitAsDraft: v.submitAsDraft ?? false,
    };
  }

  protected isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }
}
