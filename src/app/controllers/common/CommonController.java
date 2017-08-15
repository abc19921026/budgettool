package app.controllers.common;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;

import app.models.common.CommonModel;
import system.core.BaseController;

public class CommonController extends BaseController{
	/**
	 * 获得所有的员工
	 * @throws Exception
	 */
	public void get_staff_select2() throws Exception{
		String q=getPara("q");
		String department=getPara("department");
		String jsonText=JsonKit.toJson(CommonModel.get_staff_select2(q, department, "notall"));
		renderJson(jsonText);
	}
	
	/**
	 * 获得部门select2
	 * @throws Exception
	 */
	public void get_department_select2() throws Exception{
		String q = getPara("q", "");
		List<Record> re1 = CommonModel.get_department_select2(q,0,"");//查询出所有的一级部门

		if(re1 == null){
			renderText("[]");
		}else{
			for(Record r1:re1){
				int id = r1.getInt("id");
				List<Record> re2 = CommonModel.get_department_select2(q, id,"");//查询出二级
				if(re2.size() > 0){
					r1.set("children", re2);
				}
			}
		}
		String jsonList = JsonKit.toJson(re1);
		renderJson(jsonList);
	}
	
	/**
	 * 选择来源
	 * @throws Exception
	 */
	public void get_source_select2() throws Exception{
		String q = getPara("q", "");
		List<Record> re1 = CommonModel.get_source();
		
		if(re1 == null){
			renderText("[]");
		}else {
			for (int i = 0; i< re1.size(); i++) {
				List<Record> re2 = CommonModel.get_source_details(q,re1.get(i).getInt("id"));
				if (re2.size() > 0) {
					re1.get(i).set("children", re2);
				}else {
					re1.get(i);
				}
			}
		}
		String jsonList = JsonKit.toJson(re1);
		renderJson(jsonList);
	}
	
	
	/**
	 * 获得部门员工
	 * @throws Exception
	 */
	public void get_department_staff_select2() throws Exception{
		String q = getPara("q", "");
		String query_token = getPara("query_token");
		String is_all=getPara("is_all","notall");//默认是过滤掉离职的
		List<Record> first_level_departments = CommonModel.get_department_select2("",0,query_token);//查询出所有的一级部门

		if(first_level_departments == null){
			renderText("[]");
		}else{
			for(int i=0;i<first_level_departments.size();i++){
				Record first_deparment=first_level_departments.get(i);
				int id = first_deparment.getInt("id");
				List<Record> second_level_departments = CommonModel.get_department_select2("", id,"");//查询出二级
				List<Record> staffs=new ArrayList<Record>();
				if(second_level_departments.size() > 0){//有二级部门
					first_deparment.set("children", second_level_departments);
					int k=0;
					for(int j=0;j<second_level_departments.size();j++){
						Record second_deparment=second_level_departments.get(j);
						int deparment_id=second_deparment.getInt("id");
						staffs=CommonModel.get_staff_select2(q,String.valueOf(deparment_id),is_all);
						
						if(staffs.size()>0){
							second_deparment.set("children",staffs);
						}
						else{
							second_level_departments.remove(j);
							j--;
							k++;
						}
					}
					if(second_level_departments.size()==0){
						first_level_departments.remove(i);
						i--;
					}
				}
				else{//无二级部门
					staffs=CommonModel.get_staff_select2(q,String.valueOf(id),is_all);
					if(staffs.size()>0){
						first_deparment.set("children", staffs);
					}
					else{
						first_level_departments.remove(i);
						i--;
					}
				}
			}
		}
		
		String jsonList = JsonKit.toJson(first_level_departments);
		renderJson(jsonList);
	}
	
	/**
	 * 获得职位员工
	 * @throws Exception
	 */
	public void get_estimator_staff_select2() throws Exception{
		String q = getPara("q", "");
		String query_token = getPara("query_token");
		List<Record> re = CommonModel.get_estimator_select2(q,query_token);

		if(re == null){
			renderText("[]");
		}
		String jsonList = JsonKit.toJson(re);
		renderJson(jsonList);
	}
	
	/**
	 * 获得省份
	 * @throws Exception
	 */
	public void get_province() throws Exception{
		String q=getPara("q");
		String jsonText=JsonKit.toJson(CommonModel.get_provinces(q));
		renderJson(jsonText);
	}
	
	/**
	 * 获得市
	 * @throws Exception
	 */
	public void get_city() throws Exception{
		int province_id=getParaToInt("province_id",-1);//必须选省份才选市
		String q=getPara("q");
		
		String jsonText=JsonKit.toJson(CommonModel.get_citys(q, province_id));
		renderJson(jsonText);
	}
	
	/**
	 * 获得区
	 * @throws Exception
	 */
	public void get_district() throws Exception{
		int city_id=getParaToInt("city_id",-1);
		String q=getPara("q");
		
		String jsonText=JsonKit.toJson(CommonModel.get_districts(q, city_id));
		renderJson(jsonText);
	}
	
	/**
	 * 获得来源类型
	 * @throws Exception
	 */
	public void get_source_type() throws Exception{
		String q=getPara("q");
		
		String jsonText=JsonKit.toJson(CommonModel.get_source_types(q));
		renderJson(jsonText);
	}
	
	/**
	 * 获得来源
	 * @throws Exception
	 */
	public void get_source() throws Exception{
		int id=getParaToInt("id",-1);
		String q=getPara("q");
		
		String jsonText=JsonKit.toJson(CommonModel.get_sources(id, q));
		renderJson(jsonText);
	}
}
