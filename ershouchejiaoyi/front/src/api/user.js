import request from '../utils/request'

// 获取用户详情
export const getUserInfo = (id) => {
  return request.get(`/yonghu/info/${id}`)
}

// 获取当前登录用户信息（session）
export const getUserSession = () => {
  return request.get('/yonghu/session')
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request.post('/yonghu/update', data)
}

// 获取商家详情
export const getMerchantInfo = (id) => {
  return request.get(`/shangjia/info/${id}`)
}

// 获取当前登录商家信息（session）
export const getMerchantSession = () => {
  return request.get('/shangjia/session')
}

// 更新商家信息
export const updateMerchantInfo = (data) => {
  return request.post('/shangjia/update', data)
}

// 上传文件（头像/图片）
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
