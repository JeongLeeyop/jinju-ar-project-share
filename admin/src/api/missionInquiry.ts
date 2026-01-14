import request from '@/utils/request';

const PATH = '/mission/inquiry';
const PATH2 = '/mission/user/inquiry';

export const getInquiryList = () => request({
  url: PATH,
  method: 'get',
});

export const getUserInquiryDetail = (userUid: any) => request({
  url: `${PATH2}/${userUid}`,
  method: 'get',
});

export const addInquiry = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateInquiry = (data: any) => request({
  url: `${PATH}/${data.idx}`,
  method: 'put',
  data,
});

export const updatePage = (data: any) => request({
  url: `${PATH}/page/${data.idx}`,
  method: 'put',
  data,
});

export const deleteMissionInquiry = (idx: any) => request({
  url: `${PATH}/${idx}`,
  method: 'delete',
});
