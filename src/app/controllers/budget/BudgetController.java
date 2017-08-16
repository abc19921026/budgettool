package app.controllers.budget;

import java.util.Map;

import app.dao.Budget;
import app.models.budget.BudgetModel;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;

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
}
