package com.jhj.po.model.bs;

import java.util.Date;

public class OrgStaffLeave {
    private Long id;

    private Long orgId;

    private Long parentId;

    private Long staffId;

    private Date leaveDate;

    private Short start;

    private Short end;

    private String remarks;

    private Long adminId;

    private Long addTime;
    
    //请假结束日期
    private Date leaveDateEnd;
    
    //请假天数
    private int totalDays;
    
    //请假状态
    private String leaveStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public Short getStart() {
        return start;
    }

    public void setStart(Short start) {
        this.start = start;
    }

    public Short getEnd() {
        return end;
    }

    public void setEnd(Short end) {
        this.end = end;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

	public Date getLeaveDateEnd() {
		return leaveDateEnd;
	}

	public void setLeaveDateEnd(Date leaveDateEnd) {
		this.leaveDateEnd = leaveDateEnd;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	
}