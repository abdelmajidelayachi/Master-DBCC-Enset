import { CustomerDTO } from './customer.model';

export interface BankAccountDTO {
  id: string;
  balance: number;
  createdAt: string;
  status: 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
  type: 'CurrentAccount' | 'SavingAccount';
  customerDTO: CustomerDTO;
  overDraft?: number;
  interestRate?: number;
}

export interface AccountOperationDTO {
  id: number;
  operationDate: string;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  description: string;
}

export interface AccountHistoryDTO {
  accountId: string;
  balance: number;
  currentPage: number;
  totalPages: number;
  pageSize: number;
  accountOperationDTOS: AccountOperationDTO[];
}
