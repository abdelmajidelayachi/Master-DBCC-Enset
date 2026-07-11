package dev.elayachi.mbank.services;

import dev.elayachi.mbank.dtos.*;
import dev.elayachi.mbank.entities.*;
import dev.elayachi.mbank.enums.AccountStatus;
import dev.elayachi.mbank.enums.OperationType;
import dev.elayachi.mbank.exceptions.BalanceNotSufficientException;
import dev.elayachi.mbank.exceptions.BankAccountNotFoundException;
import dev.elayachi.mbank.exceptions.CustomerNotFoundException;
import dev.elayachi.mbank.mappers.BankAccountMapperImpl;
import dev.elayachi.mbank.repositories.AccountOperationRepository;
import dev.elayachi.mbank.repositories.BankAccountRepository;
import dev.elayachi.mbank.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl dtoMapper;

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Enregistrement d'un nouveau client");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customer.setCreatedBy(currentUsername());
        return dtoMapper.fromCustomer(customerRepository.save(customer));
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerDTO.getId())
                .orElseThrow(() -> new CustomerNotFoundException("Client introuvable"));
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        return dtoMapper.fromCustomer(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream().map(dtoMapper::fromCustomer).toList();
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.findByNameContainsIgnoreCase(keyword).stream()
                .map(dtoMapper::fromCustomer).toList();
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Client introuvable"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Client introuvable"));
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(initialBalance);
        account.setStatus(AccountStatus.CREATED);
        account.setOverDraft(overDraft);
        account.setCustomer(customer);
        account.setCreatedBy(currentUsername());
        return dtoMapper.fromCurrentBankAccount(bankAccountRepository.save(account));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Client introuvable"));
        SavingAccount account = new SavingAccount();
        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(new Date());
        account.setBalance(initialBalance);
        account.setStatus(AccountStatus.CREATED);
        account.setInterestRate(interestRate);
        account.setCustomer(customer);
        account.setCreatedBy(currentUsername());
        return dtoMapper.fromSavingBankAccount(bankAccountRepository.save(account));
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Compte bancaire introuvable"));
        return dtoMapper.fromBankAccount(bankAccount);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream().map(dtoMapper::fromBankAccount).toList();
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) {
        return bankAccountRepository.findByCustomerId(customerId).stream()
                .map(dtoMapper::fromBankAccount).toList();
    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Compte bancaire introuvable"));
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Solde insuffisant");
        }
        AccountOperation operation = new AccountOperation();
        operation.setType(OperationType.DEBIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setOperationDate(new Date());
        operation.setBankAccount(bankAccount);
        operation.setPerformedBy(currentUsername());
        accountOperationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Compte bancaire introuvable"));
        AccountOperation operation = new AccountOperation();
        operation.setType(OperationType.CREDIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setOperationDate(new Date());
        operation.setBankAccount(bankAccount);
        operation.setPerformedBy(currentUsername());
        accountOperationRepository.save(operation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Virement vers " + accountIdDestination + " : " + description);
        credit(accountIdDestination, amount, "Virement depuis " + accountIdSource + " : " + description);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        return accountOperationRepository.findByBankAccountId(accountId).stream()
                .map(dtoMapper::fromAccountOperation).toList();
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Compte bancaire introuvable"));
        Page<AccountOperation> operations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO historyDTO = new AccountHistoryDTO();
        historyDTO.setAccountId(bankAccount.getId());
        historyDTO.setBalance(bankAccount.getBalance());
        historyDTO.setCurrentPage(page);
        historyDTO.setPageSize(size);
        historyDTO.setTotalPages(operations.getTotalPages());
        historyDTO.setAccountOperationDTOS(
                operations.getContent().stream().map(dtoMapper::fromAccountOperation).toList());
        return historyDTO;
    }
}
