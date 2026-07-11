import { Component, inject, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { AccountHistoryDTO, BankAccountDTO } from '../../models/account.model';

@Component({
  selector: 'app-accounts',
  imports: [CurrencyPipe, DatePipe, ReactiveFormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">Comptes</h1>

      <form [formGroup]="searchForm" (ngSubmit)="search()" class="flex gap-3 mb-6">
        <input
          type="text"
          formControlName="accountId"
          placeholder="Identifiant du compte..."
          class="flex-1 max-w-lg rounded-lg border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <button
          type="submit"
          [disabled]="searchForm.invalid"
          class="rounded-lg bg-indigo-600 text-white text-sm font-semibold px-5 py-2 hover:bg-indigo-700 disabled:opacity-50 transition"
        >
          Rechercher
        </button>
      </form>

      @if (searchError()) {
        <div class="mb-6 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ searchError() }}
        </div>
      }

      @if (account(); as acc) {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div class="lg:col-span-2 space-y-6">
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
              <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
                <div>
                  <p class="text-xs text-slate-400 break-all">{{ acc.id }}</p>
                  <p class="text-3xl font-bold text-slate-800 mt-1">
                    {{ acc.balance | currency: 'MAD' : 'symbol' : '1.2-2' }}
                  </p>
                </div>
                <div class="text-right text-sm">
                  <span
                    class="inline-block text-xs font-bold uppercase px-2.5 py-1 rounded-full"
                    [class]="
                      acc.type === 'CurrentAccount'
                        ? 'bg-indigo-50 text-indigo-700'
                        : 'bg-emerald-50 text-emerald-700'
                    "
                    >{{ acc.type === 'CurrentAccount' ? 'Courant' : 'Épargne' }}</span
                  >
                  <p class="text-slate-600 mt-2 font-medium">{{ acc.customerDTO.name }}</p>
                  <p class="text-slate-400 text-xs">{{ acc.customerDTO.email }}</p>
                </div>
              </div>
              <div class="flex gap-6 text-xs text-slate-500 border-t border-slate-100 pt-3">
                <span>Statut : {{ statusLabel(acc.status) }}</span>
                <span>Créé le {{ acc.createdAt | date: 'dd/MM/yyyy' }}</span>
                @if (acc.type === 'CurrentAccount') {
                  <span>Découvert : {{ acc.overDraft | currency: 'MAD' : 'symbol' : '1.2-2' }}</span>
                } @else {
                  <span>Taux d'intérêt : {{ acc.interestRate }}%</span>
                }
              </div>
            </div>

            <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
              <h2 class="text-sm font-semibold text-slate-700 px-6 pt-5 pb-3">Opérations</h2>
              <div class="overflow-x-auto">
                <table class="w-full text-sm">
                  <thead>
                    <tr class="text-left text-slate-500 border-b border-slate-200">
                      <th class="px-6 py-3 font-medium">ID</th>
                      <th class="px-6 py-3 font-medium">Date</th>
                      <th class="px-6 py-3 font-medium">Type</th>
                      <th class="px-6 py-3 font-medium">Description</th>
                      <th class="px-6 py-3 font-medium text-right">Montant</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (op of history()?.accountOperationDTOS ?? []; track op.id) {
                      <tr class="border-b border-slate-100 last:border-0 hover:bg-slate-50">
                        <td class="px-6 py-3 text-slate-500">{{ op.id }}</td>
                        <td class="px-6 py-3 text-slate-600">
                          {{ op.operationDate | date: 'dd/MM/yyyy HH:mm' }}
                        </td>
                        <td class="px-6 py-3">
                          <span
                            class="text-xs font-bold px-2 py-0.5 rounded-full"
                            [class]="
                              op.type === 'CREDIT'
                                ? 'bg-emerald-50 text-emerald-700'
                                : 'bg-red-50 text-red-700'
                            "
                            >{{ op.type === 'CREDIT' ? 'Crédit' : 'Débit' }}</span
                          >
                        </td>
                        <td class="px-6 py-3 text-slate-600">{{ op.description }}</td>
                        <td
                          class="px-6 py-3 text-right font-semibold"
                          [class]="op.type === 'CREDIT' ? 'text-emerald-600' : 'text-red-600'"
                        >
                          {{ op.type === 'CREDIT' ? '+' : '-'
                          }}{{ op.amount | currency: 'MAD' : 'symbol' : '1.2-2' }}
                        </td>
                      </tr>
                    } @empty {
                      <tr>
                        <td colspan="5" class="px-6 py-8 text-center text-slate-400">
                          Aucune opération.
                        </td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
              @if (history(); as h) {
                @if (h.totalPages > 1) {
                  <div class="flex items-center justify-center gap-1 px-6 py-4 border-t border-slate-100">
                    <button
                      type="button"
                      (click)="goToPage(h.currentPage - 1)"
                      [disabled]="h.currentPage === 0"
                      class="rounded-lg border border-slate-300 text-slate-600 text-xs font-semibold px-3 py-1.5 hover:bg-slate-100 disabled:opacity-40 transition"
                    >
                      Précédent
                    </button>
                    @for (page of pages(); track page) {
                      <button
                        type="button"
                        (click)="goToPage(page)"
                        class="rounded-lg text-xs font-semibold w-8 h-8 transition"
                        [class]="
                          page === h.currentPage
                            ? 'bg-indigo-600 text-white'
                            : 'border border-slate-300 text-slate-600 hover:bg-slate-100'
                        "
                      >
                        {{ page + 1 }}
                      </button>
                    }
                    <button
                      type="button"
                      (click)="goToPage(h.currentPage + 1)"
                      [disabled]="h.currentPage >= h.totalPages - 1"
                      class="rounded-lg border border-slate-300 text-slate-600 text-xs font-semibold px-3 py-1.5 hover:bg-slate-100 disabled:opacity-40 transition"
                    >
                      Suivant
                    </button>
                  </div>
                }
              }
            </div>
          </div>

          <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 h-fit">
            <h2 class="text-sm font-semibold text-slate-700 mb-4">Nouvelle opération</h2>

            @if (operationError()) {
              <div
                class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm"
              >
                {{ operationError() }}
              </div>
            }
            @if (operationSuccess()) {
              <div
                class="mb-4 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 text-sm"
              >
                {{ operationSuccess() }}
              </div>
            }

            <form [formGroup]="operationForm" (ngSubmit)="performOperation()" class="space-y-4">
              <div class="flex gap-4">
                <label class="flex items-center gap-2 text-sm text-slate-700">
                  <input type="radio" formControlName="type" value="DEBIT" class="accent-indigo-600" />
                  Débit
                </label>
                <label class="flex items-center gap-2 text-sm text-slate-700">
                  <input type="radio" formControlName="type" value="CREDIT" class="accent-indigo-600" />
                  Crédit
                </label>
                <label class="flex items-center gap-2 text-sm text-slate-700">
                  <input
                    type="radio"
                    formControlName="type"
                    value="TRANSFERT"
                    class="accent-indigo-600"
                  />
                  Transfert
                </label>
              </div>

              @if (operationForm.controls.type.value === 'TRANSFERT') {
                <div>
                  <label class="block text-sm font-medium text-slate-700 mb-1"
                    >Compte destination</label
                  >
                  <input
                    type="text"
                    formControlName="destination"
                    placeholder="Identifiant du compte destination"
                    class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                  />
                </div>
              }

              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Montant</label>
                <input
                  type="number"
                  formControlName="amount"
                  min="0"
                  step="0.01"
                  class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-1">Description</label>
                <input
                  type="text"
                  formControlName="description"
                  placeholder="Description de l'opération"
                  class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <button
                type="submit"
                [disabled]="operationForm.invalid || operationLoading()"
                class="w-full rounded-lg bg-indigo-600 text-white text-sm font-semibold py-2.5 hover:bg-indigo-700 disabled:opacity-50 transition"
              >
                {{ operationLoading() ? 'Traitement...' : "Exécuter l'opération" }}
              </button>
            </form>
          </div>
        </div>
      } @else {
        <div
          class="bg-white rounded-xl shadow-sm border border-dashed border-slate-300 p-12 text-center text-slate-400"
        >
          Saisissez un identifiant de compte pour afficher son détail et ses opérations.
        </div>
      }
    </div>
  `
})
export class AccountsComponent {
  private accountService = inject(AccountService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);

  readonly pageSize = 5;

  account = signal<BankAccountDTO | null>(null);
  history = signal<AccountHistoryDTO | null>(null);
  searchError = signal<string | null>(null);
  operationError = signal<string | null>(null);
  operationSuccess = signal<string | null>(null);
  operationLoading = signal(false);

  searchForm = this.fb.nonNullable.group({
    accountId: ['', Validators.required]
  });

  operationForm = this.fb.nonNullable.group({
    type: ['DEBIT' as 'DEBIT' | 'CREDIT' | 'TRANSFERT', Validators.required],
    destination: [''],
    amount: [0, [Validators.required, Validators.min(0.01)]],
    description: ['', Validators.required]
  });

  constructor() {
    const idFromQuery = this.route.snapshot.queryParamMap.get('id');
    if (idFromQuery) {
      this.searchForm.patchValue({ accountId: idFromQuery });
      this.search();
    }
  }

  pages(): number[] {
    const h = this.history();
    if (!h) return [];
    return Array.from({ length: h.totalPages }, (_, i) => i);
  }

  search(): void {
    if (this.searchForm.invalid) return;
    const id = this.searchForm.getRawValue().accountId.trim();
    if (!id) return;
    this.searchError.set(null);
    this.operationError.set(null);
    this.operationSuccess.set(null);
    this.accountService.getAccount(id).subscribe({
      next: (account) => {
        this.account.set(account);
        this.loadHistory(0);
      },
      error: (err) => {
        this.account.set(null);
        this.history.set(null);
        this.searchError.set(
          err.status === 404 ? 'Compte introuvable.' : 'Erreur lors de la recherche du compte.'
        );
      }
    });
  }

  goToPage(page: number): void {
    const h = this.history();
    if (!h || page < 0 || page >= h.totalPages) return;
    this.loadHistory(page);
  }

  private loadHistory(page: number): void {
    const account = this.account();
    if (!account) return;
    this.accountService.getPageOperations(account.id, page, this.pageSize).subscribe({
      next: (history) => this.history.set(history),
      error: () => this.searchError.set("Erreur lors du chargement de l'historique.")
    });
  }

  performOperation(): void {
    const account = this.account();
    if (!account || this.operationForm.invalid) return;
    const { type, destination, amount, description } = this.operationForm.getRawValue();

    if (type === 'TRANSFERT' && !destination.trim()) {
      this.operationError.set('Veuillez saisir le compte destination.');
      return;
    }

    this.operationLoading.set(true);
    this.operationError.set(null);
    this.operationSuccess.set(null);

    const request =
      type === 'DEBIT'
        ? this.accountService.debit(account.id, amount, description)
        : type === 'CREDIT'
          ? this.accountService.credit(account.id, amount, description)
          : this.accountService.transfer(account.id, destination.trim(), amount, description);

    request.subscribe({
      next: () => {
        this.operationLoading.set(false);
        this.operationSuccess.set('Opération effectuée avec succès.');
        this.operationForm.patchValue({ amount: 0, description: '', destination: '' });
        this.operationForm.markAsUntouched();
        this.reloadAccount();
      },
      error: (err) => {
        this.operationLoading.set(false);
        this.operationError.set(err.error?.message ?? "Erreur lors de l'opération.");
      }
    });
  }

  private reloadAccount(): void {
    const account = this.account();
    if (!account) return;
    this.accountService.getAccount(account.id).subscribe({
      next: (acc) => this.account.set(acc)
    });
    this.loadHistory(this.history()?.currentPage ?? 0);
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
}
