$(document).ready(function(){
	$('#resultList').find('dt').each(function(){
		var key = $("#search_LIKE_title").attr("last");
		var keys = key.split(" ");
		var title = this.innerHTML;
		for(var i = 0;i < keys.length; i++){
			title = title.replace(eval("/"+keys[i]+"/gi"),'<font color=red><b>'+keys[i]+"</b></font>");
		}
		$(this).html(title);
		
	});
	
	$('#resultList').find('dd').each(function(){
		var key = $("#search_LIKE_title").attr("last");
		var keys = key.split(" ");
		var title = this.innerHTML;
		for(var i = 0;i < keys.length; i++){
			title = title.replace(eval("/"+keys[i]+"/gi"),'<font color=red><b>'+keys[i]+"</b></font>");
		}
		$(this).html(title);
		
	});
	
	$('.title').each(function(){
		var key = $("#search_LIKE_title").attr("last");
		var keys = key.split(" ");

		var value = $(this).attr("data-content");
		var first = value.indexOf(key[0]);
		value=value.substring(first-20,first+600);
		value='...'+value+'...';
		for(var i = 0;i < keys.length; i++){
			value = value.replace(eval("/"+keys[i]+"/gi"),'<span class="target">'+keys[i]+"</span>");
		}
		$(this).attr('data-content',value);
	});
	$('.clustertitle').each(function(){
		var key = $("#search_LIKE_title").attr("last");
		var keys = key.split(" ");

		var content = $(this).attr("data-content");
		var title = $(this).attr("data-title");
		for(var i = 0;i < keys.length; i++){
			content = content.replace(eval("/"+keys[i]+"/gi"),'<span class="target">'+keys[i]+"</span>");
			title = title.replace(eval("/"+keys[i]+"/gi"),'<font color=red><b>'+keys[i]+"</b></font>");
			
		}
		$(this).attr('data-content',content);
		$(this).attr("data-title",title);
		$(this).html(title);
	});
	$(".form-search").validate({
	rules : {
		search_LIKE_title : {
			required : true,
			maxlength : 16,
		}
	},
	messages : {
		search_LIKE_title : {
			required : "<span class='unit'>请输入关键词</span>",
			maxlength : "<span class='unit'>关键词过多</span>",
		}
		}
	});
	$('#search_LIKE_title').typeahead({
	    source: function (query, process) {
	    	//删除左右两端的空格
	    	//query = query.replace(/(^\s*)|(\s*$)/g,"");
	    	$.ajax({
	    		type: 'post',
	    		url: '/gudu/api/v1/query/auto/'+query,
	    		dataType: 'json',
	    		contentType:"charset=utf-8",
	    		success: function(obj) {
	    			var num = obj.length;
	    			if(num>0){
	    				process(obj);
	    			}else{
	    				process("[]");
	    			}
	    		},
	    		error: function() {
	    		}
	    		});    
	    }
	});
	 $('.title').popover({
		 trigger:"hover",
		 html:true,
		 placement:"right",
		 /*content:function(){
			 var id=$(this).attr('html_id');
			 var value="";
			 $.ajax({
		    		type: 'get',
		    		async:false,
		    		url: '/gudu/api/v1/html/'+id,
		    		dataType: 'json',
		    		success: function(obj) {
		    			var key = $("#search_LIKE_title").attr("last");
		    			var keys = key.split(" ");
		    			var first = obj.body.indexOf(key[0]);
		    			value=obj.body.substring(first-20,first+500);
		    			value='...'+value+'...';
		    			for(var i = 0;i < keys.length; i++){
		    				value = value.replace(eval("/"+keys[i]+"/gi"),'<span class="target">'+keys[i]+"</span>");
		    			}
		    		}
			 });
			 return value;
		 	}*/
		 });
	 
	 $('.clustertitle').popover({ 
		 html:true,
		 trigger : 'hover',
		 placement:'left' });
});

