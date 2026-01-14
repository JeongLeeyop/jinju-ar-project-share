<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">공지사항</p>
			</div>

			<div class="setting__btn__box">
				<button @click="$router.go(-1)" class="cancel">취소</button>
				<button class="save" type="submit">저장</button>
			</div>
		</div>

		<el-form ref="form" :model="form" onsubmit="return false" style="text-align: left">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">글 내용</p>
					<div class="board-wr">
						<div class="board">
							<div class="board-input-wr">
								<el-form-item prop="thumbUid" v-model="form.title">
									<label for="feedTitle">제목</label>
									<el-input placeholder="" type="text" id="feedTitle" class="full" />
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
			form: {
				title: '',
				content: '',
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
	},
};
</script>
