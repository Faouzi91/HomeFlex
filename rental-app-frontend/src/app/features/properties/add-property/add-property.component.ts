import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PropertyService } from 'src/app/core/services/property/property.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { IonicModule, ToastController } from '@ionic/angular';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  standalone: true,
  selector: 'app-add-property',
  imports: [CommonModule, ReactiveFormsModule, IonicModule, TranslateModule],
  templateUrl: './add-property.component.html',
  styleUrls: ['./add-property.component.scss'],
})
export class AddPropertyComponent implements OnInit {
  form!: FormGroup;
  loading = false;
  currentStep = 1;
  totalSteps = 3;

  propertyTypes = ['APARTMENT', 'HOUSE', 'STUDIO', 'VILLA', 'ROOM', 'OFFICE', 'LAND'];
  listingTypes = ['RENT', 'SALE', 'SHORT_TERM'];
  amenities = ['WiFi', 'Parking', 'Pool', 'Gym', 'AC', 'Heating', 'Balcony', 'Garden'];
  selectedAmenities: string[] = [];

  selectedFiles: File[] = [];
  filePreviews: string[] = [];

  constructor(
    private fb: FormBuilder,
    private propertyService: PropertyService,
    private auth: AuthService,
    public router: Router,
    private toastCtrl: ToastController,
    private translate: TranslateService
  ) {
    this.initializeForm();
    this.totalSteps = 4;
  }

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    if (!user || user.role !== 'LANDLORD') {
      this.router.navigate(['/properties']);
      return;
    }
  }

  initializeForm(): void {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      propertyType: ['APARTMENT', Validators.required],
      listingType: ['RENT', Validators.required],
      price: [0, [Validators.required, Validators.min(1)]],
      currency: ['XAF', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      bedrooms: [1, [Validators.required, Validators.min(0)]],
      bathrooms: [1, [Validators.required, Validators.min(0)]],
      areaSqm: [0, [Validators.required, Validators.min(1)]],
      amenities: [[]],
    });
  }

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      const files = Array.from(event.target.files) as File[];
      this.selectedFiles = [...this.selectedFiles, ...files];

      // Generate previews
      files.forEach((file) => {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.filePreviews.push(e.target.result);
        };
        reader.readAsDataURL(file);
      });
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.filePreviews.splice(index, 1);
  }

  toggleAmenity(amenity: string): void {
    const idx = this.selectedAmenities.indexOf(amenity);
    if (idx > -1) {
      this.selectedAmenities.splice(idx, 1);
    } else {
      this.selectedAmenities.push(amenity);
    }
    this.form.patchValue({ amenities: this.selectedAmenities });
  }

  isAmenitySelected(amenity: string): boolean {
    return this.selectedAmenities.includes(amenity);
  }

  nextStep(): void {
    if (this.currentStep < 4 && this.isStepValid()) {
      this.currentStep++;
    } else if (!this.isStepValid()) {
      this.presentToast(this.translate.instant('property.fillRequired'), 'warning');
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  isStepValid(): boolean {
    const fields = this.getStepFields();
    if (this.currentStep === 4) return true; // Images are optional or handled separately
    return fields.every((field) => this.form.get(field)?.valid);
  }

  getStepFields(): string[] {
    if (this.currentStep === 1) {
      return ['title', 'description', 'propertyType', 'listingType'];
    } else if (this.currentStep === 2) {
      return ['price', 'currency', 'address', 'city'];
    } else if (this.currentStep === 3) {
      return ['bedrooms', 'bathrooms', 'areaSqm'];
    }
    return [];
  }

  submit(): void {
    if (this.form.invalid) {
      this.presentToast(this.translate.instant('property.fillRequired'), 'warning');
      return;
    }

    this.loading = true;
    const payload = {
      ...this.form.value,
      amenities: this.selectedAmenities,
    };

    this.propertyService.createProperty(payload as any).subscribe({
      next: async (createdProperty) => {
        if (this.selectedFiles.length > 0) {
          await this.uploadImages(createdProperty.id);
        }
        this.loading = false;
        this.presentToast(this.translate.instant('property.createSuccess'), 'success');
        setTimeout(() => this.router.navigate(['/properties/my']), 1500);
      },
      error: (err) => {
        console.error('Create property error', err);
        this.loading = false;
        this.presentToast(this.translate.instant('property.createError'), 'danger');
      },
    });
  }

  private async uploadImages(propertyId: string): Promise<void> {
    for (const file of this.selectedFiles) {
      try {
        await this.propertyService.uploadImage(propertyId, file).toPromise();
      } catch (e) {
        console.error('Error uploading image', file.name, e);
      }
    }
  }

  cancel(): void {
    this.router.navigate(['/properties']);
  }

  private async presentToast(message: string, color: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message,
      duration: 3000,
      color,
      position: 'bottom',
    });
    await toast.present();
  }
}
