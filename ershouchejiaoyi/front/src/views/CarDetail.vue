<template>
  <div class="car-detail-container" v-loading="loading">
    <div class="car-detail-content" v-if="car">
      <!-- 图片和基本信息 -->
      <div class="detail-header">
        <div class="car-image-section">
          <img :src="car.cheliangPhoto" :alt="car.cheliangName" class="main-image" />
          <div class="image-info">
            <span v-if="car.shangxiaTypes === 1" class="badge-on-sale">在售</span>
            <span v-else class="badge-off-sale">已下架</span>
          </div>
        </div>

        <div class="car-basic-info">
          <h1 class="car-title">{{ car.cheliangName }}</h1>

          <div class="price-section">
            <span class="price-label">售价</span>
            <span class="price">¥{{ car.cheliangNewMoney }} 万</span>
          </div>

          <div class="stats-section">
            <div class="stat">
              <span class="stat-label">浏览</span>
              <span class="stat-value">{{ car.cheliangClicknum || 0 }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">点赞</span>
              <span class="stat-value">{{ car.zanNumber || 0 }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">点踩</span>
              <span class="stat-value">{{ car.caiNumber || 0 }}</span>
            </div>
          </div>

          <!-- 点赞/点踩按钮 -->
          <div class="like-actions">
            <el-button
              type="success"
              :icon="likeStatus === 2 ? 'Check' : 'Promotion'"
              @click="handleLike"
              :disabled="car.shangxiaTypes !== 1"
            >
              {{ likeStatus === 2 ? '已点赞' : '点赞' }}
            </el-button>
            <el-button
              type="danger"
              :icon="likeStatus === 3 ? 'Close' : 'Remove'"
              @click="handleDislike"
              :disabled="car.shangxiaTypes !== 1"
            >
              {{ likeStatus === 3 ? '已点踩' : '点踩' }}
            </el-button>
          </div>

          <div class="action-buttons">
            <el-button
              type="warning"
              size="large"
              @click="buyNow"
              :loading="buyLoading"
              :disabled="car.shangxiaTypes !== 1"
            >
              立即购买
            </el-button>
            <el-button
              type="primary"
              size="large"
              @click="addToCart"
              :disabled="car.shangxiaTypes !== 1"
            >
              加入购物车
            </el-button>
            <el-button
              size="large"
              @click="toggleCollection"
              :type="isCollected ? 'danger' : 'default'"
            >
              <el-icon><Star /></el-icon>
              {{ isCollected ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <div class="merchant-info">
            <div class="merchant-header">
              <span class="label">商家信息</span>
            </div>
            <div class="merchant-details">
              <p><strong>商家名称：</strong> {{ merchant?.shangjiaName || '未知' }}</p>
              <p><strong>联系电话：</strong> {{ merchant?.shangjiaPhone || '未提供' }}</p>
              <p><strong>地址：</strong> {{ merchant?.shangjiaAddress || '未提供' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 详细信息 -->
      <div class="detail-section">
        <h2>车辆介绍</h2>
        <div class="content-text">{{ car.cheliangContent }}</div>
      </div>

      <!-- 留言评论 -->
      <div class="detail-section">
        <h2>用户评论</h2>

        <!-- 发表评论 -->
        <div class="comment-form" v-if="authStore.isLoggedIn">
          <el-input
            v-model="commentForm.text"
            type="textarea"
            placeholder="发表您的看法..."
            rows="4"
            maxlength="500"
            show-word-limit
          />
          <el-button type="primary" @click="submitComment" :loading="commentLoading">
            发表评论
          </el-button>
        </div>

        <div v-else class="login-prompt">
          <p>请 <router-link to="/login">登录</router-link> 后发表评论</p>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论" />
          </div>

          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-author">{{ comment.yonghuName || '匿名用户' }}</span>
              <span class="comment-time">{{ formatTime(comment.insertTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.cheliangLiuyanText }}</div>
            <div v-if="comment.replyText" class="comment-reply">
              <span class="reply-label">商家回复：</span>
              <span class="reply-content">{{ comment.replyText }}</span>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="comments.length > 0">
          <el-pagination
            v-model:current-page="commentPage"
            :page-size="10"
            :total="commentTotal"
            layout="prev, pager, next"
            @change="loadComments"
          />
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-empty description="车辆信息不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import * as cheliangApi from '../api/cheliang'
import * as cartApi from '../api/cart'
import * as communityApi from '../api/community'
import * as userApi from '../api/user'
import * as orderApi from '../api/order'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()

const loading = ref(false)
const car = ref(null)
const merchant = ref(null)
const isCollected = ref(false)
const likeStatus = ref(0) // 0-未操作, 2-已点赞, 3-已点踩
const comments = ref([])
const commentPage = ref(1)
const commentTotal = ref(0)
const commentLoading = ref(false)
const buyLoading = ref(false)

const commentForm = ref({
  text: ''
})

const loadCarDetail = async () => {
  loading.value = true
  try {
    const carId = route.params.id
    const res = await cheliangApi.getCheliangDetail(carId)
    if (res && (res.code === 0 || res.code === 200)) {
      car.value = res.data
      if (car.value && car.value.shangjiaId) {
        loadMerchantInfo(car.value.shangjiaId)
      }
      loadComments()
      checkCollection()
    } else {
      ElMessage.error(res?.msg || '加载车辆详情失败')
    }
  } catch (error) {
    console.error('加载车辆详情失败:', error)
    ElMessage.error('加载车辆详情失败')
  } finally {
    loading.value = false
  }
}

const loadMerchantInfo = async (merchantId) => {
  try {
    const res = await userApi.getMerchantInfo(merchantId)
    if (res.code === 0 || res.code === 200) {
      merchant.value = res.data
    }
  } catch (error) {
    console.error('加载商家信息失败', error)
  }
}

const loadComments = async () => {
  try {
    const res = await communityApi.getCheliangCommentList(route.params.id, {
      page: commentPage.value,
      limit: 10
    })
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      comments.value = pageData.list || []
      commentTotal.value = pageData.total || 0
    }
  } catch (error) {
    console.error('加载评论失败', error)
  }
}

const checkCollection = async () => {
  if (!authStore.isLoggedIn) return

  try {
    // checkCollection 实际调用 page 接口，检查列表中是否有当前车辆
    const res = await cartApi.checkCollection(route.params.id)
    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      const list = pageData.list || []
      isCollected.value = list.length > 0
      // 保存收藏 id 用于取消收藏，同时检查点赞/踩状态
      if (list.length > 0) {
        car.value = { ...car.value, collectionId: list[0].id }
        // 类型2是点赞，类型3是点踩
        const likeItem = list.find(item => item.cheliangCollectionTypes === 2)
        const dislikeItem = list.find(item => item.cheliangCollectionTypes === 3)
        if (likeItem) {
          likeStatus.value = 2
        } else if (dislikeItem) {
          likeStatus.value = 3
        }
      }
    }
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

const toggleCollection = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (isCollected.value) {
      // 移除收藏
      const collectionId = car.value?.collectionId
      if (collectionId) {
        await cartApi.removeCollection(collectionId)
        isCollected.value = false
        ElMessage.success('已取消收藏')
      }
    } else {
      // 添加收藏
      await cartApi.addCollection(route.params.id)
      isCollected.value = true
      ElMessage.success('已收藏')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const addToCart = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await cartApi.addToCart(route.params.id)
    cartStore.addItem(car.value)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    ElMessage.error('添加购物车失败')
  }
}

const buyNow = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  buyLoading.value = true
  try {
    const res = await orderApi.createOrder({
      cheliangId: route.params.id
    })
    if (res.code === 0 || res.code === 200) {
      ElMessage.success('订单生成成功，请支付')
      router.push(`/pay?orderId=${res.orderId}`)
    } else {
      ElMessage.error(res.msg || '下单失败')
    }
  } catch (error) {
    ElMessage.error('网络请求异常')
  } finally {
    buyLoading.value = false
  }
}

const submitComment = async () => {
  if (!commentForm.value.text.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  commentLoading.value = true
  try {
    await communityApi.addCheliangComment({
      cheliangId: route.params.id,
      cheliangLiuyanText: commentForm.value.text
    })
    ElMessage.success('评论发表成功')
    commentForm.value.text = ''
    commentPage.value = 1
    loadComments()
  } catch (error) {
    ElMessage.error('发表评论失败')
  } finally {
    commentLoading.value = false
  }
}

const handleLike = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    // 如果已经点赞，则取消
    if (likeStatus.value === 2) {
      await cartApi.removeCollection(car.value.collectionId)
      likeStatus.value = 0
      car.value.zanNumber = (car.value.zanNumber || 0) - 1
      ElMessage.success('已取消点赞')
    } else {
      // 添加点赞（类型2）
      await cartApi.addLikeOrDislike(route.params.id, 2)
      likeStatus.value = 2
      car.value.zanNumber = (car.value.zanNumber || 0) + 1
      ElMessage.success('点赞成功')
    }
    // 重新检查状态
    checkCollection()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDislike = async () => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    // 如果已经点踩，则取消
    if (likeStatus.value === 3) {
      await cartApi.removeCollection(car.value.collectionId)
      likeStatus.value = 0
      car.value.caiNumber = (car.value.caiNumber || 0) - 1
      ElMessage.success('已取消点踩')
    } else {
      // 添加点踩（类型3）
      await cartApi.addLikeOrDislike(route.params.id, 3)
      likeStatus.value = 3
      car.value.caiNumber = (car.value.caiNumber || 0) + 1
      ElMessage.success('点踩成功')
    }
    // 重新检查状态
    checkCollection()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadCarDetail()
})
</script>

<style scoped>
.car-detail-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 40px 20px;
}

.detail-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.car-image-section {
  position: relative;
}

.main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
}

.image-info {
  position: absolute;
  top: 12px;
  left: 12px;
}

.badge-on-sale,
.badge-off-sale {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
}

.badge-on-sale {
  background: #10b981;
}

.badge-off-sale {
  background: #ef4444;
}

.car-basic-info {
  display: flex;
  flex-direction: column;
}

.car-title {
  font-size: 2rem;
  color: #1e293b;
  margin: 0 0 20px;
  font-weight: 700;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
}

.price-label {
  font-size: 1rem;
  color: #64748b;
  font-weight: 600;
}

.price {
  font-size: 2.5rem;
  color: #ef4444;
  font-weight: 700;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.9rem;
  color: #64748b;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  color: #1e293b;
  font-weight: 700;
}

.action-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.action-buttons :deep(.el-button) {
  flex: 1;
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
}

.like-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.like-actions :deep(.el-button) {
  flex: 1;
  height: 44px;
  font-size: 0.95rem;
  font-weight: 500;
}

.merchant-info {
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
}

.merchant-header {
  margin-bottom: 12px;
}

.label {
  font-weight: 600;
  color: #1e293b;
}

.merchant-details p {
  margin: 8px 0;
  color: #64748b;
  font-size: 0.95rem;
}

.detail-section {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.detail-section h2 {
  font-size: 1.5rem;
  color: #1e293b;
  margin: 0 0 20px;
  font-weight: 700;
}

.content-text {
  color: #475569;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.comment-form {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.comment-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  margin-bottom: 12px;
}

.login-prompt {
  text-align: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 24px;
}

.login-prompt a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 600;
}

.comments-list {
  margin-bottom: 24px;
}

.empty-comments {
  padding: 40px 20px;
}

.comment-item {
  padding: 16px;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  margin-bottom: 12px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #1e293b;
}

.comment-time {
  font-size: 0.85rem;
  color: #94a3b8;
}

.comment-content {
  color: #475569;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-reply {
  padding: 12px;
  background: #f8fafc;
  border-left: 3px solid #3b82f6;
  border-radius: 4px;
  font-size: 0.95rem;
}

.reply-label {
  font-weight: 600;
  color: #3b82f6;
}

.reply-content {
  color: #475569;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .detail-header {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .main-image {
    height: 300px;
  }

  .car-title {
    font-size: 1.5rem;
  }

  .price {
    font-size: 2rem;
  }

  .action-buttons {
    flex-direction: column;
  }

  .stats-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .car-detail-container {
    padding: 20px 12px;
  }

  .detail-header,
  .detail-section {
    padding: 16px;
  }

  .car-title {
    font-size: 1.25rem;
  }

  .price {
    font-size: 1.75rem;
  }
}
</style>
