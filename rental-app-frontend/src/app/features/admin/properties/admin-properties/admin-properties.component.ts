import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertController, ToastController, IonicModule } from '@ionic/angular';
import { AdminService } from 'src/app/core/services/admin/admin.service';
import { Property } from 'src/app/models/property.model';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-properties',
  standalone: true,
  imports: [IonicModule, CommonModule, TranslateModule],
  templateUrl: './admin-properties.component.html',
  styleUrls: ['./admin-properties.component.scss'],
})
export class AdminPropertiesComponent implements OnInit {
  pendingProperties: Property[] = [];
  loading = false;
  page = 0;

  constructor(
    private adminService: AdminService,
    private toastCtrl: ToastController,
    private alertCtrl: AlertController,
    private router: Router,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.loadPending();
  }

  loadPending(event?: any) {
    if (this.page === 0) {
      this.loading = true;
    }

    this.adminService.getPendingProperties(this.page).subscribe({
      next: (res) => {
        if (this.page === 0) {
          this.pendingProperties = res.data;
        } else {
          this.pendingProperties = [...this.pendingProperties, ...res.data];
        }
        this.loading = false;
        if (event) event.target.complete();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        if (event) event.target.complete();
      },
    });
  }

  async approve(property: Property) {
    const alert = await this.alertCtrl.create({
      header: this.translate.instant('admin.properties.approveConfirm.title'),
      message: this.translate.instant('admin.properties.approveConfirm.message', {
        title: property.title,
      }),
      buttons: [
        {
          text: this.translate.instant('common.cancel'),
          role: 'cancel',
        },
        {
          text: this.translate.instant('admin.properties.approve'),
          handler: () => {
            this.adminService.approveProperty(property.id).subscribe({
              next: () => {
                this.presentToast(
                  this.translate.instant('admin.properties.approveSuccess'),
                  'success'
                );
                this.loadPending();
              },
              error: () =>
                this.presentToast(
                  this.translate.instant('admin.properties.approveError'),
                  'danger'
                ),
            });
          },
        },
      ],
    });

    await alert.present();
  }

  async reject(property: Property) {
    const alert = await this.alertCtrl.create({
      header: this.translate.instant('admin.properties.rejectConfirm.title'),
      message: this.translate.instant('admin.properties.rejectConfirm.message'),
      inputs: [
        {
          name: 'reason',
          type: 'textarea',
          placeholder: this.translate.instant('admin.properties.rejectReason'),
        },
      ],
      buttons: [
        {
          text: this.translate.instant('common.cancel'),
          role: 'cancel',
        },
        {
          text: this.translate.instant('admin.properties.reject'),
          handler: (data) => {
            const reason =
              data.reason || this.translate.instant('admin.properties.defaultRejectReason');
            this.adminService.rejectProperty(property.id, reason).subscribe({
              next: () => {
                this.presentToast(
                  this.translate.instant('admin.properties.rejectSuccess'),
                  'medium'
                );
                this.loadPending();
              },
              error: () =>
                this.presentToast(this.translate.instant('admin.properties.rejectError'), 'danger'),
            });
          },
        },
      ],
    });

    await alert.present();
  }

  viewDetails(id: string) {
    this.router.navigate(['/properties', id]);
  }

  loadMore(event: any) {
    this.page++;
    this.loadPending(event);
  }

  async presentToast(message: string, color: string) {
    const toast = await this.toastCtrl.create({
      message,
      duration: 2000,
      color,
      position: 'bottom',
    });
    toast.present();
  }

  formatPrice(price: number, currency: string): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'XAF',
      minimumFractionDigits: 0,
    }).format(price);
  }

  getPrimaryImage(property: Property): string {
    const primary = property.images?.find((img) => img.isPrimary);
    return (
      primary?.imageUrl ||
      property.images?.[0]?.imageUrl ||
      '/assets/images/placeholder-property.jpg'
    );
  }
}
