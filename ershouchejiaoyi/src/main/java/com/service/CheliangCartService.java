package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.CheliangCartEntity;
import com.utils.PageUtils;

import java.util.Map;

/**
 * 购物车 服务类
 */
public interface CheliangCartService extends IService<CheliangCartEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
