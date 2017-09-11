package app.models.budget;

import java.math.BigDecimal;
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
import app.dao.BudgetPackageVariationItem;


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
	public static Map<String,Object>get_budget_package_item_list(int rows, int page,Integer budget_package_id,Integer budget_id,String title)throws Exception{
		List<Object> params=new ArrayList<Object>();
		String select = "select bpi.*";
		StringBuffer sql = new StringBuffer(" from budget_package_item bpi where bpi.budget_package_id = ?");
		params.add(budget_package_id);
		if(budget_id>0){
			sql.append(" and bpi.budget_id = ?");
			params.add(budget_id);
		}else{
			sql.append(" and bpi.budget_id is null");
		}
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
	/**
	 * 加载套餐增减配项目列表
	 * @param rows
	 * @param page
	 * @param budget_id
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> get_budget_package_variation_item_list(int rows, int page,Integer budget_id,String name)throws Exception{
		List<Object> params=new ArrayList<Object>();
		String select = "select bpvi.*";
		StringBuffer sql = new StringBuffer(" from budget_package_variation_item bpvi where bpvi.budget_id = ?");
		params.add(budget_id);
		if(StrKit.notBlank(name)){
			sql.append(" and bpvi.name like ?");
			params.add("%"+name+"%");
		}
		Page<BudgetPackageVariationItem> pages = BudgetPackageVariationItem.dao.paginate(page, rows, select, sql.toString(),params.toArray());
		List<BudgetPackageVariationItem> list = pages.getList();
		int total = pages.getTotalRow();
		List<BudgetPackageVariationItem> footerlist = new ArrayList<BudgetPackageVariationItem>();
		BudgetPackageVariationItem footer = new BudgetPackageVariationItem();
		footer.setName("总计:");
		footer.setAmount(get_budget_package_variation_item_sum(budget_id, name));
		footerlist.add(footer);
		Map<String,Object> re = new HashMap<String,Object>();
		re.put("total", total);
		re.put("rows", list);
		re.put("footer", footerlist);
		return re;
	}
	/**
	 * 增加配项目的总计
	 * @param budget_id
	 * @param name
	 * @throws Exception
	 */
	public static BigDecimal get_budget_package_variation_item_sum(Integer budget_id,String name)throws Exception{
		List<Object> params=new ArrayList<Object>();
		StringBuffer sql =new StringBuffer("select sum(bpvi.amount) ") ;
		sql.append(" from budget_package_variation_item bpvi where bpvi.budget_id = ?");
		params.add(budget_id);
		if(StrKit.notBlank(name)){
			sql.append(" and bpvi.name like ?");
			params.add("%"+name+"%");
		}
		BigDecimal sum = Db.queryBigDecimal(sql.toString(),params.toArray());
		if(sum==null){
			return new BigDecimal(0);
		}else{
			return sum;
		}	
	}
}
