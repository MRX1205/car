import request from '../utils/request'

// 获取订单列表（page 接口，需要登录，后端根据 session 角色自动过滤）
export const getOrderList = (params) => {
  return request.get('/cheliangOrder/page', { params })
}

// 获取订单详情
export const getOrderDetail = (id) => {
  return request.get(`/cheliangOrder/info/${id}`)
}

// 创建订单（前端下单）
export const createOrder = (data) => {
  return request.post('/cheliangOrder/add', data)
}

// 用户支付订单
export const payOrder = (id) => {
  return request.post('/cheliangOrder/pay', { id })
}

// 用户取消订单
export const cancelOrder = (id, cancelReason) => {
  return request.post('/cheliangOrder/cancel', { id, cancelReason })
}

// 用户确认收货（完成）
export const confirmOrder = (id) => {
  return request.post('/cheliangOrder/finish', { id })
}

// 商家发货（confirm = 状态改为3）
export const shipOrder = (id) => {
  return request.post('/cheliangOrder/confirm', { id })
}

// 商家拒绝订单
export const rejectOrder = (id, cancelReason) => {
  return request.post('/cheliangOrder/reject', { id, cancelReason })
}

// 商家完成订单
export const completeOrder = (id) => {
  return request.post('/cheliangOrder/finish', { id })
}

// 删除订单
export const deleteOrder = (id) => {
  return request.post('/cheliangOrder/delete', [id])
}
