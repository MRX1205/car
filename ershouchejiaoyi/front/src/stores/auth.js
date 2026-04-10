import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as authApi from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('Token') || '')
  const userTable = ref(localStorage.getItem('userTable') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isUser = computed(() => userTable.value === 'yonghu')
  const isMerchant = computed(() => userTable.value === 'shangjia')
  const isAdmin = computed(() => userTable.value === 'users')

  const setAuth = (newToken, newUserTable, newUserInfo) => {
    token.value = newToken
    userTable.value = newUserTable
    userInfo.value = newUserInfo

    localStorage.setItem('Token', newToken)
    localStorage.setItem('userTable', newUserTable)
    localStorage.setItem('userInfo', JSON.stringify(newUserInfo))
  }

  const clearAuth = () => {
    token.value = ''
    userTable.value = ''
    userInfo.value = {}

    localStorage.removeItem('Token')
    localStorage.removeItem('userTable')
    localStorage.removeItem('userInfo')
  }

  const updateUserInfo = (newInfo) => {
    userInfo.value = { ...userInfo.value, ...newInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    token,
    userTable,
    userInfo,
    isLoggedIn,
    isUser,
    isMerchant,
    isAdmin,
    setAuth,
    clearAuth,
    updateUserInfo
  }
})
