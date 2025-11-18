import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule, Routes } from "@angular/router";
import { PropertyDetailComponent } from "./property-detail/property-detail.component";

const routes: Routes = [
  {
    path: "properties/:id",
    component: PropertyDetailComponent,
  },
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forChild(routes)],
})
export class PropertiesModule {}
