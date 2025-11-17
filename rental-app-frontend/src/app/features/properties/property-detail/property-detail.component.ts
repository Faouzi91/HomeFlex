import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertController, ModalController } from "@ionic/angular";
import { TranslateService } from "@ngx-translate/core";
import { AuthService } from "src/app/core/services/auth/auth.service";
import { ChatService } from "src/app/core/services/chat/chat.service";
import { FavoriteService } from "src/app/core/services/favorite/favorite.service";
import { PropertyService } from "src/app/core/services/property/property.service";
import { Property } from "src/app/models/property.model";

@Component({
  selector: "app-property-detail",
  standalone: true,
  imports: [],
  templateUrl: "./property-detail.component.html",
  styleUrl: "./property-detail.component.scss",
})
export class PropertyDetailComponent implements OnInit {
  property?: Property;
  loading = false;
  isFavorite = false;
  currentImageIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private favoriteService: FavoriteService,
    private chatService: ChatService,
    private authService: AuthService,
    private alertController: AlertController,
    private modalController: ModalController,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    const propertyId = this.route.snapshot.paramMap.get("id");
    if (propertyId) {
      this.loadProperty(propertyId);
      this.checkFavorite(propertyId);
      this.incrementViewCount(propertyId);
    }
  }

  loadProperty(id: string): void {
    this.loading = true;
    this.propertyService.getPropertyById(id).subscribe({
      next: (property) => {
        this.property = property;
        this.loading = false;
      },
      error: (error) => {
        console.error("Error loading property:", error);
        this.loading = false;
      },
    });
  }

  checkFavorite(propertyId: string): void {
    if (!this.authService.isAuthenticated()) return;

    this.favoriteService.isFavorite(propertyId).subscribe({
      next: (isFav) => (this.isFavorite = isFav),
      error: (error) => console.error("Error checking favorite:", error),
    });
  }

  incrementViewCount(propertyId: string): void {
    this.propertyService.incrementViewCount(propertyId).subscribe();
  }

  toggleFavorite(): void {
    if (!this.property) return;

    if (this.isFavorite) {
      this.favoriteService.removeFromFavorites(this.property.id).subscribe({
        next: () => (this.isFavorite = false),
      });
    } else {
      this.favoriteService.addToFavorites(this.property.id).subscribe({
        next: () => (this.isFavorite = true),
      });
    }
  }

  async contactLandlord(): Promise<void> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(["/auth/login"]);
      return;
    }

    if (!this.property) return;

    // Create or get chat room
    this.chatService
      .createOrGetChatRoom({
        propertyId: this.property.id,
        tenantId: this.authService.getCurrentUser()!.id,
        landlordId: this.property.landlord.id,
      })
      .subscribe({
        next: (chatRoom) => {
          this.router.navigate(["/chat", chatRoom.id]);
        },
        error: (error) => {
          console.error("Error creating chat room:", error);
        },
      });
  }

  async requestViewing(): Promise<void> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(["/auth/login"]);
      return;
    }

    const alert = await this.alertController.create({
      header: this.translate.instant("property.requestViewing"),
      inputs: [
        {
          name: "message",
          type: "textarea",
          placeholder: "Add a message (optional)...",
        },
        {
          name: "date",
          type: "date",
          min: new Date().toISOString().split("T")[0],
        },
      ],
      buttons: [
        {
          text: this.translate.instant("common.cancel"),
          role: "cancel",
        },
        {
          text: this.translate.instant("common.submit"),
          handler: (data) => {
            this.submitBookingRequest(data);
          },
        },
      ],
    });

    await alert.present();
  }

  submitBookingRequest(data: any): void {
    // Implement booking request
    console.log("Booking request:", data);
  }

  shareProperty(): void {
    if (navigator.share && this.property) {
      navigator.share({
        title: this.property.title,
        text: this.property.description,
        url: window.location.href,
      });
    }
  }

  previousImage(): void {
    if (!this.property || this.property.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex - 1 + this.property.images.length) %
      this.property.images.length;
  }

  nextImage(): void {
    if (!this.property || this.property.images.length === 0) return;
    this.currentImageIndex =
      (this.currentImageIndex + 1) % this.property.images.length;
  }
}
