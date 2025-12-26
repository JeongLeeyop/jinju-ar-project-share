import request from '@/utils/request';

const PATH = '/client/event/history/ranking';

export const getRankingAll = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const getRanking = (uid: string, day: string) => request({
  url: `${PATH}/${uid}/${day}`,
  method: 'get',
});
