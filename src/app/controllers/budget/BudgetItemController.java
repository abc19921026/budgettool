package app.controllers.budget;

import java.util.Map;

import com.jfinal.aop.Clear;

import app.dao.Budget;
import app.models.budget.BudgetItemModel;
import app.models.budget.BudgetModel;
import system.core.BaseController;
import system.core.LoginInterceptor;

public class BudgetItemController extends BaseController{
	//预算高级编辑
	@Clear(LoginInterceptor.class)
	public void advanced_edit() throws Exception{
		//render("budget_item/advanced_edit.html");
		int budget_id = getParaToInt(0, 0);
		int budget_class_id = getParaToInt("budget_class_id", 0);
		int section = getParaToInt("section", 0);//0基础工程1主材2汇总与优惠
		
		Budget budget = BudgetModel.get_budget(budget_id);
		if(budget == null){
			renderError(404);
		}	
		setAttr("budget", budget);
		setAttr("section", section);	
		setAttr("budget_id", budget_id);
		setAttr("budget_class_id", budget_class_id);
		set_head_title("预算高级编辑-"+budget.getName());
	}
	//预算高级编辑 右边栏工程条目
	@Clear(LoginInterceptor.class)
	public void advanced_edit_budget_item_load_json(){
		int budget_class_id = getParaToInt("budget_class_id", 0);
		
		//Object re = BudgetItemModel.get_budget_item_by_class(budget_class_id, rows, page);
		Map<String, Object> re = BudgetItemModel.get_budget_item_by_class(budget_class_id, rows, page);
		//String jsonList = JsonKit.toJson(re);
		//String jsonList = JSONObject.toJSONString(re);
		//renderJson(jsonList);
		//renderText(jsonList);//这种方式field value = null 的项目不输出！
		renderJson(re);
	}
}
