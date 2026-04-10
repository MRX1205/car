package com.entity.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 汽车订单
 * 接收传参的实体类
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了）
 * 取自ModelAndView 的model名称
 */
public class CheliangOrderModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 订单号
     */
    private String cheliangOrderUuidNumber;

    /**
     * 汽车
     */
    private Integer cheliangId;

    /**
     * 用户
     */
    private Integer yonghuId;

    /**
     * 商家
     */
    private Integer shangjiaId;

    /**
     * 订单金额
     */
    private Double cheliangOrderPrice;

    /**
     * 订单状态
     */
    private Integer cheliangOrderStatusTypes;

    /**
     * 下单时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date insertTime;

    /**
     * 支付时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date payTime;

    /**
     * 完成时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date finishTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
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
