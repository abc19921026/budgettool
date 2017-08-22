/**
 * 
 */
/**
 * 左边 treegrid toolbar
 */
var tg_toolbar = [{
    text:'新建',
    iconCls:'glyphicon glyphicon-plus',
    handler:function(){tg_data_add();}
},/*'-',{
    text:'展开',
    iconCls:'glyphicon glyphicon-indent-left',
    handler:function(){tg_expand_all();}
},*/'-',{
    text:'收起',
    iconCls:'glyphicon glyphicon-indent-right',
    handler:function(){tg_collapse_all();}
}];

/*
 * 右边 datagrid toolbar
 */
var dg_toolbar = [{
    text:'新建',
    iconCls:'glyphicon glyphicon-plus',
    handler:function(){dg_data_add();}
},'-',{
    text:'删除',
    iconCls:'glyphicon glyphicon-trash',
    handler:function(){dg_data_delete();}
},'-',{
    text:'取消',
    iconCls:'fa fa-reply',
    handler:function(){dg_reject();}
},'-',{
    text:'保存',
    iconCls:'glyphicon glyphicon-saved',
    handler:function(){dg_data_save();}
},'-',{
    text:'排序',
    iconCls:'glyphicon glyphicon-list',
    handler:function(){dg_data_sort();}
},'-',{
    text:'上移',
    iconCls:'glyphicon glyphicon-arrow-up',
    handler:function(){dg_data_move_up();}
},'-',{
    text:'下移',
    iconCls:'glyphicon glyphicon-arrow-down',
    handler:function(){dg_data_move_down();}
}];

function field_formatter_tg_sn(value, row, index){
	return "&nbsp;&nbsp;" + value;
}
/**
 * treegid 操作列
 */
function field_formatter_tg_operation(value, row, index){
		
		var re = "";
		re += "&nbsp;|&nbsp;<a target='_blank' onclick='tg_data_edit(" + row.id + ");return false;' href='javascript:;'>编辑</a>";
		return re;
}

function field_formatter_dg_name(value, row, index){
	var re = "";
	if(row.description != null){
		re = "<a href=\"javascript:;\" title=\""+ row.description +"\" class=\"easyui-tooltip\">"+ value +"</a>";
	}else{
		re = value;
	}
	return re;
}
function field_formatter_dg_printable(value, row, index){
	var re = "";
	//if(row.description != null){
		//re = "<a href=\"javascript:;\" title=\""+ row.description +"\" class=\"easyui-tooltip\">"+ value +"</a>";
	//}else{
		re = value;
	//}
	return re;
}

function field_formatter_dg_operation(value, row, index){
		
		var re = "";
		//re += "&nbsp;|&nbsp;";
		//console.log(row.material_attached);
		if(row.material_attached == 1 && row.material_id == 0){
			re += "<a href='javascript:new_window(\"/budget/budget_select_material?id="+row.id+"\")'>选择主材</a>";
		}else if(row.material_id != 0){
			re += "<a href='javascript:new_window(\"/budget/budget_select_material?material_id="+row.material_id+"&id="+row.id+"\")'>重新选择</a>";
		}
		else if(row.material_id==0 && row.material_attached==-1){
			re += "";
		}
		else{
			//普通项目无需选择主材
			re += "<a target='_blank' onclick='dg_data_edit("+ row.id +");return false;' href='javascript:;'>编辑</a>";
		}
		return re;
}

function field_formatter_money_fixed(value,row){
	//console.log(value);
	if(!value&&value!=0){
		return "";
	}
	else{
		return field_formatter_money(value);
	}
}

//左侧树加载完成后执行，从hash中获得当前分类id，实现刷新也能加载数据
function onTgLoadSuccess(row, data){
	
	//$("#tg").treegrid("selectAll");
	var budget_class_id = $.hash("budget_class_id");
	//var row_index = $.hash("row_index");
	//var row_name = $.hash("row_name");
	//console.log(budget_class_id);
	//console.log(row);
	//console.log(data);
	if(budget_class_id > 0){
		//console.log("init");
		load_budget_item(budget_class_id);
	}else{
		//首次进入页面，加载第一行的数据
		//var first_row = $("#tg").treegrid("getRoots");
		if(data[0] != null){
			budget_class_id = data[0].id;
			load_budget_item(budget_class_id);
		}
		//console.log(first_row);

	}
}

function onTgDblclickRow(row){//treegrid onDblclickRow事件参数不一样！！！

	var budget_class_id = row.id;
	
	load_budget_item(budget_class_id);
}

