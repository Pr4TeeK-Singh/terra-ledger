import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

const authGuard = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  return router.createUrlTree(['/login']);
};

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: '',
    loadComponent: () => import('./land/land-main/land-main').then(m => m.LandMainComponent),
    canActivate: [authGuard]
  },
  {
    path: 'errors',
    loadComponent: () => import('./errors/error-log/error-log').then(m => m.ErrorLogComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '' }
];