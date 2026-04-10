<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人信息</h1>
    </div>

    <div class="profile-content" v-loading="loading">
      <el-form
        :model="profileForm"
        :rules="rules"
        ref="profileFormRef"
        label-width="120px"
        class="profile-form"
      >
        <el-form-item label="头像">
          <div class="avatar-section">
            <el-avatar :size="100" :src="getAvatarUrl()" />
            <div class="avatar-actions">
              <el-upload
                action="/ershouchejiaoyi/file/upload"
                :headers="{ Token: authStore.token }"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                class="avatar-uploader"
              >
                <el-button type="primary">上传头像</el-button>
              </el-upload>
            </div>
          </div>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item label="账户" prop="username">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>

        <!-- 姓名 -->
        <el-form-item label="姓名" prop="yonghuName">
          <el-input v-model="profileForm.yonghuName" placeholder="请输入姓名" />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号" prop="yonghuPhone">
          <el-input v-model="profileForm.yonghuPhone" placeholder="请输入手机号" />
        </el-form-item>

        <!-- 身份证号 -->
        <el-form-item label="身份证号" prop="yonghuIdNumber">
          <el-input v-model="profileForm.yonghuIdNumber" placeholder="请输入身份证号" />
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱" prop="yonghuEmail">
          <el-input v-model="profileForm.yonghuEmail" placeholder="请输入邮箱" />
        </el-form-item>

        <!-- 性别 -->
        <el-form-item label="性别" prop="sexTypes">
          <el-radio-group v-model="profileForm.sexTypes">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
            <el-radio label="3">保密</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 支付密码 -->
        <el-form-item label="支付密码" prop="payPassword">
          <el-input
            v-model="profileForm.payPassword"
            type="password"
            placeholder="请输入支付密码"
            show-password
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            默认支付密码与登录密码相同，建议修改后保存
          </div>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            保存修改
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as userApi from '../../api/user'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const profileFormRef = ref(null)
const loading = ref(false)
const submitLoading = ref(false)
const previewAvatar = ref('')

const profileForm = ref({
  id: '',
  username: '',
  yonghuName: '',
  yonghuPhone: '',
  yonghuIdNumber: '',
  yonghuEmail: '',
  yonghuPhoto: '',
  payPassword: '',
  sexTypes: '3'
})

const rules = {
  yonghuName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  yonghuPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  yonghuEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  payPassword: [
    { required: true, message: '请输入支付密码', trigger: 'blur' },
    { min: 6, max: 20, message: '支付密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const getAvatarUrl = () => {
  if (previewAvatar.value) {
    return previewAvatar.value
  }
  if (profileForm.value.yonghuPhoto) {
    const photo = profileForm.value.yonghuPhoto
    if (photo.startsWith('http') || photo.startsWith('/')) {
      return photo
    }
    return `/ershouchejiaoyi/upload/${photo}`
  }
  return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
}

const loadProfile = async () => {
  loading.value = true
  try {
    const userId = authStore.userInfo.id
    const res = await userApi.getUserInfo(userId)
    if (res.code === 0 || res.code === 200) {
      Object.assign(profileForm.value, res.data)
      previewAvatar.value = ''
    }
  } catch (error) {
    ElMessage.error('加载个人信息失败')
  } finally {
    loading.value = false
  }
}

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 0 || response.code === 200) {
    const filename = response.file || response.data
    // 只保存文件名，不保存完整路径
    if (filename) {
      const cleanFilename = filename.replace('/ershouchejiaoyi/upload/', '').replace(/^\//, '')
      profileForm.value.yonghuPhoto = cleanFilename
      if (uploadFile && uploadFile.raw) {
        previewAvatar.value = URL.createObjectURL(uploadFile.raw)
      }
      ElMessage.success('头像上传成功，请点击"保存修改"按钮保存！')
    }
  } else {
    ElMessage.error(response.msg || '头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
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
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await userApi.updateUserInfo(profileForm.value)
        if (res.code === 0 || res.code === 200) {
          ElMessage.success('个人信息更新成功')
          authStore.updateUserInfo(profileForm.value)
          previewAvatar.value = ''
          loadProfile()
        } else {
          ElMessage.error(res.msg || '更新失败')
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败，请重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  loadProfile()
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  width: 90%;
  margin: 0 auto;
  padding: 40px 20px;
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
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.profile-form {
  max-width: 600px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.el-form-item__content) {
  line-height: 1;
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

:deep(.el-radio) {
  margin-right: 20px;
}

@media (max-width: 480px) {
  .profile-container {
    padding: 20px 12px;
  }

  .profile-content {
    padding: 16px;
  }

  .profile-form {
    max-width: 100%;
  }

  :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>
