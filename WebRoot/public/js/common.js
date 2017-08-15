/**
 * 
 * @param url
 * @param title
 * @param width
 * @param height
 * @param opt
 */
function new_window(url, title, width, height, opt){
	//open_window("");
	//var url = "budget/budget_class_edit";
	var title =  title || "&nbsp;";
	var width = width || "800";
	var height = height || "500";
	
	if(opt != null){
		var query_params = opt.query_params  || {};
	}
	
	$("<div></div>").appendTo("#page-content-wrapper").window({
		title:title,
		href:url,
		width:width,
		height:height,
		//iconCls:"glyphicon glyphicon-edit",
		//closed:true,
		draggable:true,
		shadow:true,
		loadingMessage:"数据加载中，请稍候...",
		collapsible:false,
		minimizable:false,
		maximizable:true,		
		border:true,
		modal:true,
		inline:true,
		queryParams:query_params,
		onClose:function(){
			$(this).window("destroy");
		}
	});
	
	return false;
}

/**
 * 
 * @param url
 * @param idField
 * @param grid_id
 */
/*function handle_treegrid_delete(post_url, idField, grid_id){
	//获取选中的条数
	var idField = idField || "id";
	var grid_id = grid_id || "treegrid";
	var grid_obj = $("#" + grid_id);
	var rows = grid_obj.treegrid('getCheckedNodes');
	//console.log(rows);
	if (rows.length <= 0) {
		//console.log(rows.length);
		$.messager.alert("系统提示", "请选择您要删除的数据。", "warning");
		return;
	}
	$.messager.confirm("确认删除", "您确认要删除所选择的数据吗？", function(re){
			//console.log(rows);
			if(re){
				var checked_ids = "";
				for ( var i = 0; i < rows.length; i++) {
					//获取列名对应的值
					checked_ids += rows[i][idField] + ",";
				}
				checked_ids = checked_ids.substring(0, checked_ids.length - 1);
				var param = {
					checked_ids : checked_ids
				};
				$.ajax({
					url : post_url,
					method : "POST",
					data : param,
					//dataType : "text",
					success : function(data) {
						if(data != null && data.status == "SUCCESS"){
							$.messager.alert("提示", data.message);
							//操作成功
							grid_obj.treegrid('reload');
							//避免部分子项删除后，父项的半选中状态无法取消
							var roots = grid_obj.treegrid('getRoots');
							$.each(roots, function(index, item){
								if(item.checkState == "indeterminate"){
									grid_obj.treegrid('uncheckNode', item[idField]);
								}
							});
							
						}else if(data != null && data.status == "ERROR"){
							$.messager.alert("提示", data.message, "warning");
						}else{
							$.messager.alert("提示", "系统错误，请重试.", "error");
						}
					},
					error: function(e){
						$.messager.alert('系统提示', "网络错误，请重试.", "error");
					}
				});						
		} else {
			//取消选中
			//treegrid没有datagrid的clearChecked()方法@1.4.5 
			$.each(rows, function(index, row){
				grid_obj.treegrid("uncheckNode", row[idField]);
			});
		}
	});
}*/

/**
 * 
 * @param post_url
 * @param grid_id
 * @param id
 * @param type datagrid || treegrid
 */
