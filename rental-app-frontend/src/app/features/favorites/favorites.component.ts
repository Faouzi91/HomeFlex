import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [IonicModule, CommonModule, TranslateModule],
  template: `
    <ion-content class="ion-padding">
      <div class="flex flex-col items-center justify-center h-full">
        <ion-icon name="heart-outline" class="text-6xl text-gray-400 mb-4"></ion-icon>
        <h2 class="text-xl font-semibold text-gray-600">{{ 'favorites.title' | translate }}</h2>
        <p class="text-gray-400">{{ 'favorites.empty' | translate }}</p>
      </div>
    </ion-content>
  `,
})
export class FavoritesComponent {}
