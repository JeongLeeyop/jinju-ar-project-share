<template>
  <div
    v-loading="formLoading"
    class="category-form-wrap"
  >
    <el-form
      ref="categoryForm"
      :model="formData"
      :rules="rules"
      class="category-form"
    >
      <el-form-item
        label="카테고리 이름"
        prop="name"
      >
        <el-input v-model="formData.name" />
      </el-form-item>
      <el-form-item
        label="카테고리 설명"
        prop="descript"
      >
        <el-input
          type="textarea"
          rows="4"
          v-model="formData.descript"
        />
      </el-form-item>
      <el-form-item label="카테고리 데이터">
        <div>
          <el-button
            size="small"
            @click="append('parent')"
            style="margin-bottom:7px;"
          >
            데이터 추가
          </el-button>
        </div>
        <el-tree
          :data="treeData"
          class="category-tree"
          default-expand-all
        >
          <span class="custom-tree-node" slot-scope="{node, data}">
          <span v-if="updatableId !== data.uid">
            {{ data.name }}
          </span>
          <el-input
            v-else
            ref="categoryNameInput"
            v-model="updatableLabel"
            size="mini"
            class="label-input"
          />
          <span class="category-tree__btn">
            <!--
            <el-button
              type="text"
              size="mini"
              @click="append(node, data)"
              @click.stop
            >
              하위 추가
            </el-button>
            -->
            <el-button
              v-if="updatableId !== data.uid"
              type="text"
              size="mini"
              @click="modifyVisible(data)"
              @click.stop
            >
              수정
            </el-button>
            <el-button
              v-else
              type="text"
              size="mini"
              @click="modify(data)"
              @click.stop
            >
             확인
            </el-button>
            <el-button
              type="text"
              size="mini"
              style="color: #f56c6c;"
              @click="remove(node, data)"
            >
              삭제
            </el-button>
          </span>
        </span>
        </el-tree>
      </el-form-item>
    </el-form>
    <div class="category-form__btn">
      <el-button @click="cancle">
        취소
      </el-button>
      <el-button
        v-if="uid"
        type="primary"
        @click="updateCategory"
      >
        수정
      </el-button>
      <el-button
        v-else
        type="primary"
        @click="addCategory"
      >
        추가
      </el-button>
    </div>
  </div>
</template>

<script lang="ts">
import {
  Vue,
  Component,
  Prop,
} from 'vue-property-decorator';
import { Form, Input } from 'element-ui';
import {
  getBoardCategory,
  addBoardCategory,
  updateBoardCategory,
} from '@/api/boardCategory';

@Component({
  name: 'BoardCategoryForm',
})
export default class extends Vue {
  @Prop({ default: '' }) private uid!: string;

  mounted() {
    if (this.uid) this.setForm();
  }

  private formData = {
    name: '',
    children: [],
    descript: '',
    deleteList: [],
  };

  private treeData: any[] = [];

  private formLoading: boolean = false;

  private updatableId: string | null = null;

  private updatableLabel: string | null = null;

  private rules = {
    name: [
      {
        required: true,
        message: '카테고리 이름을 입력하세요.',
        trigger: 'blur',
      },
    ],
    descript: [
      {
        required: true,
        message: '카테고리 설명을 입력하세요.',
        trigger: 'blur',
      },
    ],
  }

  private append(node: any, data: any) {
    const newChild = {
      uid: '1',
      name: '새 카테고리',
      children: [],
      depth: 1,
      viewOrder: 1,
    };
    if (node === 'parent') {
      newChild.uid = String(this.treeData.length + 1);
      newChild.viewOrder = this.treeData.length + 1;
      this.treeData.push(newChild);
    } else {
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      newChild.uid = `${data.uid}-${data.children.length + 1}`;
      newChild.depth = node.level + 1;
      newChild.viewOrder = data.children.length + 1;
      data.children.push(newChild);
    }
  }

  private remove(node: any, data: any) {
    /* eslint-disable */
    const parent = {
      ...node.parent,
    };
    const children = parent.data.children || parent.data;
    const index = children.findIndex((d: any) => d.uid === data.uid);
    children.splice(index, 1);
    if (parent.data.children && parent.data.children.length > 0) {
      parent.data.children.forEach((child: any, idx: number) => {
        child.viewOrder = idx + 1;
      });
    }
    (this.formData.deleteList as any[]).push(data.uid);
    this.removeChildren(data);
    /* eslint-enable */
  }

  private addCategory() {
    (this.$refs.categoryForm as Form).validate((valid: boolean) => {
      if (valid) {
        (this.formData.children as any) = this.treeData;
        addBoardCategory(this.formData).then(() => {
          this.$message.success('카테고리를 추가했습니다.');
          this.$emit('finishSave');
        });
      }
    });
  }

  private updateCategory() {
    (this.$refs.categoryForm as Form).validate((valid: boolean) => {
      if (valid) {
        (this.formData.children as any) = this.treeData;
        updateBoardCategory(this.uid, this.formData).then(() => {
          this.$message.success('카테고리를 수정했습니다.');
          this.$emit('finishSave');
        });
      }
    });
  }

  private cancle() {
    this.$emit('closeDialog');
  }

  private setForm() {
    this.formLoading = true;
    getBoardCategory(this.uid).then((res) => {
      this.formLoading = false;
      this.formData = {
        ...res.data,
        deleteList: [],
      };
      this.treeData = res.data.children;
    });
  }

  private async modifyVisible(data: any) {
    await this.setModifyForm(data);
    (this.$refs.categoryNameInput as Input).focus();
  }

  private modify(data: any) {
    /* eslint-disable */
    data.name = this.updatableLabel;
    this.updatableId = '';
    /* eslint-enable */
  }

  private setModifyForm(data: any) {
    this.updatableLabel = data.name;
    this.updatableId = data.uid;
  }

  private removeChildren(data: any) {
    data.children.forEach((item: any) => {
      (this.formData.deleteList as any).push(item.uid);
      if (data.children.length > 0) this.removeChildren(item);
    });
  }
}
</script>
