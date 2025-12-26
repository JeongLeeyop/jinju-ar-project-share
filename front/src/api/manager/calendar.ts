import request from '@/utils/request';

const PATH = '/calendar';

export const getCalendar = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const viewCalendar = (uid: string) => request({
  url: `${PATH}/${uid}/view`,
  method: 'get',
});

export const getCalendarList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const addCalendar = (data: any) => request({
  url: PATH,
  method: 'post',
  data: {
    ...data,
  },
});

export const updateCalendar = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data: {
    ...data,
  },
});

export const deleteCalendar = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const addCalendarLike = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const deleteCalendarLike = (idx: any) => request({
  url: `${PATH}/like/${idx}`,
  method: 'delete',
});
