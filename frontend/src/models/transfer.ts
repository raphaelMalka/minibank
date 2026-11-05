import type { Amount } from '@/models/account';

// Noms inspirés de Berlin Group / PSD2 : débiteur = compte qui paie, créancier = compte qui reçoit
export interface TransferRequest {
  debtorAccountId: string;
  creditorAccountId: string;
  instructedAmount: Amount;
  remittanceInformation: string; // libellé / communication
}

export interface TransferResult {
  transferId: string;
  executionDate: string;
}
