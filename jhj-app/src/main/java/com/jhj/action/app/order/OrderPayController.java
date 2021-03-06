package com.jhj.action.app.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jhj.action.app.BaseController;
import com.jhj.common.ConstantMsg;
import com.jhj.common.Constants;
import com.jhj.po.model.bs.DictCoupons;
import com.jhj.po.model.order.OrderAppoint;
import com.jhj.po.model.order.OrderLog;
import com.jhj.po.model.order.OrderPriceExt;
import com.jhj.po.model.order.OrderPrices;
import com.jhj.po.model.order.Orders;
import com.jhj.po.model.period.PeriodOrder;
import com.jhj.po.model.share.OrderShare;
import com.jhj.po.model.university.PartnerServiceType;
import com.jhj.po.model.user.UserCoupons;
import com.jhj.po.model.user.Users;
import com.jhj.service.bs.DictCouponsService;
import com.jhj.service.bs.OrgStaffsService;
import com.jhj.service.order.OrderAppointService;
import com.jhj.service.order.OrderDispatchsService;
import com.jhj.service.order.OrderHourAddService;
import com.jhj.service.order.OrderLogService;
import com.jhj.service.order.OrderPayService;
import com.jhj.service.order.OrderPriceExtService;
import com.jhj.service.order.OrderPricesService;
import com.jhj.service.order.OrderQueryService;
import com.jhj.service.order.OrdersService;
import com.jhj.service.period.PeriodOrderService;
import com.jhj.service.share.OrderShareService;
import com.jhj.service.university.PartnerServiceTypeService;
import com.jhj.service.users.UserCouponsService;
import com.jhj.service.users.UserDetailPayService;
import com.jhj.service.users.UsersService;
import com.jhj.vo.order.OrderDispatchSearchVo;
import com.jhj.vo.order.OrderPriceExtVo;
import com.jhj.vo.order.OrderViewVo;
import com.jhj.vo.order.OrgStaffDispatchVo;
import com.meijia.utils.BeanUtilsExp;
import com.meijia.utils.DateUtil;
import com.meijia.utils.MathBigDecimalUtil;
import com.meijia.utils.OrderNoUtil;
import com.meijia.utils.SmsUtil;
import com.meijia.utils.TimeStampUtil;
import com.meijia.utils.vo.AppResultData;

@Controller
@RequestMapping(value = "/app/order")
public class OrderPayController extends BaseController {

	@Autowired
	private UsersService userService;

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private OrderPricesService orderPricesService;

	@Autowired
	private OrderPayService orderPayService;

	@Autowired
    private OrderQueryService orderQueryService;

	@Autowired
	private OrderLogService orderLogService;
	
	@Autowired
	private UserDetailPayService userDetailPayService;
	
	@Autowired
	private UserCouponsService userCouponsService;	
	
	@Autowired
	private OrderHourAddService orderHourAddservice;	
	
	@Autowired
	private DictCouponsService dictCouponsService;	
	
	@Autowired
	private OrgStaffsService orgStaService;
	
	@Autowired
	private PartnerServiceTypeService partService;
	
	@Autowired
	private OrderPriceExtService orderPriceExtService;
	
	@Autowired
    private OrderAppointService orderAppointService;
	
	@Autowired
	private OrderDispatchsService orderDispatchService;
	
	@Autowired
	private PeriodOrderService periodOrderService;
	
	@Autowired
	private OrderShareService ordershareService;
	
