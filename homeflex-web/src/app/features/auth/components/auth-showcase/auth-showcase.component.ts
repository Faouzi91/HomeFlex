import { Component, input } from '@angular/core';

@Component({
  selector: 'app-auth-showcase',
  templateUrl: './auth-showcase.component.html',
  styleUrl: './auth-showcase.component.scss',
})
export class AuthShowcaseComponent {
  readonly title = input('Where every move starts with trust.');
  readonly description = input(
    'Discover verified homes, premium vehicles, and a single account experience that feels clear from the first click.',
  );
}
