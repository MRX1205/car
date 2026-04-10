package com.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CheliangOrderDao;
import com.entity.CheliangOrderEntity;
import com.entity.view.CheliangOrderView;
import com.service.CheliangOrderService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 汽车订单 服务实现类
 */
@Service("cheliangOrderService")
@Transactional
public class CheliangOrderServiceImpl extends ServiceImpl<CheliangOrderDao, CheliangOrderEntity> implements CheliangOrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        if (params != null && (params.get("limit") == null || params.get("page") == null)) {
            params.put("page", "1");
            params.put("limit", "10");
        }
        Page<CheliangOrderView> page = new Query<CheliangOrderView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }
}
