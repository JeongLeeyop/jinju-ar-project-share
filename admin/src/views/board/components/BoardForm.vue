<template>
  <div
    v-loading="formLoading"
    class="board-form-wrap"
  >
    <el-tabs>
      <el-tab-pane label="일반 설정">
        <el-form
          ref="boardForm"
          label-width="160px"
          :model="formData"
          :rules="rules"
          class="board-form"
        >
          <el-form-item
            label="게시판 명"
            prop="name"
          >
            <el-input v-model="formData.name" />
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item
                label="게시판 스킨"
                prop="skin"
              >
                <el-select v-model="formData.skin">
                  <el-option
                    v-for="skin in boardSkinList"
                    :key="skin.code"
                    :label="skin.name"
                    :value="skin.code"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="게시판 카테고리">
                <draggableSelect
                  v-if="formData.categoryList"
                  multiple
                  v-model="formData.categoryList"
                  value-key="categoryUid"
                >
                  <el-option
                    v-for="category in categoryList"
                    :key="category.uid"
                    :label="category.name"
                    :value="{ categoryUid: category.uid }"
                  />
                </draggableSelect>
              </el-form-item>
              <!--
              <el-form-item
                label="사이트"
                prop="site"
              >
                <el-select
                  v-model="formData.site"
                  value-key="uid"
                >
                  <el-option
                    v-for="site in siteList"
                    :key="site.uid"
                    :label="site.siteName"
                    :value="site"
                  />
                </el-select>
              </el-form-item>
              -->
            </el-col>
          </el-row>
          <el-form-item label="게시판 기능">
            <el-checkbox v-model="formData.replyState">답글 사용</el-checkbox>
            <el-checkbox v-model="formData.commentState">댓글 사용</el-checkbox>
            <el-checkbox v-model="formData.secretState">비밀글 사용</el-checkbox>
            <el-checkbox v-model="formData.noticeState">공지글 사용</el-checkbox>
            <!--
            <el-checkbox v-model="formData.privateState">비공개 사용</el-checkbox>
            -->
          </el-form-item>
          <el-form-item label="페이지당 게시글 수" prop="listSize">
            <el-input-number
              v-model="formData.listSize"
              step-strictly
              :min="0"
            />
          </el-form-item>
          <el-form-item label="첨부파일 사용">
            <el-switch v-model="formData.fileUseState" />
          </el-form-item>
          <el-row v-if="formData.fileUseState">
            <el-col :span="12">
              <el-form-item
                label="첨부파일 개수"
                prop="fileCountLimit"
              >
                <el-input-number
                  v-model="formData.fileCountLimit"
                  step-strictly
                  :min="0"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item
                label="첨부파일 용량(MB)"
                prop="fileSizeLimit"
              >
                <el-input-number
                  v-model="formData.fileSizeLimit"
                  step-strictly
                  :min="0"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="권한 관리">
        <el-form label-width="150px" class="board-form">
          <el-row>
            <el-col :span="12">
              <el-form-item label="목록/읽기 권한">
                <el-select v-model="formData.authRead">
                  <el-option
                    v-for="auth in boardAuthList"
                    :key="auth.authCode"
                    :label="auth.authName"
                    :value="auth.authCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="작성/수정 권한">
                <el-select v-model="formData.authWrite">
                  <el-option
                    v-for="auth in boardAuthList"
                    :key="auth.authCode"
                    :label="auth.authName"
                    :value="auth.authCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col
              v-if="formData.replyState"
              :span="12"
            >
              <el-form-item label="답글 권한">
                <el-select v-model="formData.authReply">
                  <el-option
                    v-for="auth in boardAuthList"
                    :key="auth.authCode"
                    :label="auth.authName"
                    :value="auth.authCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col
              v-if="formData.commentState"
              :span="12"
            >
              <el-form-item label="댓글 권한">
                <el-select v-model="formData.authComment">
                  <el-option
                    v-for="auth in boardAuthList"
                    :key="auth.authCode"
                    :label="auth.authName"
                    :value="auth.authCode"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <!--
          <el-form-item label="회원 권한">
            <el-transfer
              v-model="formData.roleList"
              :data="roleList"
              :titles="['사용 불가능', '사용 가능']"
              :props="{
                key: 'roleCode',
                label: 'roleName'
              }"
            />
          </el-form-item>
          -->
          <!--
          <el-form-item label="관리자 권한">
            <el-transfer
              v-model="enableManageRoleList"
              :data="adminRoleList"
              :titles="['사용 불가능', '사용 가능']"
              :props="{
                key: 'roleCode',
                label: 'roleName'
              }"
            />
          </el-form-item>
          -->
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="필드 관리">
        <div class="board-form">
          <BoardFieldTable :isDefault="true" :fieldTypeList="fieldTypeList" style="margin-bottom: 15px;" />
          <BoardFieldTable v-if="formData.fieldList" :fieldTypeList="fieldTypeList" />
          <el-button
            type="primary"
            size="small"
            icon="el-icon-plus"
            style="width: 100%; margin-bottom: 10px;"
            @click="fieldAdd()"
          >
            필드 추가
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  <div class="board-form__btn">
    <el-button @click="handleCancle()">
      취소
    </el-button>
    <el-button
      type="primary"
      @click="boardSave()"
    >
      {{ boardUid ? '수정' : '추가' }}
    </el-button>
  </div>
