import { createRouter, createWebHistory } from "vue-router";
import Layout from "../layout/Layout.vue";
import { useAuthStore } from "../stores/auth";
import { ElMessage } from "element-plus";

const routes = [
  {
    path: "/",
    component: Layout,
    children: [
      {
        path: "",
        name: "Home",
        component: () => import("../views/Home.vue"),
        meta: { title: "首页" },
      },
      {
        path: "cars",
        name: "Cars",
        component: () => import("../views/Cars.vue"),
        meta: { title: "二手车辆" },
      },
      {
        path: "cars/detail/:id",
        name: "CarDetail",
        component: () => import("../views/CarDetail.vue"),
        meta: { title: "车辆详情" },
      },
      {
        path: "forum",
        name: "Forum",
        component: () => import("../views/Forum.vue"),
        meta: { title: "交流论坛" },
      },
      {
        path: "forum/detail/:id",
        name: "ForumDetail",
        component: () => import("../views/ForumDetail.vue"),
        meta: { title: "帖子详情" },
      },
      {
        path: "news",
        name: "News",
        component: () => import("../views/News.vue"),
        meta: { title: "公告信息" },
      },
      {
        path: "news/detail/:id",
        name: "NewsDetail",
        component: () => import("../views/NewsDetail.vue"),
        meta: { title: "公告详情" },
      },
      {
        path: "user",
        component: () => import("../views/center/Index.vue"),
        meta: { requiresAuth: true, role: "yonghu" },
        children: [
          {
            path: "center",
            name: "Center",
            component: () => import("../views/center/Profile.vue"),
            meta: { title: "个人信息" },
          },
          {
            path: "orders",
            name: "Orders",
            component: () => import("../views/center/Orders.vue"),
            meta: { title: "交易订单" },
          },
          {
            path: "cart",
            name: "Cart",
            component: () => import("../views/center/Cart.vue"),
            meta: { title: "购物车" },
          },
          {
            path: "collections",
            name: "Collections",
            component: () => import("../views/center/Collections.vue"),
            meta: { title: "我的收藏" },
          },
        ],
      },
    ],
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/Register.vue"),
    meta: { title: "注册" },
  },
  {
    path: "/pay",
    name: "Pay",
    component: () => import("../views/Pay.vue"),
    meta: { requiresAuth: true, title: "订单支付" },
  },
  {
    path: "/pay-success",
    name: "PaySuccess",
    component: () => import("../views/PaySuccess.vue"),
    meta: { requiresAuth: true, title: "支付成功" },
  },
  {
    path: "/confirm-pay",
    name: "ConfirmPay",
    component: () => import("../views/ConfirmPay.vue"),
    meta: { requiresAuth: true, title: "确认支付" },
  },
  // 商家后台路由
  {
    path: "/merchant",
    component: () => import("../layout/MerchantLayout.vue"),
    meta: { requiresAuth: true, role: "shangjia" },
    children: [
      {
        path: "",
        redirect: "/merchant/profile",
      },
      {
        path: "vehicles",
        name: "MerchantVehicles",
        component: () => import("../views/merchant/Vehicles.vue"),
        meta: { title: "车辆管理" },
      },
      {
        path: "vehicles/add",
        name: "MerchantVehicleAdd",
        component: () => import("../views/merchant/VehicleForm.vue"),
        meta: { title: "新增车辆" },
      },
      {
        path: "vehicles/edit/:id",
        name: "MerchantVehicleEdit",
        component: () => import("../views/merchant/VehicleForm.vue"),
        meta: { title: "编辑车辆" },
      },
      {
        path: "orders",
        name: "MerchantOrders",
        component: () => import("../views/merchant/Orders.vue"),
        meta: { title: "订单管理" },
      },
      {
        path: "profile",
        name: "MerchantProfile",
        component: () => import("../views/merchant/Profile.vue"),
        meta: { title: "商家资料" },
      },
      {
        path: "messages",
        name: "MerchantMessages",
        component: () => import("../views/merchant/Messages.vue"),
        meta: { title: "汽车留言管理" },
      },
      {
        path: "forum",
        name: "MerchantForum",
        component: () => import("../views/merchant/Forum.vue"),
        meta: { title: "交流论坛" },
      },
      {
        path: "forum/detail/:id",
        name: "MerchantForumDetail",
        component: () => import("../views/merchant/ForumDetail.vue"),
        meta: { title: "帖子详情" },
      },
    ],
  },
  // 管理员后台路由
  {
    path: "/admin",
    component: () => import("../layout/AdminLayout.vue"),
    meta: { requiresAuth: true, role: "users" },
    children: [
      {
        path: "",
        redirect: "/admin/merchants",
      },
      {
        path: "merchants",
        name: "AdminMerchants",
        component: () => import("../views/admin/Merchants.vue"),
        meta: { title: "商家管理" },
      },
      {
        path: "news",
        name: "AdminNews",
        component: () => import("../views/admin/News.vue"),
        meta: { title: "公告管理" },
      },
      {
        path: "forum",
        name: "AdminForum",
        component: () => import("../views/admin/Forum.vue"),
        meta: { title: "论坛管理" },
      },
      {
        path: "forum/detail/:id",
        name: "AdminForumDetail",
        component: () => import("../views/admin/ForumDetail.vue"),
        meta: { title: "帖子详情" },
      },
      {
        path: "orders",
        name: "AdminOrders",
        component: () => import("../views/admin/Orders.vue"),
        meta: { title: "订单管理" },
      },
      {
        path: "brands",
        name: "AdminBrands",
        component: () => import("../views/admin/Brands.vue"),
        meta: { title: "品牌管理" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  if (to.meta.title) {
    document.title = to.meta.title + " - 二手车交易系统";
  }

  // 权限检查
  if (to.meta.requiresAuth) {
    if (!authStore.isLoggedIn) {
      ElMessage.warning("请先登录");
      next("/login");
      return;
    }

    if (to.meta.role && authStore.userTable !== to.meta.role) {
      ElMessage.error("无权访问此页面");
      next("/");
      return;
    }
  }

  next();
});

export default router;
