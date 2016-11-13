package com.jhj.service.impl.order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jhj.po.dao.order.OrderRatesMapper;
import com.jhj.po.model.bs.OrgStaffs;
import com.jhj.po.model.dict.DictProvince;
import com.jhj.po.model.order.OrderPriceExt;
import com.jhj.po.model.order.OrderRates;
import com.jhj.service.bs.OrgStaffsService;
import com.jhj.service.dict.DictService;
import com.jhj.service.order.OrderRatesService;
import com.jhj.service.order.OrdersService;
import com.jhj.vo.order.OrderDispatchSearchVo;
import com.jhj.vo.staff.OrgStaffRateVo;
import com.meijia.utils.DateUtil;
import com.meijia.utils.StringUtil;
import com.meijia.utils.TimeStampUtil;

@Service
public class OrderRatesServiceImpl implements OrderRatesService {

	@Autowired
	private OrderRatesMapper orderRatesMapper;

	@Autowired
	private OrdersService ordersService;
	
	@Autowired
	private DictService dictService;
	
	@Autowired
	private OrgStaffsService orgStaffsService;

	@Override
	public OrderRates initOrderRates() {

		OrderRates record = new OrderRates();
		record.setId(0L);
		record.setOrderId(0L);
		record.setOrderNo("");
		record.setStaffId(0L);
		record.setUserId(0L);
		record.setMobile("");
		record.setRateArrival(0);
		record.setRateAttitude(0);
		record.setRateSkill(0);
		record.setRateContent("");
		record.setAddTime(TimeStampUtil.getNow() / 1000);

		return record;

	}

	@Override
	public int deleteByPrimaryKey(Long id) {
		return orderRatesMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(OrderRates record) {
		return orderRatesMapper.insert(record);
	}

	@Override
	public int insertSelective(OrderRates record) {
		return orderRatesMapper.insertSelective(record);
	}

	@Override
	public OrderRates selectByPrimaryKey(Long id) {
		return orderRatesMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKey(OrderRates record) {
		return orderRatesMapper.updateByPrimaryKey(record);
	}
	
	@Override
	public int updateByPrimaryKeySelective(OrderRates record) {
		return orderRatesMapper.updateByPrimaryKeySelective(record);
	}
	
	@Override
	public List<OrderRates> selectBySearchVo(OrderDispatchSearchVo searchVo) {
		return orderRatesMapper.selectBySearchVo(searchVo);
	}
	
	@Override
	public HashMap totalByStaff(OrderDispatchSearchVo searchVo) {
		return orderRatesMapper.totalByStaff(searchVo);
	}
	
	@Override
	public PageInfo selectByListPage(OrderDispatchSearchVo searchVo, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		List<OrderRates> list = orderRatesMapper.selectByListPage(searchVo);
		PageInfo result = new PageInfo(list);
		return result;
	}
	
	@Override
	public List<OrgStaffRateVo> changeToStaffReteVo(List<OrderRates> list) {
		
		List<OrgStaffRateVo> result = new ArrayList<OrgStaffRateVo>();
		
		if (list.isEmpty()) return result;
		
		
		for (OrderRates item : list) {
			Long staffId = item.getStaffId();
			OrgStaffs orgStaff = orgStaffsService.selectByPrimaryKey(staffId);
			
			OrgStaffRateVo vo = new OrgStaffRateVo();
			vo.setStaffId(staffId);
			vo.setName(orgStaff.getName());
			vo.setMobile(orgStaff.getMobile());
			
			String headImg = orgStaff.getHeadImg();
			if (StringUtil.isEmpty(headImg)) headImg = "http://www.jia-he-jia.com/jhj-oa/upload/headImg/default-head-img.png";
			vo.setHeadImg(headImg);
			
			//年龄
			String age = "";
			try {
				age = DateUtil.getAge(orgStaff.getBirth());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!StringUtil.isEmpty(age)) age = age + "岁";
			vo.setAge(age);
			
			String provinceName = dictService.getProvinceName(orgStaff.getProvinceId());
			String cityName = dictService.getCityName(orgStaff.getCityId());
			
			vo.setHukou(provinceName + cityName);
			
			vo.setIntro(orgStaff.getIntro());
			
			String skill = "初级";
			Short level = orgStaff.getLevel();
			if (level.equals((short)1)) skill = "初级";
			if (level.equals((short)2)) skill = "中级";
			if (level.equals((short)3)) skill = "金牌";
			if (level.equals((short)4)) skill = "VIP";
			vo.setSkill(skill);
			
			//统计平均到达率， 平均好评度
			int totalRateStar = 0;
			String totalArrival = ""; 
			
			
			OrderDispatchSearchVo odSearchVo = new OrderDispatchSearchVo();
			odSearchVo.setStaffId(staffId);
			HashMap<String, Object> totalRates = this.totalByStaff(odSearchVo);
			if (!totalRates.isEmpty()) {
				int totalOrders = 0;
				int totalArrival0 = 0;
				int totalAttitude = 0;
				int totalSkill = 0;
				
				if (totalRates.get("total_orders") != null)
					totalOrders = Integer.valueOf(totalRates.get("total_orders").toString());
				
				if (totalRates.get("total_arrival_0") != null)
					totalArrival0 = Integer.valueOf(totalRates.get("total_arrival_0").toString());
				
				if (totalRates.get("total_attitude") != null)
					totalAttitude = Integer.valueOf(totalRates.get("total_attitude").toString());
				
				if (totalRates.get("total_skill") != null)
					totalSkill = Integer.valueOf(totalRates.get("total_skill").toString());
				
				if (totalOrders > 0) {
					
					//平均到达率
					NumberFormat numberFormat = NumberFormat.getInstance();  
					numberFormat.setMaximumFractionDigits(0);  
					totalArrival = numberFormat.format((float) totalArrival0 / (float) totalOrders * 100) + "%";
					
					//客户好评度
					double avgAttitude = (double)totalAttitude /  (double)totalOrders;
					double avgSkill =  (double)totalSkill /  (double)totalOrders;
					double avgStar = (avgAttitude + avgSkill) / (double)2;
					totalRateStar = (int) Math.round(avgStar);
				}
			}
			
			vo.setTotalRateStar(totalRateStar);
			vo.setTotalArrival(totalArrival);
			
			result.add(vo);
		}
		
		return result;
	}

}
