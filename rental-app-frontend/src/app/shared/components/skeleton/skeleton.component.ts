import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  imports: [CommonModule, IonicModule],
  template: `
    <div
      class="skeleton-loader"
      [style.width]="width"
      [style.height]="height"
      [style.borderRadius]="borderRadius"
      [class.animated]="animated"
      [class.circle]="type === 'circle'"
      [class.text]="type === 'text'"
    ></div>
  `,
  styles: [
    `
      .skeleton-loader {
        background-color: #e2e8f0;
        width: 100%;
        height: 100%;
        display: inline-block;
        position: relative;
        overflow: hidden;

        &.animated::after {
          position: absolute;
          top: 0;
          right: 0;
          bottom: 0;
          left: 0;
          transform: translateX(-100%);
          background-image: linear-gradient(
            90deg,
            rgba(255, 255, 255, 0) 0,
            rgba(255, 255, 255, 0.4) 20%,
            rgba(255, 255, 255, 0.7) 60%,
            rgba(255, 255, 255, 0)
          );
          animation: shimmer 1.5s infinite;
          content: '';
        }

        &.circle {
          border-radius: 50% !important;
        }

        &.text {
          height: 1em;
          border-radius: 4px;
          margin-bottom: 0.5em;
        }
      }

      @keyframes shimmer {
        100% {
          transform: translateX(100%);
        }
      }
    `,
  ],
})
export class SkeletonComponent {
  @Input() width = '100%';
  @Input() height = '20px';
  @Input() borderRadius = '4px';
  @Input() animated = true;
  @Input() type: 'rect' | 'circle' | 'text' = 'rect';
}
