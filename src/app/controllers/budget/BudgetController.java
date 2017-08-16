package app.controllers.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.dao.Budget;
import app.dao.BudgetClass;
import app.models.budget.BudgetClassModel;
import app.models.budget.BudgetItemModel;
import app.models.budget.BudgetModel;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import system.core.BaseController;
import system.core.BaseModel;
import system.core.LoginInterceptor;
import system.tools.DateTools;

public class BudgetController extends BaseController{
	/**
	 * 预算列表页（按项目预算列出）
	 * @throws Exception 
	 */
	@Clear(LoginInterceptor.class)
	public void index() throws Exception{		
		String enddate = DateTools.getDate();//获取系统当前日期
		String startdate = DateTools.daysAgo(enddate,365);//获取系统当前日期一年之前的日期
		setAttr("startdate",startdate);
		setAttr("enddate",enddate);
		set_head_title("预算中心-项目预算");
		render("budget_list.html");
	}
	
	/**
	 * 预算列表数据加载listAll
	 * @throws Exception 
	 */
	@Clear(LoginInterceptor.class)
	public void index_json() throws Exception{
		String sn = getPara("sn", "").trim();
		String startdate = getPara("startdate", "").trim();
		String enddate =  DateTools.daysLater(getPara("enddate", "").trim(),1);//查询时当前日期+1
		Map<String,Object> re = BudgetModel.get_json_budget_list(rows,page,sn,startdate,enddate);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	/**
	 * 跳转到预算新建/修改页面
	 * @throws Exception
	 */
	@Clear(LoginInterceptor.class)
	public void budget_add()throws Exception{
		Integer id = getParaToInt("id",0);
		Budget budget = null;
		if(id>0){
			budget = Budget.dao.findById(id);
		}else{
			budget = new Budget();
		}	
		setAttr("budget", budget);
	}
	/**
	 * 保存预算新建/修改
	 * @throws Exception
	 */
	@Clear(LoginInterceptor.class)
	public void budget_list_update()throws Exception{
		Budget budget = getModel(Budget.class);
		boolean flag = false;
		if(budget.getName()!=null){
			if(budget.getId()!=null){
				BaseModel.setUpdateTime(budget);
				flag = budget.update();
			}else{
				BaseModel.setCreateTime(budget);
				BaseModel.setUpdateTime(budget);
				budget.setSn(BudgetModel.generate_budget_sn());
				budget.setType("budget");
				flag = budget.save();
			}
			if(flag==true){
				render_success_message("保存成功");
			}else{
				render_error_message("保存失败，请重新操作");
			}
		}else{
			render_error_message("请输入预算名称");
		}
	}
	
	/**
	 * 批量删除预算
	 * @throws Exception
	 */
	@Clear(LoginInterceptor.class)
	public void budget_list_delete() throws Exception{
		String checked_ids = getPara("checked_ids");
		int force_delete = getParaToInt("force_delete", 0);//是否强制删除
		String status = "SUCCESS", message = "删除成功";
		if (StrKit.notBlank(checked_ids)) {
			String[] delete_ids = checked_ids.split(",");
			for (int i = 0; i < delete_ids.length; i++) {
				String id = delete_ids[i];
				int budget_id = Integer.parseInt(id);
				//检查预算下的分类是否为空，如果不为空，禁止删除
				if(force_delete == 0 && BudgetClassModel.get_budget_class_num_by_budget_id(budget_id) > 0){
					render_error_message("请先删除预算中的项目!");
					return;//required!
				}else{
					//删除预算分类
					BudgetClassModel.delete_budget_class_by_budget_id(budget_id);
					//删除预算项目
					BudgetItemModel.delete_budget_item_by_budget_id(budget_id);
					//删除预算line item
					//BudgetLineItemModel.delete_budget_line_item_by_budget_id(budget_id);
					//最后删除预算
					Budget.dao.deleteById(id);					
				}
			}
		} else {
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		render_message(status, message);
	}
	
	@Clear(LoginInterceptor.class)
	public void json_budget_class()throws Exception{
		int budget_id = getParaToInt(0, 0);
		int section = getParaToInt("section", 0);
		if(budget_id <=0){
			renderError(404);
		}
		//按不同部分section 加载
		List<BudgetClass> re = BudgetClassModel.get_budget_class(budget_id, section);
		
		for(int i = 0; i < re.size(); i++){
			List<BudgetClass> budgetSubClass = BudgetClassModel.get_budget_subclass(re.get(i).getId());
			if(budgetSubClass.size() > 0){
				re.get(i).put("children", budgetSubClass);
			}
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	@Clear(LoginInterceptor.class)
	public void budget_class_edit()throws Exception{
		int id = getParaToInt("budget_class_id", 0);
		int budget_id = getParaToInt("budget_id", 0);
		int section = getParaToInt("section", 0);//0基础1主材2优惠与汇总
		//上级分类id
		int parent_id = getParaToInt("parent_id", 0);
		
		BudgetClass record = new BudgetClass();

		if(id > 0){
			//更新
			record = BudgetClassModel.get_budget_class_with_parent(id);
			budget_id = record.getBudgetId();
		}else{
			//新建
			record.setBudgetId(budget_id);
			record.setSection(section);
		}		
		//新建时指定了上级分类
		if(parent_id > 0){
			record.setParentId(parent_id);
			String parent_name = BudgetClassModel.dao.findById(parent_id).getName();
			record.put("parent_name", parent_name);
		}
		
		setAttr("record", record);
		setAttr("budget_id", budget_id);
		setAttr("section", section);
		
		String level = getPara("level", "");
		render("budget_class_edit.html");		
	}
	
	/**
	 * 工程分类
	 */
	@Clear(LoginInterceptor.class)
	public void json_class_catalog_select2()throws Exception{
		String q = getPara("q", "");
		int section = getParaToInt("section", 0);
		int parent_id = getParaToInt("parent_id", 0);
		Integer type = getParaToInt("type");
		if(type==0){
			List<Record> re = BudgetItemModel.get_class_catalog(q, section,0,0);
			if(re == null){
				renderText("[]");
				return;
			}
			String jsonList = JsonKit.toJson(re);
			renderJson(jsonList);
		}else{
			HashSet<Record> set = new HashSet<Record>();
			BudgetClass top_bc = BudgetClass.dao.findById(parent_id);
			List<Record> list = BudgetItemModel.get_same_class_catalog(top_bc.getName());
			for(Record record : list){
				List<Record> catalog = BudgetItemModel.get_class_catalog(q, section,1,record.getInt("id"));
				if(catalog.size()>0){
					set.addAll(catalog);					
				}
			}
			List<Record> re = new ArrayList<Record>();
			re.addAll(set);
			if(re.size()==0){
				renderText("[]");
				return;
			}
			String jsonList = JsonKit.toJson(re);
			renderJson(jsonList);
		}
	}	
	@Clear(LoginInterceptor.class)
	public void json_budget_class_update(){
		String message = "保存成功";
		boolean re = false;
		BudgetClass record = getModel(BudgetClass.class, "");
		if(record.getParentId()!= null && record.getParentId() > 0){
			//如果是子分类，把上级分类的project_item_catalog_id 当作子类的project_item_catalog_id
			int project_item_catalog_id = BudgetClassModel.dao.findById(record.getParentId()).getProjectItemCatalogId();
			record.setProjectItemCatalogId(project_item_catalog_id);
		}
		if(record.getId() != null){
			//更新
			message = "更新成功";
			BaseModel.setUpdateTime(record);
			re = record.update();
		}else{
			//新建
			BaseModel.setCreateTime(record);
			BaseModel.setUpdateTime(record);
			re = record.save();
		}
		//todo 更新序号及编号
		int budget_id = record.getBudgetId();
//		BudgetClassModel.update_budget_class_sn(budget_id);
		if(re){
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("budget_class", record);
			render_success_message(message, data, 1);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
	}
	
	/**
	 * 删除一条预算分类
	 */
	@Clear(LoginInterceptor.class)
	public void json_budget_class_delete()throws Exception{		
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS", message = "删除成功";
		int budget_id = 0;
		if (StrKit.notBlank(checked_ids)) {
			String[] delete_ids = checked_ids.split(",");
			int budget_class_id = 0;
			//BudgetClass bc = null;
			for (int i = 0; i < delete_ids.length; i++) {
				String id = delete_ids[i];
				budget_class_id = Integer.parseInt(id);
				BudgetClass bc = BudgetClass.dao.findById(budget_class_id);
				if(bc == null){
					continue;					
				}
				if(budget_id <= 0){
					budget_id = BudgetClass.dao.findById(budget_class_id).getBudgetId();
				}
				//判断该分类下是否有子工程项目，如果有，禁止删除
				if(BudgetItemModel.get_budget_item_num_by_class(budget_class_id) > 0){
					status = "ERROR";
					message = "该分类下有工程项目，请先删除工程项目再删除此分类";
					break;//required!					
					//TODO 有可能直接删除该分类下的工程项
				}if(BudgetClassModel.get_budget_subclass(budget_class_id).size() > 0){
					status = "ERROR";
					message = "该分类下有下级分类，请先删除下级分类再删除此分类";
					break;//required!				
					//TODO 有可能直接删除该分类下的工程项
				}else{
					BudgetClassModel.dao.deleteById(id);
				}

			}
			//更新预算合计
//			BudgetModel.total(budget_id);
		} else {
			render_error_message("数据错误，请重试。");
			return;//required!
		}	
		render_message(status, message);
	}
}
