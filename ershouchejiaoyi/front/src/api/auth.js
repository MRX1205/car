import request from "../utils/request";

// 用户登录 - 后端返回: { token, role, username, tableName, userId }
export const loginUser = (username, password) => {
  return request.get("/yonghu/login", {
    params: { username, password },
  });
};

// 商家登录 - 后端返回: { token, role, username, tableName, userId }
export const loginMerchant = (username, password) => {
  return request.get("/shangjia/login", {
    params: { username, password },
  });
};

// 管理员登录 - 后端返回: { token, role, username, tableName, userId }
export const loginAdmin = (username, password) => {
  const formData = new URLSearchParams();
  formData.append("username", username);
  formData.append("password", password);
  return request.post("/users/login", formData, {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });
};

// 用户注册
export const registerUser = (data) => {
  return request.post("/yonghu/register", data);
};

// 商家注册
export const registerMerchant = (data) => {
  return request.post("/shangjia/register", data);
};

// 管理员注册
export const registerAdmin = (data) => {
  return request.post("/users/register", data);
};

// 获取当前用户 session 信息
export const getUserSession = () => {
  return request.get("/yonghu/session");
};

// 获取当前商家 session 信息
export const getMerchantSession = () => {
  return request.get("/shangjia/session");
};

// 获取当前管理员 session 信息
export const getAdminSession = () => {
  return request.get("/users/session");
};

// 退出登录
export const logout = (role) => {
  let path = "/yonghu/logout";
  if (role === "shangjia") {
    path = "/shangjia/logout";
  } else if (role === "users") {
    path = "/users/logout";
  }
  return request.get(path);
};
