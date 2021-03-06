/**
 * 清除临时会话数据
 */
function removeSessionData() {
	
	//服务类别临时会话数据
	sessionStorage.removeItem("service_type_id");
	
	//时间临时会话数据
	sessionStorage.removeItem("service_date");
	sessionStorage.removeItem("service_date_str");

	//地址临时会话数据
	sessionStorage.removeItem("addr_id");
	sessionStorage.removeItem("addr_name");

	//订单临时会话数据
	sessionStorage.removeItem("order_type");
	sessionStorage.removeItem("order_no");
	sessionStorage.removeItem("order_id");
	sessionStorage.removeItem("order_pay");
	sessionStorage.removeItem("order_money");
	sessionStorage.removeItem("order_origin_pay");
	sessionStorage.removeItem("order_origin_money");
	sessionStorage.removeItem("service_addons");
	sessionStorage.removeItem("service_addons_json");
	sessionStorage.removeItem("total_service_hour");
	sessionStorage.removeItem("total_staff_nums");
	sessionStorage.removeItem("pay_order_type");

	//优惠劵临时会话数据
	sessionStorage.removeItem("user_coupon_id");
	sessionStorage.removeItem("user_coupon_value");
	sessionStorage.removeItem("user_coupon_name");
	
	//服务人员信息
	sessionStorage.removeItem("staff_id");
	sessionStorage.removeItem("staff_names");
	
	//服务人员已有的服务日期
	sessionStorage.removeItem("serDate");
	
	//充值卡ID
	sessionStorage.removeItem("card_id");
}


function getItemAddrId() {
	var selectAddrId = "";
	if (localStorage['default_addr_id'] != undefined && localStorage['default_addr_id'] != null && localStorage['default_addr_id'] != "undefined") {
		selectAddrId = localStorage['default_addr_id'];
	}
	
	if (sessionStorage.getItem('addr_id') != undefined  && sessionStorage.getItem('addr_id') != null && sessionStorage.getItem('addr_id') != "undefined") {
		selectAddrId = sessionStorage.getItem('addr_id');
	}
	
	return selectAddrId;
}

function getItemAddrName() {
	var selectAddrName = "";
	if (localStorage['default_addr_name'] != undefined && localStorage['default_addr_name'] != null && localStorage['default_addr_name'] != "undefined") {
		selectAddrName = localStorage['default_addr_name'];
	}
	
	if (sessionStorage.getItem('addr_name') != undefined  && sessionStorage.getItem('addr_name') != null && sessionStorage.getItem('addr_name') != "undefined") {
		selectAddrName = sessionStorage.getItem('addr_name');
	}
	
	return selectAddrName;
}



/**
 * 推荐服务
 *
 * */

