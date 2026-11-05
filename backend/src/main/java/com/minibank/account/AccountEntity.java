package com.minibank.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

/**
 * Un compte tel qu'il est stocké en base (table ACCOUNT).
 * À ne pas confondre avec le DTO généré com.minibank.api.model.Account, qui est le format JSON de l'API.
 */
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 34) // un IBAN fait au plus 34 caractères
    private String iban;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, precision = 19, scale = 2) // DECIMAL(19,2) : jamais de flottant pour l'argent
    private BigDecimal balance;

    @Column(nullable = false, length = 3) // code ISO 4217 : EUR, CHF
    private String currency;

    // Verrouillage optimiste : empêche deux virements simultanés d'écraser le solde l'un de l'autre
    @Version
    private long version;

    protected AccountEntity() {
        // exigé par JPA
    }

    public AccountEntity(String id, String iban, String label, BigDecimal balance, String currency) {
        this.id = id;
        this.iban = iban;
        this.label = label;
        this.balance = balance;
        this.currency = currency;
    }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public String getId() {
        return id;
    }

    public String getIban() {
        return iban;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}
