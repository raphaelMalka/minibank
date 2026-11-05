package com.minibank.error;

/**
 * Requête bien formée mais refusée par une règle métier (solde insuffisant, devises différentes...)
 * → traduite en HTTP 422 par ApiExceptionHandler.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
