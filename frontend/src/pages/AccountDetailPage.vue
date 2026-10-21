<template>
  <q-page padding>
    <div class="text-h5">Détail du compte</div>
    <p>Numéro du compte : {{ route.params.accountId }}</p>
  </q-page>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';

const route = useRoute();
</script>
