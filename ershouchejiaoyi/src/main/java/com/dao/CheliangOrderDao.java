package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.CheliangOrderEntity;
import com.entity.view.CheliangOrderView;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 汽车订单
 */
public interface CheliangOrderDao extends BaseMapper<CheliangOrderEntity> {
    List<CheliangOrderView> selectListView(Pagination page, @Param("params") Map<String, Object> params);

    List<CheliangOrderView> selectListView(@Param("params") Map<String, Object> params);
}
