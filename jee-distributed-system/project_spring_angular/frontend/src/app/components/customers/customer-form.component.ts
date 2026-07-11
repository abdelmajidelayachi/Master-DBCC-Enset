import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-form',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="max-w-xl mx-auto px-4 py-8">
      <h1 class="text-2xl font-bold text-slate-800 mb-6">
        {{ isEdit() ? 'Modifier le client' : 'Nouveau client' }}
      </h1>

      @if (error()) {
        <div class="mb-4 rounded-lg bg-red-50 border border-red-200 text-red-700 px-4 py-3 text-sm">
          {{ error() }}
        </div>
      }

      <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
        <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Nom</label>
            <input
              type="text"
              formControlName="name"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="Nom du client"
            />
            @if (form.controls.name.invalid && form.controls.name.touched) {
              <p class="mt-1 text-xs text-red-600">Le nom est obligatoire.</p>
            }
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Email</label>
            <input
              type="email"
              formControlName="email"
              class="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="email@exemple.com"
            />
            @if (form.controls.email.invalid && form.controls.email.touched) {
              <p class="mt-1 text-xs text-red-600">Veuillez saisir un email valide.</p>
            }
          </div>
          <div class="flex gap-3 pt-2">
            <button
              type="submit"
              [disabled]="form.invalid || saving()"
              class="rounded-lg bg-indigo-600 text-white text-sm font-semibold px-5 py-2.5 hover:bg-indigo-700 disabled:opacity-50 transition"
            >
              {{ saving() ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
            <a
              routerLink="/customers"
              class="rounded-lg border border-slate-300 text-slate-600 text-sm font-semibold px-5 py-2.5 hover:bg-slate-100 transition"
              >Annuler</a
            >
          </div>
        </form>
      </div>
    </div>
  `
})
export class CustomerFormComponent {
  private fb = inject(FormBuilder);
  private customerService = inject(CustomerService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isEdit = signal(false);
  saving = signal(false);
  error = signal<string | null>(null);
  private customerId: number | null = null;

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit.set(true);
      this.customerId = +idParam;
      this.customerService.getCustomer(this.customerId).subscribe({
        next: (customer) => this.form.patchValue({ name: customer.name, email: customer.email }),
        error: () => this.error.set('Impossible de charger le client.')
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.saving.set(true);
    this.error.set(null);
    const { name, email } = this.form.getRawValue();

    const request = this.isEdit()
      ? this.customerService.updateCustomer(this.customerId!, {
          id: this.customerId!,
          name,
          email
        })
      : this.customerService.createCustomer({ name, email });

    request.subscribe({
      next: () => this.router.navigate(['/customers']),
      error: (err) => {
        this.saving.set(false);
        this.error.set(err.error?.message ?? "Erreur lors de l'enregistrement du client.");
      }
    });
  }
}
