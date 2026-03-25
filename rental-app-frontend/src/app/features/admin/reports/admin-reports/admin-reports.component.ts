import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { IonicModule, ToastController } from '@ionic/angular';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AdminService, Report } from 'src/app/core/services/admin/admin.service';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [IonicModule, CommonModule, TranslateModule],
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.scss'],
})
export class AdminReportsComponent implements OnInit {
  reports: Report[] = [];
  loading = false;
  page = 0;

  constructor(
    private adminService: AdminService,
    private toastCtrl: ToastController,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.loadReports();
  }

  loadReports(event?: any) {
    if (this.page === 0) {
      this.loading = true;
    }

    this.adminService.getReports(this.page).subscribe({
      next: (res) => {
        if (this.page === 0) {
          this.reports = res.data;
        } else {
          this.reports = [...this.reports, ...res.data];
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

  resolveReport(report: Report) {
    this.adminService.resolveReport(report.id).subscribe({
      next: () => {
        report.status = 'RESOLVED';
        this.showToast(this.translate.instant('admin.reports.resolveSuccess'));
      },
      error: () => {
        this.showToast(this.translate.instant('admin.reports.resolveError'), 'danger');
      },
    });
  }

  loadMore(event: any) {
    this.page++;
    this.loadReports(event);
  }

  handleRefresh(event: any) {
    this.page = 0;
    this.loadReports(event);
  }

  getStatusColor(status: string): string {
    const colors: any = {
      PENDING: 'warning',
      RESOLVED: 'success',
      DISMISSED: 'medium',
    };
    return colors[status] || 'medium';
  }

  getTypeIcon(type: string): string {
    const icons: any = {
      SPAM: 'alert-circle',
      INAPPROPRIATE: 'warning',
      FRAUD: 'shield-half',
      OTHER: 'help-circle',
    };
    return icons[type] || 'flag';
  }

  async showToast(msg: string, color: string = 'success') {
    const toast = await this.toastCtrl.create({
      message: msg,
      duration: 2000,
      color,
      position: 'bottom',
    });
    toast.present();
  }
}
