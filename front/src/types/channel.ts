export interface IChannel {
  uid: string
  name: string
  introduce: string
  privateStatus: boolean
  price: number | null
  domain: string
  userUid: string
  password: string
  memberCount: number
  myJoinStatus: boolean
  myApprovalStatus: boolean
  myChannelStatus: boolean
  creatorName?: string
  categoryName?: string
  categoryList: any[]
  iconImageList: any[]
  coverImageList: any[]
  questionList: any[]
}


export interface IChannelAdd {
  uid: string
  name: string
  introduce: string
  privateStatus: boolean
  price: number | null
  domain: string
  userUid: string
  password: string
  memberCount: number
  myJoinStatus: boolean
  myChannelStatus: boolean
  iconImageList: any[]
  coverImageList: any[]
  questionList: any[]
}

