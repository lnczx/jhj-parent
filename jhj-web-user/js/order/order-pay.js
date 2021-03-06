myApp.onPageInit('order-pay', function(page) {
		
	var userId = localStorage['user_id'];
	var serviceTypeId = sessionStorage.getItem('service_type_id');
	var orderNo = sessionStorage.getItem('order_no');
	var orderId = sessionStorage.getItem('order_id');
	var orderPay = sessionStorage.getItem('order_pay');
	var orderOriginPay = sessionStorage.getItem('order_origin_pay');
	
	var userCouponId = sessionStorage.getItem('user_coupon_id');
	var userCouponValue = sessionStorage.getItem('user_coupon_value');
	if (userCouponValue == undefined || userCouponValue == "" || userCouponValue == null) {
		userCouponValue = 0;
	}
	
	// payOrderType 订单支付类型 0 = 订单支付 1= 充值支付 2 = 手机话费类充值 3 = 订单补差价
	var payOrderType = sessionStorage.getItem("pay_order_type");
	
	var shareUserId = sessionStorage.getItem("share_user_id");
	
	
	$$("#userId").val(userId);
	$$("#orderNo").val(orderNo);
	$$("#orderId").val(orderId);
	$$("#orderPay").val(orderPay);
	$$("#orderMoneyStrLi").html("￥"+orderPay+"元");
	$$("#orderPayStrLi").html("￥"+orderPay+"元");
	$$("#userCouponId").val(userCouponId);
	$$("#userCouponValue").val(userCouponValue);
	$$("#userCouponValueStr").html(userCouponValue + "元");
		
	$$.ajax({
		type : "GET",
		url : siteAPIPath + "user/get_userinfo.json?user_id="+userId,
		dataType : "json",
		cache : true,
		async : false,
		success : function(data) {
			var restMoney = data.data.rest_money;
			$$("#restMoney").val(restMoney);
			$$("#restMoneyStr").html("余额"+restMoney+"元");
		}
	})
	
	//默认支付类型
	var orderPayType = 0;
	
	//如果为isVip = 1, 则默认为余额支付.否则为微信支付.
	var isVip = localStorage['is_vip'];
	if (isVip == undefined || isVip == "") isVip = 0;
	
	
	var isWx = isWeiXin();
	
	if (isWx) {
		$$("#select-wxpay").css("display", "block");
		$$("#select-alipay").css("display", "none");
		
	} else  {
		$$("#select-wxpay").css("display", "none");
		$$("#select-alipay").css("display", "block");
	}
	
	if (isVip == 0) {
		if (isWx) {
			
			$$('#img-restpay').attr("src","img/dingdan-pay/dingdan-pay2.png");
			$$('#img-alipay').attr("src","img/dingdan-pay/dingdan-pay2.png");
			$$('#img-wxpay').attr("src","img/dingdan-pay/dingdan-pay1.png");
			$$("#orderPayType").val(2);
			changePayType('img-wxpay', 2);
		} else  {
			
			$$('#img-restpay').attr("src","img/dingdan-pay/dingdan-pay2.png");
			$$('#img-wxpay').attr("src","img/dingdan-pay/dingdan-pay2.png");
			$$('#img-alipay').attr("src","img/dingdan-pay/dingdan-pay1.png");
			$$("#orderPayType").val(1);
			changePayType('img-alipay', 1)
		}
	} else {
		changePayType('img-restpay', 0)
		$$("#orderPayType").val(0);
	}
	
	
	
	
	
	var postOrderPaySuccess =function(data, textStatus, jqXHR) {
		$$("#orderPaySubmit").removeAttr('disabled');  
	 	var result = JSON.parse(data.response);
		if (result.status == "999") {
			myApp.alert(result.msg);
			return;
		}

		orderPayType = result.data.pay_type;
		orderType = result.data.order_type;
		serviceTypeId = result.data.service_type;
		
		//补差价需要单独处理订单ID 和 订单编号
		if (payOrderType == 3) {
			orderNo = result.data.order_no_ext;
			orderId = result.data.id;
			
			sessionStorage.setItem('order_id_ext', orderId);
			sessionStorage.setItem('order_no_ext', orderNo);
		}
		
		console.log("orderPayType = " + orderPayType);
		
		//如果为余额支付或者 现金支付，则直接跳到完成页面
		if (orderPayType == 0) {
			//支付成功之后，把优惠劵的信息清空.
			sessionStorage.removeItem("user_coupon_id");
			sessionStorage.removeItem("user_coupon_value");
			sessionStorage.removeItem("user_coupon_name");
			mainView.router.loadPage("order/order-pay-success.html?service_type_id="+serviceTypeId);
		}
		
		
		//如果为支付宝支付，则跳转到支付宝手机网页支付页面
		if (orderPayType == 1) {
			var orderPay = result.data.order_pay;
			var alipayUrl = localUrl + "/" + appName + "/pay/alipay_order_api.jsp";
			alipayUrl +="?orderNo="+orderNo;
			alipayUrl +="&orderPay="+orderPay;
			alipayUrl +="&orderType="+orderType;
			alipayUrl +="&serviceTypeId="+serviceTypeId;
			alipayUrl +="&payOrderType="+payOrderType;
			alipayUrl +="&shareUserId="+shareUserId;
			location.href = alipayUrl;
		}
		
		//如果为微信支付，则需要跳转到微信支付页面.
		if (orderPayType == 2) {
			 var userCouponId = $$("#userCouponId").val();
			 if (userCouponId == undefined) userCouponId = 0;
			 var wxPayUrl = localUrl + "/" + appName + "/wx-pay-pre.jsp";
			 wxPayUrl +="?orderId="+orderId;
			 wxPayUrl +="&userCouponId="+userCouponId;
			 wxPayUrl +="&orderType=0";
			 wxPayUrl +="&payOrderType="+payOrderType;
			 wxPayUrl +="&serviceTypeId="+serviceTypeId;
			 wxPayUrl +="&shareUserId="+shareUserId;
			 location.href = wxPayUrl;
		}
	};
	
	
	function doOrderPay() {
		var params = {};
		params.user_id = userId;
		params.order_no = orderNo;
		var userCouponId = $$("#userCouponId").val();
		if (userCouponId == undefined) userCouponId = 0;
		params.user_coupon_id = userCouponId;
		params.order_pay_type = $$("#orderPayType").val();
		params.share_user_id = shareUserId;
		
		console.log(params);

		
		$$.ajax({
			type: "post",
			 url: siteAPIPath + "order/post_pay.json",
			data: params,
			statusCode: {
	         	200: postOrderPaySuccess,
	 	    	400: ajaxError,
	 	    	500: ajaxError
	 	    }
		});
	}
	
	function doOrderPayExt() {
		var params = {};
		params.user_id = userId;
		params.order_no = orderNo;
		params.order_pay_ext = orderPay;
		params.order_pay_type = $$("#orderPayType").val();
		params.share_user_id = shareUserId;
		
		console.log(params);

		$$.ajax({
			type: "post",
			 url: siteAPIPath + "order/post_pay_ext.json",
			data: params,
			statusCode: {
	         	200: postOrderPaySuccess,
	 	    	400: ajaxError,
	 	    	500: ajaxError
	 	    }
		});
	}
	
	//点击支付的处理
	$$("#orderPaySubmit").click(function(){
		$$("#orderPaySubmit").attr("disabled", true);
		
		if (payOrderType == 0) {
			doOrderPay();
		}
		
		//补差价订单
		if (payOrderType == 3) {
			doOrderPayExt();
		}
	});
	
});

function changePayType(imgPayType, orderPayType) {
	
	$$("#orderPayType").val(orderPayType);
	var imgPayTypes = ['img-restpay', 'img-wxpay', 'img-alipay'];
	
	$$.each(imgPayTypes,function(n,value) {  
		console.log("value = " + value + "=== imgPayType=" + imgPayType);
		if (value == imgPayType) {
			$$('#' + value).attr("src","img/dingdan-pay/dingdan-pay1.png");
		} else {
			$$('#' + value).attr("src","img/dingdan-pay/dingdan-pay2.png");
		}
	});
	
	//更换价格
	var orderPay = sessionStorage.getItem('order_pay');
	var orderOriginPay = sessionStorage.getItem('order_origin_pay');
	if (orderPayType == 0) {
		$$("#orderMoneyStrLi").html("￥"+orderPay+"元");
		$$("#orderPayStrLi").html("￥"+orderPay+"元");
	} else {
		$$("#orderMoneyStrLi").html("￥"+orderOriginPay+"元");
		$$("#orderPayStrLi").html("￥"+orderOriginPay+"元");
	}
	
}