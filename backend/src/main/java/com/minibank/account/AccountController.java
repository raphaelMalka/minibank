package com.minibank.account;

import com.minibank.api.AccountsApi;
import com.minibank.api.model.Account;
import com.minibank.api.model.Transaction;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implémente l'interface générée depuis openapi.yaml : les URLs, méthodes HTTP et paramètres
 * viennent de AccountsApi. Ici on ne fait que déléguer au service et choisir le code HTTP.
 */
@RestController
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @Override
    public ResponseEntity<Account> getAccount(String accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    @Override
    public ResponseEntity<List<Transaction>> getAccountTransactions(String accountId) {
        return ResponseEntity.ok(accountService.getTransactions(accountId));
    }
}
