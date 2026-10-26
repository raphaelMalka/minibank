import type { Account, Transaction } from '@/models/account';

// Simule la latence d'un appel réseau
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

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
  return accounts;
}

export async function getAccount(id: string): Promise<Account | undefined> {
  await delay(300);
  return accounts.find((account) => account.id === id);
}

export async function getTransactions(accountId: string): Promise<Transaction[]> {
  await delay(300);
  return transactions
    .filter((tx) => tx.accountId === accountId)
    .sort((a, b) => new Date(b.bookingDate).getTime() - new Date(a.bookingDate).getTime());
}
