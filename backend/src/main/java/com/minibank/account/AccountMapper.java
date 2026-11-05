package com.minibank.account;

import com.minibank.api.model.Account;
import com.minibank.api.model.Amount;
import com.minibank.api.model.Currency;
import com.minibank.api.model.Transaction;
import com.minibank.api.model.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Conversion entités (base) → DTO (contrat de l'API). Le seul endroit qui connaît les deux mondes. */
final class AccountMapper {

    private AccountMapper() {
        // classe utilitaire, pas d'instance
    }

    static Account toDto(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getIban(),
                entity.getLabel(),
                toAmount(entity.getBalance(), entity.getCurrency()));
    }

    static Transaction toDto(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAccount().getId(),
                entity.getBookingDate(),
                entity.getLabel(),
                toAmount(entity.getAmount(), entity.getCurrency()),
                TransactionType.fromValue(entity.getType().name()));
    }

    // BigDecimal → "2450.75" (toujours 2 décimales, jamais de notation scientifique)
    static Amount toAmount(BigDecimal value, String currency) {
        String amount = value.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
        return new Amount(amount, Currency.fromValue(currency));
    }
}
