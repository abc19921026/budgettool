
function field_formatter_staff_name(value,data){
	if(data.type){//一级父节点和footer
		return "";
	}
	else{
		if(data.cost_id){//基础库的费用
			if(value && data.staff_id){
				return "<a href='/crm/staff/commission?staff_id="+data.staff_id+"&tab=costfee' onclick=\"window.event.stopPropagation();\" target='_blank'>"+value+"<a>";
			}
			
			return value;
		}
		else{
			if(data.id<0){
				return "";
			}
			else{
				if(data.staff_id){
					return '<a href="javascript:;" onclick="selectStaff('+data.id+')">'+data.staff_name+'</a>';
				}
				else{
					return '<a href="javascript:;" onclick="selectStaff('+data.id+')">选择员工</a>';
				}
			}
		}
	}
}