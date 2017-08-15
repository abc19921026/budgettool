package app.controllers.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import system.core.BaseController;
import system.core.BaseModel;
import system.tools.StringTools;
import app.dao.CrmDepartment;
import app.dao.CrmJobTitle;
import app.dao.CrmStaff;
import app.dao.CrmStaffExtra;
import app.models.crm.DepartmentModel;
import app.models.crm.StaffModel;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
public class StaffController extends BaseController {
	
	//查看所有员工提成
	private static String PERM_ACCESS_ALL_STAFF_COMMISSION = "access_all_staff_commission";
	
	/**
	 * 员工的索引页index
	 * @throws Exception
	 */
	public void index() throws Exception{
		String department_id = getPara(0,"");//从部门管理点进来的
		setAttr("department_id", department_id);
		int job_title_id = getParaToInt(1,0);//从职位点进来的
		setAttr("job_title_id", job_title_id);
		
		String location=getPara(2,"staff");//默认是员工管理
		setAttr("location",location);
		set_head_title("员工管理");
		render("staff_list.html");
	}
	
	/**
	 * 员工列表加载页listAll
	 * @throws Exception
	 */
	public void json_staff_list() throws Exception{
		String name = getPara("name", "").trim();
		String mobile = getPara("mobile", "").trim();
		String sn = getPara("sn","").trim();
		String department =  getPara("department", "");
		String department_id = getPara("department_id","");//获取从部门管理传过来的值
		Integer type=getParaToInt("type",-1);
		if (StrKit.isBlank(department)) {
			department = department_id;
		}
		int job_title_id = getParaToInt("job_title_id",0);//从职位管理传过来的值
		int status = getParaToInt("status",1);
		String location=getPara("location");
		String cs = JsonKit.toJson(StaffModel.get_staff_list(rows, page, name, mobile, sn, department,status,job_title_id,type,location));
		renderJson(cs);
	}
	
	/**
	 * 员工详情页details
	 * @throws Exception
	 */
	public void staff_list_edit() throws Exception{
		int id = getParaToInt("id", 0);
		String department_id=getPara("department_id","");
		
		if(StrKit.notBlank(department_id)){
			CrmDepartment crmDepartment=CrmDepartment.dao.findById(Integer.parseInt(department_id));
			if(crmDepartment==null){
				renderError(404);
				return;
			}
			
			setAttr("crmDepartment", crmDepartment);
		}
		
		String tab=getPara("tab","staffinfo");
		CrmStaff staff = null;
		CrmStaffExtra staffExtra = null;
		if(id > 0){
			//更新
			staff = StaffModel.get_staff(id);
			
			String departments = staff.getDepartments();//从后台数据库中获取多个要保存的部门id
			if (StrKit.notBlank(departments)) {
				String sql= "SELECT cs.id, cs.name FROM crm_department cs WHERE FIND_IN_SET(cs.id, ?)";
				List<CrmDepartment> listDepatrment = CrmDepartment.dao.find(sql, departments);
				setAttr("listDepartment", listDepatrment);
			}
			
			staffExtra = CrmStaffExtra.dao.findById(id);
			if(staffExtra == null){
				staffExtra = new CrmStaffExtra();
			}
			set_head_title("修改员工信息-"+staff.getName());
		}else{
			staff = new CrmStaff();
			staffExtra = new CrmStaffExtra();
			set_head_title("新增员工信息");
		}
		setAttr("staffInfo", staff);
		setAttr("staffExtra", staffExtra);
		setAttr("tab", tab);
		setAttr("department_id", department_id);
	}
	
