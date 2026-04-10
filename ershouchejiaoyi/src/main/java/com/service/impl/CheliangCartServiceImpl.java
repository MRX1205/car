package com.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.CheliangCartDao;
import com.entity.CheliangCartEntity;
import com.entity.view.CheliangCartView;
import com.service.CheliangCartService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 购物车 服务实现类
 */
@Service("cheliangCartService")
@Transactional
public class CheliangCartServiceImpl extends ServiceImpl<CheliangCartDao, CheliangCartEntity> implements CheliangCartService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        if (params != null && (params.get("limit") == null || params.get("page") == null)) {
            params.put("page", "1");
            params.put("limit", "10");
        }
        Page<CheliangCartView> page = new Query<CheliangCartView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }
}
