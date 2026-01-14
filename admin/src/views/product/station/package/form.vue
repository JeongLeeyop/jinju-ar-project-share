<template>
	<div class="product-package-add-wrap">
		<div class="product-add-title">
			<div>
				<p class="tl">거점 패키지 상품</p>
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
						<el-form-item prop="thumbUid" label="패키지 이미지">
							<el-upload class="avatar-uploader" :action="`${apiUrl}/admin/product/upload`"
								:on-success="uploadSuccess" :before-upload="beforeUpload" :on-remove="removeFile"
								:show-file-list="false">
								<img v-if="form.thumbUid" :src="`${apiUrl}/attached-file/${form.thumbUid}`" class="avatar">
								<i v-else class="el-icon-plus avatar-uploader-icon"></i>
							</el-upload>
						</el-form-item>
						<div class="shop-input-wr">
							<el-form-item prop="name" label="패키지명">
								<el-input placeholder="패키지명을 입력하세요" type="text" v-model="form.name" />
							</el-form-item>
						</div>
						<!-- <div class="shop-input-wr">
							<el-form-item prop="price" label="가격">
								<el-input placeholder="가격을 입력하세요" type="number" min="0" v-model="form.price" />
							</el-form-item>
						</div> -->
						<div class="shop-input-wr">
							<el-form-item prop="deliveryFee" label="배송비">
								<el-radio-group v-model="deliveryFeeType" @change="handleDeliveryFeeChange">
									<el-radio label="free">무료</el-radio>
									<el-radio label="paid">유료</el-radio>
								</el-radio-group>
								<el-input
									v-if="deliveryFeeType === 'paid'"
									placeholder="배송비를 입력하세요"
									type="number"
									min="0"
									v-model="form.deliveryFee"
									style="margin-top: 10px;"
								/>
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<el-form-item prop="maxWeekCount" label="최대 배송 주차 수">
								<el-input-number v-model="form.maxWeekCount" :min="1" :max="52" />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<el-form-item prop="deliveryDaysPerWeek" label="주당 배송 일수">
								<el-input-number v-model="form.deliveryDaysPerWeek" :min="1" :max="7" />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<el-form-item prop="selectableProductCount" label="선택 가능한 상품 갯수">
								<el-input-number v-model="form.selectableProductCount" :min="1" :max="100" />
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
							<el-form-item prop="description" label="설명">
								<summernote
									moduleName="business"
									:contents="form.description"
									@change="changeContent"
								/>
							</el-form-item>
						</div>
					</div>
				</div>
				<div class="product-add-content1">
					<p class="content-title">패키지에 포함할 거점 판매상품</p>
					<div class="content2-input">
						<el-table
							ref="productTable"
							:data="availableProducts"
							style="width: 100%"
							row-key="idx"
							@selection-change="handleSelectionChange"
						>
							<el-table-column
								type="selection"
								width="55"
								reserve-selection
							/>
							<el-table-column
								label="상품 이미지"
								width="100"
							>
								<template slot-scope="scope">
									<img
										v-if="scope.row.thumbUid"
										:src="`${apiUrl}/attached-file/${scope.row.thumbUid}`"
										style="width: 60px; height: 60px; object-fit: cover;"
									/>
									<span v-else>-</span>
								</template>
							</el-table-column>
							<el-table-column
								prop="name"
								label="상품명"
								min-width="200"
							/>
							<el-table-column
								prop="price"
								label="가격"
								width="120"
							>
								<template slot-scope="scope">
									{{ scope.row.price.toLocaleString() }}원
								</template>
							</el-table-column>
							<el-table-column
								prop="sellStatus"
								label="판매상태"
								width="100"
							>
								<template slot-scope="scope">
									<el-tag :type="scope.row.sellStatus ? 'success' : 'info'">
										{{ scope.row.sellStatus ? '판매중' : '미판매' }}
									</el-tag>
								</template>
							</el-table-column>
						</el-table>
					</div>
					<div style="margin-top: 15px; color: #909399; font-size: 14px;">
							<i class="el-icon-info"></i>
							선택된 상품: {{ form.productIdxList.length }}개
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang='ts'>
import Summernote from '@/components/SummerNote/index.vue';
import { Component, Vue } from 'vue-property-decorator';
import {
	addPackage,
	updatePackage,
	getPackageDetail,
	getProductList,
} from '@/api/product';
import { Form } from 'element-ui';
import { max } from 'xe-utils';

