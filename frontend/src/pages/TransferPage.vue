<template>
  <q-page padding>
    <div class="text-h5 q-mb-md">Virement</div>

    <!-- Chargement des comptes : 3 états -->
    <div v-if="loading" class="flex flex-center q-pa-lg">
      <q-spinner size="40px" color="primary" />
    </div>

    <q-banner v-else-if="loadError" class="bg-negative text-white" rounded>
      {{ loadError }}
      <template #action>
        <q-btn flat color="white" label="Réessayer" @click="loadAccounts" />
      </template>
    </q-banner>

    <!-- Après envoi réussi : confirmation -->
    <q-card v-else-if="result" flat bordered class="form-width">
      <q-card-section>
        <div class="text-h6 text-positive"><q-icon name="check_circle" /> Virement effectué</div>
        <p class="q-mt-sm q-mb-none">
          {{ formatAmount(instructedAmount) }} le {{ formatDate(result.executionDate) }}
          <br />
          <span class="text-caption text-grey-7">Référence : {{ result.transferId }}</span>
        </p>
      </q-card-section>
      <q-card-actions>
        <q-btn
          flat
          color="primary"
          label="Voir le compte débité"
          :to="{ name: 'account-detail', params: { accountId: debtorAccountId } }"
        />
        <q-btn flat label="Nouveau virement" @click="startNewTransfer" />
      </q-card-actions>
    </q-card>

    <!-- Formulaire -->
    <q-form v-else class="q-gutter-md form-width" @submit="onSubmit">
      <q-select
        v-model="debtorAccountId"
        :options="accounts"
        option-value="id"
        :option-label="accountOptionLabel"
        emit-value
        map-options
        label="Compte à débiter"
        outlined
        :rules="[required]"
        lazy-rules
      />

      <q-select
        v-model="creditorAccountId"
        :options="creditorOptions"
        option-value="id"
        :option-label="accountOptionLabel"
        emit-value
        map-options
        label="Compte à créditer"
        outlined
        :disable="!debtorAccountId"
        :rules="[required]"
        lazy-rules
      >
        <template #no-option>
          <q-item>
            <q-item-section class="text-grey-7">Aucun compte compatible</q-item-section>
          </q-item>
        </template>
      </q-select>

      <q-input
        v-model="amountInput"
        label="Montant"
        inputmode="decimal"
        outlined
        :suffix="debtorAccount?.balance.currency"
        :rules="amountRules"
        lazy-rules
      />

      <q-input
        v-model="remittanceInformation"
        label="Communication"
        outlined
        counter
        maxlength="140"
        :rules="[required]"
        lazy-rules
      />

      <q-banner v-if="submitError" class="bg-negative text-white" rounded>
        {{ submitError }}
      </q-banner>

      <q-btn type="submit" color="primary" label="Valider le virement" :loading="submitting" />
    </q-form>
  </q-page>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import type { ValidationRule } from 'quasar';
import type { Account, Amount } from '@/models/account';
import type { TransferResult } from '@/models/transfer';
import { createTransfer, getAccounts } from '@/services/accountService';
import { formatAmount, formatDate } from '@/utils/format';
import { AMOUNT_PATTERN, normalizeAmountInput, toCents } from '@/utils/money';

// --- Chargement des comptes ---
const accounts = ref<Account[]>([]);
const loading = ref(true);
const loadError = ref<string | null>(null);

async function loadAccounts() {
  loading.value = true;
  loadError.value = null;
  try {
    accounts.value = await getAccounts();
  } catch (e) {
    console.error(e);
    loadError.value = 'Impossible de charger vos comptes. Réessayez plus tard.';
  } finally {
    loading.value = false;
  }
}

onMounted(loadAccounts);

// --- Champs du formulaire ---
const debtorAccountId = ref<string | null>(null);
const creditorAccountId = ref<string | null>(null);
const amountInput = ref('');
const remittanceInformation = ref('');

// --- Valeurs dérivées ---
const debtorAccount = computed(() =>
  accounts.value.find((account) => account.id === debtorAccountId.value),
);

// Destination possible : un autre compte, dans la même devise
const creditorOptions = computed(() =>
  accounts.value.filter(
    (account) =>
      account.id !== debtorAccountId.value &&
      account.balance.currency === debtorAccount.value?.balance.currency,
  ),
);

const instructedAmount = computed<Amount>(() => ({
  amount: normalizeAmountInput(amountInput.value),
  currency: debtorAccount.value?.balance.currency ?? 'EUR',
}));

// Si on change de compte source, le compte destination choisi peut ne plus être valable
watch(debtorAccountId, () => {
  if (!creditorOptions.value.some((account) => account.id === creditorAccountId.value)) {
    creditorAccountId.value = null;
  }
});

function accountOptionLabel(account: Account): string {
  return `${account.label} (${formatAmount(account.balance)})`;
}

// --- Règles de validation (confort) : true = OK, string = message d'erreur ---
// Quasar les évalue dans l'ordre et s'arrête à la première qui échoue.
const required: ValidationRule = (val) =>
  (val !== null && val !== undefined && String(val).trim() !== '') || 'Champ obligatoire';

const amountRules: ValidationRule<string>[] = [
  required,
  (val) => AMOUNT_PATTERN.test(normalizeAmountInput(val)) || 'Format attendu : 125,50',
  (val) => toCents(normalizeAmountInput(val)) > 0n || 'Le montant doit être supérieur à 0',
  (val) =>
    !debtorAccount.value ||
    toCents(normalizeAmountInput(val)) <= toCents(debtorAccount.value.balance.amount) ||
    'Solde insuffisant',
];

// --- Envoi ---
const submitting = ref(false);
const submitError = ref<string | null>(null);
const result = ref<TransferResult | null>(null);

// Appelée par q-form uniquement si toutes les règles passent
async function onSubmit() {
  if (!debtorAccountId.value || !creditorAccountId.value) return;

  submitting.value = true;
  submitError.value = null;
  try {
    result.value = await createTransfer({
      debtorAccountId: debtorAccountId.value,
      creditorAccountId: creditorAccountId.value,
      instructedAmount: instructedAmount.value,
      remittanceInformation: remittanceInformation.value.trim(),
    });
  } catch (e) {
    console.error(e);
    submitError.value = e instanceof Error ? e.message : 'Le virement a échoué.';
  } finally {
    submitting.value = false;
  }
}

function startNewTransfer() {
  debtorAccountId.value = null;
  creditorAccountId.value = null;
  amountInput.value = '';
  remittanceInformation.value = '';
  submitError.value = null;
  result.value = null;
  void loadAccounts(); // les soldes ont changé
}
</script>

<style scoped>
.form-width {
  max-width: 500px;
}
</style>
