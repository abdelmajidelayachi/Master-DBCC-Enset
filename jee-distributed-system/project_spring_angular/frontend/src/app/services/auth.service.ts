import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { backendHost } from '../config';

interface JwtPayload {
  sub: string;
  scope: string;
  exp: number;
}

const TOKEN_KEY = 'access-token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly accessToken = signal<string | null>(null);
  readonly username = signal<string | null>(null);
  readonly roles = signal<string[]>([]);
  readonly isAuthenticated = computed(() => this.accessToken() !== null);
  readonly isAdmin = computed(() => this.roles().includes('ADMIN'));

  constructor() {
    this.loadTokenFromStorage();
  }

  login(username: string, password: string): Observable<{ accessToken: string }> {
    return this.http
      .post<{ accessToken: string }>(`${backendHost}/auth/login`, { username, password })
      .pipe(tap((res) => this.storeToken(res.accessToken)));
  }

  changePassword(oldPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${backendHost}/users/changePassword`, {
      oldPassword,
      newPassword
    });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.accessToken.set(null);
    this.username.set(null);
    this.roles.set([]);
    this.router.navigate(['/login']);
  }

  private loadTokenFromStorage(): void {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) return;
    const payload = this.decodeToken(token);
    if (!payload || payload.exp * 1000 <= Date.now()) {
      localStorage.removeItem(TOKEN_KEY);
      return;
    }
    this.applyToken(token, payload);
  }

  private storeToken(token: string): void {
    const payload = this.decodeToken(token);
    if (!payload) return;
    localStorage.setItem(TOKEN_KEY, token);
    this.applyToken(token, payload);
  }

  private applyToken(token: string, payload: JwtPayload): void {
    this.accessToken.set(token);
    this.username.set(payload.sub);
    this.roles.set(payload.scope ? payload.scope.split(' ').filter((r) => r.length > 0) : []);
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64)) as JwtPayload;
    } catch {
      return null;
    }
  }
}
