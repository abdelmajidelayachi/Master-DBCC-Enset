package dev.elayachi.mbank.web;

import dev.elayachi.mbank.dtos.*;
import dev.elayachi.mbank.dtos.Requests.*;
import dev.elayachi.mbank.exceptions.BalanceNotSufficientException;
import dev.elayachi.mbank.exceptions.BankAccountNotFoundException;
import dev.elayachi.mbank.exceptions.CustomerNotFoundException;
import dev.elayachi.mbank.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class BankAccountRestController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }

    @PostMapping("/debit")
    public DebitRequest debit(@RequestBody DebitRequest request)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.debit(request.accountId(), request.amount(), request.description());
        return request;
    }

    @PostMapping("/credit")
    public CreditRequest credit(@RequestBody CreditRequest request) throws BankAccountNotFoundException {
        bankAccountService.credit(request.accountId(), request.amount(), request.description());
        return request;
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest request)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(request.accountSource(), request.accountDestination(),
                request.amount(), request.description());
    }

    @PostMapping("/current")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CurrentBankAccountDTO saveCurrentAccount(@RequestBody NewCurrentAccountRequest request)
            throws CustomerNotFoundException {
        return bankAccountService.saveCurrentBankAccount(
                request.initialBalance(), request.overDraft(), request.customerId());
    }

    @PostMapping("/saving")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public SavingBankAccountDTO saveSavingAccount(@RequestBody NewSavingAccountRequest request)
            throws CustomerNotFoundException {
        return bankAccountService.saveSavingBankAccount(
                request.initialBalance(), request.interestRate(), request.customerId());
    }
}
