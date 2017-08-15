/**
 * 表单提交
 * @param form_id 提交的表单ID
 * @param url 后台处理的链接
 * @param refresh 页面刷新方式。1：刷新列表；2：刷新页面；
 * @param close 是否关闭窗口
 * @param successFunction 自定义成功时的函数
 */
function save(form_id, url, refresh, close, successFunction){
	if(typeof form_id == undefined || form_id == null || form_id == ""){
		$.Messager.alert("","表单ID不能为空！");
		return;
	}
	if(typeof refresh == undefined || refresh == null || refresh == ""){
		refresh = 0;
	}
	if(typeof close == undefined || close == null){
		close = true;
	}else if(close == 1 || close){
		close = true
	}else{
		close = false;
	}
	
	/*$("<div class=\"datagrid-mask\" style=\"display:block\"></div>").appendTo("body").css({zIndex:10001});
	var msg=$("<div class=\"datagrid-mask-msg\" style=\"display:block;left:50%\"></div>").html("保存中...").appendTo("body");
	msg._outerHeight(40);
	msg.css({marginLeft:(-msg.outerWidth()/2),lineHeight:(msg.height()+"px"),zIndex: 10001});*/
	//进度条及遮挡层
	$.messager.progress({msg:"正在处理，请稍候...",text:""});

	$("#" + form_id).form("submit", {
		url : url,
		success : function(data){
			//var data = eval("(" + data + ")");
			//$.parse
			//console.log(typeof(data));
			/*$("div.datagrid-mask-msg").remove();
			$("div.datagrid-mask").remove();*/
			//关闭进度条和遮挡层
			$.messager.progress("close");
			
			//if(typeof successFunction != undefined && successFunction != null){
			if(typeof successFunction === "function"){
				successFunction(data);
			}else{
				var message = "操作成功";
				if(typeof data.message != "undefined"){
					message = data.message;
				}
				else{
					message = data;
				}
				$.messager.alert('', message, 'info', function(){
					//关闭窗口
					if(close){
						window.parent.$("#tmp-open-window").window("close"); 				
					}
					//1：刷新列表；2：刷新页面；3：刷新列表树
					if(refresh > 0){
						if(refresh == 1){
							$("#list").datagrid("reload");
						}else if(refresh == 2){
							window.location.reload();
						}else if(refresh == 3){
							$("#list").treegrid("reload");
						}
					}
				});
				//console.log(data);
				
			}
			
		}
	});
}

/**
 * 上传文件
 * @param id 用于存放fid的隐藏域标签ID
 * @param path 文件要保存的路径，上级路径在常量配置中设置，默认为WebRoot\upload。文件目录的分隔符建议使用“\\”
 * @param refresh 是否关闭窗口并刷新页面
 */
function uploadFile(form_id, id, path, rename, refresh){	
	if(typeof path == undefined || path == null){
		path = "";
	}
	if(typeof refresh == undefined || refresh == null || refresh == ""){
		refresh = false;
	}else if(refresh == 1 || refresh){
		refresh = true
	}else{
		refresh = false;
	}

	$.messager.progress({msg:"正在处理，请稍候...",text:""});
	
	$("#" +form_id).attr("enctype", "multipart/form-data");
	$('#' + form_id).form('submit', {
		//url : "/file/do_upload?path=" + path + "&name=" + rename,
		url : "/file/do_upload",
		success : function(data){
			//关闭进度条和遮挡层
			$.messager.progress("close");
			//console.log(data);
			data = $.parseJSON(data);
			if(data.fid > 0){
				//$("#" + id).val(data);
				//alert("文件上传成功！");
				$.messager.alert("提示","文件上传成功！","info",function(){
					
					//删除旧的数据
					var fid_old = $("#" + id).val();
					
					deleteOldFile(data.fid, fid_old, data.deleteFile);
					$("#" + id).val(data.fid).change();
					//console.log(data);
					/*if(fid_old != null && fid_old != "" && data != fid_old){
						$.ajax({
							url : "/admin/file/deleteOld?fid=" + fid_old,
							method : "POST",
							dataType : "text",
							success : function(){
								$("#" + id).val(data);
							}
						});
					}else{
						$("#" + id).val(data);
					}*/
					/*//重命名
					if(typeof rename != undefined && rename != null && rename != ""){
						$.ajax({
							url : "/admin/file/rename/" + data + "-" + rename,
							method : "POST",
							dataType : "text"
						});
					}*/
				});
			}else{
				alert(data.message);				
			}
			//设置enctype属性保证表单的正常提交
			$("form").attr("enctype", "application/x-www-form-urlencoded");
			//在指定的隐藏域中写入fid值
			//将name属性的值清除以避免提交时重复文件复制操作
			$("[name='file']").attr("name", "");
			if(refresh){
				$('#tmp-open-window').window('close'); 
				$('#list').datagrid('reload');
			}
		}
	});
}

function deleteOldFile(fid, fid_old, deleteFile){
	//console.log(deleteFile);
		if(typeof deleteFile == undefined || deleteFile == null){
			deleteFile = true;
		}else if(deleteFile == 1 || deleteFile){
			deleteFile = true;
		}else{
			deleteFile = false;
		}
		
		if(fid_old != null && fid_old != "" && fid != fid_old){
			var url = "";
			if(deleteFile){
				url = "/file/delete?checkedIds="  + fid_old;
			}else{
				url = "/file/deleteOnlyData?fid=" + fid_old;
			}
			$.ajax({
				url : url,
				method : "POST",
				dataType : "text"
			});
		}
}