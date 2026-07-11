import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CustomerService } from '../../services/customer.service';
import { CustomerDTO } from '../../models/customer.model';

@Component({
  selector: 'app-customers',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-slate-800">Clients</h1>
        @if (auth.isAdmin()) {
          <a
            routerLink="/customers/new"
            class="rounded-lg bg-indigo-600 text-white text-sm font-semibold px-4 py-2 hover:bg-indigo-700 transition"
            >+ Nouveau client</a
          >
        }
      </div>

      <form [formGroup]="searchForm" (ngSubmit)="search()" class="flex gap-3 mb-6">
        <input
          type="text"
          formControlName="keyword"
          placeholder="Rechercher un client..."
          class="flex-1 max-w-md rounded-lg border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <button
          type="submit"
          class="rounded-lg bg-slate-800 text-white text-sm font-semibold px-4 py-2 hover:bg-slate-700 transition"
        >
          Rechercher
        </button>
      </form>

      @if (error()) {
        <div class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ error() }}
        </div>
      }

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="text-left text-slate-500 border-b border-slate-200">
              <th class="px-5 py-3 font-medium">ID</th>
              <th class="px-5 py-3 font-medium">Nom</th>
              <th class="px-5 py-3 font-medium">Email</th>
              <th class="px-5 py-3 font-medium text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (customer of customers(); track customer.id) {
              <tr class="border-b border-slate-100 last:border-0 hover:bg-slate-50">
                <td class="px-5 py-3 text-slate-500">{{ customer.id }}</td>
                <td class="px-5 py-3 font-medium text-slate-800">{{ customer.name }}</td>
                <td class="px-5 py-3 text-slate-600">{{ customer.email }}</td>
                <td class="px-5 py-3">
                  <div class="flex justify-end gap-2">
                    <a
                      [routerLink]="['/customers', customer.id, 'accounts']"
                      class="rounded-lg border border-indigo-200 text-indigo-600 text-xs font-semibold px-3 py-1.5 hover:bg-indigo-50 transition"
                      >Comptes</a
                    >
                    @if (auth.isAdmin()) {
                      <a
                        [routerLink]="['/customers', customer.id, 'edit']"
                        class="rounded-lg border border-slate-300 text-slate-600 text-xs font-semibold px-3 py-1.5 hover:bg-slate-100 transition"
                        >Modifier</a
                      >
                      <button
                        type="button"
                        (click)="deleteCustomer(customer)"
                        class="rounded-lg border border-red-200 text-red-600 text-xs font-semibold px-3 py-1.5 hover:bg-red-50 transition"
                      >
                        Supprimer
                      </button>
                    }
                  </div>
                </td>
              </tr>
            } @empty {
              <tr>
                <td colspan="4" class="px-5 py-8 text-center text-slate-400">
                  {{ loading() ? 'Chargement...' : 'Aucun client trouvé.' }}
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class CustomersComponent {
  auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private fb = inject(FormBuilder);

  customers = signal<CustomerDTO[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  searchForm = this.fb.nonNullable.group({ keyword: [''] });

  constructor() {
    this.loadAll();
  }

  private loadAll(): void {
    this.loading.set(true);
    this.customerService.getCustomers().subscribe({
      next: (customers) => {
        this.customers.set(customers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les clients.');
        this.loading.set(false);
      }
    });
  }

  search(): void {
    const keyword = this.searchForm.getRawValue().keyword.trim();
    if (!keyword) {
      this.loadAll();
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.customerService.searchCustomers(keyword).subscribe({
      next: (customers) => {
        this.customers.set(customers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erreur lors de la recherche.');
        this.loading.set(false);
      }
    });
  }

  deleteCustomer(customer: CustomerDTO): void {
    if (!confirm(`Voulez-vous vraiment supprimer le client "${customer.name}" ?`)) return;
    this.customerService.deleteCustomer(customer.id).subscribe({
      next: () => this.customers.update((list) => list.filter((c) => c.id !== customer.id)),
      error: () => this.error.set('Erreur lors de la suppression du client.')
    });
  }
}