function onTgContextMenu(e, row){
			
	//选中当前行
	if(row == null){return;}
	$("#tg").treegrid("unselectAll").treegrid("select", row.id);
	e.preventDefault();
	
	//console.log(row)
	var item = $('#mm').menu('findItem', '添加下级分类');
	if(row.parent_id > 0){
		$("#mm").menu("disableItem", item.target);	
	}else{
		$("#mm").menu("enableItem", item.target);	
	}
	
	$('#mm').menu('show', {
		left: e.pageX,
		top: e.pageY
	});
}

function load_budget_item(budget_class_id){
	//$("#dg").datagrid("highlightRow", row_index);
	//TODO 如果没保存，弹出提示框
	
	if(has_row_changed()){
		//console.log(CHANGED_ROWS);
		$.messager.confirm("要离开此页面吗？", "系统可能不会保存您所做的更改。", function(re){
			if(re){
				load_budget_item_now(budget_class_id);
			}else{
				//不跳转
			}
		});
	}else{
		load_budget_item_now(budget_class_id);
	}	

}

function load_budget_item_now(budget_class_id){
	clear_row_changed();//放弃修改，清空修改项目
	
	$.hash("budget_class_id", budget_class_id);
	$("#dg").datagrid("load", {"budget_class_id":budget_class_id});
	//选中当前行
	$("#tg").treegrid("select", budget_class_id);
	
	var node = $("#tg").treegrid("getSelected");
	var title = node.name;
	//var row = $("#dg").datagrid("highlightRow", row_index);
	//var title = row_name;
	$("#easyui-layout").layout("panel", "center").panel("setTitle", title);
}

/*function enableDnd(){
	$('#dg').datagrid('enableDnd');
}*/
function onDgLoadSuccess(data){
	//console.log(data);
	//
	$.each(data.rows, function(index, element){		
		//页面加载完成后，开启 数量 编辑框
		$('#dg').datagrid('beginEdit', index);
		//绑定 编辑器 事件
        var ed = $('#dg').datagrid('getEditor', {index:index, field: "num"});
		//console.log(element);
		var initial_num = element.num;
        ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target))
        .data("row-index", index)
        .data("initial-num", initial_num)
        .attr("tabindex", index+1);//把index放在自定义数据中
        //textbox是在原input 后 新加的input
        //textbox('textbox') Return the textbox object. The user can bind any events to this editing box.
        //console.log(ed);
        
        
        ($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).bind('keyup', function(e){
        	//alert("hi");
        	//移除焦点时更新金额
         	var num = $(this).val();
         	var initial_num = $(this).data("initial-num");//页面初始化时数量，用来判断用户是否修改了数量值
         	var NUM_CHANGED = false;
         	if(num != initial_num){
         		NUM_CHANGED = true;
         	}
         	
        	var row_index = $(this).data("row-index");
        	//console.log(index);
        	var row = $('#dg').datagrid('getRows')[row_index];
        	//console.log(row);
        	var price = row.price;
        	//console.log(val);
        	//console.log(price);
        	var amount = price * num; 
        	amount = accounting.formatMoney(amount);
        	
        	//$('#dg').datagrid('endEdit', row_index);
        	//更新金额
        	//$('#dg').datagrid('updateRow', {"index":row_index, row:{"amount":amount}});
        	//更新后再次开启编辑框
        	//console.log(amount);
        	//找到金额
        	//$(this).closest("td[field='num']").css({border:"1px solid #f00"});
        	//定位到相同行的金额列，更新金额
        	$(this).closest("td[field='num']").siblings("td[field='amount']").find("div").html(amount);
        	

        	//$('#dg').datagrid('selectRow', row_index);
        	//var row = $('#dg').datagrid('getRows')[row_index];
        	if(NUM_CHANGED){
        		//$(this).closest("tr.datagrid-row").addClass("datagrid-row-changed");
        		CHANGED_ROWS.push(row_index);//有变动的行加入
        	}else{
        		//$(this).closest("tr.datagrid-row").removeClass("datagrid-row-changed");
        		if(CHANGED_ROWS.indexOf(row_index) >= 0){
        			CHANGED_ROWS.splice(CHANGED_ROWS.indexOf(row_index), 1);//有变动的行删除	
				}
        	}
        	update_dg_row_changed_style($(this), row_index);
        	//$('#dg').datagrid('beginEdit', row_index);
        	//var price = 
        }).bind("focus",function(e){
        	//获得焦点时选中
        	$(this).select();
        }).bind("keyup",function(e){
        	var row_index = $(this).data("row-index");
        	//console.log(index);
			
        	
            var code = e.keyCode || e.which;
			//console.log(code);
            if(code == 13) { //Enter keycode 回车键
              //Trigger code to save row
                                  //This executes onAfterEdit event code
              //更新 金额列

				
              	//$('#dg').datagrid('endEdit', index);
            	dg_data_save();
            }else if(code == 40){
            	//方向下键
            	//var row = $('#dg').datagrid('getRows')[row_index + 1];
            	//获取下一行的编辑框
            	var ed = $('#dg').datagrid('getEditor', {index:row_index + 1, field: "num"});
            	//console.log(row_index);
            	if(!ed){return;}
            	($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();
            	
            }else if(code == 38){
            	//方向上键
            	if(row_index == 0){
            		return;
            	}
            	var ed = $('#dg').datagrid('getEditor', {index:row_index - 1, field: "num"});
            	if(!ed){return;}
            	//console.log(row_index);
            	($(ed.target).data('textbox') ? $(ed.target).textbox('textbox') : $(ed.target)).focus();            	
            }
        });
        
        
        //处理打印checkbox 事件
        var ed_printable = $('#dg').datagrid('getEditor', {index:index, field: "printable"});
        
        $(ed_printable.target).data("INITIAL_VALUE", element.printable).data("ROW_INDEX", index);
        $(ed_printable.target).change(function(){
        	var val = $(this).is(":checked") ? 1 : 0;
        	var initial_value = $(this).data("INITIAL_VALUE");
        	var row_index = $(this).data("ROW_INDEX");
        	//var val = $(this).val();
        	//console.log(initial_value);
        	//console.log(row_index);
        	//console.log(val);
        	if(val == initial_value){
        		//没有修改
        		if(CHANGED_ROWS_PRINTABLE.indexOf(row_index) >= 0){
        			CHANGED_ROWS_PRINTABLE.splice(CHANGED_ROWS_PRINTABLE.indexOf(row_index), 1);//有变动的行删除	
				}
        	}else{
        		//有修改
        		CHANGED_ROWS_PRINTABLE.push(row_index);//有变动的行加入
        	}
        	update_dg_row_changed_style($(this), row_index);
        });
	});
}

