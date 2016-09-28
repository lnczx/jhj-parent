$(function(){ 
	$(".form-horizontal").validate({
		errorElement : 'div',
        rules:{  
            mobile : {
            	required:true,
				minlength:11,
				maxlength:11,
            },
            addrId : "required",
            serviceType : "required",
            orderFrom : "required",
            serviceDate : "required"
        },  
        messages:{  
        	mobile : {
        		required:"手机号不能为空",
        		minlength:"手机号长度为11位",
        		maxlength:"手机号长度为11位"
        	},
            addrId : "用户地址不能为空",
            serviceType : "服务类型不能为空",
            orderFrom : "订单来源不能为空",
            serviceDate : "服务时间不能为空"  
        },
        highlight : function(element) {
            $(element).closest('.form-control').css({borderColor:"red"});
        },
        success : function(errorElement , element) {
            $(element).css({borderColor:"#79FF36"});
            $(errorElement).remove();
        },
        errorPlacement : function(error, element) {
        	element.parent('div').append(error);
        },
        submitHandler : function(form) {
            form.ajaxSubmit();
        }
    });
	 $.validator.addMethod("mobile",function(value,element,params){  
          var mobile = /^1[3,5,7,8]\d{9}$/;  
          return this.optional(element)||(mobile.test(value));  
      },"*请输入正确的手机号码！");
	  
});

//设置日历控件
$('#from-servicedate').datetimepicker({
	format : "yyyy-mm-dd hh:ii",
	language : "zh-CN",
	autoclose : true,
	todayBtn : true,
	minuteStep : 30
});
$('#from-servicedate').datetimepicker('setStartDate', new Date());

//输入完手机号获取用户信息，根据用户的id获取用户的服务地址
function getAddrByMobile(){
	var mobile=$("#from-mobile").val();
	var reg = /^1[3,5,7,8]\d{9}$/;
	if(reg.test(mobile)){
		$.ajax({
			type:"post",
			url:"../user/getUser",
			data:{"mobile":mobile},
			dataType:"json",
			success:function(data){
				if(data.data!=false){
					var userId=data.data.id;
					$("#from-user-id").text(userId);
					$.ajax({
						type:"get",
						dataType:"json",
						url:"/jhj-app/app/user/get_user_addrs.json?user_id="+userId,
						success:function(result){
							var userAddr=result.data;
							var selectid=document.getElementById("from-addr");
							for(var i=0;i<userAddr.length;i++){
								selectid[i+1]=new Option(userAddr[i].name, userAddr[i].id, false, false);
							}
						}
					});
				}else{
					var isResult=confirm("是否添加该用户？");
					if(isResult){
						regUser(mobile);
					}
					
				}
			}
		});
	}
}



/*
 * 提交订单
 * */
function saveFrom(){
	var from={};
	from.userId=$("#from-user-id").text();
	from.mobile=$("#from-mobile").val();
	from.addrId=$("#from-addr").val();
	from.serviceType=$("input[name='serviceType']").val();
	from.orderFrom=$("#from-src").val();
	var serviceDate=$("#from-servicedate").val();
	from.serviceDate =  moment(serviceDate + ":00", "yyyy-MM-DD HH:mm:ss").unix();
	from.serviceHour=$("input[name='serviceHour']").val();;
	from.remarks=$("#ft-eara").val();
	var order_pay_type=$("#f-paywawy").val();
	if($(".form-horizontal").valid()){
		$.ajax({
			type:"post",
			url:"/jhj-app/app/order/post_hour.json",
			data:from,
			dataType:"json",
			success:function(data){
				var orderNo=data.data.order_no;
				var userId=data.data.user_id;
				if(data.status==0){
					alert("订单添加成功！");
					if(order_pay_type==6){
						savePay(order_pay_type,orderNo,userId);
					}
				}
				if(data.status==999){
					alert("现在不能下过去的单啦！！");
				}
			}
		});
	}
}

//订单的支付方式
function savePay(payway,orderNo,userId){
	var data={};
	data.order_pay_type=payway;
	data.order_no=orderNo;
	data.user_id=userId;
	if(orderNo!=null && userId!=null){
		$.ajax({
			type:"post",
			url:"/jhj-app/app/order/post_pay.json",
			data:data,
			dataType:"json",
			success:function(data){
				
			}
		});
	}
}

/*
 * 如果用户在系统中不存，注册新用户到系统中
 * 
 * */
function regUser(mobile){
	var mobile=mobile;
	if(mobile!=null&&mobile.length==11){
		$.ajax({
			type:"post",
			url:"/jhj-app/app/user/reg.json",
			data:{"mobile":mobile},
			dataType:"json",
			success:function(data){
				$("#from-user-id").text(data.data);
				$("#from-user-id").data("userId",data.data)
				$("#from-mobile").data("mobile",$("#from-mobile").val());
			}
		});
	}
}

//为新增用户添加注册地址
function address(){
	if($("#from-mobile").val()!='' || $("#from-mobile").val()!=undefined){
		$("#from-add-addr").show();
		$('#exampleModal').on('show.bs.modal', function (event) {
			var button = $(event.relatedTarget) // Button that triggered the modal
			var recipient = button.data('whatever') // Extract info from data-* attributes
			var modal = $(this)
			modal.find('.modal-title').text('添加地址' + recipient)
			modal.find('.modal-body input').val(recipient)
		});
	}else{
		alert("请先输入手机号码！");
	}
	
}

