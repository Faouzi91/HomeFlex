import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertController, ToastController, IonicModule } from '@ionic/angular';
import { AdminService } from 'src/app/core/services/admin/admin.service';
import { User } from 'src/app/models/user.model';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule, TranslateModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss'],
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  page = 0;
  loading = false;
  searchTerm = '';

  constructor(
    private adminService: AdminService,
    private toastCtrl: ToastController,
    private alertCtrl: AlertController,
    private translate: TranslateService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers(event?: any) {
    if (this.page === 0) {
      this.loading = true;
    }

    this.adminService.getUsers(this.page).subscribe({
      next: (res) => {
        if (this.page === 0) {
          this.users = res.data;
        } else {
          this.users = [...this.users, ...res.data];
        }
        this.loading = false;
        if (event) event.target.complete();
      },
      error: () => {
        this.loading = false;
        if (event) event.target.complete();
      },
    });
  }

  loadMore(event: any) {
    this.page++;
    this.loadUsers(event);
  }

  async toggleStatus(user: User) {
    const action = user.isActive ? 'suspend' : 'activate';
    const alert = await this.alertCtrl.create({
      header: this.translate.instant(`admin.users.${action}Confirm.title`),
      message: this.translate.instant(`admin.users.${action}Confirm.message`, {
        name: `${user.firstName} ${user.lastName}`,
      }),
      buttons: [
        {
          text: this.translate.instant('common.cancel'),
          role: 'cancel',
        },
        {
          text: this.translate.instant(`admin.users.${action}`),
          cssClass: user.isActive ? 'alert-button-danger' : 'alert-button-success',
          handler: () => {
            if (user.isActive) {
              this.suspendUser(user);
            } else {
              this.activateUser(user);
            }
          },
        },
      ],
    });

    await alert.present();
  }

  private suspendUser(user: User) {
    this.adminService.suspendUser(user.id).subscribe({
      next: () => {
        user.isActive = false;
        this.showToast(this.translate.instant('admin.users.suspendSuccess'));
      },
      error: () => {
        this.showToast(this.translate.instant('admin.users.suspendError'), 'danger');
      },
    });
  }

  private activateUser(user: User) {
    this.adminService.activateUser(user.id).subscribe({
      next: () => {
        user.isActive = true;
        this.showToast(this.translate.instant('admin.users.activateSuccess'));
      },
      error: () => {
        this.showToast(this.translate.instant('admin.users.activateError'), 'danger');
      },
    });
  }

  handleRefresh(event: any) {
    this.page = 0;
    this.loadUsers(event);
  }

  getAvatarUrl(user: User): string {
    return (
      user.avatarUrl ||
      user.profilePictureUrl ||
      `https://ui-avatars.com/api/?name=${user.firstName}+${user.lastName}&background=random`
    );
  }

  getRoleBadgeColor(role: string): string {
    const colors: any = {
      ADMIN: 'danger',
      LANDLORD: 'primary',
      TENANT: 'secondary',
    };
    return colors[role] || 'medium';
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
