<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">건강피드 글쓰기</p>
			</div>

			<div class="setting__btn__box">
				<button @click="$router.go(-1)" class="cancel">취소</button>
				<button class="save" type="submit">저장</button>
			</div>
		</div>

		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false" style="text-align: left">

			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">글 내용</p>
					<div class="board-wr">
						<div class="board">
							<div class="board-checkbox-wr">
								<el-form-item label="비밀글">
									<el-checkbox-group v-model="form.secret">
										<el-checkbox name="type">비밀글 설정</el-checkbox>
									</el-checkbox-group>
								</el-form-item>
							</div>
							<div class="board-input-wr">
								<el-form-item prop="thumbUid" v-model="form.title">
									<label for="feedTitle">제목</label>
									<el-input placeholder="" type="text" id="feedTitle" v-model="input1" class="full" />
								</el-form-item>
							</div>
							<div class="content1-textarea">
								<div class="board-textbox-wr">
									<el-form-item v-model="form.content">
										<label for="noticeText">내용</label>
										<el-input type="textarea" :autosize="{ minRows: 12, maxRows: 12 }" id="noticeText" placeholder=""
											v-model="textarea1" />
									</el-form-item>
								</div>
							</div>
							<div class="board-tag-wr">
								<div class="board-tag">
									<label for="tag">태그</label>
									<input type="textarea" v-model="newTag" @keyup.enter="addTag" placeholder="태그 입력" />
								</div>
								<div class="tags-wr">
									<div class="tags" v-for="(tag, index) in tags" :key="index">
										{{ tag }}
										<button @click="removeTag(index)">X</button>
									</div>
								</div>
							</div>
							<div class="board-upload-wr">
								<el-form-item v-model="form.file">
									<label for="noticeFile">첨부파일</label>
									<el-upload action="#" list-type="picture-card" :auto-upload="false">
										<i slot="default" class="el-icon-plus"></i>
									</el-upload>
									<el-dialog :visible.sync="dialogVisible">
										<img width="100%" :src="dialogImageUrl" alt="">
									</el-dialog>
								</el-form-item>
							</div>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script>

export default {
	components: {
	},
	data() {
		return {
			input1: '',
			textarea1: '',
			dialogImageUrl: '',
			dialogVisible: false,
			disabled: false,
			tags: [],
			newTag: '',
			form: {
				secret: '',
				title: '',
				content: '',
				tags: [],
				file: '',
			},
		};
	},
	methods: {
		handleRemove(file) {
			console.log(file);
		},
		handlePictureCardPreview(file) {
			this.dialogImageUrl = file.url;
			this.dialogVisible = true;
		},
		handleDownload(file) {
			console.log(file);
		},
		addTag() {
			if (this.newTag !== '') {
				if (!this.tags.includes(this.newTag)) { // 새로운 태그가 이미 기존 태그에 포함되어 있는지 확인
					this.tags.push(this.newTag);
				}
				this.newTag = '';
			}
		},
		// removeTag(index) {
		// this.tags.splice(in/dex, 1);
		// },
	},
};
</script>
