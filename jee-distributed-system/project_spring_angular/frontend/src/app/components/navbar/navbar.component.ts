import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="bg-slate-900 text-white shadow-md">
      <div class="max-w-7xl mx-auto px-4">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-8">
            <a routerLink="/dashboard" class="flex items-center gap-2">
              <span
                class="w-8 h-8 rounded-lg bg-indigo-500 flex items-center justify-center text-white font-bold"
                >€</span
              >
              <span class="text-lg font-bold tracking-tight">E-Bank</span>
            </a>
            <div class="hidden md:flex items-center gap-1">
              <a
                routerLink="/dashboard"
                routerLinkActive="bg-slate-700 text-white"
                class="px-3 py-2 rounded-lg text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition"
                >Dashboard</a
              >
              <a
                routerLink="/customers"
                routerLinkActive="bg-slate-700 text-white"
                class="px-3 py-2 rounded-lg text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition"
                >Clients</a
              >
              <a
                routerLink="/accounts"
                routerLinkActive="bg-slate-700 text-white"
                class="px-3 py-2 rounded-lg text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition"
                >Comptes</a
              >
              <a
                routerLink="/chat"
                routerLinkActive="bg-slate-700 text-white"
                class="px-3 py-2 rounded-lg text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition"
                >Chatbot</a
              >
            </div>
          </div>

          <div class="relative">
            <button
              type="button"
              (click)="menuOpen.set(!menuOpen())"
              class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-800 transition"
            >
              <span
                class="w-8 h-8 rounded-full bg-indigo-500 flex items-center justify-center text-sm font-semibold uppercase"
                >{{ (auth.username() ?? '?').charAt(0) }}</span
              >
              <span class="text-sm font-medium hidden sm:block">{{ auth.username() }}</span>
              @if (auth.isAdmin()) {
                <span
                  class="hidden sm:block text-[10px] font-bold uppercase bg-indigo-500/30 text-indigo-300 px-2 py-0.5 rounded-full"
                  >Admin</span
                >
              }
              <svg class="w-4 h-4 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            @if (menuOpen()) {
              <div
                class="absolute right-0 mt-2 w-56 bg-white text-slate-700 rounded-xl shadow-lg border border-slate-200 py-2 z-50"
              >
                <a
                  routerLink="/change-password"
                  (click)="menuOpen.set(false)"
                  class="block px-4 py-2 text-sm hover:bg-slate-100"
                  >Changer le mot de passe</a
                >
                <button
                  type="button"
                  (click)="logout()"
                  class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  Déconnexion
                </button>
              </div>
            }
          </div>
        </div>

        <div class="md:hidden flex items-center gap-1 pb-3">
          <a
            routerLink="/dashboard"
            routerLinkActive="bg-slate-700 text-white"
            class="px-3 py-1.5 rounded-lg text-sm text-slate-300 hover:bg-slate-800"
            >Dashboard</a
          >
          <a
            routerLink="/customers"
            routerLinkActive="bg-slate-700 text-white"
            class="px-3 py-1.5 rounded-lg text-sm text-slate-300 hover:bg-slate-800"
            >Clients</a
          >
          <a
            routerLink="/accounts"
            routerLinkActive="bg-slate-700 text-white"
            class="px-3 py-1.5 rounded-lg text-sm text-slate-300 hover:bg-slate-800"
            >Comptes</a
          >
          <a
            routerLink="/chat"
            routerLinkActive="bg-slate-700 text-white"
            class="px-3 py-1.5 rounded-lg text-sm text-slate-300 hover:bg-slate-800"
            >Chatbot</a
          >
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  auth = inject(AuthService);
  menuOpen = signal(false);

  logout(): void {
    this.menuOpen.set(false);
    this.auth.logout();
  }
}
