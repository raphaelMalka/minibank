package com.minibank.account;

/** Levée quand un compte demandé n'existe pas → traduite en HTTP 404 par ApiExceptionHandler. */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountId) {
        super("Compte introuvable : " + accountId);
    }
}
