import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { TranslateModule } from "@ngx-translate/core";

@Component({
  selector: "app-footer",
  templateUrl: "./footer.component.html",
  styleUrls: ["./footer.component.scss"],
  standalone: true,
  imports: [CommonModule, IonicModule, TranslateModule]
})
export class FooterComponent {
  constructor(private router: Router) {}

  navigate(path: string): void {
    this.router.navigate([path]);
  }
}
