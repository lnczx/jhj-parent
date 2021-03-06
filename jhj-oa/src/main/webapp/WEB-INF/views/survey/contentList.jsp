<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="../shared/taglib.jsp"%>
<%@ taglib prefix="timestampTag" uri="/WEB-INF/tags/timestamp.tld" %>


<html>
  <head>
	<!--common css for all pages-->
	<%@ include file="../shared/importCss.jsp"%>
	
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
      
      <section id="main-content">
          <section class="wrapper">

            <div class="row">
                  <div class="col-lg-12">
                      <section class="panel">
                          <header class="panel-heading">
                          	 <h4>题库列表</h4>
                          	<div class="pull-right">
                          		<button onClick="btn_add('survey/content_form?id=0')" class="btn btn-primary" type="button"><i class="icon-expand-alt"></i>新增</button>
                    		</div>      
                          </header>
                         
                          
                          <table class="table table-striped table-advance table-hover" >
                              <thead>
                              <tr>		
                              		  <th>内容名称</th>
                              		  <th>服务大类名称</th>
                              		  <th>单价</th>
                              		  <th>计数期限</th>
		                              <th>添加时间</th>
                                  	  <th>操作</th>
                              </tr>
                              </thead>
                              <tbody>
                              <c:forEach items="${contentListVoModel.list}" var="item">
	                              <tr>  
	                              		<td>${item.name }</td>
	                              		<td>${item.serviceName }</td>
	                              		<td>${item.price }</td>
	                              		<td>
	                              			<c:if test="${item.measurement == 0}">
	                              				月
	                              			</c:if>
	                              			<c:if test="${item.measurement == 1}">
	                              				年
	                              			</c:if>
	                              			<c:if test="${item.measurement == 2}">
	                              				次
	                              			</c:if>
	                              			<c:if test="${item.measurement == 3}">
	                              				免费赠送
	                              			</c:if>
	                              		</td>
										<td>
							            	<timestampTag:timestamp patten="yyyy-MM-dd HH:mm" t="${item.addTime * 1000}"/>
							            </td>
										
										<td>
											<button id="btn_update" onClick="btn_update('survey/content_form?id=${item.contentId}')" class="btn btn-primary btn-xs" title="修改">
													<i class="icon-pencil"></i>
											</button>
										</td>
								 </tr>
                              </c:forEach>
                              </tbody>
                          </table>
					</section>		
                      <c:import url = "../shared/paging.jsp">
	        				<c:param name="pageModelName" value="contentListVoModel"/>
	        				<c:param name="urlAddress" value="/survey/content_list"/>
	       			  </c:import>
               	</div>
              </div>
          </section> 
      </section>
      
      <%@ include file="../shared/pageFooter.jsp"%>
  </section>

   <%@ include file="../shared/importJs.jsp"%> 
	
  </body>
</html>