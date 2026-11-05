package com.minibank.legacy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

/**
 * Une ligne du fichier d'export legacy (style COBOL, largeur fixe, 75 caractères) :
 *
 * <pre>
 * Pos  Long  Champ        COBOL           Exemple
 *  1    10   accountId    PIC X(10)       "1         "
 * 11     8   bookingDate  PIC 9(8)        "20261102"            (AAAAMMJJ)
 * 19     1   direction    PIC X           "D" / "C"
 * 20    13   amount       PIC 9(11)V99    "0000000002350"       (= 23.50, virgule implicite)
 * 33     3   currency     PIC X(3)        "EUR"
 * 36    40   label        PIC X(40)       "PRELEVEMENT PROXIMUS                    "
 * </pre>
 */
@FixedLengthRecord(length = 75)
public class LegacyTransactionRecord {

    // Texte COBOL (PIC X) : aligné à gauche, complété par des espaces à droite → align = "L" pour les retirer
    @DataField(pos = 1, length = 10, align = "L", trim = true)
    private String accountId;

    @DataField(pos = 11, length = 8, pattern = "yyyyMMdd")
    private LocalDate bookingDate;

    @DataField(pos = 19, length = 1)
    private String direction;

    // impliedDecimalSeparator : les 2 derniers chiffres sont les décimales (pas de point dans le fichier)
    @DataField(pos = 20, length = 13, precision = 2, impliedDecimalSeparator = true)
    private BigDecimal amount;

    @DataField(pos = 33, length = 3)
    private String currency;

    @DataField(pos = 36, length = 40, align = "L", trim = true)
    private String label;

    public String getAccountId() {
        return accountId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public String getDirection() {
        return direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLabel() {
        return label;
    }
}
