import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-100 px-4">
      <div class="w-full max-w-md">
        <div class="bg-white rounded-xl shadow-lg p-8">
          <div class="text-center mb-8">
            <div
              class="mx-auto w-14 h-14 rounded-xl bg-indigo-600 flex items-center justify-center mb-4"
            >
              <span class="text-white text-2xl font-bold">€</span>
            </div>
            <h1 class="text-2xl font-bold text-slate-800">E-Bank</h1>
            <p class="text-slate-500 mt-1">Connectez-vous à votre espace</p>
          </div>

          @if (error()) {
            <div
              class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm"
            >
              {{ error() }}
            </div>
          }

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1"
                >Nom d'utilisateur</label
              >
              <input
                type="text"
                formControlName="username"
                autocomplete="username"
                class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="Nom d'utilisateur"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Mot de passe</label>
              <input
                type="password"
                formControlName="password"
                autocomplete="current-password"
                class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="Mot de passe"
              />
            </div>
            <button
              type="submit"
              [disabled]="form.invalid || loading()"
              class="w-full rounded-lg bg-indigo-600 text-white font-semibold py-2.5 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
            >
              {{ loading() ? 'Connexion...' : 'Se connecter' }}
            </button>
          </form>

          <p class="mt-6 text-center text-xs text-slate-400">
            admin / 12345 (admin) — user1 / 12345 (lecture)
          </p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);
    const { username, password } = this.form.getRawValue();
    this.auth.login(username, password).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(
          err.status === 401
            ? "Nom d'utilisateur ou mot de passe incorrect."
            : 'Erreur de connexion au serveur.'
        );
      }
    });
  }
}
