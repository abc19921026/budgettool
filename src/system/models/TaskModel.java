package system.models;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import system.dao.Task;

public class TaskModel{

	/**
	 * 
	 */
	/**
	 * 查询所有的定时任务
	 */
	private static final long serialVersionUID = 1L;
	public static List<Record> get_task_list(){
		String sql=" select * from system_task ";
		return Db.find(sql);
	}
}
