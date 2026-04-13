import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-support-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './support.page.html',
  styleUrl: './support.page.scss',
})
export class SupportPageComponent {
  // FAQs
  protected readonly faqs = [
    {
      category: 'Manage Bookings',
      items: [
        { question: 'How do I cancel my booking?', answer: 'Navigate to your Workspace, select the booking, and click Cancel Booking.' },
        { question: 'Can I change my dates?', answer: 'Yes, select the booking in your Workspace and Request Modification to send new dates to the host.' },
      ]
    },
    {
      category: 'Payments & Refunds',
      items: [
        { question: 'When will I be charged?', answer: 'You are charged immediately upon confirming the booking. For longer terms, you are billed monthly.' },
        { question: 'How long do refunds take?', answer: 'Refunds take 3-5 business days to appear on your original payment method.' },
      ]
    },
    {
      category: 'Trust & Safety',
      items: [
        { question: 'How are hosts verified?', answer: 'We require KYC verification for all hosts before their listings become active.' },
        { question: 'What if a property is not as listed?', answer: 'You can dispute a booking inside the Workspace or report the listing directly from the property page.' },
      ]
    }
  ];
}
