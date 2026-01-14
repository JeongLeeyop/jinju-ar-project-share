import request from '@/utils/request';
import { IBoard } from '@/types/board';

const PATH = '/board';

export const newBoard: IBoard = {
  uid: '',
  skin: '',
  name: '',
  fileUseState: true,
  fileCountLimit: 1,
  fileSizeLimit: 5,
  listSize: 10,
  authRead: 'GUEST',
  authWrite: 'MEMBER',
  authReply: 'MEMBER',
  authComment: 'MEMBER',
  privateState: false,
  replyState: false,
  commentState: false,
  secretState: false,
  noticeState: false,
  createDate: '',
  fieldList: [
    /*
    {
      uid: '',
      fieldName: '제목',
      fieldTypeCode: 'TEXT',
      inputLimit: 50,
      fileSizeLimit: 0,
      requiredState: true,
      searchState: true,
    },
    {
      uid: '',
      fieldName: '내용',
      fieldTypeCode: 'TEXT_AREA',
      inputLimit: 1000,
      fileSizeLimit: 0,
      requiredState: true,
      searchState: true,
    },
    */
  ],
  categoryList: [],
  roleList: [],
};

export const getBoard = (uid: string) => request({
  url: `${PATH}/detail/${uid}`,
  method: 'get',
});

export const getBoardList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getBoardListAll = () => request({
  url: `${PATH}/all/list`,
  method: 'get',
});

export const addBoard = (data: IBoard) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateBoard = (uid: string, data: IBoard) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteBoard = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
