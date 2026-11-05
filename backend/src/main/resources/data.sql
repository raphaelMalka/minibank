-- Données initiales, chargées à chaque démarrage après la création des tables par Hibernate
-- (mêmes données que le mock du front)

INSERT INTO account (id, iban, label, balance, currency, version) VALUES
  ('1', 'BE68539007547034', 'Compte courant', 2450.75, 'EUR', 0),
  ('2', 'BE71096123456769', 'Compte épargne', 12000.00, 'EUR', 0),
  ('3', 'CH9300762011623852957', 'Compte CHF', 830.20, 'CHF', 0);

INSERT INTO account_transaction (id, account_id, booking_date, label, amount, currency, type) VALUES
  ('t1', '1', DATE '2026-10-01', 'Salaire octobre', 3200.00, 'EUR', 'CREDIT'),
  ('t2', '1', DATE '2026-10-03', 'Loyer octobre', 850.00, 'EUR', 'DEBIT'),
  ('t3', '1', DATE '2026-10-15', 'Courses Delhaize', 64.30, 'EUR', 'DEBIT'),
  ('t4', '2', DATE '2026-10-05', 'Épargne mensuelle', 500.00, 'EUR', 'CREDIT'),
  ('t5', '3', DATE '2026-10-12', 'Abonnement CFF', 49.80, 'CHF', 'DEBIT'),
  ('t6', '1', DATE '2026-10-20', 'Restaurant', 42.00, 'EUR', 'DEBIT');
