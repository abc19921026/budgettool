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
    text:'取消',
    iconCls:'fa fa-reply',
    handler:function(){dg_reject();}
},'-',{
    text:'保存',
    iconCls:'glyphicon glyphicon-saved',
    handler:function(){dg_data_save();}
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
		}else{
			//普通项目无需选择主材
			re += "<a target='_blank' onclick='dg_data_edit("+ row.id +");return false;' href='javascript:;'>编辑</a>";
		}
		return re;
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
		//下面判断treegrid如果加载成功的
		if(data[0] != null){
			budget_class_id = data[0].id;
			load_budget_item(budget_class_id);
		}
		//console.log(first_row);

	}

}
//从这里我们就开始学习treegrid的双击事件
function onTgDblclickRow(row){//treegrid onDblclickRow事件参数不一样！！！

	var budget_class_id = row.id;
	//这个就是短处当前行的id
	load_budget_item(budget_class_id);
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
	clear_row_changed();//放弃修改，清空修改项目，这里就是简单进行了清空处理
	//首先把budget_class_id进行保存
	$.hash("budget_class_id", budget_class_id);
	//注意下面代码是什么意思？
	$("#dg").datagrid("load", {"budget_class_id":budget_class_id});
	//选中当前行
	$("#tg").treegrid("select", budget_class_id);
	//下面是返回当前节点
	var node = $("#tg").treegrid("getSelected");
	var title = node.name; //这里name属性和text属性一样都会取出名字
	//var row = $("#dg").datagrid("highlightRow", row_index);
	//var title = row_name;
	$("#easyui-layout").layout("panel", "center").panel("setTitle", title);
}
//这个方法就是当datagrid加载成功的时候我们要处理的东西。估计就是处理那个选择框的事件
function onDgLoadSuccess(data){
	//console.log(data);
	$.each(data.rows, function(index, element){
		//页面加载完成后，开启 数量 编辑框
		if(element.type == 1){//手动计算成本
			$('#dg').datagrid('beginEdit', index); //开始编辑节点
		}else{
			return;
		}
		
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



function tg_expand_all(){
	$("#tg").treegrid("expandAll");
}

function tg_collapse_all(){
	//var root = $("#tg").treegrid("getRoot");
	//console.log(root);
	$("#tg").treegrid("collapseAll");
}


function dg_data_edit(budget_item_id){
	//console.log(id);
	new_window('/budget/budget_item_edit?id=' + budget_item_id, "", 800, 610);
}

   
//datagrid保存按钮事件
function dg_data_save(){
    /*if(!has_row_changed()){
    	$.messager.alert("提示", "没有任何修改", "info");return;
    }*/
    
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
     	$.ajax({
    		url: "/budget/cost/advanced_edit_save?budget_class_id=" + budget_class_id+"&budget_id="+BUDGET_ID,
    		data: {"data":post_data},
    		method: "post",
    		success: function(){
    			$.messager.progress("close");
    			clear_row_changed();//保存成功之后清空修改的行记录
    			$.messager.alert("","保存成功.");
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

