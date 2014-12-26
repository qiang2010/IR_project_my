<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>检索结果</title>
	<script src="${ctx }/static/js/auto.js" type="text/javascript"></script>
	<style type="text/css">
	#resultList{
		border-right:1px solid #08c;
		
	}
	#recommend{
		float:left;
		border-bottom:1px solid #CDCDB4;
	}
	#logo{
		width:128px;
		height:67px;
		background:url(static/images/tinybanner.png) no-repeat;
		float:left;
	}
	#search_block{
		background:url(static/images/mainsearch.png) no-repeat;
		float:left;
	}
	#search_LIKE_title{
		margin-left:20px;
		margin-top:16px;
	}
	#search_btn{
		width:32px;
		height:32px;
		background:url(static/images/searchbtn.png) no-repeat;
		margin-left:0px;
		margin-top:15px;
		
		}
	#message{
		margin-top:15px;
		}
	.tip{
		color:#666;
		font-size:14px;
		padding-bottom:6px;
		}
	.item{
		width: 500px;
		white-space: normal;
		}
	.desc{
		width: 600px;
		white-space: normal;
	}
	.error{
		float:left;
		clear:both;
	}
	.in{
		font-family:宋体;
		font-size:10px;
	}
	.target{
		color:red;
		background-color:#FF0;
	}
	</style>
		
</head>

<body>
	<div class="row">
		<div id="logo"></div>
		<div class="span6" id="search_block">
			<form class="form-search" action="#" method="GET">
				<input type="text" name="search_LIKE_title" id="search_LIKE_title" class="input-xlarge search-query" autocomplete="off" data-provide="typeahead"  data-source='' value="${keyword}"  placeholder="咕嘟一下" last="${splits }"> 
				<button type="submit" class="btn" id="search_btn"></button>
		    </form>
	    </div>
	</div>
	<div id="resultList" class="span8">
	<c:choose>
		<c:when test="${htmls.total == 0}"> 
			<div id="message" class="alert alert-fail"><button data-dismiss="alert" class="close">×</button>未找到相关搜索结果.<!-- 根据相关法律法规和政策，搜索结果未予显示。 --></div>
		</c:when>
		<c:when test="${htmls.content.size() != 0}">
		<div class="tip" style="margin-top:20px;">检索到相关文档共:${htmls.total }篇</div>
		<c:forEach items="${htmls.content}" var="html">
				<dl>
					<dt class="item itemtitle" >
						<a class="title" html_id="${html.id}" rel="popover" data-content="${html.body}" target="_new" href="${html.url}">${html.title}</a>
					</dt>
					
					<dd>关键字:${html.keyWords}&nbsp;&nbsp;</dd> 
					<dd class="tip">发表时间:
					<fmt:formatDate value="${html.time}" pattern="yyyy-MM-dd"/>
					&nbsp;&nbsp;
					参与人数:${html.hot}
					</dd>
					<dd class="desc">${html.description}</dd>
				</dl> 
		</c:forEach>
		<tags:pagination page="${htmls}" paginationSize="8"/>
		</c:when>
	</c:choose>
	</div>
	<div id="recommend" class="span3">
		<table class="table">
			<thead><tr><th class="item">其他人还在搜:</th></tr></thead>
			<tbody>
			<c:forEach items="${others}" var="other">
				<tr>
				<td><a title="咕嘟一下" href="?search_LIKE_title=${other.squery}">${ other.squery}</a></td>
				<td>(${ other.count}&nbsp;次)</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="recommend cluster" class="span3">
		<table class="table">
			<thead><tr><th class="item" colspan="2">聚类中心文档(${htmls.clusters.size() }/共${htmls.clustersNum }个):</th></tr></thead>
			<tbody>
			<c:forEach items="${htmls.clusters}" var="cluster">
				<tr>
				<td><a class="clustertitle" rel="popover" data-title="${cluster.title.title}" data-content="${cluster.title.description}" target="_new" href="${cluster.title.url}">${ cluster.title.title}</a></td>
				<td>${ cluster.clusterSize}&nbsp;篇</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="clr"></div>
</body>
</html>