	/**
	 * 员工保存页update
	 * @throws Exception
	 */
	public void staff_list_update() throws Exception{
		String message = "保存成功";
		boolean re = false;
		
		CrmStaff record = getModel(CrmStaff.class,"staff");
		CrmStaffExtra cse = getModel(CrmStaffExtra.class,"staffExtra");
		
		String[] departments_arry = getParaValues("staff.departments");//从前台获取要保存的多个部门id
		if(departments_arry==null){//没有选择部门
			render_error_message("请选择部门");
			return;
		}
		
		
		String departments = StringTools.implode(departments_arry);
		
		CrmStaff s=StaffModel.get_staff_by_mobile(record.getMobile());
		int uid=current_user_id();
		if(record.getId() != null){
			if(s!=null&&s.getId().compareTo(record.getId())!=0){
				render_error_message("手机号不能重复");
				return;
			}
			
			//更新
			message = "更新成功";
			BaseModel.setUpdateTime(record);
			record.setDepartments(departments);//将前端获取的多个部门id转化成以逗号分割的字符串保存到数据库中
			record.setUpdateUid(uid);
			//TODO 更新前 判断用户账号是否已经绑定到其他员工！
			CrmStaff staff = CrmStaff.dao.findFirst("select * from crm_staff where uid = ? ",record.getUid());
			if (staff != null && staff.getId().compareTo(record.getId()) != 0) {
				render_error_message("用户账号已经绑定到其他员工，请重新选择。");
				return;
			}
			//TODO更新前判断员工编号是否重复
			CrmStaff ff = CrmStaff.dao.findFirst("select * from crm_staff where sn = ? ",record.getSn());
			if (ff != null && ff.getId().compareTo(record.getId()) != 0) {
				render_error_message("员工编号已存在，请重新输入。");
				return;
			}
			re  = record.update();			
			
			CrmStaffExtra crmStaffExtra=CrmStaffExtra.dao.findById(record.getId());
			cse.setId(record.getId());
			if(crmStaffExtra==null){
				cse.save();
			}
			else{
				cse.update();
			}
		}else{
			if(s!=null){
				render_error_message("手机号不能重复");
				return;
			}
			
			//新建
			record.setCreateUid(uid);
			BaseModel.setCreateTime(record);
			BaseModel.setUpdateTime(record);
			record.setDepartments(departments);//将前端获取的多个部门id转化成以逗号分割的字符串保存到数据库中
			
			//TODO 保存前 判断用户账号是否已经绑定到其他员工！
			CrmStaff staff = CrmStaff.dao.findFirst("select * from crm_staff where uid = ? ",record.getUid());
			if (staff != null) {
				render_error_message("用户账号已经绑定到其他员工，请重新选择！");
				return;
			}
			//TODO 保存前判断员工编号是否重复
			CrmStaff ff = CrmStaff.dao.findFirst("select * from crm_staff where sn = ? ",record.getSn());
			if (ff != null) {
				render_error_message("员工编号已存在，请重新输入。");
				return;
			}
			re = record.save();
			if(record.getNickname() == null){
				record.setNickname(record.getName());
				re = record.update();
			}
			
			cse.setId(record.getId());//crm_staff_extra表的id与crm_staff表的id一致
			cse.save();
		}
		if(re){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
	/**
	 * 员工删除页delete
	 * @throws Exception
	 */
	public void staff_list_delete() throws Exception{
		String checked_ids = getPara("checked_ids");
		int uid = current_user_id();
		String status = "SUCCESS",message = "删除成功";
		boolean flag = false;
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i =0; i < delete_ids.length; i++){
				String id = delete_ids[i];
				CrmStaff cs = CrmStaff.dao.findById(Integer.parseInt(id));
				cs.setDeleted(1);
				cs.setUpdateUid(uid);
				BaseModel.setUpdateTime(cs);
				flag = cs.update();
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		if(flag){
			render_message(status,message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
		
	}
	
	/**
	 * select2选择所在部门
	 * @throws Exception
	 */
	public void json_staff_department_select2() throws Exception{
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
	
	/**
	 * 员工选择账号
	 * @throws Exception
	 */
	public void json_uid_select2() throws Exception{
		String q = getPara("q", "");
		List<Record> re = StaffModel.get_uid_details(q);
		if(re == null){
			renderText("[]");
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	/**
	 * 根据部门匹配职位
	 * @throws Exception
	 */
/*	public void list_jobTitle() throws Exception{
		String departmentss = getPara("id", "");
		String[] departments = departmentss.split(",");
			for(String s : departments){
				int department = Integer.parseInt(s);
				if(department > 0){
					List<CrmJobTitle> listJobTitle = null;
					if(department == 32 ||department == 34){//营销部里的职位相同，直接从父类中匹配职位
						listJobTitle= CrmJobTitle.dao.find("select * from crm_job_title where department = ? ",14);//最好写在model里
					}else{
						 listJobTitle= CrmJobTitle.dao.find("select * from crm_job_title where department = ? ",department);//最好写在model里
					}
					
					CrmJobTitle please_select = new CrmJobTitle();
					please_select.setId(0);
					please_select.setName("请选择职位");
					listJobTitle.add(0, please_select);//在选择框里加一个"请选择职位"
					
				    renderJson(JsonKit.toJson(listJobTitle));
				}else{
					 renderJson("");
				}
			}
	}*/
	public void list_jobTitle() throws Exception{
		List<CrmJobTitle> listJobTitle = CrmJobTitle.dao.find("select * from crm_job_title ");
		CrmJobTitle please_select = new CrmJobTitle();
		please_select.setId(0);
		please_select.setName("请选择职位");
		listJobTitle.add(0, please_select);//在选择框里加一个"请选择职位"
		
	    renderJson(JsonKit.toJson(listJobTitle));
	}
	
	/**
	 * 获得员工电话
	 * @throws Exception
	 */
	public void get_staff_tel() throws Exception{
		String name=getPara("name");
		CrmStaff crmStaff=StaffModel.get_staff_by_name(name);
		String tel="";
		if(crmStaff!=null){//存在
			tel=crmStaff.getMobile();
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("tel", tel);
		renderJson(map);
	}

	
	/**
	 * 跳转到工种配置的页面
	 * @throws Exception
	 */
	/*public void work_type_config() throws Exception{
		int staff_id=getParaToInt("staff_id");
		CrmStaff crmStaff=StaffModel.get_staff(staff_id);
		if(crmStaff==null){
			renderError(404);
			return;
		}
		
		setAttr("crmStaff", crmStaff);
		
		List<CrmStaffWorkType> workTypes=StaffModel.get_staff_work_types(staff_id);
		setAttr("workTypes", workTypes);
		
		render("work_type_config.html");
	}*/
	
	/**
	 * 配置保存
	 * @throws Exception
	 */
	/*public void work_type_save() throws Exception{
		boolean flag=false;
		
		String message="";
		int staff_id=getParaToInt("staff_id");
		
		//TODO 保存之前先清除员工对应的工种
		StaffModel.delete_staff_work_type(staff_id);
		Integer[] worktypes=getParaValuesToInt("worktypes");
		
		if(worktypes==null){
			render_error_message("请至少选择一个工种");
			return;
		}
		
		CrmStaffWorkType crmStaffWorkType=new CrmStaffWorkType();
		for(Integer worktype:worktypes){
			crmStaffWorkType.setId(null);
			crmStaffWorkType.setWorkTypeId(worktype);
			crmStaffWorkType.setStaffId(staff_id);
			flag=crmStaffWorkType.save();
			
			message="工种设置成功";
			if(flag==false){
				break;
			}
		}
		
		if(flag){
			render_success_message(message);
		}
		
		else{
			render_error_message("网络错误，请联系管理员");
		}
	}*/
}
