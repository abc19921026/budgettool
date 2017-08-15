package app.models.common;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import app.dao.BTAddrCity;
import app.dao.BTAddrDistrict;
import app.dao.BTAddrProvince;
import app.dao.MarketingSaleSource;
import app.dao.MarketingSaleSourceType;

public class CommonModel {
	/**
	 * 获得工程select2
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_engineer_select2(String q) throws Exception{
		StringBuffer sql=new StringBuffer(" SELECT id,order_name AS text FROM engineering_info where 1=1 ");
		List<Object> param=new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql.append(" AND order_name like ? ");
			param.add("%"+q+"%");
		}
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得(不包含老工地的)工程
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_engineer(String q) throws Exception{
		StringBuffer sql = new StringBuffer(" SELECT id,order_name AS text FROM engineering_info WHERE order_id IS NOT NULL ");
		List<Object> param = new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql.append(" AND order_name LIKE ? ");
			param.add("%"+q+"%");
		}
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得员工select2
	 * @param q
	 * @param department
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_staff_select2(String q,String department,String is_all) throws Exception{
		StringBuffer sql=new StringBuffer(" SELECT id,name AS text FROM crm_staff where deleted = 0 ");
		List<Object> param=new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql.append(" AND name like ? ");
			param.add("%"+q+"%");
		}
		if(StrKit.notBlank(department)){
			sql.append(" AND FIND_IN_SET(?,departments)  ");
			param.add(department);
		}
		
		if("notall".equals(is_all)){
			sql.append(" AND status <> -1 ");
		}
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得部门
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_department_select2(String q,int pid,String query_token) throws Exception{
		StringBuffer sql=new StringBuffer(" select d.id as id, d.name as text FROM crm_department d  where d.pid = ?  ");
		List<Object> param=new ArrayList<Object>();
		param.add(pid);
		if(StrKit.notBlank(query_token)){
			sql.append(" AND FIND_IN_SET(d.query_token,? ) ");
			param.add(query_token);
		}
		
		if(StrKit.notBlank(q)){
			sql.append(" AND name like ? ");
			param.add("%"+q+"%");
		}
		sql.append(" order by d.id asc ");
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得职位
	 * @param q
	 * @param query_token
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_estimator_select2(String q,String query_token) throws Exception{
		// TODO Auto-generated method stub
		StringBuffer sql=new StringBuffer(" select cs.id as id, cs.name as text FROM crm_job_title cjt left join crm_staff cs on cs.job_title_id = cjt.id  where cs.deleted = 0  ");
		List<Object> param=new ArrayList<Object>();
		if(StrKit.notBlank(query_token)){
			sql.append(" AND FIND_IN_SET(cjt.query_token,?) ");
			param.add(query_token);
		}
		
		if(StrKit.notBlank(q)){
			sql.append(" AND cs.name like ? ");
			param.add("%"+q+"%");
		}
		sql.append(" AND status <> -1 order by cjt.id asc ");
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得投诉类型
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_type_select2(String q) throws Exception{
		StringBuilder sql = new StringBuilder(" SELECT csct.id as id, csct.name as text FROM customer_service_complaint_type csct WHERE 1=1 ");
		List<Object> param = new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql.append(" AND name like ? ");
			param.add("%"+q+"%");
		}
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 获得户型Select2
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_house_type_select2(String q) throws Exception{
		StringBuilder sql = new StringBuilder(" SELECT ht.id as id, ht.name as text FROM b_t_house_type ht WHERE 1=1 ");
		List<Object> param = new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql.append(" AND name like ? ");
			param.add("%"+q+"%");
		}
		return Db.find(sql.toString(), param.toArray());
	}
	
	/**
	 * 来源类型
	 * @return
	 */
	public static List<Record> get_source() {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "SELECT msst.id as id,msst.name as text "
						+ "From marketing_sale_source_type msst  WHERE 1 = 1 " ;
			 List<Object> list = new ArrayList<Object>();
			re = Db.find(sql+"order by msst.id asc", list.toArray());
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得来源
	 * @param q
	 * @return
	 */
	public static List<Record> get_source_details(String q,int pid) {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "SELECT mss.id as id,mss.name as text "
						+ "From marketing_sale_source mss  WHERE 1 = 1 and pid=? " ;
			List<Object> list = new ArrayList<Object>();
			list.add(pid);
			if (StrKit.notBlank(q)) {
				sql += "AND mss.name LIKE ? OR mss.sn LIKE ? ";
				list.add("%"+q+"%");
				list.add("%"+q+"%");
			}
			re = Db.find(sql+"order by mss.id asc", list.toArray());
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得所有的工种
	 * @return
	 * @throws Exception
	 */
	/*public static List<BTWorkType> get_work_types(String q) throws Exception{
		String sql=" select btwt.id,btwt.name as text from b_t_work_type btwt where 1=1 ";
		List<Object> params=new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql+=" and btwt.name like ? ";
			params.add("%"+q+"%");
		}
		return BTWorkType.dao.find(sql,params.toArray());
	}*/
	
	/**
	 * 获得某个工种下面的人
	 * @param work_type_id
	 * @return
	 * @throws Exception
	 */
	/*public static List<CrmStaffWorkType> get_work_type_staffs(int work_type_id,String q) throws Exception{
		String sql=" select cswt.staff_id as id,concat(cs.sn,'-',cs.name) as text "
				 + " from crm_staff_work_type cswt "
				 + " left join crm_staff cs on cswt.staff_id=cs.id "
				 + " where cs.deleted = 0 and cswt.work_type_id=? ";
		List<Object> params=new ArrayList<Object>();
		params.add(work_type_id);
		if(StrKit.notBlank(q)){
			sql += " and cs.name like ? ";
			params.add("%"+q+"%");
		}
		
		return CrmStaffWorkType.dao.find(sql,params.toArray());
	}*/
	
	/**
	 * 获得所有的省份
	 * @return
	 * @throws Exception
	 */
	public static List<BTAddrProvince> get_provinces(String q) throws Exception{
		String sql=" select * from b_t_addr_province where 1=1 ";
		List<Object> params=new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql += " and name like ? ";
			params.add("%"+q+"%");
		}
		List<BTAddrProvince> provinces=BTAddrProvince.dao.find(sql,params.toArray());
		
		BTAddrProvince btAddrProvince=new BTAddrProvince();
		btAddrProvince.setId(-1);
		btAddrProvince.setName("请选择省");
		provinces.add(0, btAddrProvince);
		
		return provinces;
	}
	
	/**
	 * 获得市
	 * @param q
	 * @param province_id
	 * @return
	 * @throws Exception
	 */
	public static List<BTAddrCity> get_citys(String q,int province_id) throws Exception{
		List<BTAddrCity> cities=new ArrayList<BTAddrCity>();
		List<Object> params=new ArrayList<Object>();
		if(province_id!=-1){//有省份id
			String sql=" select * from b_t_addr_city where pid=? ";
			params.add(province_id);
			
			if(StrKit.notBlank(q)){
				sql += " and name like ? ";
				params.add("%"+q+"%");
			}
			
			cities=BTAddrCity.dao.find(sql, params.toArray());
			
			BTAddrCity city=new BTAddrCity();
			city.setId(-1);
			city.setName("请选择市");
			cities.add(0,city);
		}
		return cities;
	}
	
	/**
	 * 获得区
	 * @param q
	 * @param city_id
	 * @return
	 * @throws Exception
	 */
	public static List<BTAddrDistrict> get_districts(String q,int city_id) throws Exception{
		List<BTAddrDistrict> districts=new ArrayList<BTAddrDistrict>();
		if(city_id!=-1){
			String sql=" select * from b_t_addr_district where pid=? ";
			
			List<Object> params=new ArrayList<Object>();
			params.add(city_id);
			if(StrKit.notBlank(q)){
				sql += " and name like ? ";
				params.add("%"+q+"%");
			}
			
			districts=BTAddrDistrict.dao.find(sql,params.toArray());
			
			BTAddrDistrict district=new BTAddrDistrict();
			district.setId(-1);
			district.setName("请选择区");
			
			districts.add(0, district);
		}
		
		return districts;
	}
	
	/**
	 * 获得来源类型
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<MarketingSaleSourceType> get_source_types(String q) throws Exception{
		String sql=" select * from marketing_sale_source_type where 1=1 ";
		
		List<Object> params=new ArrayList<Object>();
		if(StrKit.notBlank(q)){
			sql += " and name like ? ";
			params.add("%"+q+"%");
		}
		
		List<MarketingSaleSourceType> sourceTypes=MarketingSaleSourceType.dao.find(sql, params.toArray());
		
		MarketingSaleSourceType sourceType=new MarketingSaleSourceType();
		sourceType.setId(-1);
		sourceType.setName("----请选择----");
		sourceTypes.add(0, sourceType);
		
		return sourceTypes;
	}
	
	/**
	 * 获得来源
	 * @param id
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public static List<MarketingSaleSource> get_sources(int id,String q) throws Exception{
		List<MarketingSaleSource> sources=new ArrayList<MarketingSaleSource>();
		if(id!=-1){
			String sql=" select * from marketing_sale_source where pid=? ";
			
			List<Object> params=new ArrayList<Object>();
			params.add(id);
			if(StrKit.notBlank(q)){
				sql += " and name like ? ";
				params.add("%"+q+"%");
			}
			
			sources=MarketingSaleSource.dao.find(sql, params.toArray());
			
			MarketingSaleSource source=new MarketingSaleSource();
			source.setId(-1);
			source.setName("----请选择----");
			sources.add(0, source);
		}
		
		return sources;
	}
}
