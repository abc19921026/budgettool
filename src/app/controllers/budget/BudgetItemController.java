package app.controllers.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;

import app.dao.Budget;
import app.dao.BudgetClass;
import app.dao.BudgetItem;
import app.dao.BudgetItemAmount;
import app.dao.BudgetItemCost;
import app.dao.BudgetLineItem;
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
	public void advanced_edit_budget_item_load_json()throws Exception{
		int budget_class_id = getParaToInt("budget_class_id", 0);
		Map<String, Object> re = BudgetItemModel.get_budget_item_by_class(budget_class_id, rows, page);
		renderJson(re);
	}
	
	/**
	 * 单条更新预算项目
	 */
	@Clear(LoginInterceptor.class)
	public void json_budget_item_details_update()throws Exception{
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
			if(record.getNum().doubleValue()!=0){
				record.setPrintable(1);
			}
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
			if(record.getNum().doubleValue()!=0){
				record.setPrintable(1);
			}
			Integer max_weight = BudgetItemModel.get_max_weight_in_budget_item(budget_class_id);
			record.setWeight(max_weight+10);
			Integer max_no = BudgetItemModel.get_max_no_in_budget_item(budget_class_id);
			record.setNo(max_no+1);
			Long num = BudgetItemModel.get_num_in_budget_item(budget_class_id, null);
			Long sn = num + 1;
			record.setSn(BudgetClass.dao.findById(budget_class_id).getSn()+"-"+sn);
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
	public void json_delete()throws Exception{
		String checked_ids = getPara("checked_ids");
		String status = "SUCCESS", message = "删除成功";
		if (StrKit.notBlank(checked_ids)) {
			String[] delete_ids = checked_ids.split(",");
			int budget_class_id = 0;
			for (int i = 0; i < delete_ids.length; i++) {
				String id = delete_ids[i];
				BudgetItem bi = BudgetItemModel.dao.findById(id);
				if(budget_class_id <= 0 ){
					budget_class_id = bi.getBudgetClassId();
				}
				BudgetItemModel.change_budget_item_sn(budget_class_id, bi.getNo());
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
	/**
	 * 批量更新预算项目
	 */
	@Clear(LoginInterceptor.class)
	public void advanced_edit_save(){
		String jsonString = getPara("data", "");
		int budget_class_id = getParaToInt("budget_class_id", 0);
		//将前台获取的json数据转换成list数组
		List<BudgetItem> list_budget_item = JSON.parseArray(jsonString, BudgetItem.class);
		int count = 1;
		for(int i = 0;i < list_budget_item.size();i ++){
			BudgetItem budget_item = list_budget_item.get(i);
			if(budget_item != null){
				//金额在后台更新
				BigDecimal amount = budget_item.getPrice().multiply(budget_item.getNum());
				budget_item.setAmount(amount);
				BaseModel.setUpdateTime(budget_item);
				//如果数量不为0，设为必须打印
				if(budget_item.getNum().doubleValue() != 0){//-1 小于   0 等于    1 大于 
					budget_item.setPrintable(1);
				}
				budget_item.setWeight(count*10);
				budget_item.setNo(count);
				budget_item.setSn(budget_item.getSn().split("-")[0]+"-"+count);
				count += 1;
				budget_item.update();
				
				//更新各单项金额
				int budget_item_id = budget_item.getId();
				BigDecimal num = budget_item.getNum();
				
				//批量更新时不更新价格，只更新数量
				
				//计算各单项金额和成本
				BudgetItemModel.caculate_budget_item_amount_cost(budget_item_id, num);
			}
		}
		
		//更新分类小计与预算合计
		BudgetClassModel.subtotal(budget_class_id);
		
		render_success_message("保存成功");
	}
	@Clear(LoginInterceptor.class)
	public void line_item_update_json(){
		String jsonString = getPara("data", "");
		
		int budget_id = getParaToInt("budget_id", 0);
		
		//将前台获取的json数据转换成list数组
		List<BudgetLineItem> list_budget_item = JSON.parseArray(jsonString, BudgetLineItem.class);
		
		for(int i = 0; i< list_budget_item.size(); i++){
			BudgetLineItem budget_line_item = list_budget_item.get(i);
			if(budget_line_item != null){
				//基础工程、主材工程和管理费不处理，在计算total时计算
				String[] auto_calculate_fields = new String[] {"project","material","management"};

				if(Arrays.asList(auto_calculate_fields).contains(budget_line_item.getLineItemField())){
					//主材
					continue;
				}
				//金额在后台更新
				//BudgetLineItemModel.update_budget_line_item(budget_line_item);
				if(budget_line_item.getLineItemAmount() == null){
					BigDecimal default_amount = new BigDecimal(0);
					budget_line_item.setLineItemAmount(default_amount);
				}
				budget_line_item.update();

			}
		}
		
		//合计
		BudgetModel.total(budget_id);
		render_success_message("保存成功");
	}	
	/**
	 * 跳转到项目拖拽页面
	 * @throws Exception
	 */
	@Clear(LoginInterceptor.class)
	public void budget_item_weight_sort()throws Exception{
		Integer budget_class_id = getParaToInt("budget_class_id");
		if(budget_class_id==null){
			render_error_message("请先选择预算分类！");
		}else{
			BudgetClass bc = BudgetClass.dao.findById(budget_class_id);
			setAttr("bc", bc);
			render("budget_item_weight_sort.html");
		}
	}
	
	@Clear(LoginInterceptor.class)
	public void budget_item_weight_sort_json()throws Exception{
		int budget_class_id = getParaToInt("budget_class_id", 0);
		Map<String, Object> re = BudgetItemModel.get_budget_item_for_sort(budget_class_id);
		renderJson(re);
	}
	@Clear(LoginInterceptor.class)
	public void budget_item_weight_sort_update()throws Exception{
		String data = getPara("data");
		List<BudgetItem> list = JSON.parseArray(data, BudgetItem.class);
		boolean flag = false;
		int count = 1;
		if(list.size()>0){
			for(BudgetItem bi : list){
				bi.setWeight(count*10);
				bi.setNo(count);
				bi.setSn(bi.getSn().split("-")[0]+"-"+count);
				count += 1;
				flag = bi.update();
				if(flag==false){
					break;
				}
			}
		}		
		if(flag==true){
			render_success_message("保存成功");
		}else{
			render_error_message("保存失败，请重试");
		}
	}
}
