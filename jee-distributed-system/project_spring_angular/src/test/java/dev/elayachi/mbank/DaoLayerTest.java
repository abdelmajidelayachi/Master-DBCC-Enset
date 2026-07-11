package dev.elayachi.mbank;

import dev.elayachi.mbank.entities.AccountOperation;
import dev.elayachi.mbank.entities.CurrentAccount;
import dev.elayachi.mbank.entities.Customer;
import dev.elayachi.mbank.entities.SavingAccount;
import dev.elayachi.mbank.enums.AccountStatus;
import dev.elayachi.mbank.enums.OperationType;
import dev.elayachi.mbank.repositories.AccountOperationRepository;
import dev.elayachi.mbank.repositories.BankAccountRepository;
import dev.elayachi.mbank.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// replace=NONE : on garde la datasource H2 configuree (poolee par Hikari) car la base
// embarquee de remplacement est creee sans DB_CLOSE_DELAY et disparait entre deux connexions
@DataJpaTest(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DaoLayerTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private AccountOperationRepository accountOperationRepository;

    private Customer saveCustomer(String name) {
        return customerRepository.save(
                Customer.builder().name(name).email(name.toLowerCase() + "@mail.com").createdBy("test").build());
    }

    @Test
    void searchCustomerByName() {
        saveCustomer("Hassan");
        saveCustomer("Imane");
        assertThat(customerRepository.findByNameContainsIgnoreCase("has")).hasSize(1);
        assertThat(customerRepository.findByNameContainsIgnoreCase("a")).hasSize(2);
    }

    @Test
    void saveAccountsWithInheritanceAndFindByCustomer() {
        Customer customer = saveCustomer("Mohamed");

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(5000);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(9000);
        bankAccountRepository.save(currentAccount);

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(10000);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setStatus(AccountStatus.ACTIVATED);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(5.5);
        bankAccountRepository.save(savingAccount);

        var accounts = bankAccountRepository.findByCustomerId(customer.getId());
        assertThat(accounts).hasSize(2);
        assertThat(accounts).hasAtLeastOneElementOfType(CurrentAccount.class);
        assertThat(accounts).hasAtLeastOneElementOfType(SavingAccount.class);
    }

    @Test
    void saveOperationsAndFindByAccount() {
        Customer customer = saveCustomer("Yasmine");
        CurrentAccount account = new CurrentAccount();
        account.setId(UUID.randomUUID().toString());
        account.setBalance(5000);
        account.setCreatedAt(new Date());
        account.setStatus(AccountStatus.ACTIVATED);
        account.setCustomer(customer);
        bankAccountRepository.save(account);

        AccountOperation operation = new AccountOperation();
        operation.setOperationDate(new Date());
        operation.setAmount(1000);
        operation.setType(OperationType.CREDIT);
        operation.setBankAccount(account);
        operation.setDescription("Versement");
        operation.setPerformedBy("test");
        accountOperationRepository.save(operation);

        assertThat(accountOperationRepository.findByBankAccountId(account.getId())).hasSize(1);
    }
}
