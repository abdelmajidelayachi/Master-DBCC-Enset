import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-required',
  imports: [RouterLink],
  template: `
    <div class="max-w-md mx-auto px-4 py-16 text-center">
      <div
        class="mx-auto w-16 h-16 rounded-full bg-red-50 flex items-center justify-center mb-6"
      >
        <svg class="w-8 h-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 15v2m0 4a9 9 0 100-18 9 9 0 000 18zm0-13v4"
          />
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-slate-800 mb-2">Accès refusé</h1>
      <p class="text-slate-500 mb-8">
        Cette page est réservée aux administrateurs. Vous ne disposez pas des droits nécessaires.
      </p>
      <a
        routerLink="/dashboard"
        class="inline-block rounded-lg bg-indigo-600 text-white text-sm font-semibold px-5 py-2.5 hover:bg-indigo-700 transition"
        >Retour au tableau de bord</a
      >
    </div>
  `
})
export class AdminRequiredComponent {}
