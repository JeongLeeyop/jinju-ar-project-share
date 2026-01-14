<template>
	<div class="product-add-wrap">
		<div class="product-add-title">
			<div>
				<p class="tl">판매상품 추가</p>
			</div>

			<div class="setting__btn__box">
				<button @click="handleGoList" class="cancel">취소</button>
				<button class="save" type="submit" @click="handleAddShopItem">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="product-add-section">
				<div class="product-add-content1">
					<p class="content-title">판매상품</p>
					<div class="content1-input">
						<el-form-item prop="thumbUid" label="상품 이미지">
							<el-upload class="avatar-uploader" :action="`${apiUrl}/admin/product/upload`"
								:on-success="uploadSuccess" :before-upload="beforeUpload" :on-remove="removeFile"
								:show-file-list="false">
								<img v-if="form.thumbUid" :src="`${apiUrl}/attached-file/${form.thumbUid}`" class="avatar">
								<i v-else class="el-icon-plus avatar-uploader-icon"></i>
							</el-upload>
						</el-form-item>
						<div class="shop-input-wr">
							<el-form-item prop="name" label="샐러드 명">
								<el-input placeholder="" type="text" id="productName" v-model="form.name" />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<el-form-item prop="price" label="가격">
								<el-input placeholder="" type="num" id="productPrice" v-model="form.price" />
							</el-form-item>
						</div>
						<div class="shop-radio-wr">
							<el-form-item prop="sellStatus" label="판매여부">
								<el-radio-group v-model="form.sellStatus">
									<el-radio :label="true">판매</el-radio>
									<el-radio :label="false">미판매</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div class="shop-textarea-wr">
							<el-form-item prop="material" label="재료">
								<el-input type="textarea" size="medium" v-model="form.material"></el-input>
							</el-form-item>
						</div>
					</div>
				</div>
				<div class="product-add-content1">
					<p class="content-title">영양정보 등록</p>
					<div class="content2-input">
						<div class="shop-input-wr small">
							<el-form-item prop="calorie" label="칼로리(Kcal)">
								<el-input-number :controls="false" :min="0" v-model="form.calorie" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="carbohydrate" label="탄수화물(g)">
								<el-input-number :controls="false" :min="0" v-model="form.carbohydrate" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="protein" label="단백질(g)">
								<el-input-number :controls="false" :min="0" v-model="form.protein" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="fat" label="지방(g)">
								<el-input-number :controls="false" :min="0" v-model="form.fat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="sodium" label="나트륨(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.sodium" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="sugar" label="당(g)">
								<el-input-number :controls="false" :min="0" v-model="form.sugar" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="saturatedfat" label="포화지방산(g)">
								<el-input-number :controls="false" :min="0" v-model="form.saturatedfat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="transFat" label="트랜스지방(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.transFat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="cholesterol" label="콜레스테롤(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.cholesterol" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="calcium" label="칼슘(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.calcium" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="iron" label="철(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.iron" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="potassium" label="칼륨(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.potassium" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="dietaryFiber" label="식이섬유(g)">
								<el-input-number :controls="false" :min="0" v-model="form.dietaryFiber" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<el-form-item prop="vitaminc" label="비타민C(mg)">
								<el-input-number :controls="false" :min="0" v-model="form.vitaminc" />
							</el-form-item>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import { addProduct, updateProduct, getProductDetail } from '@/api/product';
import { Form } from 'element-ui';

@Component({
	name: 'ProductForm',
})
export default class extends Vue {
	mounted() {
		if (this.$route.params.idx) {
			this.idx = this.$route.params.idx;
			getProductDetail(this.$route.params.idx).then((res) => {
				this.form = res.data;
			});
		}
	}

	private idx: any = null;

	private form = {
		name: '',
		description: '',
		thumbUid: '',
		weight: '',
		material: '',
		amount: 0,
		price: 0,
		dietType: '',
		dietPurpose: '',
		dietPrecaution: '',
		sellStatus: true,
		calorie: 0,
		carbohydrate: 0,
		protein: 0,
		fat: 0,
		sodium: 0,
		sugar: 0,
		saturatedfat: 0,
		transFat: 0,
		cholesterol: 0,
		calcium: 0,
		iron: 0,
		potassium: 0,
		dietaryFiber: 0,
		vitaminc: 0,
		productType: 'PICKUP',
	};

	private rules = {
		name: [
			{ required: true, message: '상품명을 입력하세요.', trigger: 'blur' },
		],
		price: [
			{ required: true, message: '가격을 입력하세요.', trigger: 'blur' },
		],
		sellStatus: [
			{ required: true, message: '판매여부를 선택하세요.', trigger: 'change' },
		],
		material: [
			{ required: true, message: '재료를 입력하세요.', trigger: 'blur' },
		],
		calorie: [
			{ required: true, message: '칼로리를 입력하세요.', trigger: 'blur' },
		],
		carbohydrate: [
			{ required: true, message: '탄수화물 입력하세요.', trigger: 'blur' },
		],
		protein: [
			{ required: true, message: '단백지를 입력하세요.', trigger: 'blur' },
		],
		fat: [
			{ required: true, message: '지방을 입력하세요.', trigger: 'blur' },
		],
		sodium: [
			{ required: true, message: '나트륨을 입력하세요.', trigger: 'blur' },
		],
		sugar: [
			{ required: true, message: '당을 입력하세요.', trigger: 'blur' },
		],
		saturatedfat: [
			{ required: true, message: '포화지방산을 입력하세요.', trigger: 'blur' },
		],
		transFat: [
			{ required: true, message: '트랜스지방을 입력하세요.', trigger: 'blur' },
		],
		cholesterol: [
			{ required: true, message: '콜레스테롤을 입력하세요.', trigger: 'blur' },
		],
		calcium: [
			{ required: true, message: '칼슘을 입력하세요.', trigger: 'blur' },
		],
		iron: [
			{ required: true, message: '철을 입력하세요.', trigger: 'blur' },
		],
		potassium: [
			{ required: true, message: '칼륨을 입력하세요.', trigger: 'blur' },
		],
		dietaryFiber: [
			{ required: true, message: '식이섬유을 입력하세요.', trigger: 'blur' },
		],
		vitaminc: [
			{ required: true, message: '비타민C을 입력하세요.', trigger: 'blur' },
		],
	};

	private apiUrl = process.env.VUE_APP_BASE_API;

	private handleAddShopItem() {
		(this.$refs.form as Form).validate((valid: boolean) => {
			if (valid) {
				if (this.idx) {
					updateProduct(this.idx, this.form).then(() => {
						this.$message.success('상품을 수정했습니다.');
						this.handleGoList();
					});
				} else {
					addProduct(this.form).then((res) => {
						this.$message({
							message: '정상적으로 저장되었습니다.',
							type: 'success',
						});
						this.handleGoList();
					});
				}
			}
		});
	}

	private beforeUpload(file: any) {
		const isImage = file.type.split('/')[0] === 'image';
		const isLt5M = file.size / 1024 / 1024 < 5;
		if (!isImage) this.$message.error('이미지 파일을 업로드해주세요.');
		if (!isLt5M) this.$message.error('이미지 용량이 5MB를 초과합니다.');
		return isImage && isLt5M;
	}

	private uploadSuccess(res: any, file: any) {
		this.form.thumbUid = res.uid;
	}

	private removeFile() {
		this.form.thumbUid = '';
	}

	private handleGoList() {
		this.$router.push({ name: 'ProductIndex2' });
	}
}
</script>

<style lang="scss">
.avatar-uploader .el-upload {
	border: 1px dashed #d9d9d9;
	border-radius: 6px;
	cursor: pointer;
	position: relative;
	overflow: hidden;
}

.avatar-uploader .el-upload:hover {
	border-color: #409EFF;
}

.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 178px;
	height: 178px;
	line-height: 178px;
	text-align: center;
}

.avatar {
	width: 178px;
	height: 178px;
	display: block;
}
</style>
