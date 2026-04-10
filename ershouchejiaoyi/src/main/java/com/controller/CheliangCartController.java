package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.CheliangCartEntity;
import com.entity.CheliangEntity;
import com.entity.CheliangOrderEntity;
import com.entity.view.CheliangCartView;
import com.service.CheliangCartService;
import com.service.CheliangOrderService;
import com.service.CheliangService;
import com.service.DictionaryService;
import com.service.TokenService;
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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 购物车
 * 后端接口
 */
@RestController
@Controller
@RequestMapping("/cheliangCart")
public class CheliangCartController {
    private static final Logger logger = LoggerFactory.getLogger(CheliangCartController.class);

    @Autowired
    private CheliangCartService cheliangCartService;
    @Autowired
    private CheliangOrderService cheliangOrderService;
    @Autowired
    private CheliangService cheliangService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = cheliangCartService.queryPage(params);

        List<CheliangCartView> list =(List<CheliangCartView>)page.getList();
        for(CheliangCartView c:list){
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        CheliangCartEntity cheliangCart = cheliangCartService.selectById(id);
        if(cheliangCart !=null){
            CheliangCartView view = new CheliangCartView();
            BeanUtils.copyProperties( cheliangCart , view );
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CheliangCartEntity cheliangCart, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,cheliangCart:{}",this.getClass().getName(),cheliangCart.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");
        else if("用户".equals(role))
            cheliangCart.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        Wrapper<CheliangCartEntity> queryWrapper = new EntityWrapper<CheliangCartEntity>()
            .eq("cheliang_id", cheliangCart.getCheliangId())
            .eq("yonghu_id", cheliangCart.getYonghuId())
            ;

        CheliangCartEntity cheliangCartEntity = cheliangCartService.selectOne(queryWrapper);
        if(cheliangCartEntity==null){
            cheliangCart.setInsertTime(new Date());
            cheliangCart.setCreateTime(new Date());
            cheliangCartService.insert(cheliangCart);
            return R.ok();
        }else {
            return R.error(511,"已经加入购物车");
        }
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CheliangCartEntity cheliangCart, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,cheliangCart:{}",this.getClass().getName(),cheliangCart.toString());

        cheliangCartService.updateById(cheliangCart);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),Arrays.toString(ids));
        cheliangCartService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = cheliangCartService.queryPage(params);

        List<CheliangCartView> list =(List<CheliangCartView>)page.getList();
        for(CheliangCartView c:list)
            dictionaryService.dictionaryConvert(c, request);
        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        CheliangCartEntity cheliangCart = cheliangCartService.selectById(id);
        if(cheliangCart !=null){
            CheliangCartView view = new CheliangCartView();
            BeanUtils.copyProperties( cheliangCart , view );
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody CheliangCartEntity cheliangCart, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,cheliangCart:{}",this.getClass().getName(),cheliangCart.toString());
        Wrapper<CheliangCartEntity> queryWrapper = new EntityWrapper<CheliangCartEntity>()
            .eq("cheliang_id", cheliangCart.getCheliangId())
            .eq("yonghu_id", cheliangCart.getYonghuId())
            ;
        CheliangCartEntity cheliangCartEntity = cheliangCartService.selectOne(queryWrapper);
        if(cheliangCartEntity==null){
            cheliangCart.setInsertTime(new Date());
            cheliangCart.setCreateTime(new Date());
            cheliangCartService.insert(cheliangCart);
            return R.ok();
        }else {
            return R.error(511,"已经加入购物车");
        }
    }

    /**
     * 购物车结算（批量下单）
     */
    @RequestMapping("/checkout")
    public R checkout(@RequestBody List<Integer> ids, HttpServletRequest request){
        if(ids == null || ids.isEmpty()){
            return R.error(511, "请选择要结算的车辆");
        }
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(!"用户".equals(role)){
            return R.error(511, "请使用用户账号结算");
        }
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));

        List<CheliangCartEntity> cartList = cheliangCartService.selectBatchIds(ids);
        if(cartList == null || cartList.isEmpty()){
            return R.error(511, "购物车数据不存在");
        }

        for(CheliangCartEntity cart : cartList){
            if(!yonghuId.equals(cart.getYonghuId())){
                return R.error(511, "包含非本人购物车数据");
            }
        }

        List<Integer> orderIds = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(CheliangCartEntity cart : cartList){
            R result = createOrderFromCart(cart, yonghuId);
            if(result.get("code").toString().equals("511")){
                return result;
            }
            if(result.get("orderId") != null){
                orderIds.add(Integer.valueOf(String.valueOf(result.get("orderId"))));
            }
            if(result.get("orderPrice") != null){
                totalPrice = totalPrice.add(new BigDecimal(String.valueOf(result.get("orderPrice"))));
            }
        }

        cheliangCartService.deleteBatchIds(ids);
        return R.ok().put("orderIds", orderIds).put("totalPrice", totalPrice);
    }

    private R createOrderFromCart(CheliangCartEntity cart, Integer yonghuId){
        CheliangEntity cheliang = cheliangService.selectById(cart.getCheliangId());
        if(cheliang == null || cheliang.getCheliangDelete() == null || cheliang.getCheliangDelete() != 1){
            return R.error(511, "车辆不存在或已删除");
        }
        if(cheliang.getShangxiaTypes() == null || cheliang.getShangxiaTypes() != 1){
            return R.error(511, "车辆当前不可交易");
        }
        Wrapper<CheliangOrderEntity> queryWrapper = new EntityWrapper<CheliangOrderEntity>()
                .eq("cheliang_id", cart.getCheliangId())
                .in("cheliang_order_status_types", Arrays.asList(1,2,3));
        CheliangOrderEntity existOrder = cheliangOrderService.selectOne(queryWrapper);
        if(existOrder != null){
            return R.error(511, "该车辆正在交易中");
        }

        CheliangOrderEntity order = new CheliangOrderEntity();
        order.setCheliangId(cart.getCheliangId());
        order.setYonghuId(yonghuId);
        order.setShangjiaId(cheliang.getShangjiaId());
        order.setCheliangOrderPrice(cheliang.getCheliangNewMoney());
        order.setCheliangOrderUuidNumber(generateOrderNumber());
        order.setCheliangOrderStatusTypes(1);
        order.setInsertTime(new Date());
        order.setCreateTime(new Date());
        cheliangOrderService.insert(order);

        cheliang.setShangxiaTypes(2);
        cheliangService.updateById(cheliang);
        return R.ok()
                .put("orderId", order.getId())
                .put("orderPrice", order.getCheliangOrderPrice());
    }

    private String generateOrderNumber() {
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        int rand = new Random().nextInt(900) + 100;
        return time + rand;
    }
}
