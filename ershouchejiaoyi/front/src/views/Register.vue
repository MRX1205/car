<template>
  <div class="register-container">
    <el-card class="register-card" shadow="always">
      <div class="register-header">
        <h2>创建账户</h2>
        <p>加入二手车交易平台</p>
      </div>

      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
        <!-- 角色选择 -->
        <el-form-item prop="role">
          <el-radio-group v-model="registerForm.role" class="role-group">
            <el-radio label="yonghu">普通用户</el-radio>
            <el-radio label="shangjia">商家</el-radio>
            <el-radio label="users">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 管理员注册码 -->
        <el-form-item v-if="registerForm.role === 'users'" prop="registerCode">
          <el-input
            v-model="registerForm.registerCode"
            placeholder="请输入管理员注册码"
            prefix-icon="Key"
            size="large"
            clearable>
          </el-input>
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            请联系系统管理员获取注册码
          </div>
        </el-form-item>



        <!-- 账户 -->
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>

        <!-- 姓名/商家名称 -->
        <el-form-item v-if="registerForm.role === 'yonghu'" prop="yonghuName">
          <el-input
            v-model="registerForm.yonghuName"
            placeholder="请输入姓名"
            prefix-icon="User"
            size="large"
            clearable>
          </el-input>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 'shangjia'" prop="shangjiaName">
          <el-input
            v-model="registerForm.shangjiaName"
            placeholder="请输入商家名称"
            prefix-icon="User"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item v-if="registerForm.role !== 'users'" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item v-if="registerForm.role !== 'users'" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <!-- 商家地址 -->
        <el-form-item v-if="registerForm.role === 'shangjia'" prop="address">
          <el-input
            v-model="registerForm.address"
            placeholder="请输入商家地址"
            prefix-icon="Location"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <!-- 同意条款 -->
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <a href="#" class="agreement-link">《用户协议》</a>
            和
            <a href="#" class="agreement-link">《隐私政策》</a>
          </el-checkbox>
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button type="primary" size="large" class="register-btn" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>

        <div class="login-link">
          已有账号？ <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as authApi from '../api/auth'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  role: 'yonghu',
  username: '',
  password: '',
  confirmPassword: '',
  registerCode: '',
  yonghuName: '',
  shangjiaName: '',
  phone: '',
  email: '',
  address: '',
  agreement: false
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  yonghuName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  shangjiaName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入商家地址', trigger: 'blur' }
  ],
  agreement: [
    { required: true, message: '请同意用户协议', trigger: 'change' }
  ],
  registerCode: [
    { required: true, message: '请输入管理员注册码', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        let data = {
          username: registerForm.username,
          password: registerForm.password
        }

        if (registerForm.role === 'yonghu') {
          data = {
            ...data,
            yonghuName: registerForm.yonghuName,
            yonghuPhone: registerForm.phone,
            yonghuEmail: registerForm.email,
            payPassword: registerForm.password
          }
          await authApi.registerUser(data)
        } else if (registerForm.role === 'shangjia') {
          data = {
            ...data,
            shangjiaName: registerForm.shangjiaName,
            shangjiaPhone: registerForm.phone,
            shangjiaAddress: registerForm.address
          }
          await authApi.registerMerchant(data)
        } else if (registerForm.role === 'users') {
          // 验证注册码
          if (registerForm.registerCode !== 'admin') {
            ElMessage.error('管理员注册码错误')
            return
          }
          data = {
            ...data,
            registerCode: registerForm.registerCode,
            role: '管理员'
          }
          await authApi.registerAdmin(data)
        }

        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.response?.data?.msg || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.register-container::before {
  content: '';
  position: absolute;
  top: -100px;
  left: -100px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(96, 165, 250, 0.05) 100%);
  z-index: 0;
}

.register-container::after {
  content: '';
  position: absolute;
  bottom: -150px;
  right: -100px;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.08) 0%, rgba(56, 189, 248, 0.03) 100%);
  z-index: 0;
}

.register-card {
  width: 100%;
  max-width: 480px;
  border-radius: 24px;
  padding: 40px 30px 30px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
  z-index: 1;
  animation: slideUpFade 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0 0 12px;
  font-weight: 700;
  letter-spacing: 1px;
}

.register-header p {
  color: #64748b;
  margin: 0;
  font-size: 1.05rem;
}

.register-form {
  margin-top: 20px;
}

.role-group {
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 5px 0;
}

:deep(.el-input__wrapper) {
  padding: 10px 15px;
  background-color: #f8fafc;
  border: 1px solid transparent;
  box-shadow: inset 0 0 0 1px #e2e8f0 !important;
  border-radius: 16px !important;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: inset 0 0 0 2px #3b82f6 !important;
}

:deep(.el-checkbox__label) {
  color: #64748b;
  font-size: 0.95rem;
}

.agreement-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
}

.agreement-link:hover {
  text-decoration: underline;
}

.register-btn {
  width: 100%;
  padding: 24px 0;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 4px;
  border-radius: 20px;
  background: #2563eb !important;
  border: 2px solid #2563eb !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.register-btn:hover {
  background: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
}

.register-btn:active {
  transform: translateY(0px);
  background: #1e40af !important;
  border-color: #1e40af !important;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #64748b;
  font-size: 0.95rem;
}

.login-link a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
}

.login-link a:hover {
  color: #2563eb;
  text-decoration: underline;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 480px) {
  .register-card {
    margin: 20px;
    padding: 30px 20px;
  }

  .register-header h2 {
    font-size: 1.5rem;
  }
}
</style>
