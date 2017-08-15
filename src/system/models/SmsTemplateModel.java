package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import app.dao.CrmStaff;
import system.dao.SmsTemplate;

public class SmsTemplateModel extends SmsTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 获得短信模版列表的数据
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> json_template_list(int pageNumber,int pageSize,String content,Integer staff_id,Integer type) throws Exception{
		String selectSql=" select a.*,b.name ";
		StringBuffer exceptSelect=new StringBuffer(" from system_sms_template a ");
		exceptSelect.append(" left join crm_staff b on a.uid=b.uid ");
		exceptSelect.append(" where 1=1 ");
		List<Object> params=new ArrayList<Object>();
		if(StrKit.notBlank(content)){
			exceptSelect.append(" and a.content like ? ");
			params.add("%"+content+"%");
		}
		
		if(staff_id!=null){
			exceptSelect.append(" and a.uid = ? ");
			CrmStaff crmStaff=CrmStaff.dao.findById(staff_id);
			params.add(crmStaff.getUid());
		}
		
		if(type!=-1){
			exceptSelect.append(" and a.type=? ");
			params.add(type);
		}
		Page<SmsTemplate> pages=SmsTemplate.dao.paginate(pageNumber, pageSize,selectSql,exceptSelect.toString(),params.toArray());
		int total=pages.getTotalRow();
		List<SmsTemplate> templates=pages.getList();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("total",total);
		map.put("rows", templates);
		return map;
	}
	
	/**
	 * 查询出自己的模版（静态模版）
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static List<SmsTemplate> get_mysms_template(int uid) throws Exception{
		String sql=" select * from system_sms_template where uid=? and type=2 ";
		return SmsTemplate.dao.find(sql,uid);
	}
	
	/**
	 * 查询出所有的模版（静态模版）
	 * @return
	 * @throws Exception
	 */
	public static List<SmsTemplate> get_sms_template() throws Exception{
		String sql=" select * from system_sms_template where type=2 ";
		return SmsTemplate.dao.find(sql);
	}
	
	/**
	 * 查询出所有的动态模版
	 * @return
	 * @throws Exception
	 */
	public static List<SmsTemplate> get_sms_dynamictemplate() throws Exception{
		String sql=" select * from system_sms_template where type=1";
		return SmsTemplate.dao.find(sql);
	}
}
