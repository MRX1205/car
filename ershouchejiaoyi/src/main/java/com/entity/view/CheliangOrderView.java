package com.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.CheliangOrderEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * 汽车订单
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("cheliang_order")
public class CheliangOrderView extends CheliangOrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单状态的值
     */
    private String cheliangOrderStatusValue;

    // 级联表 cheliang
    /**
     * 汽车 的 商家
     */
    private Integer cheliangShangjiaId;
    /**
     * 汽车名称
     */
    private String cheliangName;
    /**
     * 汽车照片
     */
    private String cheliangPhoto;
    /**
     * 汽车品牌
     */
    private Integer cheliangTypes;
    /**
     * 汽车品牌的值
     */
    private String cheliangValue;
    /**
     * 价格
     */
    private Double cheliangNewMoney;
    /**
     * 是否上架
     */
    private Integer shangxiaTypes;
    /**
     * 是否上架的值
     */
    private String shangxiaValue;

    // 级联表 yonghu
    /**
     * 用户姓名
     */
    private String yonghuName;
    /**
     * 用户手机号
     */
    private String yonghuPhone;
    /**
     * 用户身份证号
     */
    private String yonghuIdNumber;
    /**
     * 用户头像
     */
    private String yonghuPhoto;
    /**
     * 电子邮箱
     */
    private String yonghuEmail;

    // 级联表 shangjia
    /**
     * 商家名称
     */
    private String shangjiaName;
    /**
     * 商家联系方式
     */
    private String shangjiaPhone;
    /**
     * 商家邮箱
     */
    private String shangjiaEmail;

    public CheliangOrderView() {
    }

    public CheliangOrderView(CheliangOrderEntity cheliangOrderEntity) {
        try {
            BeanUtils.copyProperties(this, cheliangOrderEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public String getCheliangOrderStatusValue() {
        return cheliangOrderStatusValue;
    }

    public void setCheliangOrderStatusValue(String cheliangOrderStatusValue) {
        this.cheliangOrderStatusValue = cheliangOrderStatusValue;
    }

    public Integer getCheliangShangjiaId() {
        return cheliangShangjiaId;
    }

    public void setCheliangShangjiaId(Integer cheliangShangjiaId) {
        this.cheliangShangjiaId = cheliangShangjiaId;
    }

    public String getCheliangName() {
        return cheliangName;
    }

    public void setCheliangName(String cheliangName) {
        this.cheliangName = cheliangName;
    }

    public String getCheliangPhoto() {
        return cheliangPhoto;
    }

    public void setCheliangPhoto(String cheliangPhoto) {
        this.cheliangPhoto = cheliangPhoto;
    }

    public Integer getCheliangTypes() {
        return cheliangTypes;
    }

    public void setCheliangTypes(Integer cheliangTypes) {
        this.cheliangTypes = cheliangTypes;
    }

    public String getCheliangValue() {
        return cheliangValue;
    }

    public void setCheliangValue(String cheliangValue) {
        this.cheliangValue = cheliangValue;
    }

    public Double getCheliangNewMoney() {
        return cheliangNewMoney;
    }

    public void setCheliangNewMoney(Double cheliangNewMoney) {
        this.cheliangNewMoney = cheliangNewMoney;
    }

    public Integer getShangxiaTypes() {
        return shangxiaTypes;
    }

    public void setShangxiaTypes(Integer shangxiaTypes) {
        this.shangxiaTypes = shangxiaTypes;
    }

    public String getShangxiaValue() {
        return shangxiaValue;
    }

    public void setShangxiaValue(String shangxiaValue) {
        this.shangxiaValue = shangxiaValue;
    }

    public String getYonghuName() {
        return yonghuName;
    }

    public void setYonghuName(String yonghuName) {
        this.yonghuName = yonghuName;
    }

    public String getYonghuPhone() {
        return yonghuPhone;
    }

    public void setYonghuPhone(String yonghuPhone) {
        this.yonghuPhone = yonghuPhone;
    }

    public String getYonghuIdNumber() {
        return yonghuIdNumber;
    }

    public void setYonghuIdNumber(String yonghuIdNumber) {
        this.yonghuIdNumber = yonghuIdNumber;
    }

    public String getYonghuPhoto() {
        return yonghuPhoto;
    }

    public void setYonghuPhoto(String yonghuPhoto) {
        this.yonghuPhoto = yonghuPhoto;
    }

    public String getYonghuEmail() {
        return yonghuEmail;
    }

    public void setYonghuEmail(String yonghuEmail) {
        this.yonghuEmail = yonghuEmail;
    }

    public String getShangjiaName() {
        return shangjiaName;
    }

    public void setShangjiaName(String shangjiaName) {
        this.shangjiaName = shangjiaName;
    }

    public String getShangjiaPhone() {
        return shangjiaPhone;
    }

    public void setShangjiaPhone(String shangjiaPhone) {
        this.shangjiaPhone = shangjiaPhone;
    }

    public String getShangjiaEmail() {
        return shangjiaEmail;
    }

    public void setShangjiaEmail(String shangjiaEmail) {
        this.shangjiaEmail = shangjiaEmail;
    }
}
