import { db, timestamp, arrayUnion } from '@/utils/firebase';

export interface ChatMessage {
  id: string;
  spaceId: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  text: string;
  timestamp: any;
  createdAt: Date;
  type: 'text' | 'image' | 'file';
  metadata?: {
    fileName?: string;
    fileUrl?: string;
    fileSize?: number;
  };
}

export interface ChatSpace {
  id: string;
  name: string;
  color: string;
  participants: string[];
  lastMessage?: string;
  lastMessageTime?: Date;
  createdAt: Date;
  createdBy: string;
}

class ChatService {
  private messagesCollection = 'chatMessages';
  private spacesCollection = 'chatSpaces';

  /**
   * 채팅 메시지 전송
   */
  async sendMessage(
    spaceId: string,
    userId: string,
    userName: string,
    text: string,
    userAvatar?: string,
  ): Promise<string> {
    try {
      const messageData: Partial<ChatMessage> = {
        spaceId,
        userId,
        userName,
        userAvatar,
        text,
        timestamp: timestamp(),
        createdAt: new Date(),
        type: 'text',
      };

      const docRef = await db.collection(this.messagesCollection).add(messageData);

      // Update last message in space
      await this.updateSpaceLastMessage(spaceId, text);

      return docRef.id;
    } catch (error) {
      console.error('Error sending message:', error);
      throw error;
    }
  }

  /**
   * 실시간 메시지 수신 (리스너 등록)
   */
  onMessagesChange(
    spaceId: string,
    callback: (messages: ChatMessage[]) => void,
  ): () => void {
    const unsubscribe = db
      .collection(this.messagesCollection)
      .where('spaceId', '==', spaceId)
      .orderBy('timestamp', 'asc')
      .onSnapshot(
        (snapshot) => {
          const messages: ChatMessage[] = [];
          snapshot.forEach((doc) => {
            const data = doc.data();
            messages.push({
              id: doc.id,
              ...data,
              createdAt: data.timestamp?.toDate() || data.createdAt,
            } as ChatMessage);
          });
          callback(messages);
        },
        (error) => {
          console.error('Error listening to messages:', error);
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
        const updatedParticipants = data.participants.filter(id => id !== userId);
        
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
}

export default new ChatService();
