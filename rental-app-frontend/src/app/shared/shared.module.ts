import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { IonicModule } from "@ionic/angular";
import { RouterModule } from "@angular/router";
import { TranslateModule } from "@ngx-translate/core";

import { HeaderComponent } from "./components/header/header.component";
import { FooterComponent } from "./components/footer/footer.component";
import { LoaderComponent } from "./components/loader/loader.component";
import { FormsModule } from "@angular/forms";

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    RouterModule,
    TranslateModule,
    FormsModule,
    HeaderComponent,
    FooterComponent,
    LoaderComponent,
  ],
  exports: [HeaderComponent, FooterComponent, LoaderComponent],
})
export class SharedModule {}