function saveAddress(){
	var form={}
	//$("#from-user-id").val()
	form.user_id=$("#from-user-id").data("userId");
	form.phone=$("#from-mobile").data("mobile");
	form.longitude=$("#poiLongitude").val();
	form.latitude=$("#poiLatitude").val();
	form.name=$("#suggestId").val();
	form.addr=$("#recipient-addr").val();
	form.addr_id=0;
	form.is_default=1;
	form.city="北京市";
	$.ajax({
		type:"post",
		url:"/jhj-app/app/user/post_user_addrs.json",
		data:form,
		dataType:"json",
		success:function(data){
			$("#from-add-addr").hide();
			alert("地址添加成功");
			getAddrByMobile();
		}
	});
}


function serviceTypeChange(){
	var serviceType=$("select[name='serviceType']").val();
//	alert(serviceType);
	$.ajax({
		type:"post",
		url:"",
		data:{"serviceType":serviceType},
		dataType:"json",
		success:function(data){
			
		}
	});
}






//baiduMap 相关

$(function(){
var map = new BMap.Map("containers");//初始化地图       
map.addControl(new BMap.NavigationControl());  //初始化地图控件              
map.addControl(new BMap.ScaleControl());                   
map.addControl(new BMap.OverviewMapControl());     
map.enableScrollWheelZoom(); //滚轮放大缩小控件
var point=new BMap.Point(116.404, 39.915);
map.centerAndZoom(point, 15);//初始化地图中心点
var marker = '';


var gc = new BMap.Geocoder();//地址解析类
 map.addEventListener("click", function(e){
        marker = new BMap.Marker(new BMap.Point(e.point.lng,e.point.lat));
	   map.clearOverlays(); 
        map.addOverlay(marker); 
        marker.enableDragging(); 
           $("#points").val(e.point.lng+'|'+e.point.lat);//获取拖动后的坐标
         //  alert("当前坐标:"+e.point.lng+'/'+e.point.lat);//当前坐标
           marker.addEventListener("dragstart", function(e){
        	    document.getElementById("poiLongitude").value = e.point.lng; 
       			document.getElementById("poiLatitude").value = e.point.lat; 
        	   
        	   gc.getLocation(e.point, function(rs){
        		   
        	        showLocationInfo(e.point, rs);
        	    });
        	});
    });


//信息窗口
function showLocationInfo(pt, rs){
    var opts = {
      width : 250,     //信息窗口宽度
      height: 100,     //信息窗口高度
      title : "当前位置"  //信息窗口标题
    }
     
    var addComp = rs.addressComponents;
    var addr = addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber + "<br/>";
    addr += "纬度: " + pt.lat + ", " + "经度：" + pt.lng;
    //alert(addr);
    
    //var searchTxt = document.getElementById("suggestId").value; 
    document.getElementById("poiAddress").value = addComp.district 
				+ addComp.street  + addComp.streetNumber; 
		document.getElementById("poiCity").value =  addComp.city;
     
    var infoWindow = new BMap.InfoWindow(addr, opts);  //创建信息窗口对象
    marker.openInfoWindow(infoWindow);
}
 
//搜索提示
	function G(id) {
		return document.getElementById(id);
	}
	
	var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
			{"input" : "suggestId"
			,"location" : map
		});

		ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
		var str = "";
			var _value = e.fromitem.value;
			var value = "";
			if (e.fromitem.index > -1) {
				value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
			}    
			str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
			
			value = "";
			if (e.toitem.index > -1) {
				_value = e.toitem.value;
				value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
				
				
				// jhj2.1 地址 名称
				$("#poiAddress").val(_value.district +  _value.street +  _value.business);
				$("#poiCity").val(_value.city);
				
				
			}     
			str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
			G("searchResultPanel").innerHTML = str;
		});

		var myValue;
		ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
		var _value = e.item.value;
			myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
			G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
			
			setPlace();
			
		});

		function setPlace(){
			map.clearOverlays();    //清除地图上所有覆盖物
			function myFun(){
				var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
				map.centerAndZoom(pp, 18);
				
				
				//jhj2.1 使用搜索后,设置 选中 目标地点的 坐标
				$("#poiLongitude").val(pp.lng);
				$("#poiLatitude").val(pp.lat);
				
				
				var marker = new BMap.Marker(pp);
				map.addOverlay(new BMap.Marker(pp));    //添加标注
				marker.enableDragging();
				
			}
			var local = new BMap.LocalSearch(map, { //智能搜索
			  onSearchComplete: myFun
			});
			local.search(myValue);
		}



 //页面加载时，判断是否回显地图信息
    function hhh(){
    	var b = document.getElementById("poiLatitude").value;
    	var c =  document.getElementById("poiLongitude").value;
    	
        var pt = new BMap.Point(c,b);
        var marker = new BMap.Marker(pt); //初始化地图标记，通过标记窗口，回显地图信息
        
        var myGeo = new BMap.Geocoder(); 
        //解析记录中  经纬度 确定的 标记点，在信息窗中回显该地址信息
        myGeo.getLocation(pt, function(rs){
        	var opts = {
      	          width : 250,     //信息窗口宽度
      	          height: 50,     //信息窗口高度
      	          title : "当前位置:"  //信息窗口标题
      	        }
  	       var addComp = rs.addressComponents;
        	
  	       var addr =  document.getElementById("poiAddress").value;
        	if(addr == "" ){
        		//若是新增页面。则定位在天安门，不显示信息窗
        		map.centerAndZoom(point, 15);
        	}else{
        		//若是修改页面。则定位在指定位置，并显示信息窗
	        	var infoWindow = new BMap.InfoWindow(addr, opts);  //创建信息窗口对象
	 	        map.addOverlay(marker);
	 	        marker.openInfoWindow(infoWindow);
        	}
        });
    }
});


