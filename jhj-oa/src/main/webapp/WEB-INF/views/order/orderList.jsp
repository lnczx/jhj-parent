<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	import="com.jhj.oa.common.UrlHelper"%>
<!DOCTYPE html>
<%@ include file="../shared/taglib.jsp"%>
<!-- taglib for this page -->
<%@ taglib prefix="orderFromTag" uri="/WEB-INF/tags/orderFromName.tld"%>
<%@ taglib prefix="timestampTag" uri="/WEB-INF/tags/timestamp.tld"%>
<%@ taglib prefix="orderVoStatusTag" uri="/WEB-INF/tags/orderVoStatusName.tld"%>
<%@ taglib prefix="payTypeNameTag" uri="/WEB-INF/tags/payTypeName.tld"%>
<%@ taglib prefix="orderServiceTimeTag" uri="/WEB-INF/tags/orderServiceTime.tld"%>
<%@ taglib prefix="serviceTypeTag" uri="/WEB-INF/tags/serviceTypeName.tld"%>
<%@ taglib prefix="cloudOrgSelectTag" uri="/WEB-INF/tags/CloudOrgSelect.tld"%>
<%@ taglib prefix="orgSelectTag" uri="/WEB-INF/tags/OrgSelect.tld"%>
<%@ taglib prefix="orderTypeNameTag" uri="/WEB-INF/tags/orderTypeName.tld"%>
<%@ taglib prefix="serviceTypeSelectTag" uri="/WEB-INF/tags/serviceTypeSelect.tld"%>
<%@ taglib prefix="orderStatusSelectTag" uri="/WEB-INF/tags/orderSatusSelect.tld"%>
<%@ taglib prefix="orderFromSelectTag" uri="/WEB-INF/tags/orderFromSelect.tld"%>
<html>
<head>
<!--common css for all pages-->
<%@ include file="../shared/importCss.jsp"%>
<!--css for this page-->
<link href="<c:url value='/assets/bootstrap-datepicker/css/bootstrap-datepicker3.min.css'/>" rel="stylesheet"
	type="text/css" />
