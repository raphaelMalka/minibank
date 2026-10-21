import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      { path: '', redirect: { name: 'accounts' } },
      {
        path: 'accounts',
        name: 'accounts',
        component: () => import('@/pages/AccountsPage.vue'),
      },
      {
        path: 'accounts/:accountId',
        name: 'account-detail',
        component: () => import('@/pages/AccountDetailPage.vue'),
      },
      {
        path: 'transfer',
        name: 'transfer',
        component: () => import('@/pages/TransferPage.vue'),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue'),
  },
];

export default routes;
