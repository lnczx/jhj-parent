package com.jhj.service.impl.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.jhj.common.ConstantMsg;
import com.jhj.common.Constants;
import com.jhj.po.dao.bs.OrgStaffLeaveMapper;
import com.jhj.po.dao.order.OrderDispatchsMapper;
import com.jhj.po.model.bs.OrgStaffFinance;
import com.jhj.po.model.bs.OrgStaffLeave;
import com.jhj.po.model.bs.OrgStaffSkill;
import com.jhj.po.model.bs.OrgStaffs;
import com.jhj.po.model.bs.Orgs;
import com.jhj.po.model.order.OrderDispatchs;
import com.jhj.po.model.order.Orders;
import com.jhj.po.model.user.UserAddrs;
import com.jhj.po.model.user.UserTrailReal;
import com.jhj.service.bs.OrgStaffBlackService;
import com.jhj.service.bs.OrgStaffFinanceService;
import com.jhj.service.bs.OrgStaffLeaveService;
import com.jhj.service.bs.OrgStaffSkillService;
import com.jhj.service.bs.OrgStaffsService;
import com.jhj.service.bs.OrgsService;
import com.jhj.service.order.OrderDispatchsService;
import com.jhj.service.order.OrdersService;
import com.jhj.service.users.UserAddrsService;
import com.jhj.service.users.UserTrailRealService;
import com.jhj.vo.bs.OrgDispatchPoiVo;
import com.jhj.vo.dispatch.StaffDispatchVo;
import com.jhj.vo.order.OrderDispatchSearchVo;
import com.jhj.vo.order.OrderSearchVo;
import com.jhj.vo.order.OrgStaffsNewVo;
import com.jhj.vo.org.LeaveSearchVo;
import com.jhj.vo.org.OrgSearchVo;
import com.jhj.vo.staff.OrgStaffFinanceSearchVo;
import com.jhj.vo.staff.OrgStaffSkillSearchVo;
import com.jhj.vo.staff.StaffSearchVo;
import com.jhj.vo.user.UserTrailSearchVo;
import com.meijia.utils.BeanUtilsExp;
import com.meijia.utils.TimeStampUtil;
import com.meijia.utils.baidu.BaiduPoiVo;
import com.meijia.utils.baidu.MapPoiUtil;

@Service
public class OrderDispatchsServiceImpl implements OrderDispatchsService {

	@Autowired
	private OrderDispatchsMapper orderDispatchsMapper;

	@Autowired
	private OrgStaffFinanceService orgStaffFinanceService;

	@Autowired
	private UserAddrsService userAddrService;

	@Autowired
	private OrdersService orderService;

	@Autowired
	private OrgStaffSkillService orgStaffSkillService;

	@Autowired
	private OrgsService orgService;

	@Autowired
	private OrgStaffLeaveService orgStaffLeaveService;

	@Autowired
	private OrgStaffsService orgStaffService;

	@Autowired
	private UserTrailRealService trailRealService;

