import type { Amount } from '@/models/account';

/**
 * Formate un montant pour l'affichage, à la belge.
 */
export function formatAmount(value: Amount): string {
  const formatter = new Intl.NumberFormat('fr-BE', {
    style: 'currency',
    currency: value.currency,
  });
  return formatter.format(Number(value.amount));
}

/**
 * Formate une date ISO ("2026-10-20") pour l'affichage : "20/10/2026".
 */
export function formatDate(isoDate: string): string {
  const formatter = new Intl.DateTimeFormat('fr-BE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    timeZone: 'UTC',
  });
  return formatter.format(new Date(isoDate));
}
