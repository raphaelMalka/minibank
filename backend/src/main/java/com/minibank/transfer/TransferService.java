package com.minibank.transfer;

import com.minibank.account.AccountEntity;
import com.minibank.account.AccountRepository;
import com.minibank.account.EntryType;
import com.minibank.account.TransactionEntity;
import com.minibank.account.TransactionRepository;
import com.minibank.api.model.TransferRequest;
import com.minibank.api.model.TransferResult;
import com.minibank.error.BusinessRuleException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Exécute un virement entre deux comptes de l'utilisateur. */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Tout ou rien : si une étape échoue (exception), la transaction est annulée (rollback)
     * et aucun solde n'est modifié.
     */
    @Transactional
    public TransferResult createTransfer(TransferRequest request) {
        // Le format (regex, champs obligatoires, 140 caractères) a déjà été validé par @Valid → 400.
        // Ici : les règles métier → 422.
        BigDecimal amount = new BigDecimal(request.getInstructedAmount().getAmount());
        String currency = request.getInstructedAmount().getCurrency().getValue();

        if (amount.signum() <= 0) {
            throw new BusinessRuleException("Le montant doit être supérieur à 0.");
        }
        if (request.getDebtorAccountId().equals(request.getCreditorAccountId())) {
            throw new BusinessRuleException("Les comptes source et destination doivent être différents.");
        }

        AccountEntity debtor = findAccount(request.getDebtorAccountId());
        AccountEntity creditor = findAccount(request.getCreditorAccountId());

        if (!debtor.getCurrency().equals(currency) || !creditor.getCurrency().equals(currency)) {
            throw new BusinessRuleException("Les deux comptes doivent être dans la devise du virement.");
        }
        if (debtor.getBalance().compareTo(amount) < 0) {
            throw new BusinessRuleException("Solde insuffisant.");
        }

        // Entités "managées" : Hibernate écrira les nouveaux soldes en base à la fin de la transaction
        debtor.debit(amount);
        creditor.credit(amount);

        LocalDate executionDate = LocalDate.now();
        String label = request.getRemittanceInformation().trim();
        transactionRepository.saveAll(List.of(
                new TransactionEntity(newId(), debtor, executionDate, label, amount, currency, EntryType.DEBIT),
                new TransactionEntity(newId(), creditor, executionDate, label, amount, currency, EntryType.CREDIT)));

        return new TransferResult(newId(), executionDate);
    }

    private AccountEntity findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessRuleException("Compte inconnu : " + accountId));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
