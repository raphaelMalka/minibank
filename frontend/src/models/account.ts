export type Currency = 'EUR' | 'CHF';

export interface Amount {
  amount: string; 
  currency: Currency;
}

export interface Account {
  id: string;
  iban: string;
  label: string; 
  balance: Amount;
}

export type TransactionType = 'CREDIT' | 'DEBIT';

export interface Transaction {
  id: string;
  accountId: string;
  bookingDate: string; 
  label: string;
  amount: Amount;
  type: TransactionType;
}