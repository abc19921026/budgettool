package system.models;

import java.util.List;

import com.jfinal.kit.StrKit;

import system.core.BaseModel;
import system.dao.Todo;

/**
 * 
 * @author 陈炳坤
 *
 */
public class TodoModel extends Todo{
	public static final int status_todo=0;//待办
	public static final int status_hasdao=1;//已办
	public static final int status_delete=-1;//删除
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 生成待办事宜
	 * @param title
	 * @param url
	 * @param type
	 * @param uid(多个已逗号隔开)
	 * @param apply_uid
	 * @throws Exception
	 */
	public static void generate_todo(String title,String url,int type,String check_uid,int apply_uid) throws Exception{
		if(StrKit.notBlank(check_uid)){
			String[] uids=check_uid.split(",");
			for(String uid:uids){
				Todo todo=new Todo();
				todo.setTitle(title);
				todo.setUrl(url);
				
				switch (type) {
					case 0:
						todo.setTypeName("转单申请");//转单申请
						break;
					case 1:
						todo.setTypeName("跑单申请");//跑单申请
						break;
					case 2:
						todo.setTypeName("开工预约");//开工预约
						break;
					case 3:
						todo.setTypeName("废单申请");//废单申请
						break;
					case 4:
						todo.setTypeName("预约审核");//预约审核
						break;
					case 5:
						todo.setTypeName("开工申请");//开工申请
						break;
					case 6:
						todo.setTypeName("预约过期");//预约过期
						break;
					case 7:
						todo.setTypeName("预算申请");
						break;
					case 8:
						todo.setTypeName("预算驳回");
						break;
					default:
						todo.setTypeName("无");
						break;
				}
				
				/*if(type==0){//转单申请
					todo.setTypeName("转单申请");
				}
				else if(type==3){//营销废单申请
					todo.setTypeName("废单申请");
				}
				else if(type==1){//跑单申请
					todo.setTypeName("跑单申请");
				}
				else if(type==2){//开工预约
					todo.setTypeName("开工预约");
				}
				else if(type==4){//预约审核
					todo.setTypeName("预约审核");
				}
				else if(type==5){//开工申请
					todo.setTypeName("开工申请");
				}*/
				todo.setType(type);
				todo.setStatus(status_todo);
				todo.setUid(Integer.parseInt(uid));
				BaseModel.setCreateTime(todo);
				BaseModel.setUpdateTime(todo);
				todo.setCreateUid(apply_uid);
				todo.save();
			}
		}
	}
	
	/**
	 * 获得某个用的待办事项列表
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public static List<Todo> get_todo_list(int uid,int status) throws Exception{
		StringBuffer sql=new StringBuffer(" SELECT st.*,cs.name as apply_name,su.picture ");
		sql.append(" FROM system_todo st ");
		sql.append(" LEFT JOIN crm_staff cs ON cs.uid=st.create_uid ");
		sql.append(" LEFT JOIN system_user su ON su.uid=st.create_uid ");
		sql.append(" WHERE st.uid=? and st.status=? ");
		sql.append(" ORDER BY st.create_time DESC ");
		return dao.find(sql.toString(),uid,status);
	}
}
