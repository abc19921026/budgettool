package app.models.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import app.dao.BudgetPackage;
import app.dao.BudgetPackageItem;


public class BudgetPackageModel extends BudgetPackage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 加载套餐列表
	 * @param rows
	 * @param page
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> get_budget_package_list(int rows, int page,String name)throws Exception{
		List<Object> params=new ArrayList<Object>();
		String select = "select bp.*";
		StringBuffer sql = new StringBuffer(" from budget_package bp where bp.deleted = 0");
		if(StrKit.notBlank(name)){
			sql.append(" and bp.name like ?");
			params.add("%"+name+"%");
		}
		Page<BudgetPackage> pages = BudgetPackage.dao.paginate(page, rows, select, sql.toString(),params.toArray());
		List<BudgetPackage> list = pages.getList();
		int total = pages.getTotalRow();
		Map<String,Object> re = new HashMap<String,Object>();
		re.put("total", total);
		re.put("rows", list);
		return re;
	}
	/**
	 * 加载套餐项目列表
	 * @param rows
	 * @param page
	 * @param budget_package_id
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object>get_budget_package_item_list(int rows, int page,Integer budget_package_id,String title)throws Exception{
		List<Object> params=new ArrayList<Object>();
		String select = "select bpi.*";
		StringBuffer sql = new StringBuffer(" from budget_package_item bpi where bpi.budget_package_id = ? and bpi.budget_id is null");
		params.add(budget_package_id);
		if(StrKit.notBlank(title)){
			sql.append(" and bpi.title like ?");
			params.add("%"+title+"%");
		}
		Page<BudgetPackageItem> pages = BudgetPackageItem.dao.paginate(page, rows, select, sql.toString(),params.toArray());
		List<BudgetPackageItem> list = pages.getList();
		int total = pages.getTotalRow();
		Map<String,Object> re = new HashMap<String,Object>();
		re.put("total", total);
		re.put("rows", list);
		return re;
	}
	
	/**
	 * 搜索套餐
	 * @param q
	 * @throws Exception
	 */
	public static List<Record> get_budget_package(String q)throws Exception{
		List<Object> params=new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select bp.*,bp.name as text from budget_package bp where 1=1");
		if(StrKit.notBlank(q)){
			sql.append(" and bp.name like ?");
			params.add("%"+q+"%");
		}
		List<Record> list = Db.find(sql.toString(), params.toArray());
		return list;
	}
	/**
	 * 复制套餐内容
	 * @param budget_package_id
	 * @param budget_id
	 * @throws Exception
	 */
	public static void load_budget_package(Integer budget_package_id,Integer budget_id)throws Exception{
		String sql = "select bpi.* from budget_package_item bpi where bpi.budget_id is null and bpi.budget_package_id =?";
		List<BudgetPackageItem> list = BudgetPackageItem.dao.find(sql, budget_package_id);
		for(BudgetPackageItem bpi :list){
			bpi.setId(null);
			bpi.setBudgetId(budget_id);
			bpi.save();
		}
	}
}
