<template>
  <div class="writer-info">
    <div  class="writer-info__inner">
      <div class="writer-info__item">
        <p class="writer-info__head">이름</p>
        <p class="writer-info__body">김민정</p>
      </div>
      <div class="writer-info__item">
        <p class="writer-info__head">계정</p>
        <p class="writer-info__body">kimmj@google.com</p>
      </div>
      <div class="writer-info__item">
        <p class="writer-info__head">IP</p>
        <p class="writer-info__body">192.20.11.23</p>
      </div>
    </div>
    <div class="writer-info__button">
      <button class="bgcolor--purple" @click="closeWriterInfo()">확인</button>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  name: 'Comments',
  components: {
    Pagination,
  },
})
export default class extends Vue {
  private closeWriterInfo() {
    this.$emit('close');
  }
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/views/board.scss';
</style>
