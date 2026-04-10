<template>
  <div class="cart-container">
    <div class="cart-header">
      <h1>购物车</h1>
    </div>

    <div class="cart-content" v-loading="loading">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车为空">
          <template #default>
            <el-button type="primary" @click="$router.push('/cars')">继续购物</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else class="cart-layout">
        <!-- 购物车列表 -->
        <div class="cart-items">
          <div class="cart-item" v-for="item in cartItems" :key="item.id">
            <div class="item-image">
              <img :src="item.cheliangPhoto" :alt="item.cheliangName" />
            </div>

            <div class="item-info">
              <h3 class="item-name">{{ item.cheliangName }}</h3>
              <p class="item-desc">{{ item.cheliangContent }}</p>
              <div class="item-price">¥{{ item.cheliangNewMoney }} 万</div>
            </div>

            <div class="item-actions">
              <el-button type="primary" size="small" @click="goToDetail(item.id)">
                查看详情
              </el-button>
              <el-button type="danger" size="small" @click="removeItem(item.id)">
                移除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 购物车总结 -->
        <div class="cart-summary">
          <div class="summary-card">
            <h3>订单总结</h3>

            <div class="summary-item">
              <span>商品数量</span>
              <span class="value">{{ cartItems.length }}</span>
            </div>

            <div class="summary-item">
              <span>商品总价</span>
              <span class="value">¥{{ totalPrice.toFixed(2) }} 万</span>
            </div>

            <div class="summary-divider"></div>

            <div class="summary-total">
              <span>应付金额</span>
              <span class="total-price">¥{{ totalPrice.toFixed(2) }} 万</span>
            </div>

            <el-button
              type="primary"
              size="large"
              class="checkout-btn"
              @click="checkout"
              :loading="checkoutLoading"
            >
              结算
            </el-button>

            <el-button
              text
              class="continue-shopping"
              @click="$router.push('/cars')"
            >
              继续购物
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as cartApi from '../../api/cart'
import { useAuthStore } from '../../stores/auth'
import { useCartStore } from '../../stores/cart'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const loading = ref(false)
const checkoutLoading = ref(false)
const cartItems = ref([])

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.cheliangNewMoney || 0), 0)
})

const loadCart = async () => {
  loading.value = true
  try {
    const res = await cartApi.getCartList({ page: 1, limit: 1000 })
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      cartItems.value = pageData.list || []
      cartStore.setItems(cartItems.value)
    } else {
      ElMessage.error(res.msg || '加载购物车失败')
    }
  } catch (error) {
    console.error('加载购物车失败:', error)
    ElMessage.error('加载购物车失败')
  } finally {
    loading.value = false
  }
}

const removeItem = async (cartId) => {
  ElMessageBox.confirm(
    '确定要移除此商品吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await cartApi.removeFromCart(cartId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('已移除')
        loadCart()
      } else {
        ElMessage.error(res.msg || '移除失败')
      }
    } catch (error) {
      console.error('移除失败:', error)
      ElMessage.error('移除失败')
    }
  }).catch(() => {})
}

const goToDetail = (carId) => {
  router.push(`/cars/detail/${carId}`)
}

const checkout = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  checkoutLoading.value = true
  try {
    // 使用后端 checkout 接口批量结算，传购物车 id 数组
    const cartIds = cartItems.value.map(item => item.id)
    const res = await cartApi.checkoutCart(cartIds)

    if (res.code === 0 || res.code === 200) {
      ElMessage.success('结算成功，请支付订单')
      cartStore.clearItems()
      cartItems.value = []
      setTimeout(() => {
        if (res.orderIds && res.orderIds.length > 0) {
          router.push(`/pay?orderId=${res.orderIds[0]}`)
        } else {
          router.push('/user/orders')
        }
      }, 800)
    } else {
      ElMessage.error(res.msg || '结算失败，请重试')
    }
  } catch (error) {
    console.error('结算失败:', error)
    ElMessage.error('结算失败，请重试')
  } finally {
    checkoutLoading.value = false
  }
}
onMounted(() => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadCart()
})
</script>

<style scoped>
.cart-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 40px 20px;
}

.cart-header {
  margin-bottom: 30px;
}

.cart-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.cart-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-cart {
  padding: 60px 20px;
  text-align: center;
}

.cart-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 30px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-item {
  display: grid;
  grid-template-columns: 120px 1fr auto;
  gap: 20px;
  padding: 16px;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  align-items: center;
  transition: all 0.3s ease;
}

.cart-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.item-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  background: #f1f5f9;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.item-desc {
  font-size: 0.9rem;
  color: #64748b;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-price {
  font-size: 1.25rem;
  color: #ef4444;
  font-weight: 700;
}

.item-actions {
  display: flex;
  gap: 8px;
  flex-direction: column;
}

.cart-summary {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.summary-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
}

.summary-card h3 {
  font-size: 1.1rem;
  color: #1e293b;
  margin: 0 0 16px;
  font-weight: 600;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  color: #64748b;
  font-size: 0.95rem;
}

.summary-item .value {
  font-weight: 600;
  color: #1e293b;
}

.summary-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 12px 0;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.total-price {
  color: #ef4444;
  font-size: 1.5rem;
}

.checkout-btn {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 12px;
}

.continue-shopping {
  width: 100%;
  color: #3b82f6;
}

@media (max-width: 768px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    position: static;
  }

  .cart-item {
    grid-template-columns: 100px 1fr;
    gap: 12px;
  }

  .item-image {
    width: 100px;
    height: 100px;
  }

  .item-actions {
    grid-column: 1 / -1;
    flex-direction: row;
  }

  .item-actions :deep(.el-button) {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .cart-container {
    padding: 20px 12px;
  }

  .cart-content {
    padding: 16px;
  }

  .cart-item {
    grid-template-columns: 80px 1fr;
  }

  .item-image {
    width: 80px;
    height: 80px;
  }

  .item-name {
    font-size: 1rem;
  }

  .item-price {
    font-size: 1.1rem;
  }
}
</style>
