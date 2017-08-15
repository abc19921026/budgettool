package system.models;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import system.dao.Vocabulary;

public class VocabularyModel {
	/**
	 * 通过词汇名获取其子类
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static List<Record> vocabulary_list(String name) throws Exception{
		String sql = "select * from system_vocabulary where name = ? limit 1";
		Vocabulary vocabulary = Vocabulary.dao.findFirst(sql, name);
		
		String sql2 = "select * from system_vocabulary_term where vid = ? order by weight";
		List<Record> list = Db.find(sql2,vocabulary.getVid());
		
		Record please_select = new Record();
		please_select.set("tid", 0);
		please_select.set("name","------请选择------");
		list.add(0,please_select);//在选择框里加一个"请选择"
		
		return list;
	}
}
