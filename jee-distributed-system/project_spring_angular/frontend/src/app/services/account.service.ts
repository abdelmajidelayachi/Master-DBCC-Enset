import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { backendHost } from '../config';
import {
  AccountHistoryDTO,
  AccountOperationDTO,
  BankAccountDTO
} from '../models/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private http = inject(HttpClient);

  getAccounts(): Observable<BankAccountDTO[]> {
    return this.http.get<BankAccountDTO[]>(`${backendHost}/accounts`);
  }

  getAccount(id: string): Observable<BankAccountDTO> {
    return this.http.get<BankAccountDTO>(`${backendHost}/accounts/${id}`);
  }

  getOperations(id: string): Observable<AccountOperationDTO[]> {
    return this.http.get<AccountOperationDTO[]>(`${backendHost}/accounts/${id}/operations`);
  }

  getPageOperations(id: string, page: number, size: number): Observable<AccountHistoryDTO> {
    return this.http.get<AccountHistoryDTO>(`${backendHost}/accounts/${id}/pageOperations`, {
      params: { page, size }
    });
  }

  debit(accountId: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${backendHost}/accounts/debit`, {
      accountId,
      amount,
      description
    });
  }

  credit(accountId: string, amount: number, description: string): Observable<void> {
    return this.http.post<void>(`${backendHost}/accounts/credit`, {
      accountId,
      amount,
      description
    });
  }

  transfer(
    accountSource: string,
    accountDestination: string,
    amount: number,
    description: string
  ): Observable<void> {
    return this.http.post<void>(`${backendHost}/accounts/transfer`, {
      accountSource,
      accountDestination,
      amount,
      description
    });
  }

  createCurrentAccount(
    customerId: number,
    initialBalance: number,
    overDraft: number
  ): Observable<BankAccountDTO> {
    return this.http.post<BankAccountDTO>(`${backendHost}/accounts/current`, {
      customerId,
      initialBalance,
      overDraft
    });
  }

  createSavingAccount(
    customerId: number,
    initialBalance: number,
    interestRate: number
  ): Observable<BankAccountDTO> {
    return this.http.post<BankAccountDTO>(`${backendHost}/accounts/saving`, {
      customerId,
      initialBalance,
      interestRate
    });
  }
}