	// 17.订单支付前接口
	/**
	 * @param mobile true string 手机号 
	 * @param order_id true int 订单id 
	 * @param order_no true string 订单号
	 * @param pay_type true int 支付方式： 付款方式 0 = 余额支付 1 = 支付宝 2 = 微信支付 3 = 智慧支付 4 = 上门刷卡（保留，站位） 6 = 现金支付 7 = 平台已支付
	 * @return  OrderViewVo
	 */
	@RequestMapping(value = "post_pay", method = RequestMethod.POST)
	public AppResultData<Object> postPay(
			@RequestParam("user_id") Long userId, 
			@RequestParam("order_no") String orderNo, 
			@RequestParam("order_pay_type") Short orderPayType,
			@RequestParam(value = "user_coupon_id", required = false, defaultValue="0") Long userCouponId,
			@RequestParam(value = "coupon_id", required = false, defaultValue="0") Long couponId,
			@RequestParam(value = "share_user_id", required = false, defaultValue="0") Integer shareUserId) {

		AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");

		Users u = userService.selectByPrimaryKey(userId);

		// 判断是否为注册用户，非注册用户返回 999
		if (u == null) {
			result.setStatus(Constants.ERROR_999);
			result.setMsg(ConstantMsg.USER_NOT_EXIST_MG);
			return result;
		}		
		
		Orders order = ordersService.selectByOrderNo(orderNo);
		if (order == null){
			return result;
		}
		Long orderId = order.getId();
		
		if(!order.getOrderStatus().equals(Constants.ORDER_HOUR_STATUS_1)){
			result.setStatus(Constants.ERROR_999);
			result.setMsg(ConstantMsg.HAVE_PAY);
			return result;
		}

		OrderPrices orderPrice = orderPricesService.selectByOrderId(orderId);
		
		if (orderPrice == null) {
			result.setStatus(Constants.ERROR_999);
			result.setMsg("订单不存在");
			return result;			
		}
		
		//判断如果有指定员工，则指定员工是否满足条件
		//找出是否有指定的阿姨。
		OrderDispatchSearchVo orderDispatchSearchVo = new OrderDispatchSearchVo();
		orderDispatchSearchVo.setOrderId(orderId);
		
		List<OrderAppoint> orderAppoints = orderAppointService.selectBySearchVo(orderDispatchSearchVo);
		List<Long> appointStaffIds = new ArrayList<Long>();
		if (!orderAppoints.isEmpty()) {
			for (OrderAppoint oa: orderAppoints) {
				Long appointStaffID = oa.getStaffId();
				AppResultData<Object> checkAppointResult = orderDispatchService.checAppointDispatch(orderId, appointStaffID);
				if (checkAppointResult.getStatus() == Constants.ERROR_999) {
					return checkAppointResult;
				}
			}
		}
		
		//此时 orderPay 和 orderMoney 值是相等的
		BigDecimal orderPay = orderPrice.getOrderMoney();
		BigDecimal orderMoney = orderPrice.getOrderMoney();
		BigDecimal orderOriginPay = orderPrice.getOrderOriginPrice();
		
		//只有余额支付才能使用会员价,并且不是后台下单的订单
		if ( order.getOrderFrom() !=2 ) {
			orderPay = orderOriginPay;
			orderMoney = orderOriginPay;
			
			if (orderPayType.equals(Constants.PAY_TYPE_0) && u.getIsVip() == 1) {
				orderPay = orderPrice.getOrderPrimePrice();
				orderMoney = orderPrice.getOrderPrimePrice();
			}
		}
		
		
		if(couponId!=null && couponId>0){
			UserCoupons userCoupons =new UserCoupons();
			userCoupons.setIsUsed((short)0);
			userCoupons.setUserId(userId);
			userCoupons.setCouponId(couponId);
			List<UserCoupons> coupons = userCouponsService.selectByUserCoupons(userCoupons);
			if(coupons.size()>0){
				userCouponId = coupons.get(0).getId();
			}
			
		}
		
		//处理优惠劵，判断优惠劵是否有效的问题
		if (userCouponId > 0L) {
			
			AppResultData<Object> validateResult = null;
			validateResult = userCouponsService.validateCouponForPay(userId , userCouponId, order.getId());
			if (validateResult.getStatus() == Constants.ERROR_999) return validateResult;
			
			UserCoupons userCoupon = userCouponsService.selectByPrimaryKey(userCouponId);
			DictCoupons dictCoupons = dictCouponsService.selectByPrimaryKey(userCoupon.getCouponId());
			BigDecimal couponValue = dictCoupons.getValue();
			
			orderPay = MathBigDecimalUtil.sub(orderMoney, couponValue);
		}
		
		long updateTime = TimeStampUtil.getNowSecond();

		
		if (orderPayType.equals(Constants.PAY_TYPE_0)) {
			//1.先判断用户余额是否够支付
			if(u.getRestMoney().compareTo(orderPay) < 0) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg(ConstantMsg.ERROR_999_MSG_5);
				return result;
			}
		}		
		