/**
 * 判断当前页面是否某行的数据有修改
 * @returns
 */
function has_row_changed(){
	var changed = false;
	
	if(CHANGED_ROWS.length > 0 || CHANGED_ROWS_PRINTABLE.length > 0){
		changed = true;
	}
	return changed;
}

function clear_row_changed(){
	CHANGED_ROWS = [];
	CHANGED_ROWS_PRINTABLE = [];
}

function update_dg_row_changed_style(row_obj, row_index){
	if(CHANGED_ROWS.indexOf(row_index) >= 0 || CHANGED_ROWS_PRINTABLE.indexOf(row_index) >= 0){
		row_obj.closest("tr.datagrid-row").addClass("datagrid-row-changed");
	}else{
		row_obj.closest("tr.datagrid-row").removeClass("datagrid-row-changed");
	}
}

function tg_data_add(){
	var budget_id = BUDGET_ID;
	var section = SECTION;
	new_window('../../budget_class_edit?level=top&budget_id=' + budget_id + "&section=" + section);
		
}

/**
 * 预算分类右键菜单添加子类
 */
function tg_add_sub_class(){
	var budget_id = BUDGET_ID;
	var section = SECTION;
	var budget_class_id = $("#tg").treegrid("getSelected").id;
	$.ajax({
		type:'POST',
		url:'/budget/check_has_budget_item?id='+budget_class_id,
		dataType:"json",
		success:function(data){
			if(data>0){
				$.messager.alert("提示", "该预算分类下存在项目，不能新建下级预算分类，如需新建下级预算分类，请先删除项目", "info");
				return;
			}else{
				new_window('../../budget_class_edit?budget_id=' + budget_id + "&parent_id=" + budget_class_id + "&section=" + section);
			}
		}
	});	
	
}

function tg_item_move(index, up_down){
	//向后台发送请求的方式还是前台实现
	var rows = $("#dg").datagrid("getRows");
	return false;
}

function tg_expand_all(){
	$("#tg").treegrid("expandAll");
}

function tg_collapse_all(){
	//var root = $("#tg").treegrid("getRoot");
	//console.log(root);
	$("#tg").treegrid("collapseAll");
}

