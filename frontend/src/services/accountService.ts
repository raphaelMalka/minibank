import type { Account, Transaction } from '@/models/account';
import type { TransferRequest, TransferResult } from '@/models/transfer';
import { AMOUNT_PATTERN, fromCents, toCents } from '@/utils/money';

// Simule la latence d'un appel réseau
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

// Comme une vraie API (qui renvoie du JSON neuf à chaque appel), on renvoie des copies :
// la page ne peut pas modifier les données « serveur » par accident.
const copy = <T>(value: T): T => structuredClone(value);

const accounts: Account[] = [
  {
    id: '1',
    iban: 'BE68539007547034',
    label: 'Compte courant',
    balance: { amount: '2450.75', currency: 'EUR' },
  },
  {
    id: '2',
    iban: 'BE71096123456769',
    label: 'Compte épargne',
    balance: { amount: '12000.00', currency: 'EUR' },
  },
  {
    id: '3',
    iban: 'CH9300762011623852957',
    label: 'Compte CHF',
    balance: { amount: '830.20', currency: 'CHF' },
  },
];

const transactions: Transaction[] = [
  {
    id: 't1',
    accountId: '1',
    bookingDate: '2026-10-01',
    label: 'Salaire octobre',
    amount: { amount: '3200.00', currency: 'EUR' },
    type: 'CREDIT',
  },
  {
    id: 't2',
    accountId: '1',
    bookingDate: '2026-10-03',
    label: 'Loyer octobre',
    amount: { amount: '850.00', currency: 'EUR' },
    type: 'DEBIT',
  },
  {
    id: 't3',
    accountId: '1',
    bookingDate: '2026-10-15',
    label: 'Courses Delhaize',
    amount: { amount: '64.30', currency: 'EUR' },
    type: 'DEBIT',
  },
  {
    id: 't4',
    accountId: '2',
    bookingDate: '2026-10-05',
    label: 'Épargne mensuelle',
    amount: { amount: '500.00', currency: 'EUR' },
    type: 'CREDIT',
  },
  {
    id: 't5',
    accountId: '3',
    bookingDate: '2026-10-12',
    label: 'Abonnement CFF',
    amount: { amount: '49.80', currency: 'CHF' },
    type: 'DEBIT',
  },
  {
    id: 't6',
    accountId: '1',
    bookingDate: '2026-10-20',
    label: 'Restaurant',
    amount: { amount: '42.00', currency: 'EUR' },
    type: 'DEBIT',
  },
];

export async function getAccounts(): Promise<Account[]> {
  await delay(300);
  return copy(accounts);
}

export async function getAccount(id: string): Promise<Account | undefined> {
  await delay(300);
  const account = accounts.find((account) => account.id === id);
  return account && copy(account);
}

export async function getTransactions(accountId: string): Promise<Transaction[]> {
  await delay(300);
  return copy(transactions)
    .filter((tx) => tx.accountId === accountId)
    .sort((a, b) => new Date(b.bookingDate).getTime() - new Date(a.bookingDate).getTime());
}

// --- Virements ---------------------------------------------------------------

let nextTransactionId = transactions.length + 1;

export async function createTransfer(request: TransferRequest): Promise<TransferResult> {
  await delay(300);

  // Le « serveur » revalide tout, même si le formulaire l'a déjà fait
  const debtor = accounts.find((account) => account.id === request.debtorAccountId);
  const creditor = accounts.find((account) => account.id === request.creditorAccountId);
  const { amount, currency } = request.instructedAmount;

  if (!debtor || !creditor) {
    throw new Error('Compte introuvable.');
  }
  if (debtor.id === creditor.id) {
    throw new Error('Les comptes source et destination doivent être différents.');
  }
  if (debtor.balance.currency !== currency || creditor.balance.currency !== currency) {
    throw new Error('Les deux comptes doivent être dans la devise du virement.');
  }
  if (!AMOUNT_PATTERN.test(amount) || toCents(amount) <= 0n) {
    throw new Error('Montant invalide.');
  }
  if (toCents(amount) > toCents(debtor.balance.amount)) {
    throw new Error('Solde insuffisant.');
  }

  debtor.balance = {
    amount: fromCents(toCents(debtor.balance.amount) - toCents(amount)),
    currency,
  };
  creditor.balance = {
    amount: fromCents(toCents(creditor.balance.amount) + toCents(amount)),
    currency,
  };

  const executionDate = new Date().toISOString().slice(0, 10);
  const transferId = `tr${nextTransactionId}`;
  transactions.push(
    {
      id: `t${nextTransactionId++}`,
      accountId: debtor.id,
      bookingDate: executionDate,
      label: request.remittanceInformation,
      amount: request.instructedAmount,
      type: 'DEBIT',
    },
    {
      id: `t${nextTransactionId++}`,
      accountId: creditor.id,
      bookingDate: executionDate,
      label: request.remittanceInformation,
      amount: request.instructedAmount,
      type: 'CREDIT',
    },
  );

  return { transferId, executionDate };
}
