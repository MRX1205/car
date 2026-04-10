<template>
  <div class="vehicle-form-container">
    <div class="form-header">
      <h1>{{ isEdit ? '编辑车辆' : '新增车辆' }}</h1>
    </div>

    <div class="form-content" v-loading="loading">
      <el-form
        :model="vehicleForm"
        :rules="rules"
        ref="formRef"
        label-width="120px"
        class="vehicle-form"
      >
        <!-- 车辆名称 -->
        <el-form-item label="车辆名称" prop="cheliangName">
          <el-input v-model="vehicleForm.cheliangName" placeholder="请输入车辆名称" />
        </el-form-item>

        <!-- 车辆品牌 -->
        <el-form-item label="车辆品牌" prop="cheliangTypes">
          <el-select v-model="vehicleForm.cheliangTypes" placeholder="请选择车辆品牌" class="full-width">
            <el-option
              v-for="type in cheliangTypes"
              :key="type.codeIndex"
              :label="type.indexName"
              :value="type.codeIndex"
            />
          </el-select>
        </el-form-item>

        <!-- 车辆价格 -->
        <el-form-item label="车辆价格" prop="cheliangNewMoney">
          <el-input-number 
            v-model="vehicleForm.cheliangNewMoney" 
            :min="0" 
            placeholder="请输入车辆价格"
            class="full-width"
          />
        </el-form-item>

        <!-- 车辆图片 -->
        <el-form-item label="车辆图片" prop="cheliangPhoto">
          <div class="image-upload">
            <img v-if="vehicleForm.cheliangPhoto" :src="vehicleForm.cheliangPhoto" class="preview-image" />
            <el-upload
              action="/ershouchejiaoyi/file/upload"
              :headers="{ Token: authStore.token }"
              :on-success="handleImageSuccess"
              :on-error="handleImageError"
              :before-upload="beforeImageUpload"
              class="image-uploader"
            >
              <el-button type="primary">上传图片</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <!-- 车辆描述 -->
        <el-form-item label="车辆描述" prop="cheliangContent">
          <el-input 
            v-model="vehicleForm.cheliangContent" 
            placeholder="请输入车辆描述" 
            type="textarea"
            rows="6"
          />
        </el-form-item>

        <!-- 上架状态 -->
        <el-form-item label="上架状态" prop="shangxiaTypes">
          <el-radio-group v-model="vehicleForm.shangxiaTypes">
            <el-radio label="1">上架</el-radio>
            <el-radio label="2">下架</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            {{ isEdit ? '更新' : '发布' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as cheliangApi from '../../api/cheliang'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref(null)
const loading = ref(false)
const submitLoading = ref(false)
const cheliangTypes = ref([])

const isEdit = computed(() => !!route.params.id)

const vehicleForm = ref({
  cheliangName: '',
  cheliangTypes: 1,
  cheliangNewMoney: 0,
  cheliangPhoto: '',
  cheliangContent: '',
  shangxiaTypes: 1
})

const rules = {
  cheliangName: [
    { required: true, message: '请输入车辆名称', trigger: 'blur' }
  ],
  cheliangNewMoney: [
    { required: true, message: '请输入车辆价格', trigger: 'blur' }
  ],
  cheliangPhoto: [
    { required: true, message: '请上传车辆图片', trigger: 'change' }
  ],
  cheliangContent: [
    { required: true, message: '请输入车辆描述', trigger: 'blur' }
  ]
}

const loadCheliangTypes = async () => {
  try {
    const res = await cheliangApi.getCheliangTypes()
    console.log('品牌列表响应:', res)
    if (res.code === 0 || res.code === 200) {
      // 后端返回分页数据，需要从 res.data.list 获取
      const pageData = res.data || {}
      cheliangTypes.value = pageData.list || res.data || []
      console.log('品牌列表:', cheliangTypes.value)
      if (cheliangTypes.value.length === 0) {
        console.warn('车辆类型列表为空')
      }
    } else {
      console.error('加载车辆类型失败:', res)
      ElMessage.error(res.msg || '加载车辆类型失败')
    }
  } catch (error) {
    console.error('加载车辆类型失败', error)
    ElMessage.error('加载车辆类型失败')
  }
}

const loadVehicleDetail = async () => {
  if (!isEdit.value) return

  loading.value = true
  try {
    const res = await cheliangApi.getCheliangDetail(route.params.id)
    if (res.code === 0 || res.code === 200) {
      const data = res.data
      // 处理图片路径用于预览
      let photoPath = data.cheliangPhoto || ''
      if (photoPath && !photoPath.startsWith('http') && !photoPath.startsWith('/')) {
        photoPath = `/ershouchejiaoyi/upload/${photoPath}`
      }
      vehicleForm.value = {
        cheliangName: data.cheliangName || '',
        cheliangTypes: data.cheliangTypes || 1,
        cheliangNewMoney: data.cheliangNewMoney || 0,
        cheliangPhoto: photoPath,
        cheliangContent: data.cheliangContent || '',
        shangxiaTypes: data.shangxiaTypes || 1
      }
    } else {
      ElMessage.error(res.msg || '加载车辆信息失败')
    }
  } catch (error) {
    console.error('加载车辆信息失败:', error)
    ElMessage.error('加载车辆信息失败')
  } finally {
    loading.value = false
  }
}

const handleImageSuccess = (response) => {
  console.log('图片上传响应:', response)
  if (response.code === 0 || response.code === 200) {
    // 后端返回格式: { code: 0, file: "xxx.jpg" }
    const fileName = response.file
    if (fileName) {
      // 存储完整路径用于预览
      const fullPath = fileName.startsWith('http') ? fileName : `/ershouchejiaoyi/upload/${fileName}`
      vehicleForm.value.cheliangPhoto = fullPath
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败：无法获取图片路径')
    }
  } else {
    ElMessage.error(response.msg || '图片上传失败')
  }
}

const handleImageError = (error) => {
  console.error('图片上传错误:', error)
  ElMessage.error('图片上传失败，请重试')
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 处理图片路径：提取文件名（只取最后一段）
        let photoPath = vehicleForm.value.cheliangPhoto || ''
        if (photoPath) {
          // 如果是完整URL或路径，提取文件名
          if (photoPath.includes('/')) {
            const parts = photoPath.split('/')
            photoPath = parts[parts.length - 1]
          }
        }
        
        const submitData = {
          cheliangName: vehicleForm.value.cheliangName,
          cheliangTypes: vehicleForm.value.cheliangTypes || 1,
          cheliangNewMoney: vehicleForm.value.cheliangNewMoney,
          cheliangPhoto: photoPath,
          cheliangContent: vehicleForm.value.cheliangContent,
          shangxiaTypes: vehicleForm.value.shangxiaTypes || 1
        }
        
        if (isEdit.value) {
          submitData.id = parseInt(route.params.id)
          const res = await cheliangApi.updateCheliang(submitData)
          if (res.code === 0 || res.code === 200) {
            ElMessage.success('车辆信息已更新')
            router.push('/merchant/vehicles')
          } else {
            ElMessage.error(res.msg || '更新失败')
          }
        } else {
          const res = await cheliangApi.addCheliang(submitData)
          if (res.code === 0 || res.code === 200) {
            ElMessage.success('车辆已发布')
            router.push('/merchant/vehicles')
          } else {
            ElMessage.error(res.msg || '发布失败')
          }
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadCheliangTypes()
  loadVehicleDetail()
})
</script>

<style scoped>
.vehicle-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.form-header {
  margin-bottom: 30px;
}

.form-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.form-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.vehicle-form {
  max-width: 600px;
}

.image-upload {
  display: flex;
  align-items: center;
  gap: 20px;
}

.preview-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.full-width {
  width: 100%;
}

:deep(.el-input__wrapper) {
  padding: 10px 15px;
  background-color: #f8fafc;
  border: 1px solid transparent;
  box-shadow: inset 0 0 0 1px #e2e8f0 !important;
  border-radius: 8px !important;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: inset 0 0 0 2px #3b82f6 !important;
}

@media (max-width: 480px) {
  .vehicle-form-container {
    padding: 20px 12px;
  }

  .form-content {
    padding: 16px;
  }

  .vehicle-form {
    max-width: 100%;
  }

  :deep(.el-form-item__label) {
    width: 80px !important;
  }

  .image-upload {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
