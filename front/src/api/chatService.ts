import { db, timestamp, arrayUnion } from '@/utils/firebase';

export interface ChatMessage {
  id: string;
  spaceId: string;
  userId: string;
  userName: string;
  text: string;
  timestamp?: any;
  createdAt?: Date;
  type?: 'text' | 'image' | 'file';
  metadata?: {
    fileName?: string;
    fileSize?: number;
    fileUrl?: string;
  };
  userAvatar?: string;
}

export interface ChatNotice {
  spaceId: string;
  userId: string;
  userName: string;
  text: string;
  timestamp?: any;
  createdAt?: Date;
  isActive?: boolean;
}

export interface ChatSpace {
  spaceId?: string;
  id?: string;
  name: string;
  color: string;
  participants?: string[];
  lastMessage?: string;
  lastMessageTime?: Date;
  createdAt?: Date;
  notice?: ChatNotice;
  createdBy: string;
}

class ChatService {
  private messagesCollection = 'chatMessages';
  private spacesCollection = 'chatSpaces';
  private noticesCollection = 'chatNotices';

  /**
   * 채팅 메시지 전송
   */
  async sendMessage(
    spaceId: string,
    userId: string,
    userName: string,
    text: string,
    userAvatar?: string,
  ): Promise<void> {
    try {
      const messagesRef = db.collection('chatSpaces').doc(spaceId).collection('messages');
      const messageData = {
        spaceId,
        userId,
        userName,
        text,
        userAvatar: userAvatar || null,
        timestamp: timestamp(),
      };

      console.log('Sending message to:', `chatSpaces/${spaceId}/messages`);
      console.log('Message data:', messageData);

      const docRef = await messagesRef.add(messageData);
      console.log('Message sent with ID:', docRef.id);

      // 채팅 공간의 마지막 메시지 업데이트
      await db.collection('chatSpaces').doc(spaceId).update({
        lastMessage: text,
        lastMessageTime: timestamp(),
      });
    } catch (error) {
      console.error('Failed to send message:', error);
      throw error;
    }
  }

  /**
   * 메시지 실시간 구독 (최근 N개만)
   */
  onMessagesChange(
    spaceId: string,
    callback: (messages: ChatMessage[]) => void,
    limit: number = 50,
  ): () => void {
    console.log('onMessagesChange called with limit:', limit);
    const messagesRef = db.collection('chatSpaces').doc(spaceId).collection('messages');
    // 최신순으로 limit개만 가져옴
    const q = messagesRef.orderBy('timestamp', 'desc').limit(limit);

    const unsubscribe = q.onSnapshot(
      (snapshot: any) => {
        console.log('Snapshot received, document count:', snapshot.size);
        const messages: ChatMessage[] = [];
        snapshot.forEach((docSnap: any) => {
          const data = docSnap.data();
          messages.push({
            id: docSnap.id,
            spaceId: data.spaceId || spaceId,
            userId: data.userId,
            userName: data.userName,
            text: data.text,
            createdAt: data.timestamp?.toDate() || new Date(),
            userAvatar: data.userAvatar,
          } as ChatMessage);
        });
        // 시간순 정렬 (오래된 것부터) - 화면에 표시할 때는 오래된 것이 위에
        messages.sort((a, b) => {
          const timeA = a.createdAt instanceof Date ? a.createdAt.getTime() : 0;
          const timeB = b.createdAt instanceof Date ? b.createdAt.getTime() : 0;
          return timeA - timeB;
        });
        console.log('Messages loaded:', messages.length, messages);
        callback(messages);
      },
      (error: any) => {
        console.error('Error listening to messages:', error);
        callback([]);
      },
    );

    return unsubscribe;
  }

  /**
   * 메시지 목록 가져오기 (페이지네이션)
   */
  async getMessages(
    spaceId: string,
    limit: number = 50,
    beforeTimestamp?: Date,
  ): Promise<ChatMessage[]> {
    try {
      let query = db
        .collection(this.messagesCollection)
        .where('spaceId', '==', spaceId)
        .orderBy('timestamp', 'desc')
        .limit(limit);

      if (beforeTimestamp) {
        query = query.startAfter(beforeTimestamp);
      }

      const snapshot = await query.get();
      const messages: ChatMessage[] = [];

      snapshot.forEach((doc) => {
        const data = doc.data();
        messages.push({
          id: doc.id,
          ...data,
          createdAt: data.timestamp?.toDate() || data.createdAt,
        } as ChatMessage);
      });

      return messages.reverse();
    } catch (error) {
      console.error('Error getting messages:', error);
      throw error;
    }
  }

