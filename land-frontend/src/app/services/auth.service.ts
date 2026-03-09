import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  username: string;
  expiresIn: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly baseUrl  = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'land_jwt_token';
  private readonly USER_KEY  = 'land_jwt_user';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { username, password })
      .pipe(tap(res => {
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_KEY,  res.username);
      }));
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  getToken(): string | null    { return localStorage.getItem(this.TOKEN_KEY); }
  getUsername(): string | null { return localStorage.getItem(this.USER_KEY); }
  isLoggedIn(): boolean        { return !!this.getToken(); }
}