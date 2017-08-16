package app.controllers.budget;

import java.util.List;
import java.util.Map;

import app.dao.Budget;
import app.dao.BudgetClass;
import app.models.budget.BudgetClassModel;
import app.models.budget.BudgetItemModel;
import app.models.budget.BudgetModel;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

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
}