  /**
   * 이전 메시지 가져오기 (무한 스크롤용)
   */
  async getOlderMessages(
    spaceId: string,
    oldestMessageId: string | null,
    limit: number = 20,
    oldestTimestamp?: Date,
  ): Promise<ChatMessage[]> {
    try {
      const messagesRef = db.collection('chatSpaces').doc(spaceId).collection('messages');
      let querySnapshot;

      if (oldestTimestamp && oldestTimestamp instanceof Date) {
        // timestamp 기준으로 이전 메시지 가져오기
        const q = messagesRef
          .orderBy('timestamp', 'desc')
          .startAfter(oldestTimestamp)
          .limit(limit);
        querySnapshot = await q.get();
      } else if (oldestMessageId) {
        // 메시지 ID로 문서를 찾아서 그 이전 메시지 가져오기
        const oldestDoc = await messagesRef.doc(oldestMessageId).get();
        if (oldestDoc.exists) {
          const oldestData = oldestDoc.data();
          if (oldestData?.timestamp) {
            const q = messagesRef
              .orderBy('timestamp', 'desc')
              .startAfter(oldestData.timestamp)
              .limit(limit);
            querySnapshot = await q.get();
          } else {
            return [];
          }
        } else {
          return [];
        }
      } else {
        return [];
      }

      const messages: ChatMessage[] = [];

      if (querySnapshot) {
        querySnapshot.forEach((docSnap: any) => {
          const data = docSnap.data();
          messages.push({
            id: docSnap.id,
            spaceId: data.spaceId || spaceId,
            userId: data.userId,
            userName: data.userName,
            text: data.text,
            createdAt: data.timestamp?.toDate() || new Date(),
            userAvatar: data.userAvatar,
          } as ChatMessage);
        });
      }

      // 시간순 정렬 (오래된 것부터)
      messages.sort((a, b) => {
        const timeA = a.createdAt instanceof Date ? a.createdAt.getTime() : 0;
        const timeB = b.createdAt instanceof Date ? b.createdAt.getTime() : 0;
        return timeA - timeB;
      });

      console.log('Older messages loaded:', messages.length);
      return messages;
    } catch (error) {
      console.error('Failed to get older messages:', error);
      return [];
    }
  }

  /**
   * 메시지 삭제
   */
  async deleteMessage(messageId: string): Promise<void> {
    try {
      await db.collection(this.messagesCollection).doc(messageId).delete();
    } catch (error) {
      console.error('Error deleting message:', error);
      throw error;
    }
  }

  /**
   * 채팅 공간의 마지막 메시지 업데이트
   */
  private async updateSpaceLastMessage(
    spaceId: string,
    lastMessage: string,
  ): Promise<void> {
    try {
      await db
        .collection(this.spacesCollection)
        .doc(spaceId)
        .update({
          lastMessage,
          lastMessageTime: timestamp(),
        });
    } catch (error) {
      console.error('Error updating space last message:', error);
    }
  }

  /**
   * 채팅 공간 생성
   */
  async createChatSpace(
    name: string,
    color: string,
    createdBy: string,
    participants: string[],
  ): Promise<string> {
    try {
      const spaceData: Partial<ChatSpace> = {
        name,
        color,
        participants: [...participants, createdBy],
        createdAt: new Date(),
        createdBy,
      };

      const docRef = await db.collection(this.spacesCollection).add(spaceData);
      return docRef.id;
    } catch (error) {
      console.error('Error creating chat space:', error);
      throw error;
    }
  }

  /**
   * 채팅 공간 정보 가져오기
   */
  async getChatSpace(spaceId: string): Promise<ChatSpace | null> {
    try {
      const doc = await db.collection(this.spacesCollection).doc(spaceId).get();

      if (!doc.exists) {
        return null;
      }

      return {
        id: doc.id,
        ...doc.data(),
      } as ChatSpace;
    } catch (error) {
      console.error('Error getting chat space:', error);
      throw error;
    }
  }

