package com.minibank.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Une écriture sur un compte (table ACCOUNT_TRANSACTION : "transaction" est un mot réservé en SQL).
 */
@Entity
@Table(name = "account_transaction")
public class TransactionEntity {

    @Id
    private String id;

    // Plusieurs transactions pour un compte → clé étrangère account_id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false, length = 140)
    private String label;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING) // stocke "CREDIT"/"DEBIT" plutôt que 0/1
    @Column(nullable = false, length = 6)
    private EntryType type;

    protected TransactionEntity() {
        // exigé par JPA
    }

    public TransactionEntity(
            String id,
            AccountEntity account,
            LocalDate bookingDate,
            String label,
            BigDecimal amount,
            String currency,
            EntryType type) {
        this.id = id;
        this.account = account;
        this.bookingDate = bookingDate;
        this.label = label;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public EntryType getType() {
        return type;
    }
}
