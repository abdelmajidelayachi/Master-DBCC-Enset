import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  inject,
  signal
} from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { CustomerService } from '../../services/customer.service';
import { AccountService } from '../../services/account.service';
import { AccountOperationDTO, BankAccountDTO } from '../../models/account.model';
import { CustomerDTO } from '../../models/customer.model';

Chart.register(...registerables);

// Palette validee (CVD-safe, ordre fixe)
const C_BLUE = '#2a78d6';
const C_AQUA = '#1baf7a';
const C_YELLOW = '#eda100';
const C_RED = '#e34948';
const INK_MUTED = '#898781';
const GRID = '#e1e0d9';

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, DecimalPipe],
  template: `
    <div class="max-w-7xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">Tableau de bord</h1>

      @if (error()) {
        <div class="mb-6 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ error() }}
        </div>
      }

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p class="text-sm font-medium text-slate-500">Nombre de clients</p>
          <p class="mt-2 text-3xl font-bold text-slate-800">{{ customerCount() }}</p>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p class="text-sm font-medium text-slate-500">Nombre de comptes</p>
          <p class="mt-2 text-3xl font-bold text-slate-800">{{ accountCount() }}</p>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p class="text-sm font-medium text-slate-500">Solde total</p>
          <p class="mt-2 text-3xl font-bold text-indigo-600">
            {{ totalBalance() | currency: 'MAD' : 'symbol' : '1.2-2' }}
          </p>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <p class="text-sm font-medium text-slate-500">Nombre d'opérations</p>
          <p class="mt-2 text-3xl font-bold text-slate-800">{{ operationCount() | number }}</p>
        </div>
      </div>

      @if (loading()) {
        <p class="text-slate-500 text-sm">Chargement des données...</p>
      }

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4">Comptes par type</h2>
          <div class="relative h-64"><canvas #typeChart></canvas></div>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4">Solde par client</h2>
          <div class="relative h-64"><canvas #balanceChart></canvas></div>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4">Statut des comptes</h2>
          <div class="relative h-64"><canvas #statusChart></canvas></div>
        </div>
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4">
            Opérations récentes (crédits vs débits)
          </h2>
          <div class="relative h-64"><canvas #operationsChart></canvas></div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements AfterViewInit, OnDestroy {
  private customerService = inject(CustomerService);
  private accountService = inject(AccountService);

  @ViewChild('typeChart') typeChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('balanceChart') balanceChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('statusChart') statusChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('operationsChart') operationsChartRef!: ElementRef<HTMLCanvasElement>;

  private charts: Chart[] = [];

  loading = signal(true);
  error = signal<string | null>(null);
  customerCount = signal(0);
  accountCount = signal(0);
  totalBalance = signal(0);
  operationCount = signal(0);

  ngAfterViewInit(): void {
    this.loadData();
  }

  ngOnDestroy(): void {
    this.charts.forEach((c) => c.destroy());
  }

  private loadData(): void {
    forkJoin({
      customers: this.customerService.getCustomers(),
      accounts: this.accountService.getAccounts()
    })
      .pipe(
        switchMap(({ customers, accounts }) => {
          const sample = accounts.slice(0, 6);
          const opsRequests = sample.map((a) =>
            this.accountService.getOperations(a.id).pipe(catchError(() => of([])))
          );
          return (opsRequests.length ? forkJoin(opsRequests) : of([] as AccountOperationDTO[][])).pipe(
            map((operations) => ({ customers, accounts, operations: operations.flat() }))
          );
        })
      )
      .subscribe({
        next: ({ customers, accounts, operations }) => {
          this.loading.set(false);
          this.customerCount.set(customers.length);
          this.accountCount.set(accounts.length);
          this.totalBalance.set(accounts.reduce((sum, a) => sum + a.balance, 0));
          this.operationCount.set(operations.length);
          this.buildCharts(customers, accounts, operations);
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Impossible de charger les données du tableau de bord.');
        }
      });
  }

  private buildCharts(
    customers: CustomerDTO[],
    accounts: BankAccountDTO[],
    operations: AccountOperationDTO[]
  ): void {
    this.buildTypeChart(accounts);
    this.buildBalanceChart(accounts);
    this.buildStatusChart(accounts);
    this.buildOperationsChart(operations);
  }

  private buildTypeChart(accounts: BankAccountDTO[]): void {
    const current = accounts.filter((a) => a.type === 'CurrentAccount').length;
    const saving = accounts.filter((a) => a.type === 'SavingAccount').length;
    this.charts.push(
      new Chart(this.typeChartRef.nativeElement, {
        type: 'doughnut',
        data: {
          labels: ['Courant', 'Épargne'],
          datasets: [
            {
              data: [current, saving],
              backgroundColor: [C_BLUE, C_AQUA],
              borderColor: '#ffffff',
              borderWidth: 2
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { color: '#52514e' } } }
        }
      })
    );
  }

  private buildBalanceChart(accounts: BankAccountDTO[]): void {
    const byCustomer = new Map<string, number>();
    for (const a of accounts) {
      const name = a.customerDTO?.name ?? 'Inconnu';
      byCustomer.set(name, (byCustomer.get(name) ?? 0) + a.balance);
    }
    const top = [...byCustomer.entries()].sort((a, b) => b[1] - a[1]).slice(0, 7);
    this.charts.push(
      new Chart(this.balanceChartRef.nativeElement, {
        type: 'bar',
        data: {
          labels: top.map(([name]) => name),
          datasets: [
            {
              label: 'Solde total (MAD)',
              data: top.map(([, balance]) => balance),
              backgroundColor: C_BLUE,
              borderRadius: 4,
              maxBarThickness: 32
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            x: { grid: { display: false }, ticks: { color: INK_MUTED } },
            y: { grid: { color: GRID }, ticks: { color: INK_MUTED }, border: { display: false } }
          }
        }
      })
    );
  }

  private buildStatusChart(accounts: BankAccountDTO[]): void {
    const statuses: Array<BankAccountDTO['status']> = ['CREATED', 'ACTIVATED', 'SUSPENDED'];
    const labels = ['Créé', 'Activé', 'Suspendu'];
    const data = statuses.map((s) => accounts.filter((a) => a.status === s).length);
    this.charts.push(
      new Chart(this.statusChartRef.nativeElement, {
        type: 'pie',
        data: {
          labels,
          datasets: [
            {
              data,
              backgroundColor: [C_BLUE, C_AQUA, C_YELLOW],
              borderColor: '#ffffff',
              borderWidth: 2
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { color: '#52514e' } } }
        }
      })
    );
  }

  private buildOperationsChart(operations: AccountOperationDTO[]): void {
    const byDate = new Map<string, { credit: number; debit: number }>();
    for (const op of operations) {
      const day = (op.operationDate ?? '').slice(0, 10);
      if (!day) continue;
      const entry = byDate.get(day) ?? { credit: 0, debit: 0 };
      if (op.type === 'CREDIT') entry.credit += op.amount;
      else entry.debit += op.amount;
      byDate.set(day, entry);
    }
    const days = [...byDate.keys()].sort().slice(-14);
    this.charts.push(
      new Chart(this.operationsChartRef.nativeElement, {
        type: 'line',
        data: {
          labels: days,
          datasets: [
            {
              label: 'Crédits',
              data: days.map((d) => byDate.get(d)!.credit),
              borderColor: C_BLUE,
              backgroundColor: C_BLUE,
              borderWidth: 2,
              pointRadius: 3,
              pointBackgroundColor: C_BLUE,
              tension: 0.3
            },
            {
              label: 'Débits',
              data: days.map((d) => byDate.get(d)!.debit),
              borderColor: C_RED,
              backgroundColor: C_RED,
              borderWidth: 2,
              pointRadius: 3,
              pointBackgroundColor: C_RED,
              tension: 0.3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { position: 'bottom', labels: { color: '#52514e' } } },
          scales: {
            x: { grid: { display: false }, ticks: { color: INK_MUTED } },
            y: { grid: { color: GRID }, ticks: { color: INK_MUTED }, border: { display: false } }
          }
        }
      })
    );
  }
}
