import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { CustomerService } from '../../services/customer.service';
import { CustomerDTO } from '../../models/customer.model';

@Component({
  selector: 'app-new-account',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">Nouveau compte</h1>

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
            <label class="block text-sm font-medium text-slate-700 mb-1">Type de compte</label>
            <div class="flex gap-4">
              <label class="flex items-center gap-2 text-sm text-slate-700">
                <input type="radio" formControlName="type" value="current" class="accent-indigo-600" />
                Courant
              </label>
              <label class="flex items-center gap-2 text-sm text-slate-700">
                <input type="radio" formControlName="type" value="saving" class="accent-indigo-600" />
                Épargne
              </label>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Client</label>
            <select
              formControlName="customerId"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option [ngValue]="null" disabled>Sélectionner un client...</option>
              @for (customer of customers(); track customer.id) {
                <option [ngValue]="customer.id">{{ customer.name }} ({{ customer.email }})</option>
              }
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Solde initial (MAD)</label>
            <input
              type="number"
              formControlName="initialBalance"
              min="0"
              step="0.01"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          @if (form.controls.type.value === 'current') {
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Découvert autorisé (MAD)</label>
              <input
                type="number"
                formControlName="overDraft"
                min="0"
                step="0.01"
                class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          } @else {
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Taux d'intérêt (%)</label>
              <input
                type="number"
                formControlName="interestRate"
                min="0"
                step="0.01"
                class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          }

          <div class="flex gap-3 pt-2">
            <button
              type="submit"
              [disabled]="form.invalid || saving()"
              class="rounded-lg bg-indigo-600 text-white text-sm font-semibold px-5 py-2.5 hover:bg-indigo-700 disabled:opacity-50 transition"
            >
              {{ saving() ? 'Création...' : 'Créer le compte' }}
            </button>
            <a
              routerLink="/accounts"
              class="rounded-lg border border-slate-300 text-slate-600 text-sm font-semibold px-5 py-2.5 hover:bg-slate-100 transition"
              >Annuler</a
            >
          </div>
        </form>
      </div>
    </div>
  `
})
export class NewAccountComponent {
  private fb = inject(FormBuilder);
  private accountService = inject(AccountService);
  private customerService = inject(CustomerService);
  private router = inject(Router);

  customers = signal<CustomerDTO[]>([]);
  saving = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    type: this.fb.nonNullable.control<'current' | 'saving'>('current', Validators.required),
    customerId: this.fb.control<number | null>(null, Validators.required),
    initialBalance: this.fb.nonNullable.control(0, [Validators.required, Validators.min(0)]),
    overDraft: this.fb.nonNullable.control(0, Validators.min(0)),
    interestRate: this.fb.nonNullable.control(0, Validators.min(0))
  });

  constructor() {
    this.customerService.getCustomers().subscribe({
      next: (customers) => this.customers.set(customers),
      error: () => this.error.set('Impossible de charger la liste des clients.')
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    const { type, customerId, initialBalance, overDraft, interestRate } = this.form.getRawValue();
    if (customerId === null) return;

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const request =
      type === 'current'
        ? this.accountService.createCurrentAccount(customerId, initialBalance, overDraft)
        : this.accountService.createSavingAccount(customerId, initialBalance, interestRate);

    request.subscribe({
      next: (account) => {
        this.saving.set(false);
        this.success.set(`Compte créé avec succès (${account.id}).`);
        setTimeout(
          () => this.router.navigate(['/accounts'], { queryParams: { id: account.id } }),
          800
        );
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? 'Erreur lors de la création du compte.');
      }
    });
  }
}
