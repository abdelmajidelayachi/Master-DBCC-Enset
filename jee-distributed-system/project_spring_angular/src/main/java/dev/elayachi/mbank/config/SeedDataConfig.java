package dev.elayachi.mbank.config;

import dev.elayachi.mbank.dtos.BankAccountDTO;
import dev.elayachi.mbank.dtos.CustomerDTO;
import dev.elayachi.mbank.entities.AccountOperation;
import dev.elayachi.mbank.entities.BankAccount;
import dev.elayachi.mbank.enums.OperationType;
import dev.elayachi.mbank.repositories.AccountOperationRepository;
import dev.elayachi.mbank.repositories.AppUserRepository;
import dev.elayachi.mbank.repositories.BankAccountRepository;
import dev.elayachi.mbank.services.BankAccountService;
import dev.elayachi.mbank.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Donnees de demonstration : utilisateurs (admin/user1), clients,
 * comptes et historique d'operations sur les 10 derniers jours.
 */
@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedData(BankAccountService bankAccountService,
                               BankAccountRepository bankAccountRepository,
                               AccountOperationRepository accountOperationRepository,
                               AppUserRepository appUserRepository,
                               UserService userService) {
        return args -> {
            if (appUserRepository.findByUsername("admin").isEmpty()) {
                userService.addUser("admin", "12345", "ADMIN,USER");
                userService.addUser("user1", "12345", "USER");
            }
            if (!bankAccountRepository.findAll().isEmpty()) {
                return;
            }
            for (String name : List.of("Hassan", "Imane", "Mohamed")) {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name.toLowerCase() + "@gmail.com");
                CustomerDTO saved = bankAccountService.saveCustomer(customer);
                bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, saved.getId());
                bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, saved.getId());
            }
            // historique d'operations reparti sur les 10 derniers jours
            for (BankAccountDTO accountDTO : bankAccountService.bankAccountList()) {
                BankAccount account = bankAccountRepository.findById(accountDTO.getId()).orElseThrow();
                for (int i = 10; i >= 1; i--) {
                    boolean credit = Math.random() > 0.4;
                    double amount = 1000 + Math.random() * 12000;
                    AccountOperation operation = new AccountOperation();
                    operation.setOperationDate(Date.from(Instant.now().minus(i, ChronoUnit.DAYS)));
                    operation.setType(credit ? OperationType.CREDIT : OperationType.DEBIT);
                    operation.setAmount(amount);
                    operation.setDescription(credit ? "Versement" : "Retrait");
                    operation.setBankAccount(account);
                    operation.setPerformedBy("system");
                    accountOperationRepository.save(operation);
                    account.setBalance(account.getBalance() + (credit ? amount : -amount));
                }
                bankAccountRepository.save(account);
            }
        };
    }
}
