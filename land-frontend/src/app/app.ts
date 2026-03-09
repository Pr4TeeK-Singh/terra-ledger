import { Component, ViewChild } from '@angular/core';
import { LandFormComponent } from './land/land-form/land-form';
import { LandListComponent } from './land/land-list/land-list';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [LandFormComponent, LandListComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {

  @ViewChild('landList') landList!: LandListComponent;

  refreshList(): void {
    this.landList?.loadLands();
  }
} 