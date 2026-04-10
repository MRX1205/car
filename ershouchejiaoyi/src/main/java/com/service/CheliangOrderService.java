package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.CheliangOrderEntity;
import com.utils.PageUtils;

import java.util.Map;

/**
 * 汽车订单 服务类
 */
public interface CheliangOrderService extends IService<CheliangOrderEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