/**
*	新建工程条目
*/
function dg_data_add(){
	var budget_class_id = $.hash("budget_class_id");
	if(budget_class_id == null){
		var selected_row = $("#tg").treegrid("getSelected");
		//console.log(selected_row);
		if(selected_row != null){
			budget_class_id = selected_row.id;
			$.hash("budget_class_id", budget_class_id);
		}else{
			$.messager.alert("提示", "请选择预算分类", "info");
			return;
		}
	}else {
		$.ajax({
			type:'POST',
			url:'/budget/check_has_subclass?id='+budget_class_id,
			dataType:"json",
			success:function(data){
				//console.log(data);
				if(data>0){
					$.messager.alert("提示", "该预算分类下存在下级预算分类，不能新建项目，如需新建项目，请先删除下级预算分类", "info");
					return;
				}else{
					new_window('../../budget_item_edit?budget_class_id=' + budget_class_id,"",1200,700);
				}
			}
		});		
	}
	
}

function dg_data_edit(budget_item_id){
	//console.log(id);
	new_window('/budget/budget_item_edit?id=' + budget_item_id, "", 1200, 700);
}

/**
 * 预算分类编辑
 */
function tg_data_edit(budget_class_id){
	//console.log(budget_class_id);
	new_window('/budget/budget_class_edit?budget_class_id=' + budget_class_id);
}

//datagrid保存按钮事件
function dg_data_save(){
    if(!has_row_changed()){
    	$.messager.alert("提示", "没有任何修改", "info");return;budget_item_edit
    }
    
    var rows = $("#dg").datagrid("getRows");

    if(rows.length > 0){   
    	//console.log(rows);
    	$('#dg').datagrid('acceptChanges');
    	update_data(rows);
    }else{
    	return true;
    	//$.messager.alert("", "没有任何修改");
    }
}
function dg_data_sort(budget_class_id){
	var budget_class_id = $.hash("budget_class_id");
	new_window('/budget/item/budget_item_weight_sort?budget_class_id='+budget_class_id,"",700,750);
}
function dg_reject(){
    $('#dg').datagrid('rejectChanges');
    clear_row_changed();
}

//向后台发送请求
function update_data(rows){
	//console.log(rows);
	$.messager.progress({msg:"正在处理，请稍候...",text:""});
	
	var post_data = [];
	$.each(rows, function(k, v){
		post_data.push(v);
	});
	try{
    	post_data = JSON.stringify(post_data);
    	var budget_class_id = $.hash("budget_class_id");
    	//console.log(post_data);
     	$.ajax({
    		url: "/budget/item/advanced_edit_save?budget_class_id=" + budget_class_id,
    		data: {"data":post_data},
    		method: "post",
    		success: function(){
    			$.messager.progress("close");
    			clear_row_changed();//保存成功之后清空修改的行记录
    			//$.messager.alert("","保存成功.");
    			$("#dg").datagrid("reload");
    			$("#tg").treegrid("reload");
    		},
    		error:function(){
    		   $.messager.progress("close");
    			$.messager.alert("","保存失败，请重试.", "error",function(re){
    				window.location.reload();
    			});
    		}
    	}); 
	}catch(e){
		alert("客户端错误，请更换浏览器重试.");
	}
}

function dg_data_delete(){
    var post_url = "/budget/item/json_delete";
	handle_datagrid_delete(post_url, "dg", "id", "datagrid", "", function(){
		$("#tg").treegrid("reload");
	});
}

function budget_class_edit(){
	var row = $("#tg").treegrid("getSelected");
	//console.log(row);
	tg_data_edit(row.id);
}

function budget_class_delete(){
	var row = $("#tg").treegrid("getSelected");
	//console.log(row);
	var post_url = "/budget/json_budget_class_delete";
	//handle_datagrid_delete(post_url, "dg");
	var checked_ids = row.id;
	var param = {};
	param.checked_ids = checked_ids;
	$.messager.confirm("确认删除", "您确认要删除所选择的数据吗？", function(re){
		//console.log(rows);
		if(re){
		
			$.ajax({
				url : post_url,
				method : "POST",
				data : param,
				//dataType : "text",
				success : function(data) {
					if(data != null && data.status == "SUCCESS"){
						//$.messager.alert("提示", data.message);
						//操作成功
						$("#tg").treegrid("reload");
						
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
		} else {}
	});
}

function check(){
	$.messager.confirm('提示信息','确认要审核?',function(r){
	    if (r){
	    	var budget_id=$('#budget_id').val();
	    	$.ajax({
				url :'/budget/order/check',
				method : "POST",
				data : {budget_id:budget_id},
				success : function(data) {
					if(data.status="SUCCESS"){
						$.messager.alert('提示信息', data.message, "info");
					}
					else{
						$.messager.alert('提示信息',data.message, "error");
					}
				}
			});
	    }
	});
}