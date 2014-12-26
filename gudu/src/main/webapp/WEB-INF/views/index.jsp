<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>咕嘟一下</title>
	<script src="${ctx }/static/js/auto.js" type="text/javascript"></script>
	<style type="text/css">
	#front{
	height:100%;
	width:100%;
	
	}
	#logo{
		width:310px;
		height:160px;
		background:url(static/images/banner.png) no-repeat center center;
		margin:50px auto 20px auto;
	}
	#search_block{
		width:380px;
		height:75px;
		background:url(static/images/mainsearch.png) no-repeat center center;
		margin:0px auto 200px auto;
	}
	#search_LIKE_title{
		margin-left:20px;
		margin-top:16px;
	}
	#search_btn{
		width:32px;
		height:32px;
		background:url(static/images/searchbtn.png) no-repeat;
		margin-left:3px;
		margin-top:15px;
	}
	.error{
	margin-left:40px;
	margin-top:20px;
	float:left;
	}
	</style>
</head>

<body>
	<div id="front">
		<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
		</c:if>
		<div id="logo"></div>
		<div id="search_block">
			<form class="form-search" action="${ctx}/html" method="GET">
				<input type="text" name="search_LIKE_title" id="search_LIKE_title" class="input-xlarge search-query" autocomplete="off" data-provide="typeahead"  data-source='' placeholder="咕嘟一下" autofocus="autofocus">
				<input type="submit" name="search_btn" id="search_btn" class="btn" value="">
			</form>
		</div>
	</div>
</body>
</html>