	@Override
	public int deleteByPrimaryKey(Long id) {
		return orderDispatchsMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(OrderDispatchs record) {
		return orderDispatchsMapper.insert(record);
	}

	@Override
	public int insertSelective(OrderDispatchs record) {
		return orderDispatchsMapper.insertSelective(record);
	}

	@Override
	public OrderDispatchs selectByPrimaryKey(Long id) {
		return orderDispatchsMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(OrderDispatchs record) {
		return orderDispatchsMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(OrderDispatchs record) {
		return orderDispatchsMapper.updateByPrimaryKey(record);
	}

	@Override
	public OrderDispatchs initOrderDisp() {

		OrderDispatchs dispatchs = new OrderDispatchs();

		dispatchs.setId(0L);
		dispatchs.setUserId(0L);
		dispatchs.setMobile("");
		dispatchs.setOrderId(0L);
		dispatchs.setOrderNo("");
		dispatchs.setServiceDatePre(0L);
		dispatchs.setServiceDate(0L);
		dispatchs.setServiceHours((short) 0);
		dispatchs.setOrgId(0L);
		dispatchs.setParentId(0L);
		dispatchs.setStaffId(0L);
		dispatchs.setStaffName("");
		dispatchs.setStaffMobile("");
		dispatchs.setRemarks("");
		dispatchs.setAmId(0L);
		dispatchs.setDispatchStatus(Constants.ORDER_DIS_ENABLE); // ??派工状态
																	// 默认有效吧？
		dispatchs.setPickAddrName("");
		dispatchs.setPickAddrLat("");
		dispatchs.setPickAddrLng("");
		dispatchs.setPickAddr("");
		dispatchs.setPickDistance(0);
		dispatchs.setUserAddrDistance(0);
		dispatchs.setAddTime(TimeStampUtil.getNow() / 1000);
		dispatchs.setUpdateTime(0L);
		dispatchs.setIsApply((short) 0);
		dispatchs.setApplyTime(0L);
		return dispatchs;
	}

	@Override
	public List<OrderDispatchs> selectBySearchVo(OrderDispatchSearchVo searchVo) {
		return orderDispatchsMapper.selectBySearchVo(searchVo);
	}

	@Override
	public List<OrderDispatchs> selectByMatchTime(OrderDispatchSearchVo searchVo) {
		return orderDispatchsMapper.selectByMatchTime(searchVo);
	}

	@Override
	public List<Map<String, Object>> totalByYearAndMonth(Map<String, Object> conditions) {
		return orderDispatchsMapper.totalByYearAndMonth(conditions);
	}

	@Override
	public int totalStaffTodayOrders(Long staffId) {
		return orderDispatchsMapper.totalStaffTodayOrders(staffId);
	}

	@Override
	public List<HashMap> totalByStaff(OrderDispatchSearchVo searchVo) {
		return orderDispatchsMapper.totalByStaff(searchVo);
	}

	/**
	 * 订单类自动派工
	 */
	@Override
	public Long autoDispatch(Long orderId, Long serviceDate, Double serviceHour) {

		Long dispatchStaffId = 0L;

		Orders order = orderService.selectByPrimaryKey(orderId);

		Long serviceTypeId = order.getServiceType();

		Long addrId = order.getAddrId();

		UserAddrs addrs = userAddrService.selectByPrimaryKey(addrId);

		List<OrgDispatchPoiVo> orgList = getMatchOrgs(addrs.getLatitude(), addrs.getLongitude(), 0L, 0L, true);

		if (orgList.isEmpty())
			return dispatchStaffId;

		List<Long> staffIds = new ArrayList<Long>();

		for (int i = 0; i < orgList.size(); i++) {
			OrgDispatchPoiVo org = orgList.get(i);
			Long orgId = org.getOrgId();

			// ----1. 找出所有的符合此技能的员工 |
			staffIds = new ArrayList<Long>();
			OrgStaffSkillSearchVo searchVo = new OrgStaffSkillSearchVo();
			searchVo.setOrgId(orgId);
			searchVo.setServiceTypeId(serviceTypeId);
			List<OrgStaffSkill> skillList = orgStaffSkillService.selectBySearchVo(searchVo);

			if (!skillList.isEmpty()) {
				for (OrgStaffSkill s : skillList) {
					if (!staffIds.contains(s.getStaffId()))
						staffIds.add(s.getStaffId());
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---2.服务时间内 已 排班的 阿姨, 时间跨度为 服务开始前2小时 - 服务结束时间
			Long startServiceTime = serviceDate - 3600 * 2;
			Long endServiceTime = (long) (serviceDate + serviceHour * 3600);

			OrderDispatchSearchVo searchVo1 = new OrderDispatchSearchVo();
			searchVo1.setOrgId(orgId);
			searchVo1.setDispatchStatus((short) 1);
			searchVo1.setStartServiceTime(startServiceTime);
			searchVo1.setEndServiceTime(endServiceTime);
			List<OrderDispatchs> disList = this.selectByMatchTime(searchVo1);

			for (OrderDispatchs orderDispatch : disList) {
				if (staffIds.contains(orderDispatch.getStaffId())) {
					staffIds.remove(orderDispatch.getStaffId());
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---3.排除掉在黑名单中的服务人员
			OrgStaffFinanceSearchVo searchVo2 = new OrgStaffFinanceSearchVo();
			searchVo2.setStaffIds(staffIds);
			searchVo2.setIsBlack((short) 1);
			List<OrgStaffFinance> blackList = orgStaffFinanceService.selectBySearchVo(searchVo2);

			if (!blackList.isEmpty()) {
				for (OrgStaffFinance os : blackList) {
					if (staffIds.contains(os.getStaffId())) {
						staffIds.remove(os.getStaffId());
					}
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---4.排除请假的员工.
			LeaveSearchVo searchVo3 = new LeaveSearchVo();

			searchVo3.setOrgId(orgId);
			Long orderServiceDateEnd = (long) (serviceDate + serviceHour * 3600);

			// 订单服务开始时间
			searchVo3.setLeaveStartTime(serviceDate);
			// 订单服务结束时间
			searchVo3.setLeaveEndTime(orderServiceDateEnd);

			// 服务时间内 ，同时也在 假期内的 员工
			List<OrgStaffLeave> leaveList = orgStaffLeaveService.selectBySearchVo(searchVo3);

			if (!leaveList.isEmpty()) {
				for (OrgStaffLeave ol : leaveList) {
					if (staffIds.contains(ol.getStaffId())) {
						staffIds.remove(ol.getStaffId());
					}
				}
			}

			// 如果该云店有满足员工，则直接跳出循环，返回信息.
			if (!staffIds.isEmpty())
				break;
		}

		if (staffIds.size() == 1) {
			dispatchStaffId = staffIds.get(0);
			return dispatchStaffId;
		}

		// ---5.找出已匹配的员工列表，并统计当天的订单数，优先指派订单数少的员工，如果订单数相同，则随机
		if (!staffIds.isEmpty()) {

			List<OrgStaffsNewVo> list = new ArrayList<OrgStaffsNewVo>();

			StaffSearchVo searchVo5 = new StaffSearchVo();
			searchVo5.setStaffIds(staffIds);
			searchVo5.setStatus(1);
			List<OrgStaffs> staffList = orgStaffService.selectBySearchVo(searchVo5);

			OrderDispatchSearchVo searchVo6 = new OrderDispatchSearchVo();
			searchVo6.setStaffIds(staffIds);
			List<HashMap> totalStaffs = this.totalByStaff(searchVo6);

			for (OrgStaffs item : staffList) {
				OrgStaffsNewVo vo = new OrgStaffsNewVo();
				BeanUtilsExp.copyPropertiesIgnoreNull(item, vo);
				vo.setDispathStaFlag(1);
				for (HashMap totalItem : totalStaffs) {
					Long staffId = (Long) totalItem.get("staff_id");
					int total = (int) totalItem.get("total");

					if (staffId.equals(vo.getStaffId())) {
						vo.setTodayOrderNum(total);
					}
				}

				list.add(vo);
			}

			// 进行排序，根据距离大小来正序.
			if (list.size() > 0) {
				Collections.sort(list, new Comparator<OrgStaffsNewVo>() {
					@Override
					public int compare(OrgStaffsNewVo s1, OrgStaffsNewVo s2) {
						return Integer.valueOf(s1.getTodayOrderNum()).compareTo(s2.getTodayOrderNum());
					}
				});

				dispatchStaffId = list.get(0).getStaffId();
			}

		}

		return dispatchStaffId;
	}

	/**
	 * 订单类手动派工 ,需要返回List<OrgStaffsNewVo>,
	 * 1. 找出符合派工逻辑的所有门店的员工
	 * 2. 包含门店的距离
	 * 3. 包含员工的距离.
	 */
	@Override
	public List<OrgStaffsNewVo> manualDispatch(Long orderId, Long serviceDate, Double serviceHour) {

		List<OrgStaffsNewVo> list = new ArrayList<OrgStaffsNewVo>();

		Orders order = orderService.selectByPrimaryKey(orderId);

		Long serviceTypeId = order.getServiceType();

		Long addrId = order.getAddrId();
		UserAddrs addrs = userAddrService.selectByPrimaryKey(addrId);
		String fromLat = addrs.getLatitude();
		String fromLng = addrs.getLongitude();

		List<OrgDispatchPoiVo> orgList = getMatchOrgs(fromLat, fromLng, 0L, 0L, true);

		if (orgList.isEmpty())
			return list;

		List<Long> staffIds = new ArrayList<Long>();

		for (int i = 0; i < orgList.size(); i++) {
			OrgDispatchPoiVo org = orgList.get(i);
			Long orgId = org.getOrgId();

			// ----1. 找出所有的符合此技能的员工 |
			OrgStaffSkillSearchVo searchVo = new OrgStaffSkillSearchVo();
			searchVo.setOrgId(orgId);
			searchVo.setServiceTypeId(serviceTypeId);
			List<OrgStaffSkill> skillList = orgStaffSkillService.selectBySearchVo(searchVo);

			if (!skillList.isEmpty()) {
				for (OrgStaffSkill s : skillList) {
					if (!staffIds.contains(s.getStaffId()))
						staffIds.add(s.getStaffId());
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---2.服务时间内 已 排班的 阿姨, 时间跨度为 服务开始前2小时 - 服务结束时间
			Long startServiceTime = serviceDate - 3600 * 2;
			Long endServiceTime = (long) (serviceDate + serviceHour * 3600);

			OrderDispatchSearchVo searchVo1 = new OrderDispatchSearchVo();
			searchVo1.setOrgId(orgId);
			searchVo1.setDispatchStatus((short) 1);
			searchVo1.setStartServiceTime(startServiceTime);
			searchVo1.setEndServiceTime(endServiceTime);
			List<OrderDispatchs> disList = this.selectByMatchTime(searchVo1);

			for (OrderDispatchs orderDispatch : disList) {
				if (staffIds.contains(orderDispatch.getStaffId())) {
					staffIds.remove(orderDispatch.getStaffId());
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---3.排除掉在黑名单中的服务人员
			OrgStaffFinanceSearchVo searchVo2 = new OrgStaffFinanceSearchVo();
			searchVo2.setStaffIds(staffIds);
			searchVo2.setIsBlack((short) 1);
			List<OrgStaffFinance> blackList = orgStaffFinanceService.selectBySearchVo(searchVo2);

			if (!blackList.isEmpty()) {
				for (OrgStaffFinance os : blackList) {
					if (staffIds.contains(os.getStaffId())) {
						staffIds.remove(os.getStaffId());
					}
				}
			}

			if (staffIds.isEmpty())
				continue;

			// ---4.排除请假的员工.
			LeaveSearchVo searchVo3 = new LeaveSearchVo();

			searchVo3.setOrgId(orgId);
			Long orderServiceDateEnd = (long) (serviceDate + serviceHour * 3600);

			// 订单服务开始时间
			searchVo3.setLeaveStartTime(serviceDate);
			// 订单服务结束时间
			searchVo3.setLeaveEndTime(orderServiceDateEnd);

			// 服务时间内 ，同时也在 假期内的 员工
			List<OrgStaffLeave> leaveList = orgStaffLeaveService.selectBySearchVo(searchVo3);

			if (!leaveList.isEmpty()) {
				for (OrgStaffLeave ol : leaveList) {
					if (staffIds.contains(ol.getStaffId())) {
						staffIds.remove(ol.getStaffId());
					}
				}
			}

		}
		
		if (staffIds.isEmpty()) return list;

		// ---5.找出已匹配的员工列表，并统计当天的订单数，优先指派订单数少的员工，如果订单数相同，则随机
		
		StaffSearchVo searchVo5 = new StaffSearchVo();
		searchVo5.setStaffIds(staffIds);
		searchVo5.setStatus(1);
		List<OrgStaffs> staffList = orgStaffService.selectBySearchVo(searchVo5);

		for (OrgStaffs item : staffList) {
			OrgStaffsNewVo vo = new OrgStaffsNewVo();
			BeanUtilsExp.copyPropertiesIgnoreNull(item, vo);
			vo.setDispathStaFlag(1);
			// 门店距离
			for (OrgDispatchPoiVo poiVo : orgList) {
				if (vo.getOrgId().equals(poiVo.getOrgId())) {
					vo.setOrgDistanceValue(poiVo.getDistanceValue());
					vo.setOrgDistanceText(poiVo.getDistanceText());
					break;
				}
			}

			// 员工服务日期的订单数
			OrderDispatchSearchVo searchVo6 = new OrderDispatchSearchVo();
			searchVo6.setStaffIds(staffIds);
			List<HashMap> totalStaffs = this.totalByStaff(searchVo6);
			for (HashMap totalItem : totalStaffs) {
				Long staffId = (Long) totalItem.get("staff_id");
				int total = (int) totalItem.get("total");

				if (staffId.equals(vo.getStaffId())) {
					vo.setTodayOrderNum(total);
				}
			}

			list.add(vo);
		}
		
		//员工距离
		list = this.getStaffDispatch(list, fromLat, fromLng);

		// 进行排序，根据距离大小来正序.
		if (list.size() > 0) {
			Collections.sort(list, new Comparator<OrgStaffsNewVo>() {
				@Override
				public int compare(OrgStaffsNewVo s1, OrgStaffsNewVo s2) {
					return Integer.valueOf(s1.getDistanceValue()).compareTo(s2.getDistanceValue());
				}
			});
		}
		return list;
	}
	
	/**
	 * 订单类手动派工 ,需要返回List<OrgStaffsNewVo>,
	 * 1. 找出符合派工逻辑的针对门店或云店的所有员工（包括不可派工的员工）;
	 * 2. 包含门店的距离
	 * 3. 包含员工的距离.
	 */
	@Override
	public List<OrgStaffsNewVo> manualDispatchByOrg(Long orderId, Long serviceDate, Double serviceHour, Long parentId, Long orgId) {
		
		Orders order = orderService.selectByPrimaryKey(orderId);

		Long serviceTypeId = order.getServiceType();

		Long addrId = order.getAddrId();
		UserAddrs addrs = userAddrService.selectByPrimaryKey(addrId);
		String fromLat = addrs.getLatitude();
		String fromLng = addrs.getLongitude();
		
		List<OrgStaffsNewVo> list = new ArrayList<OrgStaffsNewVo>();
		
		StaffSearchVo staffSearchVo = new StaffSearchVo();
		staffSearchVo.setStatus(1);
		
		if (parentId > 0L) staffSearchVo.setParentId(parentId);
		if (orgId > 0L) staffSearchVo.setOrgId(orgId);
		List<OrgStaffs> staffList = orgStaffService.selectBySearchVo(staffSearchVo);
		
		List<Long> staffIds = new ArrayList<Long>();
		for (OrgStaffs staff : staffList) {
			OrgStaffsNewVo vo = new OrgStaffsNewVo();
			BeanUtilsExp.copyPropertiesIgnoreNull(staff, vo);
			vo.setDispathStaFlag(1);
			list.add(vo);
			if (!staffIds.contains(staff.getStaffId())) staffIds.add(staff.getStaffId());
		}
		
		//1. 找出云店距离目标位置集合,判断如果超出20Km，则写上原因, 距离超出.
		List<OrgDispatchPoiVo> orgList = getMatchOrgs(fromLat, fromLng, parentId, orgId, false);
		if (orgList.isEmpty()) return list;
		
		List<Long> overMaxDistanceOrgIds = new ArrayList<Long>();
		for (int i = 0; i < orgList.size(); i++) {
			OrgDispatchPoiVo poiVo = orgList.get(i);
			if(poiVo.getDistanceValue() < Constants.maxDistance) {
				overMaxDistanceOrgIds.add(poiVo.getOrgId());
			}
		}
		
		for (Long maxOrgId : overMaxDistanceOrgIds) {
			for (OrgStaffsNewVo vo : list) {
				if (vo.getOrgId().equals(maxOrgId)) {
					vo.setDispathStaFlag(0);
					vo.setReason(ConstantMsg.NOT_DISPATCH_OVER_MAX_DISTANCE);
				}
			}
		}
		
		//1. 找出技能不符合的员工.
		OrgStaffSkillSearchVo searchVo = new OrgStaffSkillSearchVo();
		if (parentId > 0L) searchVo.setParentId(parentId);
		if (orgId > 0L) searchVo.setOrgId(orgId);
		searchVo.setServiceTypeId(serviceTypeId);
		List<OrgStaffSkill> skillList = orgStaffSkillService.selectBySearchVo(searchVo);
		
		List<Long> skillStaffIds = new ArrayList<Long>();
		for (OrgStaffSkill item : skillList) {
			if (!skillStaffIds.contains(item.getStaffId())) skillStaffIds.add(item.getStaffId());
		}
		
		//找出有技能和没技能的差集
		List<Long> staffIdsAll = staffIds;
		staffIdsAll.removeAll(skillStaffIds);
		
		for (OrgStaffsNewVo vo : list) {
			for (Long item : staffIdsAll) {
				if (vo.getStaffId().equals(item)) {
					vo.setDispathStaFlag(0);
					vo.setReason(vo.getReason() + ";" + ConstantMsg.NOT_DISPATCH_NOT_SKILL);
				}
			}
		}
		
		
		// ---2.服务时间内 已 排班的 阿姨, 时间跨度为 服务开始前2小时 - 服务结束时间
		Long startServiceTime = serviceDate - 3600 * 2;
		Long endServiceTime = (long) (serviceDate + serviceHour * 3600);

		OrderDispatchSearchVo searchVo1 = new OrderDispatchSearchVo();
		if (parentId > 0L) searchVo1.setParentId(parentId);
		if (orgId > 0L) searchVo1.setOrgId(orgId);
		searchVo1.setDispatchStatus((short) 1);
		searchVo1.setStartServiceTime(startServiceTime);
		searchVo1.setEndServiceTime(endServiceTime);
		List<OrderDispatchs> disList = this.selectByMatchTime(searchVo1);
		List<Long> haveDispatchStaffIds = new ArrayList<Long>();
		for (OrderDispatchs item : disList) {
			if (!haveDispatchStaffIds.contains(item.getStaffId())) haveDispatchStaffIds.add(item.getStaffId());
		}
		
		//找出有派工和没派工的差集
		staffIdsAll = staffIds;
		staffIdsAll.removeAll(haveDispatchStaffIds);
		
		for (OrgStaffsNewVo vo : list) {
			for (Long item : staffIdsAll) {
				if (vo.getStaffId().equals(item)) {
					vo.setDispathStaFlag(0);
					vo.setReason(vo.getReason() + ";" + ConstantMsg.NOT_DISPATCH_SERVICE_DATE_CONFLIT);
				}
			}
		}
		
		// ---3.排除掉在黑名单中的服务人员
		OrgStaffFinanceSearchVo searchVo2 = new OrgStaffFinanceSearchVo();
		searchVo2.setStaffIds(staffIds);
		searchVo2.setIsBlack((short) 1);
		List<OrgStaffFinance> blackList = orgStaffFinanceService.selectBySearchVo(searchVo2);
		
		for (OrgStaffsNewVo vo : list) {
			for (OrgStaffFinance os : blackList) {
				if (vo.getStaffId().equals(os.getStaffId())) {
					vo.setDispathStaFlag(0);
					vo.setReason(vo.getReason() + ";" + ConstantMsg.NOT_DISPATCH_BLACK_LIST);
				}
			}
		}
			
		// ---4.排除请假的员工.
		LeaveSearchVo searchVo3 = new LeaveSearchVo();
		if (parentId > 0L) searchVo3.setParentId(parentId);
		if (orgId > 0L) searchVo3.setOrgId(orgId);

		Long orderServiceDateEnd = (long) (serviceDate + serviceHour * 3600);
		// 订单服务开始时间
		searchVo3.setLeaveStartTime(serviceDate);
		// 订单服务结束时间
		searchVo3.setLeaveEndTime(orderServiceDateEnd);
		List<OrgStaffLeave> leaveList = orgStaffLeaveService.selectBySearchVo(searchVo3);
		
		for (OrgStaffsNewVo vo : list) {
			for (OrgStaffLeave os : leaveList) {
				if (vo.getStaffId().equals(os.getStaffId())) {
					vo.setDispathStaFlag(0);
					vo.setReason(vo.getReason() + ";" + ConstantMsg.NOT_DISPATCH_LEAVE);
				}
			}
		}
		
		for (OrgStaffsNewVo vo : list) {

			// 门店距离
			for (OrgDispatchPoiVo poiVo : orgList) {
				if (vo.getOrgId().equals(poiVo.getOrgId())) {
					vo.setOrgDistanceValue(poiVo.getDistanceValue());
					vo.setOrgDistanceText(poiVo.getDistanceText());
					break;
				}
			}

			// 员工服务日期的订单数
			OrderDispatchSearchVo searchVo6 = new OrderDispatchSearchVo();
			searchVo6.setStaffIds(staffIds);
			List<HashMap> totalStaffs = this.totalByStaff(searchVo6);
			for (HashMap totalItem : totalStaffs) {
				Long staffId = (Long) totalItem.get("staff_id");
				int total = (int) totalItem.get("total");

				if (staffId.equals(vo.getStaffId())) {
					vo.setTodayOrderNum(total);
				}
			}

			list.add(vo);
		}
		
		//员工距离
		list = this.getStaffDispatch(list, fromLat, fromLng);

		// 进行排序，根据距离大小来正序.
		if (list.size() > 0) {
			Collections.sort(list, new Comparator<OrgStaffsNewVo>() {
				@Override
				public int compare(OrgStaffsNewVo s1, OrgStaffsNewVo s2) {
					return Integer.valueOf(s1.getDistanceValue()).compareTo(s2.getDistanceValue());
				}
			});
		}

		return list;
	}	

	/**
	 * 
	 * @Title: getMatchOrgId
	 * @Description:
	 *               找出符合 指定距离的云店
	 * @throws
	 */
	public List<OrgDispatchPoiVo> getMatchOrgs(String fromLat, String fromLng, Long parentId, Long orgId, Boolean needMatchMaxDistance) {

		List<OrgDispatchPoiVo> result = new ArrayList<OrgDispatchPoiVo>();

		// 目标地点：所有云店的集合
		OrgSearchVo searchVo = new OrgSearchVo();

		if (parentId > 0L) searchVo.setParentId(parentId);
		if (orgId > 0L) searchVo.setOrgId(orgId);
		if (parentId.equals(0L) && orgId.equals(0L)) {
			searchVo.setIsCloud((short) 1);
		}
		searchVo.setOrgStatus((short) 1);
		List<Orgs> orgList = orgService.selectBySearchVo(searchVo);

		if (orgList.isEmpty())
			return result;

		List<BaiduPoiVo> orgAddrList = new ArrayList<BaiduPoiVo>();
		for (Orgs org : orgList) {
			BaiduPoiVo baiduPoiVo = new BaiduPoiVo();

			baiduPoiVo.setLat(org.getPoiLatitude());
			baiduPoiVo.setLng(org.getPoiLongitude());
			baiduPoiVo.setName(org.getOrgName());

			orgAddrList.add(baiduPoiVo);
		}

		Orgs item = null;
		try {
			List<BaiduPoiVo> voList = MapPoiUtil.getMapRouteMatrix(fromLat, fromLng, orgAddrList);
			
			if (needMatchMaxDistance) {
				voList = MapPoiUtil.getMinDest(voList, Constants.maxDistance);
			}
			for (int i = 0; i < orgList.size(); i++) {
				item = orgList.get(i);

				OrgDispatchPoiVo vo = new OrgDispatchPoiVo();
				BeanUtilsExp.copyPropertiesIgnoreNull(item, vo);

				for (BaiduPoiVo baiduPoiVo : voList) {
					if (baiduPoiVo.getLat().equals(item.getPoiLatitude()) && baiduPoiVo.getLng().equals(item.getPoiLongitude())) {

						BeanUtilsExp.copyPropertiesIgnoreNull(baiduPoiVo, vo);
						result.add(vo);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 进行排序，根据距离大小来正序.
		if (result.size() > 0) {
			Collections.sort(result, new Comparator<OrgDispatchPoiVo>() {
				@Override
				public int compare(OrgDispatchPoiVo s1, OrgDispatchPoiVo s2) {
					return Integer.valueOf(s1.getDistanceValue()).compareTo(s2.getDistanceValue());
				}
			});
		}

		return result;
	}

	@Override
	public List<OrgStaffsNewVo> getStaffDispatch(List<OrgStaffsNewVo> list, String fromLat, String fromLng) {

		// 计算出员工与目标位置的距离.
		List<BaiduPoiVo> staffPoiList = new ArrayList<BaiduPoiVo>();

		List<Long> staffIds = new ArrayList<Long>();
		for (OrgStaffsNewVo item : list) {
			if (!staffIds.contains(item.getStaffId()))
				staffIds.add(item.getStaffId());
		}

		UserTrailSearchVo searchVo = new UserTrailSearchVo();
		searchVo.setUserIds(staffIds);
		List<UserTrailReal> trailList = trailRealService.selectBySearchVo(searchVo);
		for (UserTrailReal userTrailReal : trailList) {
			BaiduPoiVo baiduPoiVo = new BaiduPoiVo();
			baiduPoiVo.setLat(userTrailReal.getLat());
			baiduPoiVo.setLng(userTrailReal.getLng());
			baiduPoiVo.setName(userTrailReal.getPoiName());
			staffPoiList.add(baiduPoiVo);
		}

		List<BaiduPoiVo> destList = MapPoiUtil.getMapRouteMatrix(fromLat, fromLng, staffPoiList);
		
		for (OrgStaffsNewVo vo : list) {
			vo.setDistanceText("");
			vo.setDurationText("");
			vo.setDistanceValue(0);
			
			for (UserTrailReal item : trailList) {
				for (BaiduPoiVo baiduPoiVo : destList) {
					if (item.getUserId().equals(vo.getStaffId()) &&
						baiduPoiVo.getLat().equals(item.getLat()) && 
						baiduPoiVo.getLng().equals(item.getLng()) ) {
						// 合适的服务人员的最新位置 与 服务地址 的 距离、时间等信息
						vo.setDistanceText(baiduPoiVo.getDistanceText());
						vo.setDurationText(baiduPoiVo.getDurationText());
						vo.setDistanceValue(baiduPoiVo.getDistanceValue());
						break;
					}
				}
			}
			
		}
		return list;
	}

}
