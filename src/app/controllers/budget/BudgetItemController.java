package app.controllers.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;

import app.dao.Budget;
import app.dao.BudgetItem;
import app.dao.BudgetItemAmount;
import app.dao.BudgetItemCost;
import app.models.budget.BudgetClassModel;
import app.models.budget.BudgetItemModel;
import app.models.budget.BudgetLineItemModel;
import app.models.budget.BudgetModel;
import system.core.BaseController;
import system.core.BaseModel;
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
	
	/**
	 * 单条更新预算项目
	 */
	@Clear(LoginInterceptor.class)
	public void json_budget_item_details_update(){
		String message = "";
		boolean re = false;
		
		BudgetItem record = getModel(BudgetItem.class, "record");
	
		BudgetItemAmount budget_item_amount = getModel(BudgetItemAmount.class, "budget_item_amount");
		int budget_class_id = record.getBudgetClassId();
		int current_user_id = current_user_id();
		if(record.getId() != null){
			//更新
			//单个更新时，价格也可以修改，从前台获取
			budget_item_amount.setId(record.getId());
			budget_item_amount.update();//保存从前台获取的单项价格
			
			BigDecimal price = budget_item_amount.getPriceAssist()
					.add(budget_item_amount.getPriceMaterial())
					.add(budget_item_amount.getPriceLabor())
					.add(budget_item_amount.getPriceMachinery())
					.add(budget_item_amount.getPriceLoss());
			
			record.setPrice(price);
			record.setAmount(record.getNum().multiply(price));
			record.setUpdateUid(current_user_id);
			BaseModel.setUpdateTime(record);
			message = "更新成功";
			re = record.update();
			
			int budget_item_id = record.getId();
			BigDecimal num = record.getNum();					
			//计算各单项金额和成本
			BudgetItemModel.caculate_budget_item_amount_cost(budget_item_id, num);
			
		}else{
			//新建工程项时初始化项目数据
			//金额
			BigDecimal amount = new BigDecimal(0);
			amount = record.getNum().multiply(record.getPrice());//初始化时设置金额，从工程项目获取价格 金额 = 数量x单价 
			record.setAmount(amount);//金额
			record.setCreateUid(current_user_id);
			record.setUpdateUid(current_user_id);
			BaseModel.setCreateTime(record);
			BaseModel.setUpdateTime(record);
			message = "保存成功";
			re = record.save();
			
			//初始化各单项价格
			BudgetItemAmount bia = budget_item_amount;
			bia.setId(record.getId());//新建后拿到id
			bia.setAmountMaterial(bia.getPriceMaterial().multiply(record.getNum()));
			bia.setAmountAssist(bia.getPriceAssist().multiply(record.getNum()));
			bia.setAmountMachinery(bia.getPriceMachinery().multiply(record.getNum()));
			bia.setAmountLabor(bia.getPriceLabor().multiply(record.getNum()));
			bia.setAmountLoss(bia.getPriceLoss().multiply(record.getNum()));
			bia.save();
		}
		if(re){
			render_success_message(message);
		}else{
			message = "操作失败，请重试";
			render_error_message(message);
		}
		
		//更新分类小计与预算合计
		BudgetClassModel.subtotal(budget_class_id);
		
		render_success_message(message);
	}	
	@Clear(LoginInterceptor.class)
	public void json_delete(){
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS", message = "删除成功";
		if (StrKit.notBlank(checked_ids)) {
			String[] delete_ids = checked_ids.split(",");
			int budget_class_id = 0;
			for (int i = 0; i < delete_ids.length; i++) {
				String id = delete_ids[i];
				if(budget_class_id <= 0 ){
					budget_class_id = BudgetItemModel.dao.findById(id).getBudgetClassId();
				}
				BudgetItemModel.dao.deleteById(id);
				BudgetItemAmount.dao.deleteById(id);
				BudgetItemCost.dao.deleteById(id);
			}
			BudgetClassModel.subtotal(budget_class_id);
		} else {
			render_error_message("数据错误，请重试。");
			return;//required!
		}
		
		render_message(status, message);
	}
	@Clear(LoginInterceptor.class)
	public void line_item() throws Exception{
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
	}
	@Clear(LoginInterceptor.class)
	public void line_item_json(){
		int budget_id = getParaToInt(0, 0);
		Map<String, Object> re = BudgetLineItemModel.get_budget_line_item(budget_id, rows, page);
		
		Map<String, Object> footer_line_item = new HashMap<String, Object>();
		List<Map<String, Object>> footer =  new ArrayList<Map<String, Object>>();

		BigDecimal budget_total = BudgetModel.dao.findById(budget_id).getTotal();
		footer_line_item.put("line_item_name", "预算总计");
		footer_line_item.put("line_item_amount", budget_total);
		
		footer.add(footer_line_item);
		re.put("footer", footer);
		renderJson(re);
	}
}
