import router from '@/router';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { Message } from 'element-ui';
import { Route } from 'vue-router';
import { getChannelDomainDetail } from '@/api/channel';

const whiteList = ['/login', '/'];
const whiteNameList = ['Channel'];

router.beforeEach(async (to: Route, _: Route, next: any) => {
    await UserModule.GetUserInfo();
    
    // 화이트리스트 경로는 무조건 허용
    if (whiteList.indexOf(to.path) !== -1) {
      next();
      return;
    }
    
    // 로그인 여부 확인 - 로그아웃 상태에서는 커뮤니티 접근 차단
    if (!UserModule.token || !UserModule.userId) {
      // 화이트리스트에 있는 페이지(Channel 홈)만 허용
      if (to.name !== undefined && to.name !== null && whiteNameList.indexOf(to.name) !== -1) {
        // domain이 있는 경우 커뮤니티 정보 로드
        if (to.params.domain) {
          try {
            const res = await getChannelDomainDetail(to.params.domain);
            if (res.status === 200) {
              ChannelModule.setSelectedChannel(res.data);
              next();
            } else {
              Message.warning('해당 커뮤니티를 찾을 수 없습니다.');
              next({ name: 'Home' });
            }
          } catch (error) {
            Message.warning('해당 커뮤니티를 찾을 수 없습니다.');
            next({ name: 'Home' });
          }
        } else {
          next();
        }
        return;
      }
      
      // 그 외 모든 페이지는 로그인 필요
      Message.warning('로그인이 필요한 서비스입니다.');
      next('/');
      return;
    }
    
    // 로그인된 사용자는 기존 로직 적용
    const { roles } = UserModule;
    for (let i = 0; i < to.matched.length; i += 1) {
        const route = to.matched[i];
        if (route.meta.roles && !route.meta.roles.some((role: any) => roles.includes(role))) {
          // Message.warning('접근권한이 없습니다.');
          // next(`/login?redirect=${to.path}`);
          // break;
        }
    }
    
    // domain가 있는 경우 실제 존재하는지 검증
    if (to.params.domain) {
      await getChannelDomainDetail(to.params.domain).then((res) => {
        if (res.status === 200) {
          // 가입안된 유저 접근
          if (to.name !== undefined && to.name !== null && whiteNameList.indexOf(to.name) !== -1) { // 화이트리스트 허용
            next();
          } else if (!res.data.myChannelStatus && (!res.data.myJoinStatus || !res.data.myApprovalStatus)) {
            // Message.warning('접근권한이 없습니다.');
            // next({ name: 'Home', params: { domain: to.params.domain } });
            next();
          }
          ChannelModule.setSelectedChannel(res.data);
          // creator페이지 진입시 채널 설정 권한이 있는지 체크
          for (let i = 0; i < to.matched.length; i += 1) {
            const route = to.matched[i];
            if (route.name === 'Creator') {
              if (res.data.myChannelStatus) next();
              else {
                // Message.warning('접근권한이 없습니다.');
                // next({ name: 'CommunityHome', params: { domain: to.params.domain } });
                next();
              }
            }
          }
          next();
        } else {
          Message.warning('커뮤니티를 선택해 주세요.');
          next({ name: 'Home' });
        }
      }).catch((error) => {
        Message.warning('해당 커뮤니티를 찾을 수 없습니다.');
        next({ name: 'Home' });
      });
      next();
    }
    
    // 커뮤니티생성 페이지는 현재 선택된 커뮤니티 초기화 해주기
    if (to.name === 'CreateCommunity') ChannelModule.setSelectedChannel({});
    next();
});
