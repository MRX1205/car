package com.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.CheliangCartEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * 购物车
 * 后端返回视图实体辅助类
 */
@TableName("cheliang_cart")
public class CheliangCartView extends CheliangCartEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // 级联表 cheliang
    private Integer cheliangShangjiaId;
    private String cheliangName;
    private String cheliangPhoto;
    private Integer cheliangTypes;
    private String cheliangValue;
    private Double cheliangNewMoney;
    private Integer shangxiaTypes;
    private String shangxiaValue;

    // 级联表 yonghu
    private String yonghuName;
    private String yonghuPhone;
    private String yonghuIdNumber;
    private String yonghuPhoto;
    private String yonghuEmail;

    // 级联表 shangjia
    private String shangjiaName;
    private String shangjiaPhone;
    private String shangjiaEmail;

    public CheliangCartView() {
    }

    public CheliangCartView(CheliangCartEntity cheliangCartEntity) {
        try {
            BeanUtils.copyProperties(this, cheliangCartEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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
