package dev.elayachi.mbank.dtos;

import dev.elayachi.mbank.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    // "CurrentAccount" ou "SavingAccount"
    private String type;
    private CustomerDTO customerDTO;
}
