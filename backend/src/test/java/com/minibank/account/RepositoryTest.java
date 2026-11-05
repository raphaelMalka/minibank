package com.minibank.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

/** Vérifie le mapping JPA et les données de data.sql (démarre seulement la couche base de données). */
@DataJpaTest
class RepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void loadsInitialAccounts() {
        assertThat(accountRepository.findAll()).hasSize(3);

        AccountEntity current = accountRepository.findById("1").orElseThrow();
        assertThat(current.getIban()).isEqualTo("BE68539007547034");
        assertThat(current.getBalance()).isEqualByComparingTo(new BigDecimal("2450.75"));
    }

    @Test
    void returnsTransactionsMostRecentFirst() {
        List<TransactionEntity> transactions = transactionRepository.findByAccountIdOrderByBookingDateDesc("1");

        assertThat(transactions)
                .extracting(TransactionEntity::getId)
                .containsExactly("t6", "t3", "t2", "t1");
    }
}
