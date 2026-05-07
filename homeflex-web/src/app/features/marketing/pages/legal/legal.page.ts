import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-legal-page',
  imports: [RouterLink],
  templateUrl: './legal.page.html',
})
export class LegalPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  protected activeSection = 'privacy';

  constructor() {
    this.route.fragment.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((f) => {
      if (f === 'terms' || f === 'cookies' || f === 'privacy') {
        this.activeSection = f;
        setTimeout(() => {
          document.getElementById(f)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 50);
      }
    });
  }
}
