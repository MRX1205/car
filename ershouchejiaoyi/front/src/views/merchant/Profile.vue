<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>商家资料</h1>
    </div>

    <div class="profile-content" v-loading="loading">
      <div class="profile-card">
        <div class="profile-avatar">
          <img :src="previewAvatar || getMerchantAvatar()" :alt="merchantInfo.shangjiaName" class="avatar-image" />
          <el-button type="primary" text @click="showAvatarUpload = true">更换头像</el-button>
        </div>

        <el-form :model="merchantInfo" :rules="rules" ref="formRef" label-width="120px" class="profile-form">
          <el-form-item label="商家名称" prop="shangjiaName">
            <el-input v-model="merchantInfo.shangjiaName" placeholder="请输入商家名称" />
          </el-form-item>

          <el-form-item label="联系电话" prop="shangjiaPhone">
            <el-input v-model="merchantInfo.shangjiaPhone" placeholder="请输入联系电话" />
          </el-form-item>

          <el-form-item label="邮箱" prop="shangjiaEmail">
            <el-input v-model="merchantInfo.shangjiaEmail" placeholder="请输入邮箱" type="email" />
          </el-form-item>

          <el-form-item label="商家简介" prop="shangjiaContent">
            <el-input v-model="merchantInfo.shangjiaContent" placeholder="请输入商家简介" type="textarea" rows="6" maxlength="500" show-word-limit />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="submitLoading">保存修改</el-button>
            <el-button @click="resetForm">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog v-model="showAvatarUpload" title="更换头像" width="400px">
      <el-upload
        action="/ershouchejiaoyi/file/upload"
        :headers="{ Token: authStore.token }"
        :on-success="handleAvatarSuccess"
        :on-error="handleAvatarError"
        :before-upload="beforeImageUpload"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此或<em>点击上传</em>
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import * as userApi from '../../api/user'
import { useAuthStore } from '../../stores/auth'

const loading = ref(false)
const submitLoading = ref(false)
const showAvatarUpload = ref(false)
const previewAvatar = ref('')

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const baseUrl = '/ershouchejiaoyi/upload/'

const merchantInfo = reactive({
  id: '',
  shangjiaName: '',
  shangjiaPhone: '',
  shangjiaEmail: '',
  shangjiaContent: '',
  shangjiaPhoto: ''
})

const rules = {
  shangjiaName: [{ required: true, message: '请输入商家名称', trigger: 'blur' }],
  shangjiaPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  shangjiaEmail: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const getMerchantAvatar = () => {
  if (!merchantInfo.shangjiaPhoto) return defaultAvatar
  const photo = merchantInfo.shangjiaPhoto
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  return baseUrl + photo.split(',')[0]
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

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 0 || response.code === 200) {
    const filename = response.file || response.data
    if (filename) {
      // 只保存文件名，不保存完整路径
      const cleanFilename = filename.replace('/ershouchejiaoyi/upload/', '').replace(/^\//, '')
      merchantInfo.shangjiaPhoto = cleanFilename
      if (uploadFile && uploadFile.raw) {
        previewAvatar.value = URL.createObjectURL(uploadFile.raw)
      }
      ElMessage.success('头像上传成功，请点击"保存修改"按钮保存！')
      showAvatarUpload.value = false
    } else {
      ElMessage.error('头像上传失败：无法获取图片路径')
    }
  } else {
    ElMessage.error(response.msg || '头像上传失败')
  }
}

const handleAvatarError = (error) => {
  console.error('头像上传错误:', error)
  ElMessage.error('头像上传失败')
}

const loadMerchantInfo = async () => {
  loading.value = true
  try {
    const res = await userApi.getMerchantSession()
    if (res.code === 0 || res.code === 200) {
      const data = res.data || {}
      Object.assign(merchantInfo, data)
    } else {
      ElMessage.error(res.msg || '加载商家信息失败')
    }
  } catch (error) {
    console.error('加载商家信息失败:', error)
    ElMessage.error('加载商家信息失败')
  } finally {
    loading.value = false
  }
}

const authStore = useAuthStore()
const formRef = ref(null)

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const res = await userApi.updateMerchantInfo(merchantInfo)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('商家资料更新成功')
        authStore.updateUserInfo(merchantInfo)
        previewAvatar.value = ''
        loadMerchantInfo()
      } else {
        ElMessage.error(res.msg || '更新失败')
      }
    } catch (error) {
      console.error('更新失败:', error)
      ElMessage.error('更新失败，请重试')
    } finally {
      submitLoading.value = false
    }
  })
}

const resetForm = () => {
  loadMerchantInfo()
}

onMounted(() => {
  loadMerchantInfo()
})

</script>

<style scoped>
.profile-container {
  padding: 30px;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.profile-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.profile-card {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 40px;
  align-items: start;
}

.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-image {
  width: 180px;
  height: 180px;
  border-radius: 12px;
  object-fit: cover;
  border: 2px solid #f1f5f9;
}

.profile-form {
  width: 100%;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }

  .profile-content {
    padding: 16px;
  }

  .profile-card {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .avatar-image {
    width: 120px;
    height: 120px;
  }
}
</style>
