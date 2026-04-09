import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

type FooterLinkGroup = {
  title: string;
  links: Array<{ label: string; href: string }>;
};

@Component({
  selector: 'app-footer',
  imports: [RouterLink],
  templateUrl: './app-footer.component.html',
  styleUrl: './app-footer.component.scss',
})
export class AppFooterComponent {
  protected readonly year = new Date().getFullYear();
  protected readonly groups: FooterLinkGroup[] = [
    {
      title: 'Explore',
      links: [
        { label: 'Landing', href: '/' },
        { label: 'Properties', href: '/properties' },
        { label: 'Vehicles', href: '/vehicles' },
      ],
    },
    {
      title: 'Account',
      links: [
        { label: 'Workspace', href: '/workspace' },
        { label: 'Sign in', href: '/login' },
        { label: 'Create account', href: '/register' },
      ],
    },
  ];
}