@Component({
	name: 'PackageForm',
	components: {
		Summernote,
	},
})
export default class extends Vue {
	async mounted() {
		// 먼저 상품 목록을 로드
		await this.loadAvailableProducts();

		// 수정 모드인 경우 패키지 상세 정보를 로드하고 선택 처리
		if (this.$route.params.idx) {
			this.idx = this.$route.params.idx;
			const res = await getPackageDetail(this.$route.params.idx);
			this.form = res.data;
			this.deliveryFeeType = this.form.deliveryFee > 0 ? 'paid' : 'free';

			if (res.data.packageItems) {
				this.form.productIdxList = res.data.packageItems.map((item: any) => item.productIdx);
				// 상품 목록이 로드된 후 선택된 항목 체크 표시
				await this.$nextTick();
				// 추가 대기 시간으로 테이블 완전 렌더링 보장
				setTimeout(() => {
					const table = this.$refs.productTable as any;
					if (table && this.availableProducts.length > 0) {
						// 선택된 상품 ID 목록
						const selectedIds = this.form.productIdxList;
						// 각 상품을 순회하며 선택
						this.availableProducts.forEach((product: any) => {
							if (selectedIds.includes(product.idx)) {
								table.toggleRowSelection(product, true);
							}
						});
					}
				}, 100);
			}
		}
	}

	private idx: any = null;

	private deliveryFeeType = 'free';

	private availableProducts: any[] = [];

	private form = {
		name: '',
		description: '',
		thumbUid: '',
		price: 0,
		deliveryFee: 0,
		sellStatus: true,
		maxWeekCount: 1,
		deliveryDaysPerWeek: 1,
		selectableProductCount: 1,
		productType: 'STATION',
		productIdxList: [] as number[],
	};

	private rules = {
		name: [
			{ required: true, message: '패키지명을 입력하세요.', trigger: 'blur' },
		],
		// price: [
		// 	{ required: true, message: '가격을 입력하세요.', trigger: 'blur' },
		// ],
		sellStatus: [
			{ required: true, message: '판매여부를 선택하세요.', trigger: 'change' },
		],
		description: [
			{ required: true, message: '설명을 입력하세요.', trigger: 'blur' },
		],
		maxWeekCount: [
			{ required: true, message: '최대 배송 주차 수를 입력하세요.', trigger: 'blur' },
		],
		deliveryDaysPerWeek: [
			{ required: true, message: '주당 배송 일수를 입력하세요.', trigger: 'blur' },
		],
		selectableProductCount: [
			{ required: true, message: '선택 가능한 상품 갯수를 입력하세요.', trigger: 'blur' },
		],
	};

	private apiUrl = process.env.VUE_APP_BASE_API;

	private async loadAvailableProducts() {
		const query = {
			page: 1,
			size: 1000,
			productType: 'STATION',
			extraProductStatus: false,
		};
		const res = await getProductList(query);
		this.availableProducts = res.data.content;
	}

	private handleSelectionChange(selection: any[]) {
		this.form.productIdxList = selection.map((item) => item.idx);
	}

	private handleDeliveryFeeChange(value: string) {
		if (value === 'free') {
			this.form.deliveryFee = 0;
		}
	}

	private handleAddShopItem() {
		(this.$refs.form as Form).validate((valid: boolean) => {
			if (valid) {
				if (this.form.productIdxList.length === 0) {
					this.$message.warning('패키지에 포함할 상품을 최소 1개 이상 선택해주세요.');
					return;
				}

				if (this.idx) {
					updatePackage(this.idx, this.form).then(() => {
						this.$message.success('패키지를 수정했습니다.');
						this.handleGoList();
					});
				} else {
					addPackage(this.form).then((res) => {
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
		this.$router.push({ name: 'ProductPackageIndex' });
	}

	private changeContent(description: string) {
		this.form.description = description;
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