function handle_datagrid_delete(post_url, grid_id, idField, type, param, success_func){	//获取选中的条数
	
	var grid_id = grid_id || "datagrid";
	var idField = idField || "id";
	var type = type || "datagrid";
	var param = param || {};
	var grid_obj = $("#" + grid_id);
	
	var rows;
	
	if(type == "treegrid"){
		rows = grid_obj.treegrid('getCheckedNodes');
	}else{
		rows = grid_obj.datagrid('getChecked');
	}
	
	//console.log(rows);
	if (rows.length <= 0) {
		//console.log(rows.length);
		$.messager.alert("系统提示", "请选择您要删除的数据。", "warning");
		return;
	}
	$.messager.confirm("确认删除", "您确认要删除所选择的数据吗？", function(re){
			//console.log(rows);
			if(re){
				var checked_ids = "";
				for ( var i = 0; i < rows.length; i++) {
					//获取列名对应的值
					checked_ids += rows[i][idField] + ",";
				}
				checked_ids = checked_ids.substring(0, checked_ids.length - 1);
				
				param.checked_ids = checked_ids;
				
				$.ajax({
					url : post_url,
					method : "POST",
					data : param,
					//dataType : "text",
					success : function(data) {
						if(data != null && data.status == "SUCCESS"){
							$.messager.alert("提示", data.message);
							//操作成功
							if(type == "treegrid"){
								grid_obj.treegrid('reload');
								//避免部分子项删除后，父项的半选中状态无法取消
								var roots = grid_obj.treegrid('getRoots');
								$.each(roots, function(index, item){
									if(item.checkState == "indeterminate"){
										grid_obj.treegrid('uncheckNode', item[idField]);
									}
								});
								
								//bug fix: 删除完成后，下次提交时仍把这次选中的记录提交。
								$.each(rows, function(index, row){
									grid_obj.treegrid("uncheckNode", row[idField]);
								});
								
							}else{
								grid_obj.datagrid("reload");
							}
							
							if(typeof success_func == "function"){
								success_func(data);
							}
							
/*							grid_obj.treegrid('reload');
							//避免部分子项删除后，父项的半选中状态无法取消
							var roots = grid_obj.treegrid('getRoots');
							$.each(roots, function(index, item){
								if(item.checkState == "indeterminate"){
									grid_obj.treegrid('uncheckNode', item[idField]);
								}
							});*/
							
						}else if(data != null && data.status == "ERROR"){
							$.messager.alert("提示", data.message, "warning");
						}else{
							$.messager.alert("提示", "系统错误，请重试.", "error");
						}
					},
					error: function(e){
						$.messager.alert('系统提示', "网络错误，请重试.", "error");
					}
				});						
		} else {
			if(type == "treegrid"){
				//取消选中
				//treegrid没有datagrid的clearChecked()方法@1.4.5 
				$.each(rows, function(index, row){
					grid_obj.treegrid("uncheckNode", row[idField]);
				});
			}else{
				grid_obj.datagrid('clearChecked');
			}
		}
	});
}


function form_data_save(form_id, post_url, success_func, options){
	var form_id = form_id || "#ff";
	var form_obj = $(form_id);
	var opt = options || {window_close:true,window_reload:false,window_location_reload:false};
	
	form_obj.form({
		iframe: true,
	    url: post_url,
	    onSubmit: function(){
			var isValid = $(this).form('validate');
			if (isValid){
				//$.messager.progress('close');	// hide progress bar while the form is invalid
				$.messager.progress({msg:"正在处理，请稍候...",text:""});
			}
			return isValid;	// return false will stop the form submission
	    },
	    success:function(data){
	    	$.messager.progress('close');
	    	try{
		    	var data = $.parseJSON(data);
				if(data.status == "SUCCESS"){
					//关闭弹出窗口
					if(opt.window_close){
						//console.log(opt);
						$(this).parents(".window .window-body").window("close");
					}else if(opt.window_reload){
						//当前窗口重新加载
						$(this).parents(".window .window-body").window("refresh");
					}
					
					if(opt.window_location_reload){
						window.location.reload();
					}
					//$("#treegrid").treegrid("reload");
					//console.log(success_func);
					if(typeof success_func == "function"){
						success_func(data);
					}
				}else if(data.status == "ERROR"){
					$.messager.alert("错误", data.message, "error");
				}else{
					$.messager.alert("网络错误", data);
				}
	    	}catch(e){
	    		console.log(e);
	    		$.messager.alert("系统错误", "系统错误，请联系管理员。");
	    	}
	    },
	    error:function(data){
	    	$.messager.progress('close');
	    	$.messager.alert("系统错误", data);
	    }
	});
	// submit the form
	form_obj.submit();
}

