<template>
  <div class="post-manage__left post-group">
    <ul>
      <li
        class="post-group__item"
        :class="{'post-group--active': listQuery.boardUid === ''}"
        @click="handleClickBoard('')"
      >
        전체 게시판
      </li>
      <li
        v-for="board in boardList"
        :key="board.uid"
        :class="{'post-group--active': listQuery.boardUid === board.uid}"
        @click="handleClickBoard(board.uid)" class="post-group__item"
      >
        {{ board.name }}
      </li>
    </ul>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { IBoard } from '@/types/board';

@Component({
  components: {
  },
})
export default class extends Vue {
  @Prop({ default: '' })private listQuery!: any

  @Prop({ default: [] })private boardList!: IBoard

  private handleClickBoard(boardUid: string) {
    this.$emit('clickBoard', boardUid);
  }
}
</script>
