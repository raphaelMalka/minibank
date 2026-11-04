import type { Amount } from '@/models/account';

/**
 * Formate un montant pour l'affichage, à la belge .
 */
export function formatAmount(value: Amount): string {
  const formatter = new Intl.NumberFormat('fr-BE', {
    style: 'currency',
    currency: value.currency,
  });
  return formatter.format(Number(value.amount));
}