$.fn.serializeObject = function() {
    var o = Object.create(null),
        elementMapper = function(element) {
            element.name = $.camelCase(element.name);
            return element;
        },
        appendToResult = function(i, element) {
            var node = o[element.name];

            if ('undefined' != typeof node && node !== null) {
                o[element.name] = node.push ? node.push(element.value) : [node, element.value];
            } else {
                o[element.name] = element.value;
            }
        };

    $.each($.map(this.serializeArray(), elementMapper), appendToResult);
    return o;
};

//检验
$.extend($.fn.validatebox.defaults.rules, {
	number: {  //判断是否是数字
            validator: function (value, param) {  
                return /^[0-9]+.?[0-9]*$/.test(value);  
            },  
            message: '请输入数字'  
        },
    tel: {  //判断手机格式是否正确
        validator: function (value, param) {  
            return /^1[34578]\d{9}$/.test(value);  
        },  
        message: '请输入正确的电话号码'  
    },
    date:{
    	validator: function (value, param) {  
            return /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01])$/.test(value);  
        },
        message: '请输入正确的日期格式'
    },
    threedigitNumber:{
    	validator: function (value, param) {  
            return /^\d{3}$/.test(value);  
        },  
        message: '请输入3位数字' 
    },
    equaldDate:{
    	validator: function (value, param) {  
            var start_date=$('#start_date').datebox('getValue');
            var end_date=value;
            return end_date>start_date;//有效时间是开始时间小于结束时间
        },  
        message: '结束日期要大于开始日期'
    }
});

//格式化金额小数点后两位
function numformat(id){
	if(id.indexOf(",")>0){
		var idarry=id.split(",");
		$.each(idarry,function(i,element){
			var oldvalue=parseFloat($('#'+element).val());
			$('#'+element).val(oldvalue.toFixed(2));
		});
	}
	else{
		var oldValue=parseFloat($('#'+id).val());
		$('#'+id).val(oldValue.toFixed(2));
	}
}

//修改人的公共页面
function editPerson(token){
	if(token=='design_department'){
		$('#personwindow').window({
			title:"选择设计师"
		});
	}
	else if(token=='budget_department'){
		$('#personwindow').window({
			title:"选择预算员"
		});
	}
	
	$('#personwindow').window('open');
	
	$('#staff_id').select2({
	    allowClear: true,
	    //theme:"default",
	    language: "zh-CN",
	    minimumInputLength: 0,//2
	    placeholder:"请选择",
	    ajax:{
	    	url:"/common/get_department_staff_select2",
		    data: function (params) {
		    	//console.log(params);
		      var query = {
		        q: params.term,
		        page: params.page,
		        rows: 20,
		        query_token:token,
		        //department:'24'
		      };
		      // Query paramters will be ?search=[term]&page=[page]
		      return query;
		    },
	    	delay: 500,
	    	//dataType: 'json',
	    	cache:true,
	     	processResults: function (data, params) {//when use ajax data this method is required!!!
		      return {results:data};
		    },     
	    },
	    //data:person_info,
	    templateResult: function (data) {
	    	//console.log(data.name);
	    	return data.text;
		}
	});
	
	if(token=='design_department'){
		var designer_id=$('#designer_id').val();
		var designer_name=$('#designer_name').val();
		if(designer_id){
			$('#staff_id').html('<option value='+designer_id+' selected>'+designer_name+'</option>');
		}
	}
	else if(token=='budget_department'){
		var estimator_id=$('#estimator_id').val();
		var estimator_name=$('#estimator_name').val();
		if(estimator_id){
			$('#staff_id').html('<option value='+estimator_id+' selected>'+estimator_name+'</option>');
		}
	}
	
	$('#token').val(token);
}

//提交方法
function submit(){
	var token=$('#token').val();
	var order_id=$('#order_id').val();
	var staff_id=$('#staff_id').select2('val');
	
	$.post('/order/update_person',{token:token,order_id:order_id,staff_id:staff_id},function(data){
		$('#personwindow').window('close');
		if(data.status=='SUCCESS'){
			$.messager.alert('提示信息',data.message,'info',function(){
				window.location.reload();
			});
		}
		else{
			$.messager.alert('提示信息',data.message,'warning',function(){
				window.location.reload();
			});
		}
	},"json");
}

function colse(){
	$('#personwindow').window('close');
}