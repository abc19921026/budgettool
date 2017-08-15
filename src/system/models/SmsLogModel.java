package system.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import system.dao.SmsLog;

public class SmsLogModel extends SmsLog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 获取自己发送的短信记录
	 * @param pageNumber
	 * @param pageSize
	 * @param uid
	 * @param content 
	 * @param mobile 
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> json_sms_log(int pageNumber,int pageSize,int uid, String mobile, String content) throws Exception{
		String select=" select * ";
		StringBuffer exceptSelect = new StringBuffer(" from system_sms_log where 1=1 ");
		List<Object> param = new ArrayList<Object>();
//		param.add(uid);
		
		if(StrKit.notBlank(mobile)){
			exceptSelect.append(" AND mobile LIKE ? ");
			param.add("%"+mobile+"%");
		}
		
		if(StrKit.notBlank(content)){
			exceptSelect.append(" AND content LIKE ? ");
			param.add("%"+content+"%");
		}
		
		exceptSelect.append(" order by create_time desc ");
		Page<SmsLog> page=SmsLog.dao.paginate(pageNumber, pageSize, select, exceptSelect.toString(),param.toArray());
		int total=page.getTotalRow();
		List<SmsLog> smsLogs=page.getList();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("total", total);
		map.put("rows", smsLogs);
		return map;
	}
}
