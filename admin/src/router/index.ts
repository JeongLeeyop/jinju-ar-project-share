import Vue from 'vue';
import VueRouter, { Route, RouteConfig } from 'vue-router';
import { UserModule } from '@/store/modules/user';
import { PermissionModule } from '@/store/modules/permission';

Vue.use(VueRouter);

export const constantRoutes: Array<RouteConfig> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: {
      hidden: true,
    },
  },
  {
    path: '/',
    name: 'Index',
    component: () => import('@/layout/default.vue'),
    meta: {
      hidden: true,
    },
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home.vue'),
      },
    ],
  },
];

export const asyncRoutes: RouteConfig[] = [
  /* 커뮤니티 관리 */
  {
    path: '/community',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '커뮤니티 관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'CommunityList',
        component: () => import('@/views/community/index.vue'),
        meta: {
          title: '커뮤니티 현황',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'detail/:uid',
        name: 'CommunityDetail',
        component: () => import('@/views/community/detail.vue'),
        meta: {
          title: '커뮤니티 상세',
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
      {
        path: 'members/:uid',
        name: 'CommunityMembers',
        component: () => import('@/views/community/members.vue'),
        meta: {
          title: '가입자 목록',
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
    ],
  },
  /* 커뮤니티 공간 */
  {
    path: '/space',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '커뮤니티 공간',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'SpaceList',
        component: () => import('@/views/space/index.vue'),
        meta: {
          title: '공간 조회',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'members/:uid',
        name: 'SpaceMembers',
        component: () => import('@/views/space/members.vue'),
        meta: {
          title: '공간 가입자 목록',
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
    ],
  },
  /* 커뮤니티 장터 */
  {
    path: '/marketplace',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '커뮤니티 장터',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'MarketplaceList',
        component: () => import('@/views/marketplace/index.vue'),
        meta: {
          title: '장터 조회',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'sales',
        name: 'MarketplaceSales',
        component: () => import('@/views/marketplace/sales.vue'),
        meta: {
          title: '매출 현황',
          roles: ['ROLE_ADMIN'],
        },
      },
    ],
  },
  /* 회원 관리 */
  {
    path: '/member',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '회원관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'MemberList',
        component: () => import('@/views/member/index.vue'),
        meta: {
          title: '회원 목록',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'detail/:uid',
        name: 'MemberDetail',
        component: () => import('@/views/member/detail.vue'),
        meta: {
          title: '회원 상세',
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
    ],
  },
  /* 문자 전송 */
  {
    path: '/sms',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '문자전송',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'SmsSend',
        component: () => import('@/views/sms/index.vue'),
        meta: {
          title: '문자 발송',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'history',
        name: 'SmsHistory',
        component: () => import('@/views/sms/history.vue'),
        meta: {
          title: '발송 내역',
          roles: ['ROLE_ADMIN'],
        },
      },
    ],
  },
];

const createRouter = () => new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: constantRoutes,
});

const router = createRouter();

export function resetRouter() {
  const newRouter = createRouter();
  (router as any).matcher = (newRouter as any).matcher;
}

router.afterEach(async (to: Route, from: Route) => {
  const pageTitle = '진주알 관리자페이지';
  document.title = pageTitle;
});

export default router;
