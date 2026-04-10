package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.CheliangEntity;
import com.entity.CheliangOrderEntity;
import com.entity.ShangjiaEntity;
import com.entity.YonghuEntity;
import com.entity.view.CheliangOrderView;
import com.service.CheliangOrderService;
import com.service.CheliangService;
import com.service.DictionaryService;
import com.service.ShangjiaService;
import com.service.TokenService;
import com.service.YonghuService;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 汽车订单
 * 后端接口
 */
@RestController
@Controller
@RequestMapping("/cheliangOrder")
public class CheliangOrderController {
    private static final Logger logger = LoggerFactory.getLogger(CheliangOrderController.class);

    @Autowired
    private CheliangOrderService cheliangOrderService;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private CheliangService cheliangService;
    @Autowired
    private YonghuService yonghuService;
    @Autowired
    private ShangjiaService shangjiaService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("page方法:,,Controller:{},,params:{}", this.getClass().getName(), JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if (false)
            return R.error(511, "永不会进入");
        else if ("用户".equals(role))
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        else if ("商家".equals(role)) {
            params.put("shangjiaId", request.getSession().getAttribute("userId"));
            params.put("cheliangOrderStatusTypesStart", 2);
        }
        if (params.get("orderBy") == null || params.get("orderBy") == "") {
            params.put("orderBy", "id");
        }
        PageUtils page = cheliangOrderService.queryPage(params);

        List<CheliangOrderView> list = (List<CheliangOrderView>) page.getList();
        for (CheliangOrderView c : list) {
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.debug("info方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
        CheliangOrderEntity cheliangOrder = cheliangOrderService.selectById(id);
        if (cheliangOrder != null) {
            CheliangOrderView view = new CheliangOrderView();
            BeanUtils.copyProperties(cheliangOrder, view);

            CheliangEntity cheliang = cheliangService.selectById(cheliangOrder.getCheliangId());
            if (cheliang != null) {
                BeanUtils.copyProperties(cheliang, view, new String[]{"id", "createTime", "insertTime", "updateTime"});
                view.setCheliangId(cheliang.getId());
            }
            YonghuEntity yonghu = yonghuService.selectById(cheliangOrder.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createTime", "insertTime", "updateTime"});
                view.setYonghuId(yonghu.getId());
            }
            ShangjiaEntity shangjia = shangjiaService.selectById(cheliangOrder.getShangjiaId());
            if (shangjia != null) {
                BeanUtils.copyProperties(shangjia, view, new String[]{"id", "createTime", "insertTime", "updateTime"});
                view.setShangjiaId(shangjia.getId());
            }

            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CheliangOrderEntity cheliangOrder, HttpServletRequest request) {
        logger.debug("save方法:,,Controller:{},,cheliangOrder:{}", this.getClass().getName(), cheliangOrder.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if (false)
            return R.error(511, "永远不会进入");
        else if ("用户".equals(role))
            cheliangOrder.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        else if ("商家".equals(role))
            cheliangOrder.setShangjiaId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<CheliangOrderEntity> queryWrapper = new EntityWrapper<CheliangOrderEntity>()
                .eq("cheliang_order_uuid_number", cheliangOrder.getCheliangOrderUuidNumber());
        CheliangOrderEntity cheliangOrderEntity = cheliangOrderService.selectOne(queryWrapper);
        if (cheliangOrderEntity == null) {
            if (StringUtil.isEmpty(cheliangOrder.getCheliangOrderUuidNumber())) {
                cheliangOrder.setCheliangOrderUuidNumber(generateOrderNumber());
            }
            cheliangOrder.setInsertTime(new Date());
            cheliangOrder.setCreateTime(new Date());
            cheliangOrderService.insert(cheliangOrder);
            return R.ok();
        } else {
            return R.error(511, "表中有相同数据");
        }
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CheliangOrderEntity cheliangOrder, HttpServletRequest request) {
        logger.debug("update方法:,,Controller:{},,cheliangOrder:{}", this.getClass().getName(), cheliangOrder.toString());

        Wrapper<CheliangOrderEntity> queryWrapper = new EntityWrapper<CheliangOrderEntity>()
                .notIn("id", cheliangOrder.getId())
                .andNew()
                .eq("cheliang_order_uuid_number", cheliangOrder.getCheliangOrderUuidNumber());

        CheliangOrderEntity cheliangOrderEntity = cheliangOrderService.selectOne(queryWrapper);
        if (cheliangOrderEntity == null) {
            cheliangOrderService.updateById(cheliangOrder);
            return R.ok();
        } else {
            return R.error(511, "表中有相同数据");
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        logger.debug("delete:,,Controller:{},,ids:{}", this.getClass().getName(), Arrays.toString(ids));
        cheliangOrderService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("list方法:,,Controller:{},,params:{}", this.getClass().getName(), JSONObject.toJSONString(params));

        if (StringUtil.isEmpty(String.valueOf(params.get("orderBy")))) {
            params.put("orderBy", "id");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("商家".equals(role)) {
            params.put("cheliangOrderStatusTypesStart", 2);
            params.put("shangjiaId", request.getSession().getAttribute("userId"));
        }
        PageUtils page = cheliangOrderService.queryPage(params);

        List<CheliangOrderView> list = (List<CheliangOrderView>) page.getList();
        for (CheliangOrderView c : list)
            dictionaryService.dictionaryConvert(c, request);
        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.debug("detail方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
        CheliangOrderEntity cheliangOrder = cheliangOrderService.selectById(id);
        if (cheliangOrder != null) {
            CheliangOrderView view = new CheliangOrderView();
            BeanUtils.copyProperties(cheliangOrder, view);

            CheliangEntity cheliang = cheliangService.selectById(cheliangOrder.getCheliangId());
            if (cheliang != null) {
                BeanUtils.copyProperties(cheliang, view, new String[]{"id", "createDate"});
                view.setCheliangId(cheliang.getId());
            }
            YonghuEntity yonghu = yonghuService.selectById(cheliangOrder.getYonghuId());
            if (yonghu != null) {
                BeanUtils.copyProperties(yonghu, view, new String[]{"id", "createDate"});
                view.setYonghuId(yonghu.getId());
            }
            ShangjiaEntity shangjia = shangjiaService.selectById(cheliangOrder.getShangjiaId());
            if (shangjia != null) {
                BeanUtils.copyProperties(shangjia, view, new String[]{"id", "createDate"});
                view.setShangjiaId(shangjia.getId());
            }

            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 前端保存（用户下单）
     */
    @RequestMapping("/add")
    public R add(@RequestBody CheliangOrderEntity cheliangOrder, HttpServletRequest request) {
        logger.debug("add方法:,,Controller:{},,cheliangOrder:{}", this.getClass().getName(), cheliangOrder.toString());

        if (cheliangOrder.getCheliangId() == null) {
            return R.error(511, "请选择要交易的车辆");
        }
        CheliangEntity cheliang = cheliangService.selectById(cheliangOrder.getCheliangId());
        if (cheliang == null || cheliang.getCheliangDelete() == null || cheliang.getCheliangDelete() != 1) {
            return R.error(511, "车辆不存在或已删除");
        }
        if (cheliang.getShangxiaTypes() == null || cheliang.getShangxiaTypes() != 1) {
            return R.error(511, "车辆当前不可交易");
        }

        Wrapper<CheliangOrderEntity> queryWrapper = new EntityWrapper<CheliangOrderEntity>()
                .eq("cheliang_id", cheliangOrder.getCheliangId())
                .in("cheliang_order_status_types", Arrays.asList(1, 2, 3));
        CheliangOrderEntity existOrder = cheliangOrderService.selectOne(queryWrapper);
        if (existOrder != null) {
            return R.error(511, "该车辆正在交易中");
        }

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            cheliangOrder.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        }
        if (cheliangOrder.getYonghuId() == null) {
            return R.error(511, "请使用用户账号下单");
        }
        cheliangOrder.setShangjiaId(cheliang.getShangjiaId());
        cheliangOrder.setCheliangOrderPrice(cheliang.getCheliangNewMoney());
        cheliangOrder.setCheliangOrderUuidNumber(generateOrderNumber());
        cheliangOrder.setCheliangOrderStatusTypes(1);
        cheliangOrder.setInsertTime(new Date());
        cheliangOrder.setCreateTime(new Date());
        cheliangOrderService.insert(cheliangOrder);

        cheliang.setShangxiaTypes(2);
        cheliangService.updateById(cheliang);

        return R.ok()
                .put("orderId", cheliangOrder.getId())
                .put("orderPrice", cheliangOrder.getCheliangOrderPrice());
    }

    /**
     * 商家确认订单
     */
    @RequestMapping("/confirm")
    public R confirm(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        if (id == null) {
            return R.error(511, "缺少订单ID");
        }
        CheliangOrderEntity order = cheliangOrderService.selectById(id);
        if (order == null) {
            return R.error(511, "订单不存在");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("商家".equals(role)) {
            Integer shangjiaId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!shangjiaId.equals(order.getShangjiaId())) {
                return R.error(511, "无权限操作该订单");
            }
        }
        if (order.getCheliangOrderStatusTypes() == null || order.getCheliangOrderStatusTypes() != 2) {
            return R.error(511, "当前状态不可发货");
        }
        order.setCheliangOrderStatusTypes(3);
        cheliangOrderService.updateById(order);
        return R.ok();
    }

    /**
     * 商家拒绝订单
     */
    @RequestMapping("/reject")
    public R reject(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        String cancelReason = params.get("cancelReason") == null ? "商家拒绝" : String.valueOf(params.get("cancelReason"));
        if (id == null) {
            return R.error(511, "缺少订单ID");
        }
        CheliangOrderEntity order = cheliangOrderService.selectById(id);
        if (order == null) {
            return R.error(511, "订单不存在");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("商家".equals(role)) {
            Integer shangjiaId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!shangjiaId.equals(order.getShangjiaId())) {
                return R.error(511, "无权限操作该订单");
            }
        }
        if (order.getCheliangOrderStatusTypes() == null || order.getCheliangOrderStatusTypes() != 2) {
            return R.error(511, "当前状态不可拒绝");
        }
        order.setCheliangOrderStatusTypes(6);
        order.setCancelReason(cancelReason);
        cheliangOrderService.updateById(order);

        CheliangEntity cheliang = cheliangService.selectById(order.getCheliangId());
        if (cheliang != null) {
            cheliang.setShangxiaTypes(1);
            cheliangService.updateById(cheliang);
        }
        return R.ok();
    }

    /**
     * 用户支付订单
     */
    @RequestMapping("/pay")
    public R pay(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        if (id == null) {
            return R.error(511, "缺少订单ID");
        }
        CheliangOrderEntity order = cheliangOrderService.selectById(id);
        if (order == null) {
            return R.error(511, "订单不存在");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!yonghuId.equals(order.getYonghuId())) {
                return R.error(511, "无权限操作该订单");
            }
        }
        if (order.getCheliangOrderStatusTypes() == null || order.getCheliangOrderStatusTypes() != 1) {
            return R.error(511, "当前状态不可支付");
        }
        order.setCheliangOrderStatusTypes(2);
        order.setPayTime(new Date());
        cheliangOrderService.updateById(order);
        return R.ok();
    }

    /**
     * 商家确认成交
     */
    @RequestMapping("/finish")
    public R finish(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        if (id == null) {
            return R.error(511, "缺少订单ID");
        }
        CheliangOrderEntity order = cheliangOrderService.selectById(id);
        if (order == null) {
            return R.error(511, "订单不存在");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("商家".equals(role)) {
            Integer shangjiaId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!shangjiaId.equals(order.getShangjiaId())) {
                return R.error(511, "无权限操作该订单");
            }
        } else if ("用户".equals(role)) {
            Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!yonghuId.equals(order.getYonghuId())) {
                return R.error(511, "无权限操作该订单");
            }
        }
        if (order.getCheliangOrderStatusTypes() == null || order.getCheliangOrderStatusTypes() != 3) {
            return R.error(511, "当前状态不可完成");
        }
        order.setCheliangOrderStatusTypes(4);
        order.setFinishTime(new Date());
        cheliangOrderService.updateById(order);
        return R.ok();
    }

    /**
     * 用户取消订单
     */
    @RequestMapping("/cancel")
    public R cancel(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        String cancelReason = params.get("cancelReason") == null ? "用户取消" : String.valueOf(params.get("cancelReason"));
        if (id == null) {
            return R.error(511, "缺少订单ID");
        }
        CheliangOrderEntity order = cheliangOrderService.selectById(id);
        if (order == null) {
            return R.error(511, "订单不存在");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if ("用户".equals(role)) {
            Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
            if (!yonghuId.equals(order.getYonghuId())) {
                return R.error(511, "无权限操作该订单");
            }
        }
        if (order.getCheliangOrderStatusTypes() == null || order.getCheliangOrderStatusTypes() != 1) {
            return R.error(511, "当前状态不可取消");
        }
        order.setCheliangOrderStatusTypes(5);
        order.setCancelReason(cancelReason);
        cheliangOrderService.updateById(order);

        CheliangEntity cheliang = cheliangService.selectById(order.getCheliangId());
        if (cheliang != null) {
            cheliang.setShangxiaTypes(1);
            cheliangService.updateById(cheliang);
        }
        return R.ok();
    }

    private String generateOrderNumber() {
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        int rand = new Random().nextInt(900) + 100;
        return time + rand;
    }
}
