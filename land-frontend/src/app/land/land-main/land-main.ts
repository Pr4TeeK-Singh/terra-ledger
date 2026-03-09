import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { LandFormComponent } from '../land-form/land-form';
import { LandListComponent } from '../land-list/land-list';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-land-main',
  standalone: true,
  imports: [CommonModule, LandFormComponent, LandListComponent],
  templateUrl: './land-main.html',
  styleUrl: './land-main.css'
})
export class LandMainComponent {

  @ViewChild('landList') landList!: LandListComponent;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  refreshList(): void { this.landList?.loadLands(); }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}