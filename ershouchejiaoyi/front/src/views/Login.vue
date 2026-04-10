<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <h2>欢迎回来</h2>
        <p>二手车交易平台</p>
      </div>

      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
            size="large"
            clearable>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin">
          </el-input>
        </el-form-item>

        <el-form-item prop="role">
          <el-radio-group v-model="loginForm.role" class="role-group">
            <el-radio label="yonghu">普通用户</el-radio>
            <el-radio label="shangjia">商家</el-radio>
            <el-radio label="users">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住密码，下次自动填充</el-checkbox>
        </el-form-item>

        <el-form-item>
          <div class="form-actions">
            <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
              登 录
            </el-button>
          </div>
        </el-form-item>

        <div class="register-link">
          还没有账号？ <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import * as authApi from '../api/auth'

const router = useRouter()
const authStore = useAuthStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  role: 'yonghu',
  rememberMe: false
})

const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        let res
        if (loginForm.role === 'yonghu') {
          res = await authApi.loginUser(loginForm.username, loginForm.password)
        } else if (loginForm.role === 'shangjia') {
          res = await authApi.loginMerchant(loginForm.username, loginForm.password)
        } else if (loginForm.role === 'users') {
          res = await authApi.loginAdmin(loginForm.username, loginForm.password)
        }

        // 后端登录成功直接返回 token，不包含 code 字段
        if (res.token) {
          // 先保存token
          authStore.setAuth(res.token, loginForm.role, {
            id: res.userId,
            username: res.username,
            role: res.role,
            tableName: res.tableName
          })

          // 获取完整用户信息（包含头像等）
          let sessionRes
          if (loginForm.role === 'yonghu') {
            sessionRes = await authApi.getUserSession()
          } else if (loginForm.role === 'shangjia') {
            sessionRes = await authApi.getMerchantSession()
          } else if (loginForm.role === 'users') {
            sessionRes = await authApi.getAdminSession()
          }

          // 更新完整的用户信息
          if (sessionRes && (sessionRes.code === 0 || sessionRes.code === 200)) {
            authStore.updateUserInfo(sessionRes.data || {})
          }

          ElMessage.success('登录成功')

          if (loginForm.rememberMe) {
            localStorage.setItem('savedLogin', JSON.stringify({
              username: loginForm.username,
              password: loginForm.password,
              role: loginForm.role
            }))
          } else {
            localStorage.removeItem('savedLogin')
          }

          setTimeout(() => {
            if (loginForm.role === 'shangjia') {
              router.push('/merchant')
            } else if (loginForm.role === 'users') {
              router.push('/admin')
            } else {
              router.push('/')
            }
          }, 300)
        } else {
          ElMessage.error(res.msg || '账号或密码不正确')
        }
      } catch (error) {
        console.error('Login error:', error)
        ElMessage.error('登录失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }
  })
}

import { onMounted } from 'vue'
onMounted(() => {
  const saved = localStorage.getItem('savedLogin')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      loginForm.username = data.username || ''
      loginForm.password = data.password || ''
      loginForm.role = data.role || 'yonghu'
      loginForm.rememberMe = true
    } catch(e){}
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
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

.login-container::after {
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

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 24px;
  padding: 40px 30px 30px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
  z-index: 1;
  animation: slideUpFade 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.login-header h2 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0 0 12px;
  font-weight: 700;
  letter-spacing: 1px;
}

.login-header p {
  color: #64748b;
  margin: 0;
  font-size: 1.05rem;
}

.login-form {
  margin-top: 20px;
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

.role-group {
  display: flex;
  justify-content: center;
  width: 100%;
  margin: 5px 0;
}

.form-actions {
  width: 100%;
  margin-top: 15px;
}

.login-btn {
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

.login-btn:hover {
  background: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
}

.login-btn:active {
  transform: translateY(0px);
  background: #1e40af !important;
  border-color: #1e40af !important;
}

.register-link {
  text-align: center;
  margin-top: 25px;
  color: #64748b;
  font-size: 0.95rem;
  position: relative;
  z-index: 100;
}

.register-link a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s;
  cursor: pointer;
  pointer-events: auto;
  position: relative;
  z-index: 100;
}

.register-link a:hover {
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
  .login-card {
    margin: 20px;
    padding: 30px 20px;
  }
}
</style>
