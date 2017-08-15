package app.models.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dao.Budget;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

public class BudgetModel extends Budget{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Map<String, Object> get_json_budget_list(int rows, int page,String sn, String startdate, String enddate) throws Exception{
		// TODO Auto-generated method stub
		List<Object> params=new ArrayList<Object>();
		String select = "SELECT b.*";
		StringBuffer sqlExceptSelect = new StringBuffer( " FROM budget b");
				sqlExceptSelect.append(" WHERE b.order_id <> 0 ");
		if(StrKit.notBlank(sn)){
			sqlExceptSelect.append(" and b.sn like ?");
			params.add("%"+sn+"%");
		}
		if(StrKit.notBlank(startdate)&&StrKit.notBlank(enddate)){
			sqlExceptSelect.append(" AND DATE_FORMAT(b.update_time,'%Y-%m-%d') >= ? AND DATE_FORMAT(b.update_time,'%Y-%m-%d') <= ? ");
			params.add(startdate);
			params.add(enddate);
		}
		sqlExceptSelect.append(" order by b.update_time desc");
		Page<Budget> pages;
		
		pages = dao.paginate(page, rows, select, sqlExceptSelect.toString(),params.toArray());
		List<Budget> list = pages.getList();
		int total = pages.getTotalRow();
		Map<String,Object> re = new HashMap<String,Object>();
		re.put("total", total);
		re.put("rows", list);
		return re;
	}
}