<link href="<c:url value='/assets/bootstrap-datetimepicker/css/datetimepicker.css'/>" rel="stylesheet" type="text/css" />
</head>
<body>
	<section id="container">
		<!--header start-->
		<%@ include file="../shared/pageHeader.jsp"%>
		<!--header end-->
		<!--sidebar start-->
		<%@ include file="../shared/sidebarMenu.jsp"%>
		<!--sidebar end-->
		<!--main content start-->
		<section id="main-content">
			<section class="wrapper">
				<!-- page start-->
				<div class="row">
					<div class="col-lg-12">
						<section class="panel">
							<header class="panel-heading">
								<h4>数据搜索</h4>
								<form:form modelAttribute="searchModel" onsubmit="return checkEndTime()" class="form-inline"
									action="order-hour-list" method="GET" id="oaSearchForm">
									<form:hidden path="orderType"/>
									<table class="table">
										<tr>
											<td>选择门店:</td>
											<td><orgSelectTag:select selectId="${searchModel.parentId }" sessionOrgId="${loginOrgId }"/></td>
											<td>选择云店:</td>
											<td>
												<select name="orgId" id="orgId" class="form-control">
													<option value="0">全部</option>
												</select>
											</td>
											<td>订单状态：</td>
											<td><c:if test="${loginOrgId == 0 }">
													<form:select path="orderStatus" class="form-control">
														<option value="">订单状态</option>
														<form:option value="0">已取消</form:option>
														<form:option value="1">未支付</form:option>
														<form:option value="2">已支付</form:option>
														<form:option value="3">已派工</form:option>
														<form:option value="5">开始服务</form:option>
														<form:option value="7">完成服务</form:option>
														<form:option value="8">已评价</form:option>
														<form:option value="9">已关闭</form:option>
													</form:select>
												</c:if> <!-- 如果是 店长登录,则 只能选择 已派工之后的 订单状态 --> <c:if test="${loginOrgId > 0 }">
													<form:select path="orderStatus" class="form-control">
														<option value="">订单状态</option>
														<form:option value="3">已派工</form:option>
														<form:option value="5">开始服务</form:option>
														<form:option value="7">完成服务</form:option>
														<form:option value="8">已评价</form:option>
														<form:option value="9">已关闭</form:option>
													</form:select>
												</c:if>
											</td>
										</tr>
										
										<tr>
											<td>手机号：</td>
											<td><form:input path="mobile" class="form-control" placeholder="请输入手机号" /></td>
											<td>服务地址：</td>
											<td><form:input path="addrName" class="form-control" placeholder="请输入服务地址" /></td>
											
											<td>是否接单</td>
											<td><form:select path="isApply" class="form-control">
													<option value="">全部</option>
													<form:option value="1">是</form:option>
													<form:option value="0">否</form:option>
												</form:select></td>
											
										</tr>
										
										<tr>
											<td>下单时间：</td>
											<td><input id="startTimeStr" name="startTimeStr" value="${startTimeStr }" class="form-control form_datetime"
													style="width: 170px; margin-bottom: 0" readonly="true" /> <span>至</span> <input id="endTimeStr"
													name="endTimeStr" value="${endTimeStr }" class="form-control form_datetime" style="width: 170px; margin-bottom: 0" readonly="true" /></td>
											<td>服务日期：</td>
											<td colspan="2"><input id="serviceStartTimeStr" name="serviceStartTimeStr" value="${serviceStartTimeStr }"
													class="form-control form-datetime" style="width: 170px; margin-bottom: 0" readonly="true" /> <span>至</span>
												<input id="serviceEndTimeStr" name="serviceEndTimeStr" value="${serviceEndTimeStr }" class="form-control form-datetime"
													style="width: 170px; margin-bottom: 0" readonly="true" /></td>
											<td></td>
											<td></td>
											
										</tr>
										
										<tr>
											<td colspan="6">
												<button type="button" id="btnSearch" name="searchForm" class="btn btn-primary">搜索</button>
												<button type="button" id="btnExport" name="searchForm" class="btn btn-success">导出excel</button>
												<button type="reset" class="btn btn-primary">重置</button>
												<a href="oaOrderHourAdd" class="btn btn-primary">基本保洁</a>
												<a href="orderAmAdd" class="btn btn-primary">深度服务</a>
											</td>					
										</tr>
									</table>
								</form:form>
							</header>
							
							<hr style="width: 100%; color: black; height: 1px; background-color: black;" />

							<table class="table table-striped table-advance table-hover" id="table2excel">
								<thead>
									<tr>
										<th>门店</th>
										<th>云店</th>
										<th>服务人员</th>
										<th>下单时间</th>
										<th>订单类型</th>
										<th>服务日期</th>
										<th>用户手机号</th>
										<th>服务地址</th>
										<th>是否接单</th>
										<th>订单状态</th>
										<th>支付方式</th>
										<th>支付金额</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${oaOrderListVoModel.list}" var="item">
										<c:forEach items="${item.statusNameMap }" var="sta">
											<tr>
												<input type="hidden" id="itemPayType" value="${item.payType }">
												<input type="hidden" id="itemOrderStatus" value="${item.orderStatus }">
												<td>${ item.orgName }</td>
												<td>${ item.cloudOrgName }</td>
												<td>${ item.staffName }</td>
												<td><timestampTag:timestamp patten="MM-dd" t="${item.addTime * 1000}" /></td>
												<td>${item.orderTypeName }</td>
												<td><timestampTag:timestamp patten="MM-dd HH:mm" t="${item.serviceDate * 1000}" /></td>
												<td>${ item.mobile }</td>
												<td>${ item.orderAddress }</td>
												<td>${ item.applyStatus }</td>
												<td><orderVoStatusTag:orderstatus orderStatus="${item.orderStatus }" orderType="${item.orderType }" /></td>
												<td>${ item.payTypeName }</td>
												<td>${ item.orderPay }</td>
												<td>
													<button id="btn_detail"
														onClick="javascript:btnDetail('${ item.orderNo }', '${item.orderType }', '${item.disStatus}')"
														class="btn btn-primary btn-xs" title="订单详情">
														<i class=" icon-ambulance"></i>
													</button> <!-- 如果 运营人员备注为 空，可以添加，不为空，不让添加 --> 
													<c:if test="${ empty item.remarksBussinessConfirm  }">
														<button onClick="btn_update('order/remarks_bussiness_form?orderId=${ item.id }&orderType=${item.orderType }')"
															class="btn btn-primary btn-xs" title="添加订单备注">
															<i class="icon-plus-sign-alt"></i>
														</button>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</c:forEach>
								</tbody>
							</table>
						</section>
						<%@ include file="../shared/importJs.jsp"%>
						<c:import url="../shared/paging.jsp">
							<c:param name="pageModelName" value="oaOrderListVoModel" />
							<c:param name="urlAddress" value="/order/${listUrl }" />
						</c:import>
					</div>
				</div>
				<!-- page end-->
			</section>
		</section>
		<!--main content end-->
		<!--footer start-->
		<%@ include file="../shared/pageFooter.jsp"%>
		<!--footer end-->
	</section>
	<!-- js placed at the end of the document so the pages load faster -->
	<!--common script for all pages-->
	
	<script src="<c:url value='/assets/jquery.table2excel.js'/>"></script>
	<!--script for this page-->
	<script src="<c:url value='/assets/bootstrap-datepicker/js/bootstrap-datepicker.min.js'/>"></script>
	<script src="<c:url value='/assets/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js'/>"></script>
	<script src="<c:url value='/assets/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js'/>"></script>
	<script src="<c:url value='/assets/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js'/>"></script>
	
	<script type="text/javascript" src="<c:url value='/js/jhj/select-org-cloud.js'/>"></script>
	<script src="<c:url value='/js/order/orderList.js'/>"></script>
</body>
</html>
