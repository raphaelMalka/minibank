package com.minibank.legacy;

import com.minibank.account.AccountEntity;
import com.minibank.account.AccountRepository;
import com.minibank.account.EntryType;
import com.minibank.account.TransactionEntity;
import com.minibank.account.TransactionRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Enregistre en base les transactions lues dans un fichier legacy et met à jour les soldes. */
@Service
public class LegacyImportService {

    private static final Logger log = LoggerFactory.getLogger(LegacyImportService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public LegacyImportService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Tout le fichier dans une seule transaction : une ligne invalide → rien n'est importé
     * (et Camel déplace le fichier dans error/).
     */
    @Transactional
    public int importRecords(List<LegacyTransactionRecord> records) {
        for (LegacyTransactionRecord record : records) {
            AccountEntity account = accountRepository.findById(record.getAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Compte inconnu : " + record.getAccountId()));

            if (!account.getCurrency().equals(record.getCurrency())) {
                throw new IllegalArgumentException("Devise incohérente pour le compte " + account.getId());
            }

            EntryType type = toEntryType(record.getDirection());
            if (type == EntryType.CREDIT) {
                account.credit(record.getAmount());
            } else {
                account.debit(record.getAmount());
            }

            transactionRepository.save(new TransactionEntity(
                    UUID.randomUUID().toString(),
                    account,
                    record.getBookingDate(),
                    record.getLabel(),
                    record.getAmount(),
                    record.getCurrency(),
                    type));
        }
        log.info("{} transaction(s) legacy importée(s)", records.size());
        return records.size();
    }

    private static EntryType toEntryType(String direction) {
        return switch (direction) {
            case "C" -> EntryType.CREDIT;
            case "D" -> EntryType.DEBIT;
            default -> throw new IllegalArgumentException("Sens inconnu : " + direction);
        };
    }
}
