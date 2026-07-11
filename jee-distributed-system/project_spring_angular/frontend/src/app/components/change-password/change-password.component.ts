import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password',
  imports: [ReactiveFormsModule],
  template: `
    <div class="max-w-md mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">Changer le mot de passe</h1>

      @if (error()) {
        <div class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ error() }}
        </div>
      }
      @if (success()) {
        <div
          class="mb-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 text-sm"
        >
          {{ success() }}
        </div>
      }

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Ancien mot de passe</label>
            <input
              type="password"
              formControlName="oldPassword"
              autocomplete="current-password"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Nouveau mot de passe</label>
            <input
              type="password"
              formControlName="newPassword"
              autocomplete="new-password"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            @if (form.controls.newPassword.invalid && form.controls.newPassword.touched) {
              <p class="mt-1 text-xs text-red-600">
                Le nouveau mot de passe doit contenir au moins 4 caractères.
              </p>
            }
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1"
              >Confirmer le nouveau mot de passe</label
            >
            <input
              type="password"
              formControlName="confirmPassword"
              autocomplete="new-password"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            @if (mismatch()) {
              <p class="mt-1 text-xs text-red-600">Les mots de passe ne correspondent pas.</p>
            }
          </div>
          <button
            type="submit"
            [disabled]="form.invalid || loading()"
            class="w-full rounded-lg bg-indigo-600 text-white text-sm font-semibold py-2.5 hover:bg-indigo-700 disabled:opacity-50 transition"
          >
            {{ loading() ? 'Enregistrement...' : 'Changer le mot de passe' }}
          </button>
        </form>
      </div>
    </div>
  `
})
export class ChangePasswordComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  loading = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    oldPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(4)]],
    confirmPassword: ['', Validators.required]
  });

  mismatch(): boolean {
    const { newPassword, confirmPassword } = this.form.getRawValue();
    return (
      this.form.controls.confirmPassword.touched &&
      confirmPassword.length > 0 &&
      newPassword !== confirmPassword
    );
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { oldPassword, newPassword, confirmPassword } = this.form.getRawValue();
    if (newPassword !== confirmPassword) {
      this.error.set('Les mots de passe ne correspondent pas.');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);
    this.auth.changePassword(oldPassword, newPassword).subscribe({
      next: () => {
        this.loading.set(false);
        this.success.set('Mot de passe modifié avec succès.');
        this.form.reset();
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(
          err.status === 400
            ? "L'ancien mot de passe est incorrect."
            : 'Erreur lors du changement de mot de passe.'
        );
      }
    });
  }
}
