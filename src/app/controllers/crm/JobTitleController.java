package app.controllers.crm;

import java.util.List;

import system.core.BaseController;
import app.dao.CrmDepartment;
import app.dao.CrmJobTitle;
import app.dao.CrmStaff;
import app.models.crm.JobTitleModel;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
/**
 * 此类操作职位名称
 * @author 疏离
 *
 */
public class JobTitleController extends BaseController {
	
	/**
	 * 职位名称索引方法index
	 * @throws Exception
	 */
	public void index() throws Exception{
		set_head_title("职位管理");
		render("job_title_list.html");
	}
	
	/**
	 * 职位名称列表页listAll
	 * @throws Exception
	 */
/*	public void json_job_title_list() throws Exception{
		int department =  getParaToInt("department",0);
		String name = getPara("name", "").trim();
		String jtl = JsonKit.toJson(JobTitleModel.get_job_list(rows, page, department, name));
		renderJson(jtl);
	}*/
	
	public void json_job_title_list() throws Exception{
		int department =  getParaToInt("department",0);
		String name = getPara("name", "").trim();
		List<CrmDepartment> re = JobTitleModel.get_department();//获取部门
		for(int i = 0;i < re.size(); i++){
			int real_id = re.get(i).getInt("real_id");//后台数据库中查到的id有冲突,这里把一级分类的id做了处理(model层做的处理)
			List<CrmJobTitle> listJobTitle = JobTitleModel.get_job_title_list(real_id,department,name);//部门下的职位
			if(listJobTitle.size() > 0){
				re.get(i).put("children",listJobTitle);
			}
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	/**
	 * 职位名称详情方法details
	 * @throws Exception
	 */
	public void job_title_list_edit() throws Exception{
		int id = getParaToInt(0, 0);
		CrmJobTitle jobTitle = null;
		if(id > 0){
			jobTitle = JobTitleModel.get_job_title(id);
		}else{
			jobTitle = new CrmJobTitle();
		}
		setAttr("jobTitle", jobTitle);
	}
	
	/**
	 * 职位名称更新(保存)方法save
	 * @throws Exception
	 */
	public void job_title_list_update() throws Exception{
		String message = "保存成功";
		boolean re = false;
		CrmJobTitle record = getModel(CrmJobTitle.class, "jobTitle");
		
		//数据校验(健壮性)
		if(record.getDepartment() == null){
			message = "上级部门不能为空，请重试！！！";
			render_error_message(message);
			return;
		}
		//record.setDepartment(record.getDepartment());
		
		if (record.getId() != null) {
			// 更新
			message = "更新成功";
			re = record.update();
		} else {
			// 新建
			re = record.save();
		}
		if (re) {
			render_success_message(message);
		} else {
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
	/**
	 * 职位名称删除方法delete
	 * @throws Exception
	 */
	public void job_title_delete() throws Exception{
		String checkedIds = getPara("checked_ids");
		if (StrKit.notBlank(checkedIds)) {
			String[] ids = checkedIds.split(",");
			boolean succeed = false;
			for (String id : ids) {
				List<CrmStaff> listCrmStaffs = CrmStaff.dao.find("select * from crm_staff where job_title_id = ? ", id);
				if (listCrmStaffs.size() > 0) {
					render_error_message("此职位有对应员工，不能删除。");
					return;
				}
				succeed = CrmJobTitle.dao.deleteById(id);
			}
			if(succeed) {
				// renderText("删除成功");
				render_success_message("删除成功");
			}else {
				render_success_message("删除失败");
			}
		} else {
			renderError(404);
		}
	}
	
	/**
	 * select2选择部门
	 * @throws Exception
	 */
	public void json_job_title_select2() throws Exception{
		String q = getPara("q", "");
		List<Record> re = JobTitleModel.get_department(q);
		if(re == null){
			renderText("[]");
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
}
