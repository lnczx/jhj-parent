package com.jhj.po.model.survey;

import java.math.BigDecimal;

public class SurveyContent {
    private Long contentId;

    private Long serviceId;

    private String name;

    private BigDecimal price;

    private BigDecimal unitPrice;

    private String priceDescription;

    private String itemUnit;

    private Short measurement;

    private Short enable;

    private Long addTime;

    private Long updateTime;

    private Short contentChildType;

    private Long defaultTime;

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription == null ? null : priceDescription.trim();
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit == null ? null : itemUnit.trim();
    }

    public Short getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Short measurement) {
        this.measurement = measurement;
    }

    public Short getEnable() {
        return enable;
    }

    public void setEnable(Short enable) {
        this.enable = enable;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Short getContentChildType() {
        return contentChildType;
    }

    public void setContentChildType(Short contentChildType) {
        this.contentChildType = contentChildType;
    }

    public Long getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(Long defaultTime) {
        this.defaultTime = defaultTime;
    }
}