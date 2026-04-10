package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.CheliangCartEntity;
import com.entity.view.CheliangCartView;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 购物车 Dao 接口
 */
public interface CheliangCartDao extends BaseMapper<CheliangCartEntity> {
    List<CheliangCartView> selectListView(Pagination page, @Param("params") Map<String, Object> params);
}
