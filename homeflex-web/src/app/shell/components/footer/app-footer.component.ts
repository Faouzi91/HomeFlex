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
        { label: 'Home', href: '/' },
        { label: 'Properties', href: '/properties' },
        { label: 'Vehicles', href: '/vehicles' },
        { label: 'Support', href: '/support' },
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
    {
      title: 'Hosting',
      links: [
        { label: 'List your property', href: '/workspace' },
        { label: 'List your vehicle', href: '/workspace' },
        { label: 'Host dashboard', href: '/workspace' },
      ],
    },
  ];
}
