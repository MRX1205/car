import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])

  const itemCount = computed(() => items.value.length)

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + (item.cheliangNewMoney || 0), 0)
  })

  const addItem = (item) => {
    const existingItem = items.value.find(i => i.id === item.id)
    if (!existingItem) {
      items.value.push(item)
    }
  }

  const removeItem = (id) => {
    items.value = items.value.filter(item => item.id !== id)
  }

  const clearItems = () => {
    items.value = []
  }

  const setItems = (newItems) => {
    items.value = newItems
  }

  return {
    items,
    itemCount,
    totalPrice,
    addItem,
    removeItem,
    clearItems,
    setItems
  }
})
