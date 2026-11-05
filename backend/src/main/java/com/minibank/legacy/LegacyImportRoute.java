package com.minibank.legacy;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.fixed.BindyFixedLengthDataFormat;
import org.springframework.stereotype.Component;

/**
 * Route Camel : surveille le dossier inbox, lit chaque fichier .txt (largeur fixe),
 * le transforme en objets Java et les enregistre en base.
 * Succès → fichier déplacé dans processed/ ; échec → dans error/.
 */
@Component
public class LegacyImportRoute extends RouteBuilder {

    @Override
    public void configure() {
        BindyFixedLengthDataFormat legacyFormat = new BindyFixedLengthDataFormat(LegacyTransactionRecord.class);
        legacyFormat.setUnwrapSingleInstance(false); // toujours une List, même pour un fichier d'une ligne

        from("file:{{minibank.legacy.inbox}}"
                        + "?include=.*\\.txt"
                        + "&move=../processed/${file:name}"
                        + "&moveFailed=../error/${file:name}")
                .routeId("legacy-transactions-import")
                .log("Import du fichier legacy ${header.CamelFileName}")
                .unmarshal(legacyFormat)
                .bean(LegacyImportService.class, "importRecords")
                .log("Fichier ${header.CamelFileName} importé : ${body} transaction(s)");
    }
}
