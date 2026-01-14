import Vue from 'vue';
import VueRouter, { Route, RouteConfig } from 'vue-router';
import { Message } from 'element-ui';

Vue.use(VueRouter);

export const constantRoutes: Array<RouteConfig> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: {
      hidden: true,
      title: '로그인 페이지',
    },
  },
  {
    path: '',
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
        meta: {
          title: '메인페이지',
        },
      },
      {
        path: '/channel/:domain',
        name: 'Channel',
        component: () => import('@/views/channel.vue'),
        beforeEnter: (to, from, next) => {
          if (!to.params.domain) {
              Message.warning('커뮤니티를 선택해 주세요.');
          } else next();
        },
        meta: {
          title: '채널 정보',
        },
      },
    ],
  },
  {
    path: '/initCommunity',
    name: 'InitCommunity',
    component: () => import('@/layout/creator.vue'),
    meta: {
      hidden: true,
      roles: ['ROLE_CREATOR'],
    },
    children: [
      {
        path: '/',
        name: 'CreateCommunity',
        component: () => import('@/views/setting/communitySetting.vue'),
        meta: {
          title: '커뮤니티 생성',
        },
      },
    ],
  },
  {
    path: '/community/:domain',
    name: 'Community',
    component: () => import('@/layout/user.vue'),
    beforeEnter: (to, from, next) => {
      if (!to.params.domain) {
          Message.warning('커뮤니티를 선택해 주세요.');
      } else next();
    },
    meta: {
      hidden: true,
      // roles: ['ROLE_USER'], // 로그아웃 상태에서도 커뮤니티 접근 가능하도록 제거
      title: '커뮤니티',
    },
    children: [
      {
        path: '/',
        name: 'CommunityMain',
        component: () => import('@/views/community.vue'),
        meta: {
          title: '커뮤니티',
        },
      },
      {
        path: 'space/:spaceId',
        name: 'CommunitySpace',
        component: () => import('@/views/communitySpace.vue'),
        meta: {
          title: '커뮤니티 공간',
        },
      },
      {
        path: 'chat/:spaceId',
        name: 'CommunityChat',
        component: () => import('@/views/communityChat.vue'),
        meta: {
          title: '커뮤니티 채팅',
        },
      },
      {
        path: 'rpoint-history',
        name: 'RPointHistory',
        component: () => import('@/views/rpointHistory.vue'),
        meta: {
          title: '알포인트 내역',
        },
      },
      {
        path: 'lession',
        name: 'Lession',
        component: () => import('@/views/lession.vue'),
        meta: {
          title: '강의 목록',
        },
      },
      {
        path: 'lession/:lessionUid',
        name: 'Video',
        component: () => import('@/views/video.vue'),
        meta: {
          title: '강의 영상 목록',
        },
      },
      {
        path: 'lession/:lessionUid/video/:videoIdx',
        name: 'VideoDetail',
        component: () => import('@/views/videoDetail.vue'),
        meta: {
          title: '동영상 강좌',
        },
      },
      {
        path: 'member',
        name: 'Member',
        component: () => import('@/views/member.vue'),
        meta: {
          title: '회원들',
        },
      },
      {
        path: 'calendar',
        name: 'Calendar',
        component: () => import('@/views/calendar.vue'),
        meta: {
          title: '캘린더',
        },
      },
      {
        path: 'marketplace',
        name: 'Marketplace',
        component: () => import('@/views/marketplace.vue'),
        meta: {
          title: '장터',
        },
      },
      {
        path: 'marketplace/my',
        name: 'MyMarketplace',
        component: () => import('@/views/myMarketplace.vue'),
        meta: {
          title: '내 장터 관리',
        },
      },
      {
        path: 'schedule/my',
        name: 'MySchedule',
        component: () => import('@/views/mySchedule.vue'),
        meta: {
          title: '내 일정 관리',
        },
      },
      {
        path: 'marketplace/offline/:marketplaceUid',
        name: 'OfflineMarketplace',
        component: () => import('@/views/marketplace.vue'),
        meta: {
          title: '오프라인 장터',
        },
      },
      {
        path: 'marketplace/:productId',
        name: 'MarketplaceDetail',
        component: () => import('@/views/marketplaceDetail.vue'),
        meta: {
          title: '상품 상세',
        },
      },
      {
        path: 'activity-list',
        name: 'ActivityList',
        component: () => import('@/views/activityList.vue'),
        meta: {
          title: '활동리스트',
        },
      },
      {
        path: 'admin/members',
        name: 'MemberManagement',
        component: () => import('@/views/memberManagement.vue'),
        meta: {
          title: '회원 관리',
        },
      },
      {
        path: 'admin/community-management',
        name: 'CommunityManagement',
        component: () => import('@/views/communityManagement.vue'),
        meta: {
          title: '커뮤니티 관리',
        },
      },
      {
        path: 'admin/space-management',
        name: 'SpaceManagement',
        component: () => import('@/views/spaceManagement.vue'),
        meta: {
          title: '공간 관리',
        },
      },
      {
        path: 'admin/sms-management',
        name: 'SmsManagement',
        component: () => import('@/views/smsManagement.vue'),
        meta: {
          title: '문자 발송 관리',
        },
      },
      {
        path: 'community-settings',
        name: 'CommunitySettings',
        component: () => import('@/views/communitySettings.vue'),
        meta: {
          title: '커뮤니티 설정',
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
  const pageTitle = `진주알 커뮤니티 : ${to.meta?.title}`;
  document.title = pageTitle;
  // const { roles } = UserModule;
  // await PermissionModule.GenerateRoutes(roles);
  // await router.addRoutes(PermissionModule.routes);
});

export default router;
