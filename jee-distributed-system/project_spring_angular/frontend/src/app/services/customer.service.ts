import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backendHost } from '../config';
import { CustomerDTO } from '../models/customer.model';
import { BankAccountDTO } from '../models/account.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private http = inject(HttpClient);

  getCustomers(): Observable<CustomerDTO[]> {
    return this.http.get<CustomerDTO[]>(`${backendHost}/customers`);
  }

  searchCustomers(keyword: string): Observable<CustomerDTO[]> {
    return this.http.get<CustomerDTO[]>(`${backendHost}/customers/search`, {
      params: { keyword }
    });
  }

  getCustomer(id: number): Observable<CustomerDTO> {
    return this.http.get<CustomerDTO>(`${backendHost}/customers/${id}`);
  }

  createCustomer(customer: { name: string; email: string }): Observable<CustomerDTO> {
    return this.http.post<CustomerDTO>(`${backendHost}/customers`, customer);
  }

  updateCustomer(id: number, customer: CustomerDTO): Observable<CustomerDTO> {
    return this.http.put<CustomerDTO>(`${backendHost}/customers/${id}`, customer);
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${backendHost}/customers/${id}`);
  }

  getCustomerAccounts(id: number): Observable<BankAccountDTO[]> {
    return this.http.get<BankAccountDTO[]>(`${backendHost}/customers/${id}/accounts`);
  }
}
