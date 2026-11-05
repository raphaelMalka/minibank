package com.minibank.account;

import com.minibank.api.model.Account;
import com.minibank.api.model.Transaction;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Logique métier de consultation des comptes. Ne connaît pas HTTP : ça, c'est le rôle du contrôleur. */
@Service
@Transactional(readOnly = true) // chaque méthode = une transaction en lecture seule
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    // Injection par constructeur : Spring fournit les repositories
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll(Sort.by("id")).stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .map(AccountMapper::toDto)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    public List<Transaction> getTransactions(String accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return transactionRepository.findByAccountIdOrderByBookingDateDesc(accountId).stream()
                .map(AccountMapper::toDto)
                .toList();
    }
}
