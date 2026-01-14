<template>
  <div class="board-field__form">
    <h2 class="board-field__title">{{ isDefault ? '기본 필드' : '추가 필드' }}</h2>
    <el-table
      ref="fieldTable"
      :data="formData"
      border
      row-key="viewOrder"
      size="mini"
    >
      <el-table-column label="필드 명" width="140">
        <template slot-scope="scope">
          <el-input
            v-model="scope.row.fieldName"
            :disabled="isDefault"
            size="mini"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="필드 타입"
        min-width="130"
      >
        <template slot-scope="scope">
          <el-select
            v-model="scope.row.fieldTypeCode"
            value-key="fieldTypeCode"
            size="mini"
            :disabled="isDefault"
            @change="(fieldType) => fieldTypeChange(fieldType, scope.row)"
          >
            <el-option
              v-for="fieldType in fieldTypeList"
              :key="fieldType.fieldTypeCode"
              :label="fieldType.typeName"
              :value="fieldType.typeCode"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column
        label="입력 길이(파일 개수)"
        min-width="160"
      >
        <template slot-scope="scope">
          <el-input-number
            v-model="scope.row.inputLimit"
            v-if="!scope.row.inputInfinite"
            :min="1"
            :disabled="isDefault"
            step-strictly
            size="mini"
          />
          <div v-else>최대 65535글자</div>
        </template>
      </el-table-column>
      <el-table-column
        label="파일 용량(MB)"
        min-width="160"
      >
        <template slot-scope="scope">
          <el-input-number
            v-if="scope.row.fieldTypeCode === 'FILE' || scope.row.fieldTypeCode === 'PHOTO'"
            v-model="scope.row.fileSizeLimit"
            :min="1"
            :disabled="isDefault"
            step-strictly
            size="mini"
          />
          <el-tag
            v-else
            type="info"
          >
            해당 없음
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="필수 입력"
        min-width="80"
        align="center"
      >
        <template slot-scope="scope">
          <el-switch v-model="scope.row.requiredState" :disabled="isDefault"/>
        </template>
      </el-table-column>
      <el-table-column
        label="검색 사용"
        min-width="80"
        align="center"
      >
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.searchState"
            :disabled="scope.row.fieldTypeCode === 'FILE' || isDefault"
          />
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="삭제"
        width="80"
      >
        <template slot-scope="scope">
          <el-button
            type="danger"
            size="mini"
            :disabled="isDefault"
            @click="deleteField(scope.row.uid, scope.$index)"
          >
            삭제
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Sortable from 'sortablejs';
import { BoardModule } from '@/store/modules/board';

@Component({
  name: 'BoardFieldTable',
  components: {
  },
})
export default class extends Vue {
  @Prop({ default: false }) private isDefault!: boolean;

  @Prop({ default: [] }) private fieldTypeList!: any[];

  get formData() {
    if (this.isDefault) {
      const defaultFields = [
        {
          uid: '1',
          fieldName: '제목',
          fieldTypeCode: 'TEXT',
          fileSizeLimit: 1,
          inputLimit: 200,
          inputInfinite: false,
          requiredState: true,
          searchState: true,
        },
        {
          uid: '2',
          fieldName: '내용',
          fieldTypeCode: 'TEXT_AREA',
          fileSizeLimit: 1,
          inputLimit: 10000,
          inputInfinite: true,
          requiredState: false,
          searchState: true,
        },
      ];
      return defaultFields;
    }
    return BoardModule.form.fieldList;
  }

  mounted() {
    this.$nextTick(() => {
      if (!this.isDefault) this.setSort();
    });
  }

  private sortable: Sortable | null = null;

  private setSort() {
    if ((this.$refs.fieldTable as Vue) && this.formData.length > 0) {
      const el = (this.$refs.fieldTable as Vue).$el.querySelectorAll('.el-table__body-wrapper > table > tbody')[0] as HTMLElement;
      this.sortable = Sortable.create(el, {
        ghostClass: 'sortable-ghost', // Class name for the drop placeholder
        onEnd: (evt: any) => {
          if (typeof (evt.oldIndex) !== 'undefined' && typeof (evt.newIndex) !== 'undefined') {
            const moveField = this.formData[evt.oldIndex];
            this.formData.splice(evt.oldIndex, 1);
            setTimeout(() => {
              this.formData.splice(evt.newIndex, 0, moveField);
            }, 100);
          }
        },
      });
    }
  }

  private deleteField(uid: string, index: number) {
    this.formData.splice(index, 1);
  }
}
</script>

<style lang="scss">
.board-field__form {
  .el-table thead > tr > th {
    font-size: 0.9em;
    padding: 10px 0;
  }
}
.board-field__title {
  text-align: left;
  font-size: 1.1em;
  margin: 10px 0;
}
</style>
