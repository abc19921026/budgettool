package app.models.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.dao.CrmStaff;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class StaffModel extends CrmStaff {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7618124036123180229L;

	public static Map<String, Object> get_staff_list(int rows, int page, String name, String mobile, String sn, String department, int status, int job_title_id,Integer type,String location) throws Exception{
		// TODO Auto-generated method stub
			String select = "SELECT s.*, su.uid as uid, su.name as system_username, su.status as system_user_status, cjt.name as title ";
			StringBuilder sqlExceptSelect = new StringBuilder( " FROM crm_staff s LEFT JOIN system_user su ON s.uid = su.uid LEFT JOIN crm_job_title cjt ON cjt.id = s.job_title_id WHERE s.deleted = 0 ");
			List<Object> param = new ArrayList<Object>();
			if (StrKit.notBlank(name)) {
				sqlExceptSelect.append(" AND s.name LIKE ? ");
				param.add("%"+name+"%");
			}
			if (StrKit.notBlank(mobile)) {
				sqlExceptSelect.append(" AND s.mobile LIKE ? ");
				param.add("%"+mobile+"%");
			}
			if (StrKit.notBlank(sn)) {
				sqlExceptSelect.append(" AND s.sn LIKE ? ");
				param.add("%"+sn+"%");
			}
			if(StrKit.notBlank(department)){
				//sqlExceptSelect.append(" AND FIND_IN_SET(?,s.departments) AND s.status <> -1 ");
				sqlExceptSelect.append(" AND FIND_IN_SET(?,s.departments)");
				param.add(department);
			}
			if (status != 1) {
				sqlExceptSelect.append(" AND s.status = ? ");
				param.add(status);
			}
			if (job_title_id > 0) {
				sqlExceptSelect.append(" AND s.job_title_id = ? ");
				param.add(job_title_id);
			}
			
			if("workerGroup".equals(location)){//工班信息维护
				sqlExceptSelect.append(" AND s.worker_group=1 ");//工班组的人
			}
			
			if(type!=-1){
				sqlExceptSelect.append(" AND s.type=? ");
				param.add(type);
			}
			//sqlExceptSelect.append(" ORDER BY s.create_time ");
			sqlExceptSelect.append(" ORDER BY s.updated DESC");
			Page<Record> pages;
				pages = Db.paginate(page, rows, select,sqlExceptSelect.toString(),param.toArray());
			int total = pages.getTotalRow();
			List<Record> list = pages.getList();
			
			//TODO 下面这段可以重构
/*			for(Record record : list){
				String departments = (String)record.get("departments");
				String department_name = "";
				if(StrKit.notBlank(departments)){
					String[] department_ids = departments.split(",");
					for(int i = 0; i < department_ids.length;i++){
						//根据部门id查询部门name
						CrmDepartment crmDepartment = CrmDepartment.dao.findById(Integer.parseInt(department_ids[i]));
						if(i == department_ids.length-1){
							department_name += crmDepartment.getName();
						}else{
							department_name += crmDepartment.getName()+",";
						}
					}
				}
				record.set("department_names", department_name);
			}*/
			
			for(Record record : list){
				Integer staff_id = record.getInt("id");
				String department_name = DepartmentModel.get_departments_name(staff_id);
				record.set("department_names", department_name);
				
				//TODO 获得员工对应的工种信息
				/*String staff_work_type="";
				List<CrmStaffWorkType> workTypes=get_staff_work_types(staff_id);
				
				if(workTypes.size()>0){
					for(int i=0;i<workTypes.size();i++){
						CrmStaffWorkType workType=workTypes.get(i);
						
						if(i==(workTypes.size()-1)){
							staff_work_type += workType.getStr("work_type_name");
						}
						else{
							staff_work_type += workType.getStr("work_type_name")+",";
						}
					}
				}
				
				record.set("staff_work_type", staff_work_type);*/
			}
			
			Map<String,Object> jsonMap = new HashMap<String,Object>();
			jsonMap.put("total", total);
			jsonMap.put("rows", list);
			return jsonMap;//此处最好返回jsonMap方便以后继续处理数据
	}

	public static CrmStaff get_staff(int id) throws Exception{
		// TODO Auto-generated method stub
		String sql = " SELECT s.*, d.name as department_name,su.name as uid_name "
				+ " FROM crm_staff s "
				+ " LEFT JOIN crm_department d ON s.departments = d.id "
				+ " LEFT JOIN system_user su ON su.uid = s.uid "
				+ " WHERE s.deleted = 0 and s.id = ? ";
		CrmStaff cs = CrmStaff.dao.findFirst(sql, id);
		return cs;
	}
	
	public static CrmStaff get_staff_by_uid(int uid) throws Exception {
		String sql = "select s.*, d.name as department_name from crm_staff s left join crm_department d on s.departments = d.id where s.deleted = 0 and s.uid = ?";
		CrmStaff cs = CrmStaff.dao.findFirst(sql, uid);
		return cs;		
	}
	
	/**
	 * 根据员工名称查询员工信息
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static CrmStaff get_staff_by_name(String name) throws Exception{
		String sql=" select id,mobile from crm_staff a where deleted = 0 and name=? limit 1 ";
		return CrmStaff.dao.findFirst(sql, name);
	}
	
	/**
	 * 根据员工电话查询员工信息
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static CrmStaff get_staff_by_mobile(String mobile) throws Exception{
		String sql=" select * from crm_staff a where deleted = 0 and mobile=? limit 1 ";
		return CrmStaff.dao.findFirst(sql, mobile);
	}
	
	/**
	 * 选择账号详情
	 * @param q
	 * @return
	 */
	public static List<Record> get_uid_details(String q) {
		// TODO Auto-generated method stub
		try{
			List<Record> re = null;
			String sql = "SELECT su.uid as id,su.name as text "
						+ "From system_user su "
						+ "WHERE su.uid <> 1 " ;
			 List<Object> list = new ArrayList<Object>();
			 if (StrKit.notBlank(q)) {
				sql += "AND name like ? ";
				list.add("%"+q+"%");
			}
			re = Db.find(sql+"order by su.uid desc", list.toArray());
			return re;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}	
	}
	
	/**
	 * 根据职称id查询员工
	 * @param job_title_id
	 * @return
	 * @throws Exception
	 */
	public static List<CrmStaff> get_staff_list(int job_title_id) throws Exception{
		String sql=" select id,name,mobile from crm_staff where deleted = 0 and job_title_id=? ";
		return CrmStaff.dao.find(sql, job_title_id);
	}
	
	/**
	 * 获得客户经理和设计师的列表
	 * @return
	 * @throws Exception
	 */
	/*public static Map<String,Object> get_staffs(int page,int rows,Map<String,Object> params) throws Exception{
		Integer staff_id=(Integer)params.get("staff_id");
		String start_date=(String)params.get("start_date");
		String end_date=(String)params.get("end_date");
		
		String selectSql=" SELECT cs.id,cs.`name`,cs.departments,cd.name as department_name ";
		StringBuffer exceptSelectSql=new StringBuffer(" FROM ");
		exceptSelectSql.append(" crm_staff cs ");
		exceptSelectSql.append(" LEFT JOIN crm_department cd on cs.departments=cd.id ");
		exceptSelectSql.append(" WHERE cs.deleted = 0 and");
		exceptSelectSql.append(" departments in (24,32) ");//营销和设计部
		exceptSelectSql.append(" and status=0 ");//在职
		List<Object> param=new ArrayList<Object>();
		if(staff_id!=null){
			exceptSelectSql.append(" AND cs.id=? ");
			param.add(staff_id);
		}
		
		exceptSelectSql.append(" ORDER BY cs.departments ");
		
		Page<CrmStaff> pages=dao.paginate(page,rows,selectSql,exceptSelectSql.toString(),param.toArray());
		
		List<CrmStaff> staffs=pages.getList();
		for(CrmStaff cs:staffs){
			List<Record> contracts=get_staff_contracts(cs,start_date,end_date);
			BigDecimal actual_amount_total=new BigDecimal("0.00");
			if(contracts.size()>0){
				for(Record r:contracts){
					BigDecimal contract_amount=r.getBigDecimal("amount");
					if(contract_amount==null){
						contract_amount=new BigDecimal("0.00");
					}
					
					BigDecimal coupon=BudgetModel.get_budget_item_sum(r.getInt("order_id"));
					if(coupon==null){
						coupon=new BigDecimal("0.00");
					}
					
					actual_amount_total=actual_amount_total.add(contract_amount.subtract(coupon));
				}
			}
			cs.put("actual_amount_total", actual_amount_total);
		}
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("total", pages.getTotalRow());
		map.put("rows", staffs);
		return map;
	}*/
	
	/**
	 * 
	 * @param cs
	 * @return
	 * @throws Exception
	 */
	public static List<Record> get_staff_contracts(CrmStaff cs,String start_date,String end_date) throws Exception{
		String sql= " SELECT "+
				    " oc.* "+
					" FROM "+
					" order_contract oc "+
					" LEFT JOIN `order` o ON oc.order_id = o.id "+
					" LEFT JOIN marketing_record mr ON mr.id = o.record_id "+
					" WHERE "+
					" o.type !=- 2 "+
					" AND oc.type = 0 ";
		List<Object> params=new ArrayList<Object>();
		if("24".equals(cs.getDepartments())){//设计部
			sql+=" AND o.designer_id=? ";
			params.add(cs.getId());
		}
		else{//营销部
			sql+=" AND mr.salesman_id=? ";
			params.add(cs.getId());
		}
		
		if(StrKit.notBlank(start_date)&&StrKit.isBlank(end_date)){
			sql+=" AND oc.sign_date >= ?";
			params.add(start_date);
		}
		if(StrKit.notBlank(start_date)&&StrKit.notBlank(end_date)){
			sql+=" AND oc.sign_date >= ? AND oc.sign_date <= ? ";
			params.add(start_date);
			params.add(end_date);
		}
		if(StrKit.isBlank(start_date)&&StrKit.notBlank(end_date)){
			sql+=" AND oc.sign_date <= ? ";
			params.add(end_date);
		}
		return Db.find(sql,params.toArray());
	}
	
	/**
	 * 根据员工id 获得对应的工种
	 * @param staff_id
	 * @return
	 * @throws Exception
	 */
	/*public static List<CrmStaffWorkType> get_staff_work_types(int staff_id) throws Exception{
		String sql=" SELECT cswt.*,btwt.name as work_type_name "
				 + " FROM crm_staff_work_type cswt"
				 + " LEFT JOIN b_t_work_type btwt on cswt.work_type_id=btwt.id "
				 + " WHERE cswt.staff_id=? ";
		return CrmStaffWorkType.dao.find(sql, staff_id);
	}*/
	
	/**
	 * 根据员工id 删除员工对应的工种
	 * @param staff_id
	 * @throws Exception
	 */
	public static void delete_staff_work_type(int staff_id) throws Exception{
		String sql=" delete from crm_staff_work_type where staff_id=? ";
		Db.update(sql, staff_id);
	}
	
	/**
	 * 查询出某个部门下的员工
	 * @param department_id
	 * @return
	 * @throws Exception
	 */
	public static List<CrmStaff> get_staff_by_department(int department_id) throws Exception{
		String sql=" SELECT id,name FROM crm_staff WHERE deleted = 0 and FIND_IN_SET(?,departments) and status!=-1 ";
		return CrmStaff.dao.find(sql, department_id);
	}
	
	/**
	 * 获得所有的员工
	 * @return
	 * @throws Exception
	 */
	public static List<CrmStaff> get_all_staffs() throws Exception{
		String sql=" select id,name from crm_staff where deleted = 0 ";
		return CrmStaff.dao.find(sql);	
	}
	
	/**
	 * 根据员工id集合获得员工
	 * @param staff_ids
	 * @return
	 * @throws Exception
	 */
	public static List<CrmStaff> get_staffs(String staff_ids) throws Exception{
		String sql=" SELECT cs.id, cs.name FROM crm_staff cs WHERE FIND_IN_SET(cs.id, ?) ";
		return dao.find(sql, staff_ids);
	}
}
