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
      {
        path: 'setting',
        name: 'Setting',
        component: () => import('@/views/setting.vue'),
      },
      {
				path: 'manager/list',
				name: 'ManagerOrderList',
				component: () => import('@/views/order2/manager/index.vue'),
				meta: {
					title: '전체주문',
					roles: ['ROLE_SHOP_ADMIN'],
				},
			},
      {
				path: 'shop',
				name: 'Shop',
				component: () => import('@/views/order2/shop.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
          hidden: true,
				},
			},
      {
        path: 'post',
        component: () => import('@/views/post/List/index.vue'),
        name: 'Post',
        meta: {
          title: '게시글 관리',
          roles: ['ROLE_BOARD'],
        },
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
      roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
    },
    children: [
      {
        path: '',
        name: 'CommunityList',
        component: () => import('@/views/community/index.vue'),
        meta: {
          title: '커뮤니티 현황',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
      {
        path: 'detail/:uid',
        name: 'CommunityDetail',
        component: () => import('@/views/community/detail.vue'),
        meta: {
          title: '커뮤니티 상세',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
          hidden: true,
        },
      },
      {
        path: 'members/:uid',
        name: 'CommunityMembers',
        component: () => import('@/views/community/members.vue'),
        meta: {
          title: '가입자 목록',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
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
      roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
    },
    children: [
      {
        path: '',
        name: 'SpaceList',
        component: () => import('@/views/space/index.vue'),
        meta: {
          title: '공간 조회',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
      {
        path: 'members/:uid',
        name: 'SpaceMembers',
        component: () => import('@/views/space/members.vue'),
        meta: {
          title: '공간 가입자 목록',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
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
      roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
    },
    children: [
      {
        path: '',
        name: 'MarketplaceList',
        component: () => import('@/views/marketplace/index.vue'),
        meta: {
          title: '장터 조회',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
      /* {
        path: 'sales',
        name: 'MarketplaceSales',
        component: () => import('@/views/marketplace/sales.vue'),
        meta: {
          title: '매출 현황',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      }, */
    ],
  },
  /* 회원 관리 */
  {
    path: '/member',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '회원관리',
      roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
    },
    children: [
      {
        path: '',
        name: 'MemberList',
        component: () => import('@/views/member/index.vue'),
        meta: {
          title: '회원 목록',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
      {
        path: 'detail/:uid',
        name: 'MemberDetail',
        component: () => import('@/views/member/detail.vue'),
        meta: {
          title: '회원 상세',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
          hidden: true,
        },
      },
    ],
  },
  /* 문자 전송 */
  /* {
    path: '/sms',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '문자전송',
      roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
    },
    children: [
      {
        path: '',
        name: 'SmsSend',
        component: () => import('@/views/sms/index.vue'),
        meta: {
          title: '문자 발송',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
      {
        path: 'history',
        name: 'SmsHistory',
        component: () => import('@/views/sms/history.vue'),
        meta: {
          title: '발송 내역',
          roles: ['ROLE_ADMIN', 'ROLE_CREATOR'],
        },
      },
    ],
  }, */
  /* 최고관리자 메뉴 START */
  {
    path: '/user',
    redirect: '/user',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '고객관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'UserList',
        component: () => import('@/views/user/index.vue'),
        meta: {
          title: '일반회원',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'add',
        name: 'UserAdd',
        component: () => import('@/views/user/add.vue'),
        meta: {
          title: '일반회원',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'update/:uid',
        name: 'UserUpdate',
        component: () => import('@/views/user/add.vue'),
        meta: {
          title: '일반회원',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        // path: 'detail/:uid',
        path: 'detail',
        name: 'DetailUser',
        component: () => import('@/views/user/detail.vue'),
        meta: {
          title: '보기',
          roles: ['ROLE_ADMIN'],
        },
      },
    ],
  },
  {
    path: '/shop',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '매장관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: 'shopList',
        name: 'ShopList',
        component: () => import('@/views/shop/index.vue'),
        meta: {
          title: '매장관리',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'form',
        name: 'ShopAdd',
        component: () => import('@/views/shop/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'form/:idx',
        name: 'ShopUpdate',
        component: () => import('@/views/shop/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'detail',
        name: 'ShopDetail',
        component: () => import('@/views/shop/form_bak.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'manager',
        name: 'ShopManager',
        component: () => import('@/views/shop/manager/index.vue'),
        meta: {
          title: '매니저관리',
          roles: ['ROLE_ADMIN'],
        },
      },
			{
        path: 'manager/add',
        name: 'ShopManagerAdd',
        component: () => import('@/views/shop/manager/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
			{
        path: 'manager/update/:uid',
        name: 'ShopManagerUpdate',
        component: () => import('@/views/shop/manager/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
    ],
  },
  {
    path: '/station',
    redirect: '/station',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '거점관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'StationList',
        component: () => import('@/views/station/index.vue'),
        meta: {
          title: '거점관리',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'form',
        name: 'StationAdd',
        component: () => import('@/views/station/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'form/:idx',
        name: 'StationUpdate',
        component: () => import('@/views/station/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
    ],
  },
  {
    path: '/product',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '판매상품관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: 'stationPackage',
        name: 'ProductPackageIndex',
        component: () => import('@/views/product/station/package/index.vue'),
        meta: {
          title: '거점 패키지상품',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'stationPackage/add',
        name: 'ProductPackageAdd',
        component: () => import('@/views/product/station/package/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'stationPackage/update/:idx',
        name: 'ProductPackageUpdate',
        component: () => import('@/views/product/station/package/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'station',
        name: 'ProductIndex',
        component: () => import('@/views/product/station/index.vue'),
        meta: {
          title: '거점 판매상품',
          roles: ['ROLE_ADMIN'],
        },
      },
			{
        path: 'station/add',
        name: 'ProductAdd',
        component: () => import('@/views/product/station/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'station/update/:idx',
        name: 'ProductUpdate',
        component: () => import('@/views/product/station/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'station/extra',
        name: 'ExtraProductIndex',
        component: () => import('@/views/product/station/extra/index.vue'),
        meta: {
          title: '거점 추가상품',
          roles: ['ROLE_ADMIN'],
        },
      },
			{
        path: 'station/extra/add',
        name: 'ExtraProductAdd',
        component: () => import('@/views/product/station/extra/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'station/extra/update/:idx',
        name: 'ExtraProductUpdate',
        component: () => import('@/views/product/station/extra/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'pickup',
        name: 'ProductIndex2',
        component: () => import('@/views/product/pickup/index.vue'),
        meta: {
          title: '픽업 판매상품',
          roles: ['ROLE_ADMIN'],
        },
      },
			{
        path: 'pickup/add',
        name: 'ProductAdd2',
        component: () => import('@/views/product/pickup/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'pickup/update/:idx',
        name: 'ProductUpdate2',
        component: () => import('@/views/product/pickup/form.vue'),
				meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'pickup/rotation',
        name: 'dRotation',
        component: () => import('@/views/product/pickup/rotation.vue'),
        meta: {
          title: ' 픽업 로테이션 관리',
          roles: ['ROLE_ADMIN'],
        },
      },
    ],
  },
  {
    path: '/order',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '거점 주문관리',
      roles: ['ROLE_ADMIN'],
    },
		children: [
			{
				path: '',
				name: 'OrderList',
				component: () => import('@/views/order/index.vue'),
				meta: {
					title: '전체주문',
					roles: ['ROLE_ADMIN'],
				},
			},
      {
        path: 'item',
        name: 'Item',
        component: () => import('@/views/order/item.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
      {
				path: 'station',
				name: 'Station',
				component: () => import('@/views/order/station.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
          hidden: true,
				},
			},
      {
				path: 'refundList',
				name: 'RefundList',
				component: () => import('@/views/order/refundList.vue'),
				meta: {
					title: '취소목록',
					roles: ['ROLE_ADMIN'],
				},
			},
      {
				path: 'OrderWeekList',
				name: 'OrderWeekList',
				component: () => import('@/views/order/weekList.vue'),
				meta: {
					title: '주차별 주문 확인',
					roles: ['ROLE_ADMIN'],
          hidden: true,
				},
			},
			// {
			// 	path: 'category',
			// 	name: 'OrderCategory',
			// 	component: () => import('@/views/order/category/index.vue'),
			// 	meta: {
			// 		title: '주문항목관리',
			// 		roles: ['ROLE_ADMIN'],
			// 	},
			// },
		],
  },
  {
    path: '/order2',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '픽업 주문관리',
      roles: ['ROLE_ADMIN', 'ROLE_SHOP_ADMIN'],
    },
		children: [
			{
				path: '',
				name: 'OrderList2',
				component: () => import('@/views/order2/index.vue'),
				meta: {
					title: '전체주문',
					roles: ['ROLE_ADMIN'],
				},
			},
      {
				path: 'manager/list',
				name: 'ManagerOrderList2',
				component: () => import('@/views/order2/manager/index.vue'),
				meta: {
					title: '전체주문',
					roles: ['ROLE_SHOP_ADMIN'],
				},
			},
      {
				path: 'manager/refundList',
				name: 'ManagerRefundList2',
				component: () => import('@/views/order2/manager/refundList.vue'),
				meta: {
					title: '취소목록',
					roles: ['ROLE_SHOP_ADMIN'],
				},
			},
      {
        path: 'item',
        name: 'Item2',
        component: () => import('@/views/order2/item.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
          hidden: true,
        },
      },
      {
        path: 'item/:idx',
        name: 'ItemOrderList2',
        component: () => import('@/views/order2/itemDetail.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
				path: 'shop',
				name: 'Shop2',
				component: () => import('@/views/order2/shop.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
          hidden: true,
				},
			},
      {
        path: 'shop/:idx',
        name: 'ShopOrderList2',
        component: () => import('@/views/order2/shopDetail.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
				path: 'refundList',
				name: 'RefundList2',
				component: () => import('@/views/order2/refundList.vue'),
				meta: {
					title: '취소목록',
					roles: ['ROLE_ADMIN'],
				},
			},
      {
				path: 'OrderWeekList',
				name: 'OrderWeekList2',
				component: () => import('@/views/order2/weekList.vue'),
				meta: {
					title: '주차별 주문 확인',
					roles: ['ROLE_ADMIN'],
          hidden: true,
				},
			},
			// {
			// 	path: 'category',
			// 	name: 'OrderCategory',
			// 	component: () => import('@/views/order/category/index.vue'),
			// 	meta: {
			// 		title: '주문항목관리',
			// 		roles: ['ROLE_ADMIN'],
			// 	},
			// },
		],
  },
  {
    path: '/food',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '음식관리',
      roles: ['ROLE_ADMIN'],
    },
    children: [
      {
        path: '',
        name: 'FoodList',
        component: () => import('@/views/food/index.vue'),
        meta: {
          title: '음식관리',
          roles: ['ROLE_ADMIN'],
        },
      },
      {
        path: 'form',
        name: 'FoodAdd',
        component: () => import('@/views/food/form.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
      {
        path: 'form/:idx',
        name: 'FoodUpdate',
        component: () => import('@/views/food/form.vue'),
        meta: {
          roles: ['ROLE_ADMIN'],
					hidden: true,
        },
      },
    ],
  },
  {
    path: '/settlement',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '정산관리',
      roles: ['ROLE_ADMIN', 'ROLE_SHOP_ADMIN'],
    },
		children: [
			{
				path: '',
				name: 'SettlementList',
				component: () => import('@/views/settlement/index.vue'),
				meta: {
					title: '정산관리',
					roles: ['ROLE_ADMIN'],
				},
			},
      {
				path: 'manager/list',
				name: 'ManagerSettlementList',
				component: () => import('@/views/settlement/manager/index.vue'),
				meta: {
					title: '정산내역',
					roles: ['ROLE_SHOP_ADMIN'],
				},
			},
      {
				path: 'manager/apply',
				name: 'ManagerSettlementApply',
				component: () => import('@/views/settlement/manager/apply.vue'),
				meta: {
					title: '정산신청',
					roles: ['ROLE_SHOP_ADMIN'],
				},
			},
			{
				path: 'edit',
				name: 'SettlementEdit',
				component: () => import('@/views/settlement/edit.vue'),
				meta: {
					title: '정산금액관리',
					roles: ['ROLE_ADMIN'],
				},
			},
		],
  },
  {
    path: '/coupon',
    redirect: '/coupon',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '쿠폰관리',
      roles: ['ROLE_ADMIN'],
    },
		children: [
			{
				path: '',
				name: 'CouponList',
				component: () => import('@/views/coupon/index.vue'),
				meta: {
					title: '쿠폰관리',
					roles: ['ROLE_ADMIN'],
				},
			},
			{
				path: 'add',
				name: 'CouponAdd',
				component: () => import('@/views/coupon/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
			{
				path: 'edit/:idx',
				name: 'CouponEdit',
				component: () => import('@/views/coupon/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
		],
  },
	{
    path: '/board',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '게시판',
      roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
    },
		children: [
      {
        path: 'list',
        component: () => import('@/views/board/boardManagement.vue'),
        name: 'Board',
        meta: {
          title: '게시판 관리',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
        },
      },
      {
        path: 'category',
        component: () => import('@/views/board/categoryManagement.vue'),
        name: 'Category',
        meta: {
          title: '카테고리 관리',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
        },
      },
      // {
      //   path: 'comments',
      //   component: () => import('@/views/comments/index.vue'),
      //   name: 'Comments',
      //   meta: {
      //     title: '댓글 관리',
      //     roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
      //   },
      // },
      {
        path: 'post',
        component: () => import('@/views/post/List/index.vue'),
        name: 'Post',
        meta: {
          title: '게시글 관리',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
        },
      },
      {
        path: 'post/:postUid/view',
        component: () => import('@/views/post/detail.vue'),
        name: 'PostDetail',
        meta: {
          title: '게시글 관리',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
          hidden: true,
        },
      },
      {
        path: 'post/add',
        component: () => import('@/views/post/write.vue'),
        name: 'PostAdd',
        meta: {
          title: '게시글 작성',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
          hidden: true,
        },
      },
      {
        path: 'post/:postUid/update',
        component: () => import('@/views/post/write.vue'),
        name: 'PostUpdate',
        meta: {
          title: '게시글 수정',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
          hidden: true,
        },
      },
      {
        path: 'post/:parentUid/reply',
        component: () => import('@/views/post/write.vue'),
        name: 'PostReply',
        meta: {
          title: '게시글 작성',
          roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
          hidden: true,
        },
      },
		],
  },
  {
    path: '/tfse',
    component: () => import('@/layout/default.vue'),
    name: 'TfseBoard',
    meta: {
      title: 'TFSE',
      roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
    },
    children: [
    {
      path: 'post',
      component: () => import('@/views/tfse/index.vue'),
      name: 'Tfse',
      meta: {
        title: 'Tfse 관리',
        roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
      },
    },
    {
      path: 'post/:postUid/view',
      component: () => import('@/views/tfse/detail.vue'),
      name: 'TfseDetail',
      meta: {
        title: '게시글 관리',
        roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
        hidden: true,
      },
    },
    {
      path: 'post/feedBack',
      component: () => import('@/views/tfse/feedBack/index.vue'),
      name: 'FeedBack',
      meta: {
        title: '피드백 관리',
        roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
      },
    },
    {
      path: 'post/feedBack/:postUid/view',
      component: () => import('@/views/tfse/feedBack/detail.vue'),
      name: 'FeedBackDetail',
      meta: {
        title: '게시글 관리',
        roles: ['ROLE_ADMIN', 'ROLE_BOARD'],
        hidden: true,
      },
    },
  ],
  },
  {
    path: '/challenge',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '챌린지',
      roles: ['ROLE_ADMIN'],
    },
		children: [
			{
				path: '',
				name: 'ChallengeList',
				component: () => import('@/views/challenge/index.vue'),
				meta: {
					title: '챌린지관리',
					roles: ['ROLE_ADMIN'],
				},
			},
			{
				path: 'add',
				name: 'ChallengeAdd',
				component: () => import('@/views/challenge/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
			{
				path: 'edit/:uid',
				name: 'ChallengeEdit',
				component: () => import('@/views/challenge/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
		],
  },
  {
    path: '/mission',
    component: () => import('@/layout/default.vue'),
    meta: {
      title: '맞춤미션',
      roles: ['ROLE_ADMIN'],
    },
		children: [
      {
				path: 'template',
				name: 'MissionTemplateList',
				component: () => import('@/views/mission/index.vue'),
				meta: {
					title: '템플릿관리',
					roles: ['ROLE_ADMIN'],
          // hidden: true,
				},
			},
      {
				path: 'template/add',
				name: 'MissionTemplateAdd',
				component: () => import('@/views/mission/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
			{
				path: 'template/edit/:uid',
				name: 'MissionTemplateEdit',
				component: () => import('@/views/mission/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
			{
				path: '',
				name: 'MissionList',
				component: () => import('@/views/mission/user/index.vue'),
				meta: {
					title: '맞춤미션관리',
					roles: ['ROLE_ADMIN'],
				},
			},
			{
				path: 'add',
				name: 'MissionAdd',
				component: () => import('@/views/mission/user/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
			{
				path: 'edit/:uid',
				name: 'MissionEdit',
				component: () => import('@/views/mission/user/form.vue'),
				meta: {
					roles: ['ROLE_ADMIN'],
					hidden: true,
				},
			},
      {
				path: 'inquiry',
				name: 'MissionInquiry',
				component: () => import('@/views/mission/inquiry/index.vue'),
				meta: {
					title: '설문조사 항목관리',
					roles: ['ROLE_ADMIN'],
				},
			},
		],
  },
	// {
  //   path: '/point',
  //   redirect: '/point',
  //   component: () => import('@/layout/default.vue'),
  //   meta: {
  //     title: '적립률 관리',
  //     roles: ['ROLE_ADMIN'],
  //   },
  //   children: [
  //     {
  //       path: '',
  //       name: 'PointList',
  //       component: () => import('@/views/point/index.vue'),
  //       meta: {
  //         title: '적립률 관리',
  //         roles: ['ROLE_ADMIN'],
  //       },
  //     },
  //   ],
  // },
  /* 최고관리자 메뉴 END */

  /* 매장회원 메뉴 START */
  /* 매장회원 메뉴 END */
];

const createRouter = () => new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: constantRoutes,
});

const router = createRouter();

export function resetRouter() {
  const newRouter = createRouter();
  (router as any).matcher = (newRouter as any).matcher; // reset router
}

router.afterEach(async (to: Route, from: Route) => {
  const pageTitle = '웨일리잇 관리자페이지';
  document.title = pageTitle;
});

export default router;
