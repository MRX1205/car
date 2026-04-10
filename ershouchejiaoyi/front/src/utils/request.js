import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";

// 创建 axios 实例
const request = axios.create({
  baseURL: "/ershouchejiaoyi", // 配置后端统一的前缀
  timeout: 10000, // 请求超时时间
});

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(
  (config) => {
    // 如果没有明确设置Content-Type，则使用默认值
    if (!config.headers["Content-Type"]) {
      config.headers["Content-Type"] = "application/json;charset=utf-8";
    }

    // 设置请求头Token
    const token = localStorage.getItem("Token");
    if (token) {
      config.headers["Token"] = token;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

const formatPhotoUrl = (url) => {
  if (!url || typeof url !== "string") return url;
  return url
    .split(",")
    .map((item) => {
      let photo = item.trim();
      if (!photo) return photo;
      if (photo.includes("/ershouchejiaoyi/upload/")) {
        const match = photo.match(/\/ershouchejiaoyi\/upload\/[^,]+/);
        if (match) return match[0];
      }
      if (!photo.startsWith("http") && !photo.startsWith("/")) {
        return `/ershouchejiaoyi/upload/${photo}`;
      }
      return photo;
    })
    .join(",");
};

const deepFormat = (obj) => {
  if (!obj || typeof obj !== "object") return;
  // 处理数组
  if (Array.isArray(obj)) {
    obj.forEach((item) => deepFormat(item));
    return;
  }
  // 处理对象
  for (let key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      // 匹配各种图片字段
      if (typeof obj[key] === "string" && key.toLowerCase().includes("photo")) {
        obj[key] = formatPhotoUrl(obj[key]);
      } else if (typeof obj[key] === "object") {
        deepFormat(obj[key]);
      }
    }
  }
};

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  (response) => {
    let res = response.data;
    // 如果是返回的文件
    if (response.config.responseType === "blob") {
      return res;
    }
    // 兼容服务端返回的字符串数据
    if (typeof res === "string") {
      res = res ? JSON.parse(res) : res;
    }

    // 全局处理图片绝对路径问题
    if (res && res.data) {
      deepFormat(res.data);
    }

    // 根据后端的返回码统一进行处理
    if (res.code === 401) {
      ElMessage.error("请先登录系统");
      localStorage.removeItem("Token");
      localStorage.removeItem("userTable");
      localStorage.removeItem("userInfo");
      router.push("/login");
    } else if (res.code !== 0 && res.code !== 200 && res.code !== undefined) {
      // 只在明确的错误code时才显示错误
      console.error("API Error:", res);
    }

    return res;
  },
  (error) => {
    console.error("Network error:", error);
    if (error.response?.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      localStorage.removeItem("Token");
      localStorage.removeItem("userTable");
      localStorage.removeItem("userInfo");
      router.push("/login");
    } else {
      ElMessage.error("网络请求异常，请稍后重试");
    }
    return Promise.reject(error);
  },
);

export default request;
