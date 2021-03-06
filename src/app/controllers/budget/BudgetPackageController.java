package app.controllers.budget;

import java.util.List;
import java.util.Map;

import system.core.BaseController;
import system.core.BaseModel;
import system.core.LoginInterceptor;
import app.dao.BudgetPackage;
import app.dao.BudgetPackageItem;
import app.dao.BudgetPackageVariationItem;
import app.models.budget.BudgetModel;
import app.models.budget.BudgetPackageModel;

import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class BudgetPackageController extends BaseController{
	/**
	 * 跳转到套餐页面
	 * @throws Exception
	 */
	public void budget_package_list()throws Exception{
		set_head_title("预算套餐管理");
	}
	/**
	 * 加载套餐列表
	 * @throws Exception
	 */
	public void json_budget_package_list()throws Exception{
		String name = getPara("name");
		int uid = current_user_id();
		Map<String, Object> re = BudgetPackageModel.get_budget_package_list(rows, page, name,uid);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	/**
	 * 添加、修改套餐
	 * @throws Exception
	 */
	public void budget_package_add()throws Exception{
		Integer id = getParaToInt("id",0);
		BudgetPackage bp = null;
		if(id>0){
			bp = BudgetPackage.dao.findById(id);
		}else{
			bp = new BudgetPackage();
		}
		setAttr("bp", bp);
	}
	/**
	 * 保存套餐的添加，修改
	 * @throws Exception
	 */
	public void budget_package_list_update()throws Exception{
		BudgetPackage bp = getModel(BudgetPackage.class,"bp");
		int uid = current_user_id();
		boolean flag = false;
		if(bp.getId()!=null){
			BaseModel.setUpdateTime(bp);
			bp.setUpdateUid(uid);
			flag = bp.update();
		}else{
			BaseModel.setCreateTime(bp);
			BaseModel.setUpdateTime(bp);
			bp.setCreateUid(uid);
			bp.setUpdateUid(uid);
			flag = bp.save();
		}
		if(flag==true){
			render_success_message("保存成功");
		}else{
			render_error_message("保存失败，请重新操作");
		}
	}
	/**
	 * 删除套餐
	 * @throws Exception
	 */
	public void budget_package_list_delete()throws Exception{
		String checked_ids = getPara("checked_ids");
		boolean flag = false;
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i = 0; i < delete_ids.length; i++){
				BudgetPackage bp = BudgetPackage.dao.findById(delete_ids[i]);
				bp.setDeleted(1);
				flag = bp.update();
				if(flag==false){
					break;
				}
			}
			if(flag==true){
				render_success_message("删除成功");
			}else{
				render_error_message("删除失败，请重新操作");
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;
		}
	}
	/**
	 * 跳转到套餐明细页面
	 * @throws Exception
	 */
	public void budget_package_item_list()throws Exception{
		Integer id = getParaToInt("id",0);
		if(id==0){
			renderError(404);
			return;
		}else{
			BudgetPackage bp = BudgetPackage.dao.findById(id);
			setAttr("bp",bp);
			set_head_title("套餐项目");
		}
	}
	/**
	 * 加载套餐细则列表
	 * @throws Exception
	 */
	public void json_budget_package_item_list()throws Exception{
		String title = getPara("title");
		Integer budget_package_id = getParaToInt("budget_package_id");
		Integer budget_id = getParaToInt("budget_id",0);
		Map<String, Object> re = BudgetPackageModel.get_budget_package_item_list(rows, page, budget_package_id,budget_id, title);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	/**
	 * 跳转到套餐细则添加，修改页面
	 * @throws Exception
	 */
	public void budget_package_item_add()throws Exception{
		Integer budget_package_id = getParaToInt("budget_package_id",0);
		Integer id = getParaToInt("id",0);
		if(budget_package_id==0){
			render_error_message("数据错误，请重试。");
			return;
		}else{
			BudgetPackage bp = BudgetPackage.dao.findById(budget_package_id);
			setAttr("bp", bp);
			BudgetPackageItem bpi = null;
			if(id>0){
				bpi = BudgetPackageItem.dao.findById(id);
			}else{
				bpi = new BudgetPackageItem();
			}
			setAttr("bpi", bpi);
		}
	}
	/**
	 * 保存套餐项目的添加，修改
	 * @throws Exception
	 */
	public void budget_package_item_list_update()throws Exception{
		BudgetPackageItem bpi = getModel(BudgetPackageItem.class,"bpi");
		boolean flag = false;
		if(bpi.getId()!=null){
			BaseModel.setUpdateTime(bpi);
			flag = bpi.update();
		}else{
			BaseModel.setCreateTime(bpi);
			BaseModel.setUpdateTime(bpi);
			flag = bpi.save();
		}
		if(flag==true){
			render_success_message("保存成功");
		}else{
			render_error_message("保存失败，请重新操作");
		}
	}
	/**
	 * 删除套餐项目
	 * @throws Exception
	 */
	public void budget_package_item_list_delete()throws Exception{
		String checked_ids = getPara("checked_ids");
		boolean flag = false;
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i = 0; i < delete_ids.length; i++){
				BudgetPackageItem bpi = BudgetPackageItem.dao.findById(delete_ids[i]);
				flag = bpi.delete();
				if(flag==false){
					break;
				}
			}
			if(flag==true){
				render_success_message("删除成功");
			}else{
				render_error_message("删除失败，请重新操作");
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;
		}
	}
	/**
	 * select2选套餐
	 * @throws Exception
	 */
	public void budget_package_select2()throws Exception{
		String q = getPara("q");
		int uid = current_user_id();
		List<Record> list = BudgetPackageModel.get_budget_package(q,uid);
		String jsonList = JsonKit.toJson(list);
		renderJson(jsonList);
	}
	/**
	 * 加载某预算的增减配项目
	 * @throws Exception
	 */
	public void json_budget_package_variation_item_list()throws Exception{
		Integer budget_id = getParaToInt("budget_id");
		String name = getPara("name");
		Map<String, Object> re = BudgetPackageModel.get_budget_package_variation_item_list(rows, page, budget_id, name);
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	/**
	 * 新建，修改增减配项目
	 * @throws Exception
	 */
	public void budget_package_variation_item_edit()throws Exception{
		Integer budget_id = getParaToInt("budget_id",0);
		Integer id = getParaToInt("id",0);
		if(budget_id==0){
			render_error_message("数据错误，请重试。");
			return;
		}else{
			setAttr("budget_id", budget_id);
			BudgetPackageVariationItem bpvi = null;
			if(id==0){
				bpvi = new BudgetPackageVariationItem();
			}else{
				bpvi = BudgetPackageVariationItem.dao.findById(id);
			}
			setAttr("bpvi", bpvi);
		}
	}
	/**
	 * 保存增减配项目的新建，修改
	 * @throws Exception
	 */
	public void budget_package_variation_item_update()throws Exception{
		BudgetPackageVariationItem bpvi = getModel(BudgetPackageVariationItem.class,"bpvi");
		boolean flag = false;
		if(bpvi.getId()==null){
			BaseModel.setCreateTime(bpvi);
			BaseModel.setUpdateTime(bpvi);
			flag = bpvi.save();
		}else{
			BaseModel.setUpdateTime(bpvi);
			flag = bpvi.update();
		}
		if(flag==true){
			render_success_message("保存成功");
		}else{
			render_error_message("保存失败，请重新操作");
		}
	}
	/**
	 * 删除增减配项目
	 * @throws Exception
	 */
	public void budget_package_variation_item_delete()throws Exception{
		String checked_ids = getPara("checked_ids");
		boolean flag = false;
		if(StrKit.notBlank(checked_ids)){
			String[] delete_ids = checked_ids.split(",");
			for(int i = 0; i < delete_ids.length; i++){
				BudgetPackageVariationItem bpvi = BudgetPackageVariationItem.dao.findById(delete_ids[i]);
				flag = bpvi.delete();
				if(flag==false){
					break;
				}
			}
			if(flag==true){
				render_success_message("删除成功");
			}else{
				render_error_message("删除失败，请重新操作");
			}
		}else{
			render_error_message("数据错误，请重试。");
			return;
		}
	}
}
