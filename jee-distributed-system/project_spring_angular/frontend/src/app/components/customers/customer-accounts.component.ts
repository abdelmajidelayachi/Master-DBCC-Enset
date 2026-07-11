import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer.service';
import { BankAccountDTO } from '../../models/account.model';
import { CustomerDTO } from '../../models/customer.model';

@Component({
  selector: 'app-customer-accounts',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-2xl font-bold text-slate-800">
            Comptes de {{ customer()?.name ?? '...' }}
          </h1>
          @if (customer()) {
            <p class="text-sm text-slate-500 mt-1">{{ customer()!.email }}</p>
          }
        </div>
        <a
          routerLink="/customers"
          class="rounded-lg border border-slate-300 text-slate-600 text-sm font-semibold px-4 py-2 hover:bg-slate-100 transition"
          >← Retour aux clients</a
        >
      </div>

      @if (error()) {
        <div class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ error() }}
        </div>
      }

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        @for (account of accounts(); track account.id) {
          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
            <div class="flex items-center justify-between mb-3">
              <span
                class="text-xs font-bold uppercase px-2.5 py-1 rounded-full"
                [class]="
                  account.type === 'CurrentAccount'
                    ? 'bg-indigo-50 text-indigo-700'
                    : 'bg-emerald-50 text-emerald-700'
                "
                >{{ account.type === 'CurrentAccount' ? 'Courant' : 'Épargne' }}</span
              >
              <span
                class="text-xs font-semibold px-2.5 py-1 rounded-full"
                [class]="statusClass(account.status)"
                >{{ statusLabel(account.status) }}</span
              >
            </div>
            <p class="text-xs text-slate-400 break-all mb-2">{{ account.id }}</p>
            <p class="text-2xl font-bold text-slate-800">
              {{ account.balance | currency: 'MAD' : 'symbol' : '1.2-2' }}
            </p>
            <p class="text-xs text-slate-500 mt-1">
              Créé le {{ account.createdAt | date: 'dd/MM/yyyy' }}
            </p>
            @if (account.type === 'CurrentAccount') {
              <p class="text-xs text-slate-500">
                Découvert : {{ account.overDraft | currency: 'MAD' : 'symbol' : '1.2-2' }}
              </p>
            } @else {
              <p class="text-xs text-slate-500">Taux d'intérêt : {{ account.interestRate }}%</p>
            }
            <a
              [routerLink]="['/accounts']"
              [queryParams]="{ id: account.id }"
              class="mt-4 inline-block rounded-lg bg-indigo-600 text-white text-xs font-semibold px-4 py-2 hover:bg-indigo-700 transition"
              >Voir les opérations</a
            >
          </div>
        } @empty {
          <p class="text-slate-400 col-span-full">
            {{ loading() ? 'Chargement...' : 'Aucun compte pour ce client.' }}
          </p>
        }
      </div>
    </div>
  `
})
export class CustomerAccountsComponent {
  private customerService = inject(CustomerService);
  private route = inject(ActivatedRoute);

  customer = signal<CustomerDTO | null>(null);
  accounts = signal<BankAccountDTO[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor() {
    const id = +(this.route.snapshot.paramMap.get('id') ?? 0);
    this.customerService.getCustomer(id).subscribe({
      next: (customer) => this.customer.set(customer),
      error: () => this.error.set('Impossible de charger le client.')
    });
    this.customerService.getCustomerAccounts(id).subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les comptes du client.');
        this.loading.set(false);
      }
    });
  }

  statusLabel(status: BankAccountDTO['status']): string {
    switch (status) {
      case 'ACTIVATED':
        return 'Activé';
      case 'SUSPENDED':
        return 'Suspendu';
      default:
        return 'Créé';
    }
  }

  statusClass(status: BankAccountDTO['status']): string {
    switch (status) {
      case 'ACTIVATED':
        return 'bg-emerald-50 text-emerald-700';
      case 'SUSPENDED':
        return 'bg-red-50 text-red-700';
      default:
        return 'bg-slate-100 text-slate-600';
    }
  }
}