		//判断当前是否有满足条件阿姨，没有则返回提示信息.
//		List<OrgStaffsNewVo> orgStaffsNewVos = new ArrayList<OrgStaffsNewVo>();
		Long addrId = order.getAddrId();
		Long serviceType = order.getServiceType();
		Long serviceDate = order.getServiceDate();
		int staffNums = order.getStaffNums();
		double serviceHour = order.getServiceHour();
		List<OrgStaffDispatchVo> autoStaffs = orderDispatchService.autoDispatch(addrId, serviceType, serviceDate, serviceHour, staffNums, appointStaffIds);
		if (autoStaffs.isEmpty()) {
			result.setStatus(Constants.ERROR_999);
			result.setMsg("你选择的服务时间服务人员都已经预约满了，请尝试更换服务时间，为你带来的不便，敬请谅解");
			return result;
		}
		
		
		orderPrice.setOrderMoney(orderMoney);
		orderPrice.setOrderPay(orderPay);
		orderPrice.setPayType(orderPayType);
		orderPrice.setUpdateTime(updateTime);
		orderPrice.setCouponId(userCouponId);
		orderPricesService.updateByPrimaryKey(orderPrice);
		
		
		/*
		 *  2016年4月1日15:34:55
		 * 
		 * 	 对于 现金支付。  orderPayType == 6 ，不进行扣款操作。只更改 订单状态 
		 */
		
		//如果是余额支付或者需支付金额为0 
		if (orderPayType.equals(Constants.PAY_TYPE_0) || 
				orderPayType.equals(Constants.PAY_TYPE_6) ||
				orderPayType.equals(Constants.PAY_TYPE_7) ||
				orderPay.compareTo(new BigDecimal(0)) == 0) {
			// 1. 扣除用户余额.
			// 2. 用户账号明细增加.
			// 3. 订单状态变为已支付.
			// 4. 订单日志
			if (orderPayType.equals(Constants.PAY_TYPE_0)) {
				u.setRestMoney(u.getRestMoney().subtract(orderPay));
				u.setUpdateTime(updateTime);
				userService.updateByPrimaryKeySelective(u);
				
				List<OrderShare> orderShareList = ordershareService.selectByShareId(shareUserId);
				OrderShare os = ordershareService.selectByShareIdAndUserId(shareUserId, userId.intValue());
				if(orderShareList!=null && orderShareList.size()>0 && os==null){
					OrderShare orderShare = orderShareList.get(0);
					userCouponsService.shareSuccessSendCoupons(orderShare,order.getUserId());
					orderShare.setUserId(userId.intValue());
					orderShare.setOrderNo(orderNo);
					orderShare.setOrderId(orderId.intValue());
					ordershareService.updateByPrimaryKeySelective(orderShare);
				}
			}
						
			if(order.getOrderType() == Constants.ORDER_TYPE_0 ||
			   order.getOrderType() == Constants.ORDER_TYPE_1){
				
				order.setOrderStatus(Constants.ORDER_HOUR_STATUS_2);//已支付
			}
			
			
			// 修改 24小时已支付 的助理单，需要用到这个 修改时间
			order.setUpdateTime(TimeStampUtil.getNowSecond());
			ordersService.updateByPrimaryKeySelective(order);
			
			//记录订单日志.
			OrderLog orderLog = orderLogService.initOrderLog(order);
			orderLog.setAction(Constants.ORDER_ACTION_PAY);
			orderLog.setUserId(u.getId());
			orderLog.setUserName(u.getMobile());
			orderLog.setUserType((short)0);
			orderLogService.insert(orderLog);
			
			//记录用户消费明细
			userDetailPayService.addUserDetailPayForOrder(u, order, orderPrice, "", "", "");
			
			//服务时间
			String serviceTime = TimeStampUtil.timeStampToDateStr(serviceDate * 1000, "yyyy年MM月dd日HH:mm");
			

			PartnerServiceType type = partService.selectByPrimaryKey(serviceType);
			//服务类型名称
			String name = type.getName();
			
			String[] paySuccessForUser = new String[] {serviceTime,name};
			//订单支付成功后
			if (order.getOrderType().equals(Constants.ORDER_TYPE_0)) {
				
				/*
				 *   2016年4月14日10:21:50 
				 *    您预定的{1}{2}服务已经确认，感谢您的支持，服务人员会尽快与您联系，如有任何疑问请拨打010-56429112
				 */
				
				SmsUtil.SendSms(u.getMobile(),  Constants.PAY_SUCCESS_ORDER_SMS, paySuccessForUser);
				
				
				orderPayService.orderPaySuccessToDoForHour(u.getId(), order.getId(), false);
			}
			
			if (order.getOrderType().equals(Constants.ORDER_TYPE_1)) {
				
				/* 
				 *  2016年4月13日18:50:29  没啥大用了， jhj2.1 已经没有 深度 保洁订单类型了
 				 * 
				 */
				SmsUtil.SendSms(u.getMobile(),  Constants.PAY_SUCCESS_ORDER_SMS, paySuccessForUser);
				orderPayService.orderPaySuccessToDoForDeep(order);
			}

			if (order.getOrderType().equals(Constants.ORDER_TYPE_2)) {
				
				/*
				 * 2016年4月13日18:50:48  新增 的 短信
				 * 
				 */
//				您预定的{1}{2}服务已经确认，感谢您的支持，服务人员会尽快与您联系，如有任何疑问请拨打010-56429112
				
				orderPayService.orderPaySuccessToDoForAm(order);
				SmsUtil.SendSms(u.getMobile(),  Constants.PAY_SUCCESS_ORDER_SMS, paySuccessForUser);
			}
		}
		
