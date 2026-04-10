import request from '../utils/request'

// 获取论坛帖子列表
export const getForumList = (params) => {
  return request.get('/forum/list', { params })
}

// 获取论坛帖子详情
export const getForumDetail = (id) => {
  return request.get(`/forum/detail/${id}`)
}

// 创建论坛帖子
export const createForum = (data) => {
  return request.post('/forum/add', data)
}

// 更新论坛帖子
export const updateForum = (data) => {
  return request.post('/forum/update', data)
}

// 删除论坛帖子
export const deleteForum = (id) => {
  const ids = Array.isArray(id) ? id : [id]
  return request.post('/forum/delete', ids)
}

// 获取公告列表
export const getNewsList = (params) => {
  return request.get('/news/list', { params })
}

// 获取公告详情
export const getNewsDetail = (id) => {
  return request.get(`/news/detail/${id}`)
}

// 获取车辆留言列表（前端接口，路径正确）
export const getCheliangCommentList = (cheliangId, params) => {
  return request.get('/cheliangLiuyan/list', {
    params: { ...params, cheliangId }
  })
}

// 添加车辆留言
export const addCheliangComment = (data) => {
  return request.post('/cheliangLiuyan/save', data)
}

// 删除车辆留言
export const deleteCheliangComment = (id) => {
  const ids = Array.isArray(id) ? id : [id]
  return request.post('/cheliangLiuyan/delete', ids)
}
