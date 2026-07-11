package dev.elayachi.mbank.dtos;

/**
 * Objets de requete simples de l'API, regroupes sous forme de records.
 */
public class Requests {
    public record LoginRequest(String username, String password) {
    }

    public record DebitRequest(String accountId, double amount, String description) {
    }

    public record CreditRequest(String accountId, double amount, String description) {
    }

    public record TransferRequest(String accountSource, String accountDestination, double amount,
                                  String description) {
    }

    public record NewCurrentAccountRequest(Long customerId, double initialBalance, double overDraft) {
    }

    public record NewSavingAccountRequest(Long customerId, double initialBalance, double interestRate) {
    }

    public record ChangePasswordRequest(String oldPassword, String newPassword) {
    }

    public record ChatRequest(String message) {
    }
}
