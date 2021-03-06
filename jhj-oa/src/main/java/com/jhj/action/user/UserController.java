package com.jhj.action.user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jhj.action.BaseController;
import com.jhj.common.ConstantMsg;
import com.jhj.common.ConstantOa;
import com.jhj.common.Constants;
import com.jhj.oa.auth.AccountAuth;
import com.jhj.oa.auth.AuthHelper;
import com.jhj.oa.auth.AuthPassport;
import com.jhj.po.model.bs.Orgs;
import com.jhj.po.model.dict.DictCardType;
import com.jhj.po.model.order.OrderCards;
import com.jhj.po.model.user.FinanceRecharge;
import com.jhj.po.model.user.UserCoupons;
import com.jhj.po.model.user.UserDetailPay;
import com.jhj.po.model.user.UserSmsToken;
import com.jhj.po.model.user.Users;
import com.jhj.service.bs.OrgsService;
import com.jhj.service.dict.CardTypeService;
import com.jhj.service.jhjSetting.JhjSettingService;
import com.jhj.service.order.OrderCardsService;
import com.jhj.service.order.OrdersService;
import com.jhj.service.order.poi.PoiExportExcelService;
import com.jhj.service.users.FinanceRechargeService;
import com.jhj.service.users.UserCouponsService;
import com.jhj.service.users.UserDetailPayService;
import com.jhj.service.users.UserSmsTokenService;
import com.jhj.service.users.UsersService;
import com.jhj.vo.finance.FinanceSearchVo;
import com.jhj.vo.order.OrderCardsVo;
import com.jhj.vo.org.OrgSearchVo;
import com.jhj.vo.user.FinanceRechargeVo;
import com.jhj.vo.user.UserChargeVo;
import com.jhj.vo.user.UserCouponsVo;
import com.jhj.vo.user.UserDetailSearchVo;
import com.jhj.vo.user.UserSearchVo;
import com.jhj.vo.user.UsersSmsTokenVo;
import com.jhj.vo.user.UsersVo;
import com.meijia.utils.DateUtil;
import com.meijia.utils.MathBigDecimalUtil;
import com.meijia.utils.RandomUtil;
import com.meijia.utils.SmsUtil;
import com.meijia.utils.StringUtil;
import com.meijia.utils.TimeStampUtil;
import com.meijia.utils.poi.POIUtils;
import com.meijia.utils.vo.AppResultData;
import com.sun.tools.internal.ws.processor.model.Request;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private UserCouponsService usersCounpsService;

	@Autowired
	private UserSmsTokenService userSmsTokenService;

	@Autowired
	private UserDetailPayService userDetailPayService;

	@Autowired
	private OrderCardsService orderCardsService;

	@Autowired
	private CardTypeService cardTypeService;

	@Autowired
	private FinanceRechargeService financeService;

	@Autowired
	private JhjSettingService settingService;

	@Autowired
	private OrgsService orgService;

	@Autowired
	private OrdersService orderService;
	
	@Autowired
	private PoiExportExcelService poiExcelService;

	@AuthPassport
	@RequestMapping(value = "/update_name", method = { RequestMethod.POST })
	public AppResultData<Object> detail(@RequestParam("pk") Long userId, @RequestParam("value") String userName) {

		AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");

		if (userId == null || userName == null) {
			return result;
		}
		Users user = usersService.selectByPrimaryKey(userId);
		if (user == null) {
			return result;
		}

		user.setName(userName);
		user.setUpdateTime(TimeStampUtil.getNow() / 1000);

		usersService.updateByPrimaryKeySelective(user);
		result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, user);
		return result;
	}

	@AuthPassport
	@RequestMapping(value = "/user-list", method = { RequestMethod.GET })
	public String userList(HttpServletRequest request, Model model, @ModelAttribute("userListSearchVoModel") UserSearchVo searchVo) throws ParseException {

		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		searchVo.setOrgIds(getCouldId(request));
		/*searchVo.setIsVip((short) 1);
		searchVo.setOrderNum("2");*/
		PageInfo<Users> result = usersService.selectByListPage(searchVo, pageNo, pageSize);
		
		List list = result.getList();
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				Users users = (Users)list.get(i);
				UsersVo usersVo = usersService.transVo(users);
				list.set(i,usersVo);
			}
		}
		
		model.addAttribute("userList", result);
		model.addAttribute("userListSearchVoModel", searchVo);

		return "user/userList";
	}

	/**
	 * 用户查看其优惠券列表
	 * 
	 * @param model
	 * @param userId
	 * @return
	 */
	@AuthPassport
	@RequestMapping(value = "/coupons-list", method = { RequestMethod.GET })
	public String couponsList(Model model, @RequestParam("user_id") Long userId) {

		List<UserCoupons> userCouponsList = usersCounpsService.selectAllByUserId(userId);

		List<UserCouponsVo> userCouponsVos = new ArrayList<UserCouponsVo>();
		if (userCouponsList != null) {
			for (Iterator<UserCoupons> iterator = userCouponsList.iterator(); iterator.hasNext();) {
				UserCoupons userCoupons = iterator.next();
				UserCouponsVo vo = usersCounpsService.getUsersCounps(userCoupons);
				userCouponsVos.add(vo);
			}
		}
		model.addAttribute("userCouponsVos", userCouponsVos);

		return "user/userCouponsList";
	}

	/**
	 * 
	 * @Title: payDetailList
	 * @Description: 消费明细列表
	 * 
	 */
	@AuthPassport
	@RequestMapping(value = "/user-pay-detail", method = { RequestMethod.GET })
	public String payDetailList(HttpServletRequest request, Model model, @ModelAttribute("userPayDetailSearchVoModel") UserDetailSearchVo searchVo)
			throws ParseException {
		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

		model.addAttribute("searchModel", searchVo);
		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		// 转换为数据库 参数字段
		String startTimeStr = searchVo.getStartTimeStr();
		if (!StringUtil.isEmpty(startTimeStr)) {
			searchVo.setStartTime(DateUtil.getUnixTimeStamp(DateUtil.getBeginOfDay(startTimeStr)));
		}

		String endTimeStr = searchVo.getEndTimeStr();
		if (!StringUtil.isEmpty(endTimeStr)) {
			searchVo.setEndTime(DateUtil.getUnixTimeStamp(DateUtil.getEndOfDay(endTimeStr)));
		}

		// 得到 当前登录 的 门店id，并作为搜索条件
		Long sessionOrgId = AuthHelper.getSessionLoginOrg(request);

		UserSearchVo userSearchVo = new UserSearchVo();

		List<Long> cloudIdList = new ArrayList<Long>();

		if (sessionOrgId > 0L) {

			OrgSearchVo searchVo1 = new OrgSearchVo();
			searchVo1.setParentId(sessionOrgId);
			searchVo1.setOrgStatus((short) 1);
			List<Orgs> cloudList = orgService.selectBySearchVo(searchVo1);

			for (Orgs orgs : cloudList) {
				cloudIdList.add(orgs.getOrgId());
			}

		} else {
			cloudIdList.add(0L);
		}

		userSearchVo.setOrgIds(cloudIdList);

		/*
		 * 根据 在 本门店 下过 订单的 用户 id 集合（先分页） ，得到对应的 消费明细
		 */

		// 在店长登录门店 下过单的 用户集合
			
		PageHelper.startPage(pageNo, pageSize);
		searchVo.setIsVip((short)1);
		List<UserDetailPay> list = userDetailPayService.selectBySearchVo(searchVo);
		
		PageInfo<UserDetailPay> result = new PageInfo<UserDetailPay>(list);
		Map<String, BigDecimal> totolMoeny = userDetailPayService.totolMoeny(searchVo);
		model.addAllAttributes(totolMoeny);

		model.addAttribute("userPayDetailSearchVoModel", searchVo);
		model.addAttribute("userPayDetailList", result);
		model.addAttribute("listUrl", "user-pay-detail");

		return "user/userDetailPayList";
	}

	//普通用户消费明细列表
	@AuthPassport
	@RequestMapping(value = "/not-vip-user-pay-detail", method = { RequestMethod.GET })
	public String noVipUserPayDetail(HttpServletRequest request, Model model, @ModelAttribute("userPayDetailSearchVoModel") UserDetailSearchVo searchVo)
			throws ParseException {
		
		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

		model.addAttribute("searchModel", searchVo);
		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		// 转换为数据库 参数字段
		String startTimeStr = searchVo.getStartTimeStr();
		if (!StringUtil.isEmpty(startTimeStr)) {
			searchVo.setStartTime(DateUtil.getUnixTimeStamp(DateUtil.getBeginOfDay(startTimeStr)));
		}

		String endTimeStr = searchVo.getEndTimeStr();
		if (!StringUtil.isEmpty(endTimeStr)) {
			searchVo.setEndTime(DateUtil.getUnixTimeStamp(DateUtil.getEndOfDay(endTimeStr)));
		}

		// 得到 当前登录 的 门店id，并作为搜索条件
		Long sessionOrgId = AuthHelper.getSessionLoginOrg(request);

		List<Long> cloudIdList = new ArrayList<Long>();

		if (sessionOrgId > 0L) {

			OrgSearchVo searchVo1 = new OrgSearchVo();
			searchVo1.setParentId(sessionOrgId);
			searchVo1.setOrgStatus((short) 1);
			List<Orgs> cloudList = orgService.selectBySearchVo(searchVo1);

			for (Orgs orgs : cloudList) {
				cloudIdList.add(orgs.getOrgId());
			}

		} else {
			cloudIdList.add(0L);
		}

//		userSearchVo.setOrgIds(cloudIdList);
//
//		/*
//		 * 根据 在 本门店 下过 订单的 用户 id 集合（先分页） ，得到对应的 消费明细
//		 */
//

		searchVo.setIsVip((short)0);
		PageHelper.startPage(pageNo, pageSize);
		List<UserDetailPay> list = userDetailPayService.selectBySearchVo(searchVo);
		
		PageInfo<UserDetailPay> result = new PageInfo<UserDetailPay>(list);
		
		Map<String, BigDecimal> totolMoeny = userDetailPayService.totolMoeny(searchVo);
		model.addAllAttributes(totolMoeny);

		model.addAttribute("userPayDetailSearchVoModel", searchVo);
		model.addAttribute("userPayDetailList", result);
		model.addAttribute("listUrl", "not-vip-user-pay-detail");

		return "user/userDetailPayList";
		
	}
	
	
	@RequestMapping(value = "/token-list", method = { RequestMethod.GET })
	public String userTokenList(HttpServletRequest request, Model model, UsersSmsTokenVo usersSmsTokenVo) {

		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		// 分页
		PageHelper.startPage(pageNo, pageSize);

		List<UserSmsToken> lists = userSmsTokenService.selectByListPage(usersSmsTokenVo, pageNo, pageSize);

		PageInfo<UserSmsToken> result = new PageInfo<UserSmsToken>(lists);
		model.addAttribute("usersSmsTokenVo", usersSmsTokenVo);
		model.addAttribute("userSmsTokenModel", result);
		
		return "user/userTokenList";
	}

	/**
	 * 跳转到>1w充值页面
	 */
	@AuthPassport
	@RequestMapping(value = "/charge-form", method = { RequestMethod.GET })
	public String toChargeForm(Model model, @RequestParam("user_id") Long userId) {

		UserChargeVo userChargeVo = new UserChargeVo();
		// 设置用户Id;充值方式
		userChargeVo.setUserId(userId);
		userChargeVo.setChargeWay((short) 0);// 0=固定充值

		Users users = usersService.selectByPrimaryKey(userId);

		userChargeVo.setUserMobile(users.getMobile());

		/*
		 * 充值时获取 验证码的 手机号
		 */

		// JhjSetting jhjSetting =
		// settingService.selectBySettingType(Constants.OA_CHARGE_SETTING_TYPE);

		userChargeVo.setOwnerMobile("18611289885");

		model.addAttribute("userChargeVo", userChargeVo);
		model.addAttribute("selectDataSource", usersService.selectDictCardDataSource());
		model.addAttribute("chargeWayDataSource", usersService.getChargeWayDataSource());

		return "user/chargeForm";
	}

	/**********
	 * 2016年3月25日18:28:50
	 * 
	 * 会员充值 相关
	 * 
	 */

	/*
	 * 运营平台-- 会员充值列表--充值--获取验证码
	 */
	// @AuthPassport
	@RequestMapping(value = "get_user_sms_token.json", method = RequestMethod.GET)
	public AppResultData<String> getSmsToken(@RequestParam("userId") Long userId, @RequestParam("userMobile") String userMobile,
			@RequestParam("smsType") int sms_type) {

		AppResultData<String> result = new AppResultData<String>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");
		/*
		 * 校验 手机号 和 用户(主管手机号。接收验证码) 是否匹配
		 */

		// JhjSetting setting =
		// settingService.selectBySettingType(Constants.OA_CHARGE_SETTING_TYPE);
		//
		// String value = setting.getSettingValue();

		String value = "18611289885";
		if (!userMobile.equals("18611289885")) {

			result.setStatus(Constants.ERROR_999);
			result.setMsg("请联系主管获得验证码");

			return result;
		}

		// 1.调用RandomUtil.randomNumber()产生六位验证码
		String code = RandomUtil.randomNumber(4);

		String[] content = new String[] { code, Constants.GET_CODE_MAX_VALID };
		// 2.短信平台发送给用户，并返回相关信息（短信平台是30分钟有效）
		HashMap<String, String> sendSmsResult = SmsUtil.SendSms(value, Constants.GET_USER_VERIFY_ID, content);
		UserSmsToken record = userSmsTokenService.initUserSmsToken(value, sms_type, code, sendSmsResult);
		// 3.操作user_sms_token，保存验证码信息
		userSmsTokenService.insert(record);

		result.setStatus(Constants.SUCCESS_0);
		result.setMsg("发送成功");

		return result;
	}

	/**
	 * 
	 * @Title: doChargeForm
	 * @Description: 运营平台-- 提交 会员充值 结果
	 * @param userId
	 *            用户id
	 * @param chargeWay
	 *            充值方式
	 * @param chargeMoney
	 *            充值金额
	 * 
	 * 
	 * @param userCode
	 *            验证码
	 */
	// @AuthPassport
	@RequestMapping(value = "/charge-form.json", method = { RequestMethod.POST })
	public AppResultData<Object> doChargeForm(HttpServletRequest request, @RequestParam("userId") Long userId, @RequestParam("userMobile") String userMobile,
			@RequestParam("chargeWay") Short chargeWay, @RequestParam("chargeMoney") Long chargeMoney, @RequestParam("userCode") String userCode) {

		AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, "", "");

		// 1. 判断 手机号 和 验证码是否 匹配
		Users user = usersService.selectByPrimaryKey(userId);

		// 最新的 一条 验证码。类型为 3,表示 运营平台--会员充值验证码
		UserSmsToken smsToken = userSmsTokenService.selectByMobileAndType("18611289885", Constants.SMS_TYPE_3);
		if (smsToken == null) {
			result.setStatus(Constants.ERROR_999);
			result.setMsg("验证码错误");
			return result;
		}
		String token = smsToken.getSmsToken();

		if (!token.equals(userCode)) {

			result.setStatus(Constants.ERROR_999);
			result.setMsg("验证码错误");
			return result;
		}

		FinanceRecharge finace = financeService.initFinace();

		finace.setUserId(userId);
		// 用户余额
		BigDecimal restMoney = user.getRestMoney();

		finace.setRestMoneyBefore(restMoney);

		// 充值金额
		BigDecimal cardValue = new BigDecimal(0);

		// 2. 金额相关
		if (chargeWay == (short) 0) {
			/*
			 * 如果是 固定金额充值
			 * 
			 * chargeMoney 表示在 dict_card_type 表中，该记录的 主键id
			 */
			DictCardType cardType = cardTypeService.selectByPrimaryKey(chargeMoney);

			cardValue = cardType.getCardValue();

		} else {

			// 如果是任意金额充值。该值就是 充值的具体数字, 保留两位小数
			cardValue = MathBigDecimalUtil.round(new BigDecimal(chargeMoney), 2);
		}

		// 设置充值金额
		finace.setRechargeValue(cardValue);

		// 充值后 余额
		BigDecimal afterMoney = MathBigDecimalUtil.add(restMoney, cardValue);

		finace.setRestMoneyAfter(afterMoney);

		/*
		 * 3. 充值操作的 人员
		 */
		AccountAuth auth = AuthHelper.getSessionAccountAuth(request);

		// 当前登录 用户的 id
		Long id = auth.getId();

		finace.setAdminId(id);

		finace.setAdminName(auth.getUsername());

		// TODO 登录角色 的 手机号， 无法获得
		finace.setAdminMobile("");

		/*
		 * 4. 审批手机号、验证码
		 */
		finace.setApproveMobile(userMobile);
		finace.setApproveToken(userCode);

		financeService.insertSelective(finace);

		// 此时 更新 用户 余额

		user.setRestMoney(afterMoney);
		usersService.updateByPrimaryKeySelective(user);

		/*
		 * 记录 用户 消费明细
		 */

		UserDetailPay userDetailPay = userDetailPayService.initUserDetailPay();

		userDetailPay.setUserId(user.getId());
		userDetailPay.setMobile(user.getMobile());

		userDetailPay.setOrderType(Constants.PAY_ORDER_TYPE_1);
		/*
		 * 运营平台的 会员充值， 是由 财务操作。
		 * 
		 * 逻辑上已经保证了。充值后，用户也已经完成了 支付。
		 * 
		 * 所以总金额和 支付金额 相等即可。
		 */
		userDetailPay.setOrderMoney(cardValue);
		userDetailPay.setOrderPay(cardValue);

		userDetailPay.setAddTime(TimeStampUtil.getNowSecond());

		userDetailPayService.insert(userDetailPay);

		// TODO 5. 充值成功后,将该验证码。设为无效
		// userSmsTokenService.deleteByPrimaryKey(smsToken.getId());

		result.setMsg("充值成功");

		return result;
	}

	/*
	 * 充值记录
	 */
	@AuthPassport
	@RequestMapping(value = "finace_recharge_list", method = RequestMethod.GET)
	public String getAllChargeList(Model model, HttpServletRequest request, FinanceSearchVo searchVo) {

		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		PageHelper.startPage(pageNo, pageSize);

		// 过滤 显示 当前 登录 用户
		AccountAuth auth = AuthHelper.getSessionAccountAuth(request);

		Long id = auth.getId();

		searchVo.setAdminId(id);

		List<FinanceRecharge> list = financeService.selectByListPage(searchVo);

		FinanceRecharge finance = null;
		for (int i = 0; i < list.size(); i++) {

			finance = list.get(i);

			FinanceRechargeVo financeVo = financeService.transToFinanceVo(finance);

			list.set(i, financeVo);
		}

		PageInfo<FinanceRecharge> result = new PageInfo<FinanceRecharge>(list);

		model.addAttribute("financeListModel", result);

		return "user/financeList";
	}

	/*
	 * 发起充值操作的 列表页
	 */

	// @AuthPassport
	@RequestMapping(value = "/finance_user-list", method = { RequestMethod.GET })
	public String getUserList(HttpServletRequest request, Model model, UserSearchVo searchVo) {
		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());

		model.addAttribute("searchModel", searchVo);
		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);

		PageInfo<Users> result = usersService.selectByListPage(searchVo, pageNo, pageSize);
		model.addAttribute("userList", result);

		return "user/financeUserList";
	}

	/*
	 * 首页新增用户显示信息
	 */
	@RequestMapping(value = "/home-user-list", method = RequestMethod.GET)
	public String showNewUser(Model model, HttpServletRequest request) throws ParseException {
		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);
		UserSearchVo vo = new UserSearchVo();

		String startTimeStr = DateUtil.getBeginOfDay();
		String endTimeStr = DateUtil.getEndOfDay();
		Long startTime = TimeStampUtil.getMillisOfDayFull(startTimeStr) / 1000;
		Long endTime = TimeStampUtil.getMillisOfDayFull(endTimeStr) / 1000;
		vo.setStartTime(startTime);
		vo.setEndTime(endTime);
		PageInfo<Users> result = usersService.selectByListPage(vo, pageNo, pageSize);
		model.addAttribute("userList", result);
		model.addAttribute("userListSearchVoModel", vo);
		return "user/userList";
	}

	// 根据当前登录的用户获取云店id
	public List<Long> getCouldId(HttpServletRequest request) {
		Long sessionOrgId = AuthHelper.getSessionLoginOrg(request);

		List<Long> cloudIdList = new ArrayList<Long>();

		if (sessionOrgId > 0L) {

			OrgSearchVo searchVo1 = new OrgSearchVo();
			searchVo1.setParentId(sessionOrgId);
			searchVo1.setOrgStatus((short) 1);
			List<Orgs> cloudList = orgService.selectBySearchVo(searchVo1);

			for (Orgs orgs : cloudList) {
				cloudIdList.add(orgs.getOrgId());
			}
		} else {
			cloudIdList.add(0L);
		}

		return cloudIdList;
	}
	
	@RequestMapping(value="/getUser",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> selectByMobile(@RequestParam("mobile") String mobile){
		Map<String,Object> map=new HashMap<String,Object>();
		UserSearchVo searchVo=new UserSearchVo();
		searchVo.setMobile(mobile);
		List<Users> users = usersService.selectBySearchVo(searchVo);
		if(users!=null && users.size()>0){
			map.put("data", users.get(0));
		}else{
			map.put("data", false);
		}
		return map;
	}
	
	
	//用户充值列表
	@AuthPassport
	@RequestMapping(value="/user-charge-list",method=RequestMethod.GET)
	public String userCharge(Model model,HttpServletRequest request,OrderCardsVo orderCardsVo) throws UnsupportedEncodingException{
		
		int pageNo = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_NO_NAME, ConstantOa.DEFAULT_PAGE_NO);
		int pageSize = ServletRequestUtils.getIntParameter(request, ConstantOa.PAGE_SIZE_NAME, ConstantOa.DEFAULT_PAGE_SIZE);
		Long sessionParentId = AuthHelper.getSessionLoginOrg(request);
		UserSearchVo userVo=new UserSearchVo();
		userVo.setIsVip((short) 1);
		String mobile = orderCardsVo.getMobile();
		if(mobile!=null && !mobile.equals("")){
			userVo.setMobile(mobile);
		}
		List<Users> userList = usersService.selectBySearchVo(userVo);
		if(userList!=null && userList.size()>0){
			List<Long> userIds=new ArrayList<Long>();
			for(int i=0;i<userList.size();i++){
				Users users = userList.get(i);
				userIds.add(users.getId());
			}
			orderCardsVo.setUserIds(userIds);
		}
		String staffName = orderCardsVo.getStaffName();
		if(staffName!=null && !staffName.equals("")){
			orderCardsVo.setStaffName(new String(staffName.getBytes("ISO-8859-1") ,"UTF-8"));
		}
		String addStartTimeStr = request.getParameter("addStartTimeStr");
		if(addStartTimeStr!=null && !addStartTimeStr.equals("")){
			orderCardsVo.setAddStartTime(TimeStampUtil.getMillisOfDayFull(addStartTimeStr+" 00:00:00")/1000);
		}
		String addEndTimeStr = request.getParameter("addEndTimeStr");
		if(addEndTimeStr!=null && !addEndTimeStr.equals("")){
			orderCardsVo.setAddEndTime(TimeStampUtil.getMillisOfDayFull(addEndTimeStr+" 23:59:59")/1000);
		}
		orderCardsVo.setOrderStatus((short)1);
		PageInfo page = orderCardsService.selectByListPage(orderCardsVo,pageNo, pageSize);
		List<OrderCards> orderCardsList = page.getList();
		for(int i=0;i<orderCardsList.size();i++){
			OrderCards orderCards = orderCardsList.get(i);
			OrderCardsVo orderCard = orderCardsService.transVo(orderCards);
			orderCardsList.set(i, orderCard);
		}
		page=new PageInfo(orderCardsList);
		
		//用户充值金额统计
		Map<String, Double> chargeMoney = orderCardsService.countTotal(orderCardsVo);
		model.addAllAttributes(chargeMoney);
		
		//用户余额统计
		Double countUserRestMoney = usersService.countUserRestMoney();
		model.addAttribute("userRestMoney", countUserRestMoney);
		
		model.addAttribute("pageList", page);
		model.addAttribute("loginOrgId", sessionParentId);
		model.addAttribute("orderCardsVo", orderCardsVo);
		return "user/chargeList";
	}
	
	@RequestMapping(value = "/updateUserRestMoney", method = RequestMethod.GET)
	public String updateUserRestMoney(Model model,@RequestParam("userId") Long userId){
		Users user = usersService.selectByPrimaryKey(userId);
		model.addAttribute("user", user);
		return "user/userRestMoney";
	}
	
	@RequestMapping(value = "/updateUserRestMoney", method = RequestMethod.POST)
	public String updateUserRestMoney(@RequestParam("id") Long userId,
			@RequestParam("restMoney") Double restMoney,HttpServletRequest request){
		
		Users user = usersService.selectByPrimaryKey(userId);
		BigDecimal restMoney1 = user.getRestMoney();
		BigDecimal money = new BigDecimal(restMoney);
		user.setRestMoney(money);
		usersService.updateByPrimaryKeySelective(user);
		
		AccountAuth accountAuth = AuthHelper.getSessionAccountAuth(request);
		String username = accountAuth.getUsername();
		
		UserDetailPay initUserDetailPay = userDetailPayService.initUserDetailPay();
		initUserDetailPay.setUserId(userId);
		initUserDetailPay.setMobile(user.getMobile());
		initUserDetailPay.setOrderType((short)100);
		initUserDetailPay.setRestMoney(money);
		initUserDetailPay.setRemarks(username+"将用户"+user.getMobile()+"的余额从"+restMoney1+"修改成"+money);
		userDetailPayService.insert(initUserDetailPay);
		
		return "redirect:user-list";
	}
	@RequestMapping(value = "/export",method = RequestMethod.GET)
	public void chargeExport(HttpServletRequest request,HttpServletResponse response, OrderCardsVo orderCardsVo) throws IOException{
		
		UserSearchVo userVo=new UserSearchVo();
		userVo.setIsVip((short) 1);
		String mobile = orderCardsVo.getMobile();
		if(mobile!=null && !mobile.equals("")){
			userVo.setMobile(mobile);
		}
		List<Users> userList = usersService.selectBySearchVo(userVo);
		if(userList!=null && userList.size()>0){
			List<Long> userIds=new ArrayList<Long>();
			for(int i=0;i<userList.size();i++){
				Users users = userList.get(i);
				userIds.add(users.getId());
			}
			orderCardsVo.setUserIds(userIds);
		}
		String staffName = orderCardsVo.getStaffName();
		if(staffName!=null && !staffName.equals("")){
			orderCardsVo.setStaffName(new String(staffName.getBytes("ISO-8859-1") ,"UTF-8"));
		}
		String addStartTimeStr = request.getParameter("addStartTimeStr");
		if(addStartTimeStr!=null && !addStartTimeStr.equals("")){
			orderCardsVo.setAddStartTime(TimeStampUtil.getMillisOfDayFull(addStartTimeStr+" 00:00:00")/1000);
		}
		String addEndTimeStr = request.getParameter("addEndTimeStr");
		if(addEndTimeStr!=null && !addEndTimeStr.equals("")){
			orderCardsVo.setAddEndTime(TimeStampUtil.getMillisOfDayFull(addEndTimeStr+" 23:59:59")/1000);
		}
		orderCardsVo.setOrderStatus((short)1);
		List<OrderCards> orderCardsList = orderCardsService.selectBySearchVo(orderCardsVo);
		List<OrderCardsVo> orderCardsVoList = new ArrayList<>();
		for(int i=0;i<orderCardsList.size();i++){
			OrderCards orderCards = orderCardsList.get(i);
			OrderCardsVo orderCard = orderCardsService.transVo(orderCards);
			orderCardsVoList.add(orderCard);
		}
		
		String fileName = "充值列表";
		List<Map<String, Object>> list = poiExcelService.chargeExport(orderCardsVoList);
		
		String[] columnKey = {"mobile","cardMoney","staffCode","staffName","addTime"};
		String[] columnNames = {"会员手机号","充值金额","员工编号","员工名称","充值时间"};
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			POIUtils.createWorkBook(list, columnKey, columnNames).write(os);
			byte[] byteArray = os.toByteArray();
			
			InputStream in = new ByteArrayInputStream(byteArray);
			response.reset();
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
			ServletOutputStream outputStream = response.getOutputStream();
			
			bufferedInputStream = new BufferedInputStream(in);
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bufferedInputStream.read(buff, 0, buff.length))) {
				bufferedOutputStream.write(buff, 0, bytesRead);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (bufferedInputStream != null)
				bufferedInputStream.close();
			if (bufferedOutputStream != null)
				bufferedOutputStream.close();
		}
	}
	
}
