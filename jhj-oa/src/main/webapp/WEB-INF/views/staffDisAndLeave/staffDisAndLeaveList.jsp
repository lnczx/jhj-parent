<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
import="com.jhj.oa.common.UrlHelper"%>
<!DOCTYPE html>

<%@ include file="../shared/taglib.jsp"%>
<%@ taglib prefix="cloudOrgSelectTag" uri="/WEB-INF/tags/CloudOrgSelect.tld"%>
<%@ taglib prefix="orgSelectTag" uri="/WEB-INF/tags/OrgSelect.tld"%>
<html>
  <head>
	
	<!--common css for all pages-->
	<%@ include file="../shared/importCss.jsp"%>
	
	<!--css for this page-->
	<link href="<c:url value='/assets/bootstrap-datepicker/css/bootstrap-datepicker3.min.css'/>"
	     rel="stylesheet" type="text/css" />
	<style type="text/css">
		td{padding:0!important;}
		.result-content{
			border-bottom:1px solid #ccc
		}
		td div{min-height:30px;}
		td span{min-height:30px;display:block;}
	</style>
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
	                      	  <form:form modelAttribute="disAndLeaveSearchVoModel"
	                      	  class="form-inline" action="calendar_list" method="GET" id="oaSearchForm">
		                     			<div class="form-group">		
											选择云店:<%-- <cloudOrgSelectTag:select 
													selectId="${disAndLeaveSearchVoModel.orgId }"
													logInParentOrgId="${loginOrgId }"/> --%>
													<orgSelectTag:select selectId="${searchModel.parentId }" sessionOrgId="${loginOrgId }"/>
										</div>
										<div class="form-group">		
											选择云店:<%-- <cloudOrgSelectTag:select 
													selectId="${disAndLeaveSearchVoModel.orgId }"
													logInParentOrgId="${loginOrgId }"/> --%>
													<select name="orgId" id="orgId" class="form-control">
													<option value="0">全部</option>
												</select>
										</div>
										<div class="form-group">
											    姓名：<form:input class="form-control" path="staffName" placeholder="请输入姓名"/>
										</div>
										<div class="form-group">
											    手机号：<form:input class="form-control" path="staffMobile" placeholder="请输入手机号"/>
										</div>
										<div class="form-group">
			                          		服务人员技能：
											<form:select path="serviceTypeId" class="form-control" >
												<form:option value="">--请选择技能--</form:option>
												<form:options items="${serviceTypeList }" itemLabel="name" itemValue="serviceTypeId" />
											</form:select>
										</div>	
										<div class="form-group">
			                          		日期：
											<input id="startTimeStr" name="startTimeStr" class="form-control form_datetime"
											 style="width:220px; margin-bottom:0" readonly="true" value="${startTimeStr }"/>
										</div>
										<!-- <div class="form-group">
											结束时间：
											<input id="endTimeStr" name="endTimeStr" class="form-control " 
											style="width:110px; margin-bottom:0" readonly="true" />
										</div>  -->
										
										<button type="submit" id="btnSearch" class="btn btn-primary" >搜索</button>
	                           </form:form>
	                           <table style="margin-top:15px;font-size:16px;">
		                           <tr>
	                          			<td>可接单人数：</td>
	                          			<td tyle="color:red;">${dispatchNum}人</td>
	                          		</tr>
	                           		<tr>
	                           			<td rowspan="2">可派单：</td>
	                           			<td style="color:red;">上午可派单:<b>&nbsp;${amStaffSize }&nbsp;</b>单</td>
	                           		</tr>
	                           		<tr>
	                           			<td style="color:green;">下午可派单:<b>&nbsp;${pmStaffSize }&nbsp;</b>单</td>
	                           		</tr>
	                           </table>
	                            <%-- <aside>
	                            	<span>今日派单人数：<em style="color:red;">${todayOrder }</em></span>
	                            	<span>明日派单人数：<em style="color:red;">${tomorrowOrder }</em></span>
	                            </aside> --%>
	                         </header>
                           
                      	<hr style="width: 100%; color: black; height: 1px; background-color:black;" />  
                      	
                          <header class="panel-heading">
                          	<h4>服务人员状态跟踪</h4>
                          </header>
                          
                          <table class="table table-bordered" >
                          
                          <!-- 返回json数据 ,,注意 要用 单引号！！-->
                          <input type="hidden" id="returnListData" value='${listVoModel }'>
                          
                        		<thead id="resultThead">
                        			<th>姓名</th>
	                         		<c:forEach items="${weekDateModel }" var="weekDateItem">
										  <th>${weekDateItem }</th>                     				
	                         		</c:forEach>	
                        		</thead>
                         		
								<tbody id="resultDisplay">
									
								</tbody >                 
								
								<div id="resultDiv">
									<tr id="resultTr" style="display:none">
										<td style="line-height:60px;"></td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
										<td>
											<div class="result-content"></div>
											<span></span>
										</td>
									</tr>
								</div>
                          </table>
                      </section>
                  </div>
              </div>
              <!-- page end-->
          </section> 
      </section>
      <!--main content end-->
      
  </section>

    <!--common script for all pages-->
    <%@ include file="../shared/importJs.jsp"%>
    
    <script src="<c:url value='/assets/bootstrap-datepicker/js/bootstrap-datepicker.min.js'/>"></script>
	<script src="<c:url value='/assets/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js'/>"></script>
	
	<script src="<c:url value='/js/staffDisAndLeave/staffDisAndLeaveList.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/jhj/select-org-cloud.js'/>"></script>
	
  </body>
</html>
