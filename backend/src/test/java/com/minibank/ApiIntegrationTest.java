package com.minibank;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Teste l'API de bout en bout (HTTP simulé → contrôleur → service → H2), sans démarrer de vrai serveur.
 * @Transactional : chaque test est annulé à la fin, la base repart des données de data.sql.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Lecture ---

    @Test
    void listsAccounts() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].balance.amount").value("2450.75"))
                .andExpect(jsonPath("$[0].balance.currency").value("EUR"));
    }

    @Test
    void returns404ProblemForUnknownAccount() throws Exception {
        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Compte introuvable : 99"));
    }

    @Test
    void listsTransactionsMostRecentFirst() throws Exception {
        mockMvc.perform(get("/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("t6"))
                .andExpect(jsonPath("$[0].bookingDate").value("2026-10-20"));
    }

    // --- Virement ---

    @Test
    void transferMovesMoneyAndCreatesTwoTransactions() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer("1", "2", "100.50", "EUR", "Épargne novembre")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transferId").isNotEmpty());

        mockMvc.perform(get("/accounts/1")).andExpect(jsonPath("$.balance.amount").value("2350.25"));
        mockMvc.perform(get("/accounts/2")).andExpect(jsonPath("$.balance.amount").value("12100.50"));
        mockMvc.perform(get("/accounts/1/transactions"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].type").value("DEBIT"))
                .andExpect(jsonPath("$[0].label").value("Épargne novembre"));
    }

    @Test
    void rejectsTransferWithInsufficientBalanceWith422() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer("1", "2", "99999.00", "EUR", "Trop cher")))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Solde insuffisant."));

        // Rien n'a bougé
        mockMvc.perform(get("/accounts/1")).andExpect(jsonPath("$.balance.amount").value("2450.75"));
    }

    @Test
    void rejectsTransferBetweenDifferentCurrenciesWith422() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer("1", "3", "10.00", "EUR", "Vers CHF")))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void rejectsMalformedAmountWith400() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer("1", "2", "-50", "EUR", "Négatif")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void rejectsEmptyRemittanceInformationWith400() throws Exception {
        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer("1", "2", "10.00", "EUR", "")))
                .andExpect(status().isBadRequest());
    }

    private static String transfer(String from, String to, String amount, String currency, String label) {
        return """
                {
                  "debtorAccountId": "%s",
                  "creditorAccountId": "%s",
                  "instructedAmount": { "amount": "%s", "currency": "%s" },
                  "remittanceInformation": "%s"
                }
                """.formatted(from, to, amount, currency, label);
    }
}
