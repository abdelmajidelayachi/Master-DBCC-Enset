package dev.elayachi.mbank.mappers;

import dev.elayachi.mbank.dtos.*;
import dev.elayachi.mbank.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount) {
        SavingBankAccountDTO dto = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount, dto);
        dto.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
        dto.setType("SavingAccount");
        return dto;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount) {
        CurrentBankAccountDTO dto = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount, dto);
        dto.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
        dto.setType("CurrentAccount");
        return dto;
    }

    public BankAccountDTO fromBankAccount(BankAccount bankAccount) {
        if (bankAccount instanceof SavingAccount savingAccount) {
            return fromSavingBankAccount(savingAccount);
        }
        return fromCurrentBankAccount((CurrentAccount) bankAccount);
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO dto = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation, dto);
        return dto;
    }
}
