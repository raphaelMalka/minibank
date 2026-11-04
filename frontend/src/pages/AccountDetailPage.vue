<template>
  <q-page padding>
    <q-btn flat icon="arrow_back" label="Mes comptes" :to="{ name: 'accounts' }" class="q-mb-md" />

    <!-- État 1 : chargement -->
    <div v-if="loading" class="flex flex-center q-pa-lg">
      <q-spinner size="40px" color="primary" />
    </div>

    <!-- État 2 : erreur (y compris compte introuvable) -->
    <q-banner v-else-if="error || !account" class="bg-negative text-white" rounded>
      {{ error ?? 'Compte introuvable.' }}
      <template v-if="error" #action>
        <q-btn flat color="white" label="Réessayer" @click="loadData" />
      </template>
    </q-banner>

    <!-- État 3 : données -->
    <template v-else>
      <q-card flat bordered class="q-mb-lg">
        <q-card-section>
          <div class="text-h6">{{ account.label }}</div>
          <div class="text-caption text-grey-7">{{ account.iban }}</div>
          <div class="text-h5 q-mt-sm">{{ formatAmount(account.balance) }}</div>
        </q-card-section>
      </q-card>

      <div class="text-subtitle1 q-mb-sm">Transactions</div>

      <div v-if="transactions.length === 0" class="text-grey-7">Aucune transaction.</div>

      <q-list v-else bordered separator>
        <q-item v-for="tx in transactions" :key="tx.id">
          <q-item-section>
            <q-item-label>{{ tx.label }}</q-item-label>
            <q-item-label caption>{{ formatDate(tx.bookingDate) }}</q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-item-label :class="tx.type === 'CREDIT' ? 'text-positive' : 'text-negative'">
              {{ tx.type === 'CREDIT' ? '+' : '−' }} {{ formatAmount(tx.amount) }}
            </q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </template>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import type { Account, Transaction } from '@/models/account';
import { getAccount, getTransactions } from '@/services/accountService';
import { formatAmount, formatDate } from '@/utils/format';

const route = useRoute();
const accountId = String(route.params.accountId);

const account = ref<Account | undefined>();
const transactions = ref<Transaction[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

async function loadData() {
  loading.value = true;
  error.value = null;
  try {
    // Les deux appels partent en même temps : on attend ~300 ms au lieu de ~600 ms
    const [acc, txs] = await Promise.all([getAccount(accountId), getTransactions(accountId)]);
    account.value = acc;
    transactions.value = txs;
  } catch (e) {
    console.error(e);
    error.value = 'Impossible de charger ce compte. Réessayez plus tard.';
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>
