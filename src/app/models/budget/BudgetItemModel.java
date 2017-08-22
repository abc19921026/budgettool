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

import app.dao.BudgetClass;
import app.dao.BudgetItem;
import app.dao.BudgetItemAmount;
import app.dao.BudgetItemCost;

public class BudgetItemModel extends BudgetItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int delete_budget_item_by_budget_id(int budget_id){
		int re = 0;
		
		String sql = "";
		sql = "delete bi, bia, bic from budget_item bi"
				+ " INNER JOIN budget_item_amount bia on bi.id = bia.id"
				+ " INNER JOIN budget_item_cost bic on bi.id = bic.id"
				+ " WHERE bi.budget_id = ?";
		re = Db.update(sql, budget_id);
		return re;
	}
	
	public static Map<String, Object> get_budget_item_by_class(int budget_class_id, int rows, int page){
		Map<String, Object> re = new HashMap<String, Object>();
		List<BudgetItem> list = null;
		//List<Record> re =  ;
		try{
			String select = "select b_i.*";
			//re = Db.find(sql, budget_class_id);
			String sqlExceptSelect = " from budget_item b_i "
					+ "where b_i.budget_class_id = ? order by b_i.weight asc";
			Page<BudgetItem> pages = dao.paginate(page, rows, select, sqlExceptSelect, budget_class_id);
			list = pages.getList();
			int total = pages.getTotalRow();			
			
			List<BudgetItem> footlist=new ArrayList<BudgetItem>();
			BudgetItem budgetItem=new BudgetItem();
			budgetItem.setName("总计:");
			BigDecimal totalamount=Db.queryBigDecimal(" select sum(amount) from budget_item where budget_class_id = ? ", budget_class_id);
			if(totalamount==null){
				totalamount=new BigDecimal(0.00);
			}
			budgetItem.setAmount(totalamount);
			budgetItem.setMaterialId(0);
			budgetItem.setMaterialAttached(-1);
			footlist.add(budgetItem);
			re.put("total", total);
			re.put("rows", list);
			re.put("footer", footlist);
			return re;
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		return re;
	}
	
	/**
	 * 按不同类别加载，0基础工程1主材2优惠
	 * @param q
	 * @param section
	 * @return
	 */
	public static List<Record> get_class_catalog(String q, int section,int type,int parent_id)throws Exception{
		List<Object> param=new ArrayList<Object>();
		StringBuffer sql = new StringBuffer("select bc.name as id, bc.name as text ");
			sql.append(" FROM budget_class bc");
			sql.append(" where bc.section = ?");
			param.add(section);
		if(type==0){
			sql.append(" and bc.parent_id = 0");
		}	
		if(StrKit.notBlank(q)){
			sql.append(" and bc.name like ?");
			param.add("%"+q+"%");
		}
		if(parent_id>0){
			sql.append(" and bc.parent_id = ?");
			param.add(parent_id);
		}
			sql.append(" group by bc.name order by bc.weight asc");
		List<Record> re = Db.find(sql.toString(), param.toArray());
		return re;
	}	
	
	public static List<Record> get_same_class_catalog(String name,int section)throws Exception{
		String sql = "select * from budget_class "
				+" where name = ? and section = ?";
		List<Record> list = Db.find(sql, name,section);
		return list;
	}	
	
	/**
	 * 根据预算分类id查询分类下的工程项目个数
	 * @param budget_class_id
	 * @return
	 */
	public static int get_budget_item_num_by_class(int budget_class_id)throws Exception{
		String sql = "select count(*) as total from budget_item where budget_class_id = ?";
		Record re = Db.findFirst(sql, budget_class_id);
		int total = re.getLong("total").intValue();
		return total;
	}
	/**
	 * 根据预算分类id查询分类下的工程项目
	 * @param budget_class_id
	 * @return
	 */
	public static List<Record> get_budget_item_by_budget_class_id(int budget_class_id)throws Exception{
		String sql = "select name from budget_item where budget_class_id = ?";
		List<Record> re = Db.find(sql, budget_class_id);
		return re;
	}
	
	public static Map<String, Object> get_budget_item_select2(int page,int rows,String q)throws Exception{
		List<Object> param=new ArrayList<Object>();
		String select = "select distinct bi.name,bi.section,bi.unit,bi.price,bia.price_material,bia.price_assist,bia.price_labor,bia.price_machinery,bia.price_loss,"
						+" if(top_bc.name is null,bc.name,concat(top_bc.name,'-',bc.name))as class_name";
		StringBuffer sql = new StringBuffer(" from budget_item bi");
				sql.append(" left join budget_item_amount bia on bia.id=bi.id");
				sql.append(" left join budget_class bc on bc.id = bi.budget_class_id");
				sql.append(" left join budget_class top_bc on top_bc.id = bc.parent_id");
				sql.append(" where 1=1");
		if(StrKit.notBlank(q)){
			sql.append(" and bi.name like ?");
			param.add("%"+q+"%");
		}		
		List<Record> list = Db.find(select+sql,param.toArray());
		Map<String,Object> map=new HashMap<String,Object>();
		int total = list.size();
		int start_num = (page-1)*rows;
		int end_num = page*rows;
		if(end_num>total){
			end_num = total;
		}
		map.put("total", list.size());
		map.put("rows", list.subList(start_num, end_num));
		return map;
	}
	/**
	 * 计算各单项金额
	 * @param budget_item_id
	 * @param num
	 */
	public static void caculate_budget_item_amount_cost(int budget_item_id, BigDecimal num){
		//计算各单项金额
		BudgetItemAmount bia = BudgetItemAmount.dao.findById(budget_item_id);
		if(bia == null){
			return;
		}
		bia.setAmountAssist(bia.getPriceAssist().multiply(num));
		bia.setAmountMaterial(bia.getPriceMaterial().multiply(num));
		bia.setAmountLabor(bia.getPriceLabor().multiply(num));
		bia.setAmountMachinery(bia.getPriceMachinery().multiply(num));
		bia.setAmountLoss(bia.getPriceLoss().multiply(num));
		bia.update();
	}
	/**
	 * 获得某个预算分类下项目中最大的weight
	 * @param budget_class_id
	 * @throws Exception
	 */
	public static Integer get_max_weight_in_budget_item(Integer budget_class_id)throws Exception{
		String sql = "select max(weight) from budget_item where budget_class_id = ?";
		Integer max = Db.queryInt(sql, budget_class_id);
		if(max==null){
			return 0;
		}else{
			return max;
		}
	}
	
	public static List<BudgetItem> get_budget_item_by_weight(Integer budget_class_id,Integer start_weight,Integer end_weight)throws Exception{
		String sql = "select * from budget_item bi where bi.budget_class_id = ? and bi.weight between ? and ?";
		List<BudgetItem> list = BudgetItem.dao.find(sql, budget_class_id,start_weight,end_weight);
		return list;
	}
	
	public static Map<String, Object> get_budget_item_for_sort(Integer budget_class_id)throws Exception{
		String sql = "select bi.* from budget_item bi where bi.budget_class_id = ? order by bi.weight asc";
		List<BudgetClass> list = BudgetClass.dao.find(sql, budget_class_id);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("total", list.size());
		map.put("rows", list);
		return map;
	}
}
