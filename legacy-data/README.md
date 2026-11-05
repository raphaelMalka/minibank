# legacy-data

Faux exports mainframe (fichiers texte à largeur fixe, style COBOL) importés par la route Camel du backend.

- `samples/` : fichiers d'exemple.
- `inbox/` : dossier surveillé. Copier un fichier ici pendant que le backend tourne :
  `cp samples/TRANSACTIONS_20261105.txt inbox/`
- `processed/` / `error/` : où Camel déplace le fichier après import (réussi / échoué). Non versionnés.

Format d'une ligne (75 caractères) : voir `backend/src/main/java/com/minibank/legacy/LegacyTransactionRecord.java`.