  /**
   * 참여자 추가
   */
  async addParticipant(spaceId: string, userId: string): Promise<void> {
    try {
      await db
        .collection(this.spacesCollection)
        .doc(spaceId)
        .update({
          participants: arrayUnion(userId),
        });
    } catch (error) {
      console.error('Error adding participant:', error);
      throw error;
    }
  }

  /**
   * 참여자 제거
   */
  async removeParticipant(spaceId: string, userId: string): Promise<void> {
    try {
      const spaceRef = db.collection(this.spacesCollection).doc(spaceId);
      const spaceDoc = await spaceRef.get();

      if (spaceDoc.exists) {
        const data = spaceDoc.data() as ChatSpace;
        const updatedParticipants = (data.participants || []).filter((id: string) => id !== userId);

        await spaceRef.update({
          participants: updatedParticipants,
        });
      }
    } catch (error) {
      console.error('Error removing participant:', error);
      throw error;
    }
  }

  /**
   * 사용자가 읽은 마지막 메시지 업데이트 (읽음 표시)
   */
  async updateLastReadMessage(
    spaceId: string,
    userId: string,
    messageId: string,
  ): Promise<void> {
    try {
      await db
        .collection('userReadStatus')
        .doc(`${spaceId}_${userId}`)
        .set({
          spaceId,
          userId,
          lastReadMessageId: messageId,
          lastReadAt: timestamp(),
        }, { merge: true });
    } catch (error) {
      console.error('Error updating read status:', error);
      throw error;
    }
  }

  /**
   * 공지사항 설정
   */
  async setNotice(
    spaceId: string,
    userId: string,
    userName: string,
    text: string,
  ): Promise<string> {
    try {
      const noticeData: Partial<ChatNotice> = {
        spaceId,
        userId,
        userName,
        text,
        timestamp: timestamp(),
        createdAt: new Date(),
        isActive: true,
      };

      await db
        .collection(this.spacesCollection)
        .doc(spaceId)
        .update({
          notice: noticeData,
        });

      const docRef = await db.collection(this.noticesCollection).add(noticeData);
      return docRef.id;
    } catch (error) {
      console.error('Error setting notice:', error);
      throw error;
    }
  }

  /**
   * 공지사항 가져오기
   */
  async getNotice(spaceId: string): Promise<ChatNotice | null> {
    try {
      const doc = await db.collection(this.spacesCollection).doc(spaceId).get();

      if (!doc.exists) {
        return null;
      }

      const data = doc.data();
      return data?.notice || null;
    } catch (error) {
      console.error('Error getting notice:', error);
      throw error;
    }
  }

  /**
   * 공지사항 삭제
   */
  async deleteNotice(spaceId: string): Promise<void> {
    try {
      await db
        .collection(this.spacesCollection)
        .doc(spaceId)
        .update({
          notice: null,
        });
    } catch (error) {
      console.error('Error deleting notice:', error);
      throw error;
    }
  }

  /**
   * 공지사항 실시간 리스너
   */
  onNoticeChange(
    spaceId: string,
    callback: (notice: ChatNotice | null) => void,
  ): () => void {
    const unsubscribe = db
      .collection(this.spacesCollection)
      .doc(spaceId)
      .onSnapshot(
        (doc) => {
          if (doc.exists) {
            const data = doc.data();
            callback(data?.notice || null);
          } else {
            callback(null);
          }
        },
        (error) => {
          console.error('Error listening to notice:', error);
        },
      );

    return unsubscribe;
  }

  /**
   * 채팅 공간 생성 또는 가져오기
   */
  async getOrCreateChatSpace(
    spaceId: string,
    name: string,
    color: string,
    createdBy: string,
  ): Promise<ChatSpace> {
    try {
      const docRef = db.collection(this.spacesCollection).doc(spaceId);
      const doc = await docRef.get();

      if (doc.exists) {
        return {
          id: doc.id,
          ...doc.data(),
        } as ChatSpace;
      }

      const spaceData: Partial<ChatSpace> = {
        id: spaceId,
        name,
        color,
        participants: [createdBy],
        createdAt: new Date(),
        createdBy,
      };

      await docRef.set(spaceData);
      return spaceData as ChatSpace;
    } catch (error) {
      console.error('Error getting or creating chat space:', error);
      throw error;
    }
  }
}

export default new ChatService();
