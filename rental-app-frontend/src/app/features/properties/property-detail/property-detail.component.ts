import {
  Component,
  CUSTOM_ELEMENTS_SCHEMA,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { PropertyService } from "src/app/core/services/property/property.service";
import { Property } from "src/app/models/property.model";
import { FavoriteService } from "src/app/core/services/favorite/favorite.service";
import { BookingService } from "src/app/core/services/booking/booking.service";
import { ChatService } from "src/app/core/services/chat/chat.service";
import { AuthService } from "src/app/core/services/auth/auth.service";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { Subscription } from "rxjs";
import { TranslateModule, TranslateService } from "@ngx-translate/core";
import { ReviewService } from "src/app/core/services/review/review.service";
import { IonicModule, IonicSlides } from "@ionic/angular";
import { PropertyCardComponent } from "../property-card/property-card.component";
import { CommonModule } from "@angular/common";
// import { SwiperModule } from "swiper/angular";

@Component({
  selector: "app-property-detail",
  templateUrl: "./property-detail.component.html",
  styleUrls: ["./property-detail.component.scss"],
  standalone: true,
  imports: [
    IonicModule,
    ReactiveFormsModule,
    TranslateModule,
    PropertyCardComponent,
    // SwiperModule,
    CommonModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class PropertyDetailComponent implements OnInit, OnDestroy {
  property?: Property;
  id!: string;
  gallery: string[] = [];
  reviews: any[] = [];
  averageRating: number = 0;
  similarProperties: Property[] = [];
  isFavorite = false;
  bookingModalOpen = false;
  bookingForm: FormGroup;
  bookingLoading = false;
  canMessage = false;

  slideOpts = {
    initialSlide: 0,
    speed: 400,
    slidesPerView: 1,
    spaceBetween: 0,
  };

  private subs: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private propertyService: PropertyService,
    private reviewService: ReviewService,
    private favoriteService: FavoriteService,
    private bookingService: BookingService,
    private chatService: ChatService,
    public authService: AuthService,
    private fb: FormBuilder,
    private translate: TranslateService
  ) {
    this.bookingForm = this.fb.group({
      requestedDate: [null, Validators.required],
      message: ["", Validators.maxLength(500)],
      bookingType: ["VIEWING"],
    });
  }

  ngOnInit(): void {
    this.subs.push(
      this.route.paramMap.subscribe((params) => {
        const id = params.get("id");
        if (id) {
          this.id = id;
          this.loadProperty(id);
          this.loadSimilar(id);
          this.loadReviews(id);
        } else {
          this.router.navigate(["/properties"]);
        }
      })
    );

    // update canMessage based on auth
    this.canMessage = this.authService.isAuthenticated();
  }

  ngOnDestroy(): void {
    this.subs.forEach((s) => s.unsubscribe());
  }

  loadProperty(id: string): void {
    this.propertyService.getPropertyById(id).subscribe({
      next: (p) => {
        this.property = p;
        this.gallery = (p.images || []).map((i: any) => i.imageUrl || i);
        this.checkFavorite();
        // increment view count (optional)
        try {
          this.propertyService.incrementViewCount(p.id).subscribe();
        } catch {}
      },
      error: (err) => {
        console.error("Error loading property", err);
      },
    });
  }

  loadReviews(id: string): void {
    this.reviewService.getPropertyReviews(id).subscribe({
      next: (r) => {
        this.reviews = r;
      },
      error: (err) => {
        console.error("Error loading reviews", err);
      },
    });

    this.reviewService.getAverageRating(id).subscribe({
      next: (avg) => (this.averageRating = avg),
      error: () => (this.averageRating = 0),
    });
  }

  loadSimilar(id: string): void {
    this.propertyService.getSimilarProperties(id).subscribe({
      next: (res) => {
        this.similarProperties = res;
      },
      error: (err) => {
        console.error("Error loading similar properties", err);
      },
    });
  }

  checkFavorite(): void {
    if (!this.authService.isAuthenticated() || !this.property) return;
    this.favoriteService.isFavorite(this.property.id).subscribe({
      next: (res) => (this.isFavorite = res),
    });
  }

  toggleFavorite(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(["/auth/login"]);
      return;
    }
    if (!this.property) return;

    if (this.isFavorite) {
      this.favoriteService.removeFromFavorites(this.property.id).subscribe({
        next: () => (this.isFavorite = false),
        error: (err) => console.error(err),
      });
    } else {
      this.favoriteService.addToFavorites(this.property.id).subscribe({
        next: () => (this.isFavorite = true),
        error: (err) => console.error(err),
      });
    }
  }

  openBooking(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(["/auth/login"]);
      return;
    }
    this.bookingModalOpen = true;
  }

  submitBooking(): void {
    if (this.bookingForm.invalid || !this.property) return;

    this.bookingLoading = true;
    const payload = {
      propertyId: this.property.id,
      bookingType: this.bookingForm.value.bookingType,
      requestedDate: this.bookingForm.value.requestedDate,
      message: this.bookingForm.value.message,
    };

    this.bookingService.createBooking(payload).subscribe({
      next: (b) => {
        this.bookingLoading = false;
        this.bookingModalOpen = false;
        // show a simple toast via native service or navigate
        alert(this.translate.instant("property.bookingSuccess"));
      },
      error: (err) => {
        this.bookingLoading = false;
        console.error("Booking error", err);
        alert(this.translate.instant("property.bookingError"));
      },
    });
  }

  openAddReview(): void {
    // For now navigate to /properties/:id/review or open a modal - simple alert placeholder
    this.router.navigate(["/properties", this.id, "add-review"]);
  }

  startChat(): void {
    if (!this.authService.isAuthenticated() || !this.property) {
      this.router.navigate(["/auth/login"]);
      return;
    }
    // create or get chat room with landlord
    const tenantId = this.authService.getCurrentUser()?.id;
    const landlordId = this.property?.landlord?.id;
    const request = {
      propertyId: this.property!.id,
      tenantId: tenantId!,
      landlordId: landlordId!,
    };
    this.chatService.createOrGetChatRoom(request).subscribe({
      next: (room) => {
        this.router.navigate(["/chat", room.id]);
      },
      error: (err) => {
        console.error("Chat error", err);
      },
    });
  }

  reportListing(): void {
    if (!this.authService.isAuthenticated() || !this.property) {
      this.router.navigate(["/auth/login"]);
      return;
    }

    const reason = prompt(this.translate.instant("property.reportReasonPrompt"));
    if (!reason || reason.trim().length === 0) {
      alert(this.translate.instant("property.reportReasonRequired"));
      return;
    }

    const description = prompt(this.translate.instant("property.reportDescriptionPrompt"));

    this.propertyService
      .reportProperty(this.property.id, { reason: reason.trim(), description })
      .subscribe({
        next: () => {
          alert(this.translate.instant("property.reportSuccess"));
        },
        error: (err) => {
          console.error("Report error", err);
          alert(this.translate.instant("property.reportError"));
        },
      });
  }

  navigateToProperty(id: string): void {
    this.router.navigate(["/properties", id]);
    // scroll to top
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  goBack(): void {
    this.router.navigate(["/properties"]);
  }

  scrollToSection(id: string): void {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth' " as any });
  }

  formatPrice(price?: number, currency?: string): string {
    if (!price) return "";
    try {
      return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: currency || "XAF",
        minimumFractionDigits: 0,
      }).format(price);
    } catch {
      return `${price} ${currency || ""}`;
    }
  }
}
