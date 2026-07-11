package dev.elayachi.mbank.services;

import dev.elayachi.mbank.dtos.*;
import dev.elayachi.mbank.exceptions.BalanceNotSufficientException;
import dev.elayachi.mbank.exceptions.BankAccountNotFoundException;
import dev.elayachi.mbank.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException;

    void deleteCustomer(Long customerId);

    List<CustomerDTO> listCustomers();

    List<CustomerDTO> searchCustomers(String keyword);

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException;

    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException;

    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;

    List<BankAccountDTO> bankAccountList();

    List<BankAccountDTO> getCustomerAccounts(Long customerId);

    void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException;

    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;

    void transfer(String accountIdSource, String accountIdDestination, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
