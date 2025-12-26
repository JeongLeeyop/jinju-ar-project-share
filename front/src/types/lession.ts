export interface ILession {
  uid: string
  name: string
  description: string
  privateState?: boolean
  secretState?: boolean
  createDate: string
  channelUid: string
  myWatchPercent: number
  video?: IVideo[]
  videoList?: IVideo[]  // API 응답에서 videoList로 옴
  fileList: IFile[]
}

export interface IVideo {
  idx: number | string
  userUid?: string
  writer?: string
  lessionUid: string | null
  createDate: string
  viewOrder: number
  title: string | null
  description: string | null
  content?: string | null
  contents?: string | null
  postTitle?: string | null
  urlCode: string | null
  viewCount: number
  myWatchPercent: number
  lastWatchSecond: number
  lastWatchUpdate: string | null
  fileList?: any[]
  timeLineList?: any[]
}

export interface IFile {
  fileUid: string | null
  name?: string  // API 응답에 포함됨
  lessionUid?: string | null
  viewOrder: number
  file?: {
    byteSize: number
    createDate: string
    fileType: string
    originalName: string
    path: string
    useState: boolean
  }
}

export interface IWatchHistory {
  videoIdx: string
  percent: string
  lastWatchSecond: number
  channelUid: any
}
