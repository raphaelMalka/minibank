<template>
  <q-page padding>
    <div class="text-h5 q-mb-md">Mes comptes</div>

    <!-- État 1 : chargement -->
    <div v-if="loading" class="flex flex-center q-pa-lg">
      <q-spinner size="40px" color="primary" />
    </div>

    <!-- État 2 : erreur -->
    <q-banner v-else-if="error" class="bg-negative text-white" rounded>
      {{ error }}
      <template #action>
        <q-btn flat color="white" label="Réessayer" @click="loadAccounts" />
      </template>
    </q-banner>

    <!-- État 3 : données (cas particulier : liste vide) -->
    <div v-else-if="accounts.length === 0" class="text-grey-7">Aucun compte.</div>

    <q-list v-else bordered separator>
      <q-item
        v-for="account in accounts"
        :key="account.id"
        clickable
        :to="{ name: 'account-detail', params: { accountId: account.id } }"
      >
        <q-item-section>
          <q-item-label>{{ account.label }}</q-item-label>
          <q-item-label caption>{{ account.iban }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <q-item-label class="text-weight-medium">
            {{ formatAmount(account.balance) }}
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { Account } from '@/models/account';
import { getAccounts } from '@/services/accountService';
import { formatAmount } from '@/utils/format';

const accounts = ref<Account[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

async function loadAccounts() {
  loading.value = true;
  error.value = null;
  try {
    accounts.value = await getAccounts();
  } catch (e) {
    console.error(e);
    error.value = 'Impossible de charger vos comptes. Réessayez plus tard.';
  } finally {
    loading.value = false;
  }
}

onMounted(loadAccounts);
</script>
