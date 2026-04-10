import request from "../utils/request";

// 获取车辆列表（前端）
export const getCheliangList = (params) => {
  return request.get("/cheliang/page", { params });
};

// 获取车辆详情（前端）
export const getCheliangDetail = (id) => {
  return request.get(`/cheliang/info/${id}`);
};

// 后端列表（商家/管理员）
export const getCheliangPage = (params) => {
  return request.get("/cheliang/page", { params });
};

// 新增车辆（商家前端用）
export const addCheliang = (data) => {
  return request.post("/cheliang/add", data);
};

// 更新车辆
export const updateCheliang = (data) => {
  return request.post("/cheliang/update", data);
};

// 上架车辆
export const publishCheliang = (id) => {
  return request.post("/cheliang/update", { id, shangxiaTypes: 1 });
};

// 下架车辆
export const unpublishCheliang = (id) => {
  return request.post("/cheliang/update", { id, shangxiaTypes: 2 });
};

// 删除车辆（传数组）
export const deleteCheliang = (ids) => {
  const idArr = Array.isArray(ids) ? ids : [ids];
  return request.post("/cheliang/delete", idArr);
};

// 点赞车辆
export const likeCheliang = (id) => {
  return request.post(`/cheliang/zan/${id}`);
};

// 踩车辆
export const dislikeCheliang = (id) => {
  return request.post(`/cheliang/cai/${id}`);
};

// 获取车辆品牌字典
export const getCheliangTypes = () => {
  return request.get("/dictionary/page", {
    params: {
      dicCode: "cheliang_types",
      page: 1,
      limit: 100,
    },
  });
};

// 兼容别名
export const createCheliang = addCheliang;
