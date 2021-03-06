package com.jhj.po.model.university;

import java.math.BigDecimal;

public class PartnerServiceType {
    private Long serviceTypeId;

    private String name;

    private Long parentId;

    private String unit;

    private Short defaultNum;

    private BigDecimal price;
    
    private BigDecimal mprice;
    
    private BigDecimal pprice;
    
    private BigDecimal mpprice;
    
    private BigDecimal aprice;
    
    private BigDecimal apprice;
        
    private BigDecimal staffPrice;
    
    private BigDecimal staffMprice;
    
    private BigDecimal staffPprice;
    
    private BigDecimal staffMpprice;
    
    private BigDecimal StaffAprice;
    
    private BigDecimal StaffApprice;

    private String remarks;

    private Short viewType;

    private Short no;

    private String serviceImgUrl;

    private Short enable;

    private Short serviceProperty;

    private Double serviceTimes;

    private String serviceContent;
    
    private Short isAuto;
    
    private Short isMulti;
    
    private double serviceHour;

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public Short getDefaultNum() {
        return defaultNum;
    }

    public void setDefaultNum(Short defaultNum) {
        this.defaultNum = defaultNum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Short getViewType() {
        return viewType;
    }

    public void setViewType(Short viewType) {
        this.viewType = viewType;
    }

    public Short getNo() {
        return no;
    }

    public void setNo(Short no) {
        this.no = no;
    }

    public String getServiceImgUrl() {
        return serviceImgUrl;
    }

    public void setServiceImgUrl(String serviceImgUrl) {
        this.serviceImgUrl = serviceImgUrl == null ? null : serviceImgUrl.trim();
    }

    public Short getEnable() {
        return enable;
    }

    public void setEnable(Short enable) {
        this.enable = enable;
    }

    public Short getServiceProperty() {
        return serviceProperty;
    }

    public void setServiceProperty(Short serviceProperty) {
        this.serviceProperty = serviceProperty;
    }

    public Double getServiceTimes() {
        return serviceTimes;
    }

    public void setServiceTimes(Double serviceTimes) {
        this.serviceTimes = serviceTimes;
    }

    public String getServiceContent() {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent == null ? null : serviceContent.trim();
    }

	public Short getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(Short isAuto) {
		this.isAuto = isAuto;
	}

	public Short getIsMulti() {
		return isMulti;
	}

	public void setIsMulti(Short isMulti) {
		this.isMulti = isMulti;
	}

	public double getServiceHour() {
		return serviceHour;
	}

	public void setServiceHour(double serviceHour) {
		this.serviceHour = serviceHour;
	}

	public BigDecimal getMprice() {
		return mprice;
	}

	public void setMprice(BigDecimal mprice) {
		this.mprice = mprice;
	}

	public BigDecimal getPprice() {
		return pprice;
	}

	public void setPprice(BigDecimal pprice) {
		this.pprice = pprice;
	}

	public BigDecimal getMpprice() {
		return mpprice;
	}

	public void setMpprice(BigDecimal mpprice) {
		this.mpprice = mpprice;
	}

	public BigDecimal getStaffPrice() {
		return staffPrice;
	}

	public void setStaffPrice(BigDecimal staffPrice) {
		this.staffPrice = staffPrice;
	}

	public BigDecimal getStaffMprice() {
		return staffMprice;
	}

	public void setStaffMprice(BigDecimal staffMprice) {
		this.staffMprice = staffMprice;
	}

	public BigDecimal getStaffPprice() {
		return staffPprice;
	}

	public void setStaffPprice(BigDecimal staffPprice) {
		this.staffPprice = staffPprice;
	}

	public BigDecimal getStaffMpprice() {
		return staffMpprice;
	}

	public void setStaffMpprice(BigDecimal staffMpprice) {
		this.staffMpprice = staffMpprice;
	}

	public BigDecimal getAprice() {
		return aprice;
	}

	public void setAprice(BigDecimal aprice) {
		this.aprice = aprice;
	}

	public BigDecimal getApprice() {
		return apprice;
	}

	public void setApprice(BigDecimal apprice) {
		this.apprice = apprice;
	}

	public BigDecimal getStaffAprice() {
		return StaffAprice;
	}

	public void setStaffAprice(BigDecimal staffAprice) {
		StaffAprice = staffAprice;
	}

	public BigDecimal getStaffApprice() {
		return StaffApprice;
	}

	public void setStaffApprice(BigDecimal staffApprice) {
		StaffApprice = staffApprice;
	}
	
}