myApp.onPageBeforeInit('order-user-rate', function(page) {
	
	removeSessionData();
	
	var userId = localStorage['user_id'];
	var loading = false;// 加载flag
	var page = $$("#page").val();
	
	var orderListSuccess = function(data, textStatus, jqXHR) {

		var result = JSON.parse(data.response);
		var orderRates = result.data;

		var htmlTemplate = $$('#order-user-rate-temp').html();
		var html = ''; // 当前订单

		for (var i = 0; i < orderRates.length; i++) {
			var or = orderRates[i];
			var htmlPart = htmlTemplate;
			
			if(or.staff_list!=null){
				var staffhtml="";
				var headImgHtml="";
				var staffs=or.staff_list;
				var staffLen=staffs.length;
				var orderId=or.order_id;
//				sessionStorage.setItem("order_id",or.order_id);
				for(var k=0;k<staffLen;k++){
					staffhtml +="<a href='order/order-rate-success.html?order_id="+orderId+"&staff_id="+staffs[k].staff_id+"'><span class='special-color2'>"+staffs[k].name+"</span></a>";
					if(k<2){
						headImgHtml+="<a href='order/order-rate-success.html?order_id="+orderId+"&staff_id="+staffs[k].staff_id+"'><div id='staffId-img' class='waiter10-2-1'><img src='"+staffs[k].head_img+"' alt='' /></div></a>";
					}
				}
				htmlPart = htmlPart.replace(new RegExp('{staff_name}', "gm"), staffhtml);
				htmlPart = htmlPart.replace(new RegExp('{head_img}', "gm"), headImgHtml);
			}else{
				htmlPart = htmlPart.replace(new RegExp('{staff_name}', "gm"), "");
			}
			
			htmlPart = htmlPart.replace(new RegExp('{service_type_name}', "gm"), or.service_type_name);
			htmlPart = htmlPart.replace(new RegExp('{service_date_str}', "gm"), or.service_date_str);
			if(or.rate_content!='' && or.rate_content!=null){
				htmlPart = htmlPart.replace(new RegExp('{rate_content}', "gm"), or.rate_content);
			}else{
				htmlPart = htmlPart.replace(new RegExp('{rate_content}', "gm"), "暂无评价");
			}
			
			var rateArrival="";
			if(or.rate_arrival==0){
				rateArrival="<span class='waiter10-3-1'>准时</span><span class='waiter10-3-2'>迟到</span></li>";
			}else if(or.rate_arrival==1){
				rateArrival="<span class='waiter10-3-2'>准时</span><span class='waiter10-3-1'>迟到</span></li>"
			}
			htmlPart = htmlPart.replace(new RegExp('{rate_arrival}', "gm"), rateArrival);

			//服务态度
			var rateAttitude = "";
			if (or.rate_attitude > 0) {
				for (var s = 1 ; s <= 5; s++) {
					if (s <= or.rate_attitude) {
						rateAttitude+='<img src="img/yudingayi/xx.png" alt="">';
					} else {
						rateAttitude+='<img src="img/yudingayi/xx1.png" alt="">';
					}

				}
			}
			htmlPart = htmlPart.replace(new RegExp('{rate_attitude}', "gm"), rateAttitude);
			
			//技能
			var rateSkill = "";
			if (or.rate_attitude > 0) {
				for (var o = 1 ; o <= 5; o++) {
					if (o <= or.rate_attitude) {
						rateSkill+='<img src="img/yudingayi/xx.png" alt="">';
					} else {
						rateSkill+='<img src="img/yudingayi/xx1.png" alt="">';
					}

				}
			}
			htmlPart = htmlPart.replace(new RegExp('{rate_skill}', "gm"), rateSkill);
			
			//图片
			var rateUrl="";
			if(or.order_rate_url!=null){
				var rateUrlLen=or.order_rate_url.length;
				for(var j=0;j<rateUrlLen;j++){
					if(j<4){
						rateUrl += "<div class='waiter10-4-1'><img src='"+or.order_rate_url[j]+"' alt=''></div>";
					}
				}
				htmlPart = htmlPart.replace(new RegExp('{rate_url}', "gm"), rateUrl);
			}else{
				htmlPart = htmlPart.replace(new RegExp('{rate_url}', "gm"), "");
			}
			
			html+= htmlPart;
		}
		// 当前订单
		if (page == 1) {
			$$("#order-user-rate").html(html);
		} else {
			$$("#order-user-rate").append(html);
		}

		loading = false;
		
		$$("#page").val(page);
		if (orderRates.length >= 10) {
			console.log("order-list-more block");
			$$('#order-list-more').css("display", "block");
		} else {
			$$('#order-list-more').css("display", "none");
		}
	};
	
	function loadOrderList(userId, page) {
		console.log("page = " + page);
		var postdata = {};
		
		var apiUrl = "order/get_user_rates.json";
		postdata.user_id = userId;
		postdata.page = page;

		$$.ajax({
			type : "GET",
			url : siteAPIPath + apiUrl,
			dataType : "json",
			cache : true,
			data : postdata,
			statusCode : {
				200 : orderListSuccess,
				400 : ajaxError,
				500 : ajaxError
			},

		});
	}	
		
	$$('#order-list-more').on('click', function() {
		var cpage = ++page;
		loadOrderList(userId, cpage);
	});
	
	loadOrderList(userId, page);
	
	$$('#order-user-rate .waiter10-1 #staffId').on("click",function(){
//		<a href='order/order-rate-success.html'>
		alert();
		var staffId = $$("#staff_id").val();
		sessionStorage.setItem('staff_id',staffId);
		mainView.router.loadPage("order/order-rate-success.html");
	});
	
	$$('').on("click",function(){
//		<a href='order/order-rate-success.html'>
		alert();
		var staffId = $$("#staff_id").val();
		sessionStorage.setItem('staff_id',staffId);
		mainView.router.loadPage("order/order-rate-success.html");
	});
	
	
});





