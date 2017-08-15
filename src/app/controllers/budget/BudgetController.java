package app.controllers.budget;

import java.util.Map;

import app.models.budget.BudgetModel;

import com.jfinal.kit.JsonKit;

import system.core.BaseController;
import system.tools.DateTools;

public class BudgetController extends BaseController{
	/**
	 * 预算列表页（按项目预算列出）
	 * @throws Exception 
	 */
	public void index() throws Exception{		
		String enddate = DateTools.getDate();//获取系统当前日期
		String startdate = DateTools.daysAgo(enddate,365);//获取系统当前日期一年之前的日期
		setAttr("startdate",startdate);
		setAttr("enddate",enddate);
		set_head_title("预算中心-项目预算");
	}
	
	/**
	 * 预算列表数据加载listAll
	 * @throws Exception 
	 */
	public void index_json() throws Exception{
		String sn = getPara("sn", "").trim();
		String startdate = getPara("startdate", "").trim();
		String enddate =  DateTools.daysLater(getPara("enddate", "").trim(),1);//查询时当前日期+1
		Map<String,Object> re = BudgetModel.get_json_budget_list(rows,page,sn,startdate,enddate);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
}
