import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CustomersComponent } from './components/customers/customers.component';
import { CustomerFormComponent } from './components/customers/customer-form.component';
import { CustomerAccountsComponent } from './components/customers/customer-accounts.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { NewAccountComponent } from './components/accounts/new-account.component';
import { ChatComponent } from './components/chat/chat.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { AdminRequiredComponent } from './components/admin-required/admin-required.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'customers', component: CustomersComponent, canActivate: [authGuard] },
  {
    path: 'customers/new',
    component: CustomerFormComponent,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'customers/:id/edit',
    component: CustomerFormComponent,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'customers/:id/accounts',
    component: CustomerAccountsComponent,
    canActivate: [authGuard]
  },
  { path: 'accounts', component: AccountsComponent, canActivate: [authGuard] },
  {
    path: 'accounts/new',
    component: NewAccountComponent,
    canActivate: [authGuard, adminGuard]
  },
  { path: 'chat', component: ChatComponent, canActivate: [authGuard] },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [authGuard] },
  { path: 'admin-required', component: AdminRequiredComponent, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' }
];
