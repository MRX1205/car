import request from "../utils/request";

// 获取购物车列表（需要登录，后端根据 session 自动过滤当前用户）
export const getCartList = (params) => {
  return request.get("/cheliangCart/page", { params });
};

// 添加到购物车
export const addToCart = (cheliangId) => {
  return request.post("/cheliangCart/save", { cheliangId });
};

// 从购物车移除（传数组）
export const removeFromCart = (id) => {
  const ids = Array.isArray(id) ? id : [id];
  return request.post("/cheliangCart/delete", ids);
};

// 购物车结算（传购物车id数组）
export const checkoutCart = (ids) => {
  return request.post("/cheliangCart/checkout", ids);
};

// 清空购物车（逐个删除）
export const clearCart = () => {
  // 由前端逻辑调用 removeFromCart 实现，此处暂留
  return Promise.resolve({ code: 0 });
};

// 获取收藏列表
export const getCollectionList = (params) => {
  return request.get("/cheliangCollection/page", { params });
};

// 添加收藏
export const addCollection = (cheliangId, type = 1) => {
  return request.post("/cheliangCollection/save", {
    cheliangId,
    cheliangCollectionTypes: type,
  });
};

// 移除收藏（传数组）
export const removeCollection = (id) => {
  const ids = Array.isArray(id) ? id : [id];
  return request.post("/cheliangCollection/delete", ids);
};

// 检查是否已收藏
export const checkCollection = (cheliangId) => {
  return request.get("/cheliangCollection/page", {
    params: { cheliangId, page: 1, limit: 1 },
  });
};

// 添加点赞或点踩（type: 2-点赞, 3-点踩）
export const addLikeOrDislike = (cheliangId, type) => {
  return request.post("/cheliangCollection/save", {
    cheliangId,
    cheliangCollectionTypes: type,
  });
};