		OrderViewVo orderViewVo = orderQueryService.getOrderView(order);
		result.setData(orderViewVo);
		
		return result;
	}
	
	// 17.订单支付前接口
		/**
		 * @param mobile true string 手机号 
		 * @param order_id true int 订单id 
		 * @param order_no true string 订单号
		 * @param pay_type true int 支付方式： 付款方式 0 = 余额支付 1 = 支付宝 2 = 微信支付 3 = 智慧支付 4 = 上门刷卡（保留，站位） 6 = 现金支付 7 = 平台已支付
		 * @param pay_order_type int  订单支付类型 0 = 订单支付 1= 充值支付 2 = 手机话费类充值 3 = 订单补差价
		 * @return  OrderViewVo
		 */
		@RequestMapping(value = "post_pay_ext", method = RequestMethod.POST)
		public AppResultData<Object> postPayExt(
				@RequestParam("user_id") Long userId, 
				@RequestParam("order_no") String orderNo, 
				@RequestParam("order_pay_ext") BigDecimal orderPayExt, 
				@RequestParam("order_pay_type") Short orderPayType,
				@RequestParam(value = "share_user_id", required = false, defaultValue="0") Integer shareUserId) {

			AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");

			Users u = userService.selectByPrimaryKey(userId);

			// 判断是否为注册用户，非注册用户返回 999
			if (u == null) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg(ConstantMsg.USER_NOT_EXIST_MG);
				return result;
			}		
			
			Orders order = ordersService.selectByOrderNo(orderNo);
			if (order == null){
				return result;
			}
			
			Long orderId = order.getId();
			
			
			
			if(order.getOrderStatus() <= Constants.ORDER_HOUR_STATUS_1 || order.getOrderStatus() > Constants.ORDER_HOUR_STATUS_5){
				result.setStatus(Constants.ERROR_999);
				result.setMsg("订单当前状态不能进行补差价");
				return result;
			}
			
			OrderPrices orderPrice = orderPricesService.selectByOrderId(orderId);
			
			if (orderPrice != null && orderPrice.getPayType().equals(Constants.PAY_TYPE_6)) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg("现金支付不能补差价.");
				return result;
			}

			if (orderPayExt.compareTo(new BigDecimal(0)) == 0) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg("金额不能为0");
				return result;
			}
			
			if (orderPayType.equals(Constants.PAY_TYPE_0)) {
				//1.先判断用户余额是否够支付
				if(u.getRestMoney().compareTo(orderPayExt) < 0) {
					result.setStatus(Constants.ERROR_999);
					result.setMsg(ConstantMsg.ERROR_999_MSG_5);
					return result;
				}
			}
			
			OrderPriceExt orderPriceExt = orderPriceExtService.initOrderPriceExt();
			orderPriceExt.setUserId(order.getUserId());
			orderPriceExt.setMobile(order.getMobile());
			orderPriceExt.setOrderId(order.getId());
			orderPriceExt.setOrderNo(order.getOrderNo());
			
			//生成唯一的补差价订单号
			String orderNoExt = String.valueOf(OrderNoUtil.genOrderNo());
			orderPriceExt.setOrderNoExt(orderNoExt);
			orderPriceExt.setOrderPay(orderPayExt);
			orderPriceExt.setPayType(orderPayType);
			
			orderPriceExtService.insert(orderPriceExt);

			//如果是余额支付或者需支付金额为0 
			if (orderPayType.equals(Constants.PAY_TYPE_0)) {
				
				//1. 用户余额扣除
				u.setRestMoney(u.getRestMoney().subtract(orderPayExt));
				u.setUpdateTime(TimeStampUtil.getNowSecond());
				userService.updateByPrimaryKeySelective(u);
				
				//2记录用户消费明细
				userDetailPayService.addUserDetailPayForOrderPayExt(u, order, orderPriceExt, "", "", "");
				
				//更新订单差价为已支付
				orderPriceExt.setOrderStatus(2);
				orderPriceExt.setUpdateTime(TimeStampUtil.getNowSecond());
				orderPriceExtService.updateByPrimaryKey(orderPriceExt);
				
				//更新通知服务人员.
				orderPayService.orderPaySuccessToDoOrderPayExt(order, orderPriceExt);
				
				List<OrderShare> orderShareList = ordershareService.selectByShareId(shareUserId);
				OrderShare os = ordershareService.selectByShareIdAndUserId(shareUserId, userId.intValue());
				if(orderShareList!=null && orderShareList.size()>0 && os==null){
					OrderShare orderShare = orderShareList.get(0);
					userCouponsService.shareSuccessSendCoupons(orderShare,order.getUserId());
					orderShare.setUserId(userId.intValue());
					orderShare.setOrderNo(orderNo);
					orderShare.setOrderId(orderId.intValue());
					ordershareService.updateByPrimaryKeySelective(orderShare);
				}
			}
			
			//补差价日志
			OrderLog orderLog = orderLogService.initOrderLog(order);
			orderLog.setUserType((short)0);
			orderLog.setUserId(u.getId());
			orderLog.setUserName(u.getMobile());
			orderLog.setAction(Constants.ORDER_ACTION_PAY_EXT+orderPayExt+"元");
			orderLogService.insert(orderLog);
			
			OrderPriceExtVo vo = new OrderPriceExtVo();
			BeanUtilsExp.copyPropertiesIgnoreNull(order, vo);
			vo.setId(orderPriceExt.getId());
			vo.setOrderNoExt(orderPriceExt.getOrderNoExt());
			vo.setPayType(orderPriceExt.getPayType());
			vo.setOrderPayStatus(orderPriceExt.getOrderStatus());
			vo.setOrderPay(orderPriceExt.getOrderPay());
			vo.setRemarks(orderPriceExt.getRemarks());
			
			result.setData(vo);
			
			
			return result;
		}
		
		@RequestMapping(value = "/post_pay_period_order.json", method = RequestMethod.POST)
		public AppResultData<Object> postPayPeriodOrder(
				@RequestParam("user_id") Long userId, 
				@RequestParam("order_no") String orderNo, 
				@RequestParam("pay_type") Short payType,
				@RequestParam(value = "user_coupon_id", required = false, defaultValue="0") Long userCouponId,
				@RequestParam(value = "coupon_id", required = false, defaultValue="0") Long couponId,
				@RequestParam(value = "share_user_id", required = false, defaultValue="0") Integer shareUserId) {

			AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");

			Users u = userService.selectByPrimaryKey(userId);

			// 判断是否为注册用户，非注册用户返回 999
			if (u == null) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg(ConstantMsg.USER_NOT_EXIST_MG);
				return result;
			}		
			
			PeriodOrder periodOrder = periodOrderService.selectByOrderNo(orderNo);
			if (periodOrder == null){
				return result;
			}
			//更新支付方式
			periodOrder.setPayType(payType.intValue());
			periodOrderService.updateByPrimaryKeySelective(periodOrder);
			
			if(periodOrder.getOrderStatus().equals(Constants.ORDER_HOUR_STATUS_2)){
				result.setStatus(Constants.ERROR_999);
				result.setMsg(ConstantMsg.HAVE_PAY);
				return result;
			}

			OrderPrices orderPrice = orderPricesService.selectByOrderNo(orderNo);
			
			if (orderPrice == null) {
				result.setStatus(Constants.ERROR_999);
				result.setMsg("订单不存在");
				return result;			
			}
			
			//此时 orderPay 和 orderMoney 值是相等的
			BigDecimal orderPay = orderPrice.getOrderMoney();
			BigDecimal orderMoney = orderPrice.getOrderMoney();
			BigDecimal orderOriginPay = orderPrice.getOrderOriginPrice();
			
			//只有余额支付才能使用会员价,并且不是后台下单的订单
			if (periodOrder.getOrderFrom() == 1 ) {
				orderPay = orderOriginPay;
				orderMoney = orderOriginPay;
				if (payType.equals(Constants.PAY_TYPE_0) && u.getIsVip() == 1) {
					orderPay = orderPrice.getOrderPrimePrice();
				}
			}
			
			if (payType.equals(Constants.PAY_TYPE_0)) {
				//1.先判断用户余额是否够支付
				if(u.getRestMoney().compareTo(orderPay) < 0) {
					result.setStatus(Constants.ERROR_999);
					result.setMsg(ConstantMsg.ERROR_999_MSG_5);
					return result;
				}
			}
			
			orderPrice.setOrderMoney(orderMoney);
			orderPrice.setOrderPay(orderPay);
			orderPrice.setPayType(payType);
			orderPrice.setUpdateTime(TimeStampUtil.getNowSecond());
			orderPrice.setCouponId(userCouponId);
			orderPricesService.updateByPrimaryKey(orderPrice);
			
			/*
			 * 	 对于 现金支付。  orderPayType == 6 ，不进行扣款操作。只更改 订单状态 
			 */
			//如果是余额支付或者需支付金额为0 
			if (payType.equals(Constants.PAY_TYPE_0) || 
					payType.equals(Constants.PAY_TYPE_6) ||
					payType.equals(Constants.PAY_TYPE_7) ||
					orderPay.compareTo(new BigDecimal(0)) == 0) {
				// 1. 扣除用户余额.
				// 2. 用户账号明细增加.
				// 3. 订单状态变为已支付.
				// 4. 订单日志
				if (payType.equals(Constants.PAY_TYPE_0)) {
					u.setRestMoney(u.getRestMoney().subtract(orderPay));
					u.setUpdateTime(TimeStampUtil.getNowSecond());
					userService.updateByPrimaryKeySelective(u);
					
					List<OrderShare> orderShareList = ordershareService.selectByShareId(shareUserId);
					OrderShare os = ordershareService.selectByShareIdAndUserId(shareUserId, userId.intValue());
					if(orderShareList!=null && orderShareList.size()>0 && os==null){
						OrderShare orderShare = orderShareList.get(0);
						userCouponsService.shareSuccessSendCoupons(orderShare,periodOrder.getUserId().longValue());
						orderShare.setUserId(userId.intValue());
						orderShare.setOrderNo(orderNo);
						orderShare.setOrderId(periodOrder.getId().intValue());
						ordershareService.updateByPrimaryKeySelective(orderShare);
					}
				}
				
				periodOrder.setOrderStatus(2);//已支付
				periodOrder.setUpdateTime(TimeStampUtil.getNowSecond());
				periodOrderService.updateByPrimaryKeySelective(periodOrder);
				
				//记录订单日志.
				OrderLog orderLog = orderLogService.initOrderLog(periodOrder);
				orderLog.setAction(Constants.PERIOD_ORDER_ACTION_PAY);
				orderLog.setUserId(u.getId());
				orderLog.setUserName(u.getMobile());
				orderLog.setUserType((short)0);
				orderLogService.insert(orderLog);
				
				//记录用户消费明细
				userDetailPayService.addUserDetailPayForOrder(u, periodOrder, orderPrice, "", "", "");
				
				//服务类型名称
				String[] paySuccessForUser = new String[] {DateUtil.getNow(DateUtil.DEFAULT_FULL_PATTERN),"定制服务"};
				//订单支付成功后
				SmsUtil.SendSms(u.getMobile(),  Constants.PAY_SUCCESS_ORDER_SMS, paySuccessForUser);
			}
			
			result.setData(periodOrder);
			
			return result;
		}
}