</div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Sortable from 'sortablejs';
import { Form } from 'element-ui';
import {
  getBoard,
  addBoard,
  updateBoard,
  newBoard,
} from '@/api/board';
import { getFieldTypeList } from '@/api/fieldType';
import { listAllBoardCategory } from '@/api/boardCategory';
import { getRoleList } from '@/api/role';
import { getBoardSkinList } from '@/api/boardSkin';
import {
  IFieldType,
  ICategory,
  IBoard,
  IBoardSkin,
} from '@/types/board';
import { IRole } from '@/types/role';
import DraggableSelect from '@/components/DraggableSelect/index.vue';
import BoardFieldTable from './BoardFieldTable.vue';
import { BoardModule } from '@/store/modules/board';

@Component({
  name: 'BoardForm',
  components: {
    DraggableSelect,
    BoardFieldTable,
  },
})
export default class extends Vue {
  @Prop({ required: true }) private boardUid!: string;

  mounted() {
    this.setForm();
    this.getBoardSkinList();
  }

  get formData() {
    return BoardModule.form;
  }

  private formLoading: boolean = true;

  private rules = {
    name: [
      {
        required: true,
        message: '게시판 명을 입력하세요.',
        trigger: 'blur',
      },
    ],
    listSize: [
      {
        required: true,
        message: '페이지당 게시글 수를 입력하세요.',
        trigger: 'blur',
      },
    ],
    skin: [
      {
        required: true,
        message: '게시판 스킨을 선택하세요.',
        trigger: 'blur',
      },
    ],
    fileCountLimit: [
      {
        required: true,
        message: '첨부파일 개수를 입력하세요.',
        trigger: 'blur',
      },
    ],
    fileSizeLimit: [
      {
        required: true,
        message: '첨부파일 용량을 입력하세요.',
        trigger: 'blur',
      },
    ],
  };

  private fieldTypeList: IFieldType[] = []; // 필드 타입 목록

  private categoryList: ICategory[] = []; // 카테고리 전체항목

  private roleList: IRole[] = [];

  private enableManageRoleList: string[] = [];

  private roleListQuery = {
    page: 1,
    size: 100,
  };

  private boardSkinList: IBoardSkin[] = [];

  private boardAuthList = [
    {
      authCode: 'GUEST',
      authName: '비회원',
    },
    {
      authCode: 'MEMBER',
      authName: '회원',
    },
    {
      authCode: 'MANAGER',
      authName: '관리자',
    },
  ];

  private sortable: Sortable | null = null

  private cancle() {
    this.$emit('closeDialog');
  }

  private setForm() {
    listAllBoardCategory().then((res) => {
      this.formLoading = false;
      this.categoryList = res.data;
    });
    getFieldTypeList().then((res) => {
      this.fieldTypeList = res.data;
    });
    getRoleList(this.roleListQuery).then((res) => {
      this.roleList = res.data.content;
    });
    if (this.boardUid) {
      getBoard(this.boardUid).then((res) => {
        this.formLoading = false;
        BoardModule.setForm({ ...res.data });
      });
    } else {
      BoardModule.setForm({ ...newBoard });
    }
  }

  private getBoardSkinList() {
    getBoardSkinList().then((res) => {
      this.boardSkinList = res.data;
    });
  }

  private boardSave() {
    (this.$refs.boardForm as Form).validate((valid: boolean) => {
      if (valid) {
        if (this.boardUid) {
          updateBoard(this.boardUid, this.formData).then(() => {
            this.$message.success('성공적으로 게시판을 수정했습니다.');
            this.$emit('save');
          });
        } else {
          addBoard(this.formData).then(() => {
            this.$message.success('성공적으로 게시판을 추가했습니다.');
            this.$emit('save');
          });
        }
        return;
      }
      this.$message.warning('누락된 정보가 있습니다.');
    });
  }

  private fieldAdd() {
    const defaultField = {
      uid: '',
      fieldName: '새 필드',
      fieldTypeCode: this.fieldTypeList[0].typeCode,
      inputLimit: 100,
      fileSizeLimit: 1,
      requiredState: false,
      searchState: false,
    };
    this.formData.fieldList.push({ ...defaultField });
  }

  private fieldTypeChange(fieldType: any, boardField: any) {
    /* eslint-disable */
    if (fieldType.typeCode === 'FILE') boardField.inputLimit = 1;
    /* eslint-enable */
  }

  private handleCancle() {
    this.$emit('close');
  }
}
</script>

<style lang="scss">
.board-form-wrap {
  .board-form {
    padding: 20px;
    border-bottom: 1px solid lightgray;
    .el-form-item {
      text-align: left;
    }
  }
  .board-form__btn { padding: 10px 25px; text-align: end; }
  .el-tabs__nav-wrap { border-top: 1px solid rgb(211, 211, 211); &::after { height: 1px; background-color: rgb(228, 228, 228);  } }
  .el-tabs__header.is-top { margin: 0; }
  .el-tabs--top .el-tabs__item.is-top { padding: 0 15px; }
  .el-tabs__active-bar { background-color: #409eff; }
}
</style>