function orderRecomment(serviceTypeId){
	var recoArr=[];

	if(serviceTypeId==undefined || serviceTypeId=='' || serviceTypeId==null) return ;

	//金牌保洁
	var jinpai=[
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"擦玻璃","url":"order/order-deep-intro.html?service_type_id=54","serviceTypeId":"54"}
	];

	//冰箱清洗
	var bingxiang=[
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"}
	];

	//擦玻璃
	var caboli=[
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"}
	];

	//厨卫消毒清洁杀菌
	var chuwei=[
		{"name":"擦玻璃","url":"order/order-deep-intro.html?service_type_id=54","serviceTypeId":"54"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"床铺除螨杀菌","url":"order/order-deep-intro.html?service_type_id=34","serviceTypeId":"34"}
	];

	//床铺除螨杀菌
	var chuangpu=[
		{"name":"擦玻璃","url":"order/order-deep-intro.html?service_type_id=54","serviceTypeId":"54"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"地板保养打蜡","url":"order/order-deep-intro.html?service_type_id=52","serviceTypeId":"52"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"}
	];

	//地板保养打蜡
	var diban=[
		{"name":"擦玻璃","url":"order/order-deep-intro.html?service_type_id=54","serviceTypeId":"54"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"床铺除螨杀菌","url":"","serviceTypeId":"34"}
	];

	//房屋大扫除
	var fangwu=[
		{"name":"擦玻璃","url":"order/order-deep-intro.html?service_type_id=54","serviceTypeId":"54"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"床铺除螨杀菌","url":"order/order-deep-intro.html?service_type_id=34","serviceTypeId":"34"}
	];

	//空调清洗
	var kongtiao=[
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"}
	];

	//洗衣机清洗
	var xiyiji=[
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"},
		{"name":"空调清洗","url":"order/order-deep-intro.html?service_type_id=51","serviceTypeId":"51"}
	];

	//新居开荒
	var xinju=[
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"地板保养打蜡","url":"order/order-deep-intro.html?service_type_id=52","serviceTypeId":"52"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"}
	];

	//油烟机清洗
	var youyanji=[
		{"name":"冰箱清洗","url":"order/order-deep-intro.html?service_type_id=50","serviceTypeId":"50"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"},
		{"name":"金牌保洁","url":"order/order-hour-intro.html?service_type_id=28","serviceTypeId":"28"}
	];

	//家务定制
	var jiawu=[
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"洗衣机清洗","url":"order/order-deep-intro.html?service_type_id=60","serviceTypeId":"60"},
		{"name":"空调清洗","url":"order/order-deep-intro.html?service_type_id=51","serviceTypeId":"51"},
	];

	//孕家洁
	var yunjiajie=[
		{"name":"月子房","url":"order/order-yuezifang-intro.html","serviceTypeId":"62"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"}
	];

	//月子房
	var yuezifang=[
		{"name":"洁宝宝","url":"order/order-cleanbaby-intro.html","serviceTypeId":"64"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"}
	];
	
	//安居宝
	var anjubao=[
		{"name":"洁宝宝","url":"order/order-cleanbaby-intro.html","serviceTypeId":"64"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"}
	];

	//洁宝宝
	var jiebaobao=[
		{"name":"月子房","url":"order/order-yuezifang-intro.html","serviceTypeId":"62"},
		{"name":"贴心家事","url":"order/order-deep-intro.html?service_type_id=80","serviceTypeId":"80"},
		{"name":"家务定制","url":"order/fiveservice/order-cus-hostwork-month.html?service_type_id=61","serviceTypeId":"61"},
		{"name":"油烟机清洗","url":"order/order-deep-intro.html?service_type_id=36","serviceTypeId":"36"}
	];
	
	recoArr=[
		{"serviceTypeId":"28","list":jinpai},
		{"serviceTypeId":"50","list":bingxiang},
		{"serviceTypeId":"54","list":caboli},
		{"serviceTypeId":"35","list":chuwei},
		{"serviceTypeId":"34","list":chuangpu},
		{"serviceTypeId":"52","list":diban},
		{"serviceTypeId":"53","list":fangwu},
		{"serviceTypeId":"51","list":kongtiao},
		{"serviceTypeId":"60","list":xiyiji},
		{"serviceTypeId":"56","list":xinju},
		{"serviceTypeId":"36","list":youyanji},
		{"serviceTypeId":"61","list":jiawu},
		{"serviceTypeId":"63","list":yunjiajie},
		{"serviceTypeId":"62","list":yuezifang},
		{"serviceTypeId":"65","list":anjubao},
		{"serviceTypeId":"29","list":jinpai},
		{"serviceTypeId":"64","list":jiebaobao},
		{"serviceTypeId":"67","list":jinpai},
		{"serviceTypeId":"68","list":jinpai},
		{"serviceTypeId":"69","list":jinpai},
		{"serviceTypeId":"70","list":jinpai},
		{"serviceTypeId":"71","list":youyanji}
	]

	for(var i=0;i<recoArr.length;i++){
		var reco=recoArr[i];
		var _serviceTypeId = reco.serviceTypeId;
		if(_serviceTypeId==serviceTypeId){
			return reco.list;
		}
	}
}