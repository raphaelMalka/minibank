package com.minibank.legacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.minibank.account.AccountRepository;
import com.minibank.account.TransactionRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.FileSystemUtils;

/**
 * Dépose le fichier d'exemple dans un dossier inbox de test et vérifie que la route Camel l'importe.
 * Base H2 dédiée (legacytest) pour ne pas modifier les données des autres tests.
 */
@SpringBootTest(properties = {
    "minibank.legacy.inbox=target/test-legacy/inbox",
    "spring.datasource.url=jdbc:h2:mem:legacytest;DB_CLOSE_DELAY=-1"
})
@DirtiesContext
class LegacyImportRouteTest {

    private static final Path WORK_DIR = Path.of("target/test-legacy");
    private static final Path SAMPLE = Path.of("../legacy-data/samples/TRANSACTIONS_20261105.txt");

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeAll
    static void cleanWorkDir() throws IOException {
        FileSystemUtils.deleteRecursively(WORK_DIR);
        Files.createDirectories(WORK_DIR.resolve("inbox"));
    }

    @Test
    void importsLegacyFileAndMovesItToProcessed() throws IOException {
        Files.copy(SAMPLE, WORK_DIR.resolve("inbox/TRANSACTIONS_20261105.txt"));

        // Camel scrute le dossier en tâche de fond : on attend que le fichier soit traité
        await().atMost(Duration.ofSeconds(10))
                .until(() -> Files.exists(WORK_DIR.resolve("processed/TRANSACTIONS_20261105.txt")));

        // Compte 1 : 2450.75 - 23.50 + 150.00 = 2577.25
        assertThat(accountRepository.findById("1").orElseThrow().getBalance())
                .isEqualByComparingTo(new BigDecimal("2577.25"));
        // 6 transactions de data.sql + 4 importées
        assertThat(transactionRepository.count()).isEqualTo(10);
    }
}
