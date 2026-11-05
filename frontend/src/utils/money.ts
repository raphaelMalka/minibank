// Calculs d'argent en centimes entiers (bigint), jamais en number : 0.1 + 0.2 !== 0.3

/** Montant décimal positif, au plus 2 décimales : "125", "125.5", "125.50". */
export const AMOUNT_PATTERN = /^\d+(\.\d{1,2})?$/;

/** "2450.75" → 245075n. Suppose une chaîne qui respecte AMOUNT_PATTERN. */
export function toCents(amount: string): bigint {
  const [integerPart = '0', decimalPart = ''] = amount.split('.');
  return BigInt(integerPart) * 100n + BigInt(decimalPart.padEnd(2, '0'));
}

/** 245075n → "2450.75" (montants positifs uniquement). */
export function fromCents(cents: bigint): string {
  const decimals = (cents % 100n).toString().padStart(2, '0');
  return `${cents / 100n}.${decimals}`;
}

/** Saisie utilisateur → chaîne décimale : " 125,5 " → "125.5". */
export function normalizeAmountInput(input: string): string {
  return input.trim().replace(',', '.');
}
