package com.entity.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 汽车订单
 * 手机端接口返回实体辅助类
 * （主要作用去除一些不必要的字段）
 */
@TableName("cheliang_order")
public class CheliangOrderVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableField(value = "id")
    private Integer id;

    /**
     * 订单号
     */
    @TableField(value = "cheliang_order_uuid_number")
    private String cheliangOrderUuidNumber;

    /**
     * 汽车
     */
    @TableField(value = "cheliang_id")
    private Integer cheliangId;

    /**
     * 用户
     */
    @TableField(value = "yonghu_id")
    private Integer yonghuId;

    /**
     * 商家
     */
    @TableField(value = "shangjia_id")
    private Integer shangjiaId;

    /**
     * 订单金额
     */
    @TableField(value = "cheliang_order_price")
    private Double cheliangOrderPrice;

    /**
     * 订单状态
     */
    @TableField(value = "cheliang_order_status_types")
    private Integer cheliangOrderStatusTypes;

    /**
     * 下单时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "insert_time")
    private Date insertTime;

    /**
     * 支付时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "pay_time")
    private Date payTime;

    /**
     * 完成时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "finish_time")
    private Date finishTime;

    /**
     * 取消原因
     */
    @TableField(value = "cancel_reason")
    private String cancelReason;

    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    @TableField(value = "create_time")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCheliangOrderUuidNumber() {
        return cheliangOrderUuidNumber;
    }

    public void setCheliangOrderUuidNumber(String cheliangOrderUuidNumber) {
        this.cheliangOrderUuidNumber = cheliangOrderUuidNumber;
    }

    public Integer getCheliangId() {
        return cheliangId;
    }

    public void setCheliangId(Integer cheliangId) {
        this.cheliangId = cheliangId;
    }

    public Integer getYonghuId() {
        return yonghuId;
    }

    public void setYonghuId(Integer yonghuId) {
        this.yonghuId = yonghuId;
    }

    public Integer getShangjiaId() {
        return shangjiaId;
    }

    public void setShangjiaId(Integer shangjiaId) {
        this.shangjiaId = shangjiaId;
    }

    public Double getCheliangOrderPrice() {
        return cheliangOrderPrice;
    }

    public void setCheliangOrderPrice(Double cheliangOrderPrice) {
        this.cheliangOrderPrice = cheliangOrderPrice;
    }

    public Integer getCheliangOrderStatusTypes() {
        return cheliangOrderStatusTypes;
    }

    public void setCheliangOrderStatusTypes(Integer cheliangOrderStatusTypes) {
        this.cheliangOrderStatusTypes = cheliangOrderStatusTypes;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
