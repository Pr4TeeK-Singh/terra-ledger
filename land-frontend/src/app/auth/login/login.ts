import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  username     = '';
  password     = '';
  errorMsg     = '';
  loading      = false;
  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    if (!this.username || !this.password) {
      this.errorMsg = 'Please enter both username and password.';
      return;
    }
    this.loading  = true;
    this.errorMsg = '';
    this.authService.login(this.username, this.password).subscribe({
      next:  () => { this.loading = false; this.router.navigate(['/']); },
      error: () => { this.loading = false; this.errorMsg = 'Invalid username or password.'; }
    });
  }
}