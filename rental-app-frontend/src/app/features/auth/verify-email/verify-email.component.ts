import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';
import { environment } from 'src/app/environments/environment';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [IonicModule, CommonModule, TranslateModule],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss'],
})
export class VerifyEmailComponent implements OnInit {
  status: 'loading' | 'success' | 'error' = 'loading';
  message = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.status = 'error';
      this.message = 'No verification token provided.';
      return;
    }

    this.http
      .get<{ value: string }>(`${environment.apiUrl}/auth/verify`, {
        params: { token },
      })
      .subscribe({
        next: (res) => {
          this.status = 'success';
          this.message = res.value || 'Email verified successfully!';
        },
        error: (err) => {
          this.status = 'error';
          this.message = err?.error?.message || 'Verification failed. The link may have expired.';
        },
      });
  }

  goToLogin(): void {
    this.router.navigate(['/auth/login']);
  }
}
