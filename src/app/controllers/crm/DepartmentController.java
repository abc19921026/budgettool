package app.controllers.crm;

import java.util.List;

import system.core.BaseController;
import app.dao.CrmDepartment;
import app.dao.CrmStaff;
import app.models.crm.DepartmentModel;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
public class DepartmentController extends BaseController {
	
	/**
	 * 部门索引页index
	 * @throws Exception
	 */
	public void index() throws Exception{
		set_head_title("部门管理");
		render("department_list.html");
	}
	
	/**
	 * 部门列表加载页listAll
	 * @throws Exception
	 */
	public void json_department_list() throws Exception{
		List<CrmDepartment> re = DepartmentModel.get_department_class();//一级部门
		//调用递归判断是否有下级部门
		re = DepartmentModel.get_department_recursive(re);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	/**
	 * 部门详情页details
	 * @throws Exception
	 */
	public void department_list_edit() throws Exception{
		int id = getParaToInt(0, 0);
		CrmDepartment department = null;
		if(id > 0){
			department = DepartmentModel.get_department(id);
		}else{
			department = new CrmDepartment();
		}
		setAttr("department", department);
	}
	
	/**
	 * 部门保存页update
	 * @throws Exception
	 */
	public void department_list_update() throws Exception{
		String message = "保存成功";
		boolean re = false;
		CrmDepartment record = getModel(CrmDepartment.class,"department");
		CrmDepartment check = CrmDepartment.dao.findFirst("select * from crm_department where query_token = ? ",record.getQueryToken());
		if(record.getId() != null){
			//更新
			message = "更新成功";
			//BaseModel.setCreateTime(record);
			if (check != null && check.getId().compareTo(record.getId()) != 0) {
				render_error_message("部门索引不能重复。");
				return;
			}
			re = record.update();
		}else{
			//新建
			//BaseModel.setCreateTime(record);
			if (check != null) {
				render_error_message("部门索引不能重复。");
				return;
			}
			re = record.save();
		}
		if(re){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
		
	}
	
	/**
	 * 部门删除页delete
	 * @throws Exception
	 */
	public void department_list_delete() throws Exception{
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS",message = "删除成功";
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i = 0; i < delete_ids.length;i++){
				String id = delete_ids[i];
				List<CrmStaff> listStaffs = CrmStaff.dao.find("select * from crm_staff where FIND_IN_SET(?,departments) ", id);
				if (listStaffs.size() > 0) {//有员工不能删除
					render_error_message("此部门有员工，不能删除。");
					return;
				}
				List<CrmDepartment> listDepartments = CrmDepartment.dao.find("select * from crm_department where pid = ? ", id);
				if (listDepartments.size() > 0) {//有子部门不能删除
					render_error_message("此部门有子部门，不能删除。");
					return;
				}
				CrmDepartment.dao.deleteById(id);
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status,message);
	}
	
	/**
	 * select2获取上级部门id
	 * @throws Exception
	 */
	public void json_department_select2() throws Exception{
		String q = getPara("q", "");
		List<Record> re = DepartmentModel.get_staff_department_name(q);

		if(re == null){
			renderText("[]");
		}else{
			for(int i = 0; i< re.size(); i++){
				
				int id = re.get(i).getInt("id");

				List<Record> re_dmm = DepartmentModel.get_staff_department_name(id, q);
				if(re_dmm.size() > 0){
					re.get(i).set("children", re_dmm);
				}else{
					re.get(i);
				}
			}
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
}
