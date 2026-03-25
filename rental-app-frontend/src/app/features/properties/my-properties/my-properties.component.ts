import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Property } from 'src/app/models/property.model';
import { PropertyService } from 'src/app/core/services/property/property.service';
import { AuthService } from 'src/app/core/services/auth/auth.service';
import { IonicModule, AlertController, ToastController } from '@ionic/angular';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  standalone: true,
  selector: 'app-my-properties',
  imports: [CommonModule, RouterModule, IonicModule, TranslateModule],
  templateUrl: './my-properties.component.html',
  styleUrls: ['./my-properties.component.scss'],
})
export class MyPropertiesComponent implements OnInit {
  properties: Property[] = [];
  loading = false;
  isLandlord = false;
  page = 0;
  pageSize = 20;
  totalCount = 0;

  // Statistics
  totalProperties = 0;
  activeListings = 0;
  totalViews = 0;
  totalFavorites = 0;

  // Filtering
  filterType = 'all'; // all, rent, sale, short_term

  constructor(
    private propertyService: PropertyService,
    private auth: AuthService,
    public router: Router,
    private alertCtrl: AlertController,
    private toastCtrl: ToastController,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const user = this.auth.getCurrentUser();
    this.isLandlord = !!user && user.role === 'LANDLORD';

    if (!this.isLandlord) {
      this.router.navigate(['/properties']);
      return;
    }

    this.loadMyProperties();
  }

  loadMyProperties(): void {
    this.loading = true;
    this.propertyService.getMyProperties().subscribe({
      next: (list) => {
        // Backend returns the full landlord list (not paged).
        this.properties = list || [];
        this.totalCount = this.properties.length;
        this.calculateStats();
        this.loading = false;
      },
      error: (err) => {
        console.error('Load my properties error', err);
        this.loading = false;
        this.presentToast(this.translate.instant('common.error'), 'danger');
      },
    });
  }

  calculateStats(): void {
    this.totalProperties = this.properties.length;
    this.activeListings = this.properties.filter((p) => p.status !== 'INACTIVE').length;
    this.totalViews = this.properties.reduce((sum, p) => sum + (p.viewCount || 0), 0);
    this.totalFavorites = this.properties.reduce((sum, p) => sum + (p.favoriteCount || 0), 0);
  }

  getFilteredProperties(): Property[] {
    if (this.filterType === 'all') {
      return this.properties;
    }
    return this.properties.filter((p) => p.listingType?.toLowerCase() === this.filterType);
  }

  setFilter(type: string): void {
    this.filterType = type;
  }

  goToDetail(id: string): void {
    this.router.navigate(['/properties', id]);
  }

  editProperty(id: string, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/properties', id, 'edit']);
  }

  async deleteProperty(id: string, event: Event): Promise<void> {
    event.stopPropagation();

    const alert = await this.alertCtrl.create({
      header: this.translate.instant('property.deleteConfirm.title'),
      message: this.translate.instant('property.deleteConfirm.message'),
      buttons: [
        {
          text: this.translate.instant('common.cancel'),
          role: 'cancel',
        },
        {
          text: this.translate.instant('common.delete'),
          role: 'destructive',
          handler: () => {
            this.propertyService.deleteProperty(id).subscribe({
              next: () => {
                this.properties = this.properties.filter((p) => p.id !== id);
                this.calculateStats();
                this.presentToast(this.translate.instant('property.deleteSuccess'), 'success');
              },
              error: (err) => {
                console.error('Delete error', err);
                this.presentToast(this.translate.instant('property.deleteError'), 'danger');
              },
            });
          },
        },
      ],
    });

    await alert.present();
  }

  loadMore(event: any): void {
    this.page++;
    this.loadMyProperties();
    if (event) {
      event.target.complete();
    }
  }

  refreshProperties(event: any): void {
    this.page = 0;
    this.loadMyProperties();
    setTimeout(() => {
      if (event) {
        event.detail.complete();
      }
    }, 1000);
  }

  formatPrice(price?: number, currency?: string): string {
    if (!price) return '-';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'XAF',
      minimumFractionDigits: 0,
    }).format(price);
  }

  private async presentToast(message: string, color: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message,
      duration: 2500,
      color,
      position: 'bottom',
    });
    await toast.present();
  }
}
