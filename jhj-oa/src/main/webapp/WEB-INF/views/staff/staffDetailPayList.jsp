<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
import="com.jhj.oa.common.UrlHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="../shared/taglib.jsp"%>

<!-- taglib for this page -->
<%@ taglib prefix="timestampTag" uri="/WEB-INF/tags/timestamp.tld" %>

<html>
  <head>
	
	<!--common css for all pages-->
	<%@ include file="../shared/importCss.jsp"%>
	<link
	href="<c:url value='/assets/bootstrap-datepicker/css/bootstrap-datepicker3.min.css'/>"
	rel="stylesheet" type="text/css" />
	<!--css for this page-->
  </head>

  <body>

  <section id="container" >
	  
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
                      	 	<form:form class="form-inline" onsubmit="return checkEndTime()"
                      	 			modelAttribute="orgStaffDetailPaySearchVoModel" 
                      	 			action="staffPay-list" method="GET">
                          		<div class="form-group">
                          			手机号：<form:input path="mobile" class="form-control"/>
                          		</div>
                          		<div class="form-group">
	                          		开始时间：
									<form:input path="startTimeStr" class="form-control form_datetime"
									 style="width:110px; margin-bottom:0" readonly="true" />
								</div>
								<div class="form-group">
									结束时间：
									<form:input path="endTimeStr" class="form-control form_datetime" 
									style="width:110px; margin-bottom:0" readonly="true" />
								</div> 
								 <div class="form-group">	
										选择云店: 
									<form:select path="orgId">
										<form:option value="">请选择云店</form:option>
										<form:options items="${orgList}" itemValue="orgId" itemLabel="orgName" />
									</form:select>
								 </div>
								<button type="submit" class="btn btn-primary" >搜索</button>								
                           </form:form>     
                          </header>
                           
                           <table class="table table-hover table-bordered">
                           	<thead>
	                           	<tr>
	                           		<td>订单总金额（元）</td>
	                           		<td>订单收入（元）</td>
	                           		<td>余额支付（元）</td>
	                           		<td>支付宝（元）</td>
	                           		<td>微信（元）</td>
	                           		<td>平台已支付（元）</td>
	                           		<td>现金收入（元）</td>
	                           		<td>还款金额（元）</td>
	                           	</tr>
                           	</thead>
                           	<tbody>
	                           	<tr>
	                           		<td>${totalMoney }</td>
	                           		<td>${orderPayMoney }</td>
	                           		<td>${spareMoney }</td>
	                           		<td>${alipayMoney }</td>
	                           		<td>${wechatMoney }</td>
	                           		<td>${platformMoney }</td>
	                           		<td>${cashMoney }</td>
	                           		<td>${refundMoney }</td>
	                           	</tr>
                           	</tbody>
                           </table>
                      	<hr style="width: 100%; color: black; height: 1px; background-color:black;" />  
                      	
                          <header class="panel-heading">
                          	<h4>服务人员财务明细列表</h4>
                          </header>
                          
                          
                          <table class="table table-striped table-advance table-hover" id="table2excel">
                              <thead>
                              <tr>	  
                           		  <th >员工姓名</th>
                              	  <th >员工手机号 </th>
	                              <th >订单类型</th>
	                              <th >订单金额</th>
	                              <th >订单收入</th>
	                              <th>支付方式</th>
	                              <th >订单状态</th>
	                              <th >添加时间</th>
	                              <th >备注</th>
                              </tr>
                              </thead>
                              <tbody>
                              <c:forEach items="${contentModel.list}" var="item">
                              <tr>	
					            <td style="width:6%">${ item.name }</td>
                                <td style="width:10%">${ item.mobile }</td>
					            <td style="width:15%">${ item.orderTypeName }</td>
					            <td style="width:8%">${ item.orderMoney }</td>
					            <td style="width:8%">${ item.orderPay }</td>
					            <td style="width:10%">${item.payTypeName }</td>
					            <td style="width:8%">${ item.orderStatusStr }</td>
					            <td style="width:10%"><timestampTag:timestamp patten="yyyy-MM-dd" t="${item.addTime * 1000}"/></td>
					            <td style="width:25%">${ item.remarks }</td>
                              </tr>
                              </c:forEach>
                              </tbody>
                          </table>
                      </section>
                      <c:import url = "../shared/paging.jsp">
	        				<c:param name="pageModelName" value="contentModel"/>
	        				<c:param name="urlAddress" value="/staff/staffPay-list"/>
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
    <%@ include file="../shared/importJs.jsp"%>

    <%-- <script type="text/javascript" src="<c:url value='/assets/jquery.table2excel.js'/>"></script> --%>
    <!--script for this page-->	
    <script	type="text/javascript"  src="<c:url value='/assets/jquery-validation/dist/jquery.validate.min.js'/>"></script>
    <script type="text/javascript"
		src="<c:url value='/assets/bootstrap-datepicker/js/bootstrap-datepicker.min.js'/>"></script>
	<script type="text/javascript"
		src="<c:url value='/assets/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js'/>"></script>
  
    <script type="text/javascript"
		src="<c:url value='/js/staff/staffDetailPayList.js'/>"></script>
  </body>
</html>
