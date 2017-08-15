package system.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class MyMetaBuilder extends MetaBuilder{

	public Set<String> includedTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
	
	public MyMetaBuilder(DataSource dataSource) {
		super(dataSource);
		// TODO Auto-generated constructor stub
	}

	public void addIncludedTable(String... includedTables){
		if(includedTables != null){
			for (String table: includedTables){
				this.includedTables.add(table);
			}
		}
	}
	
	/**
	 * 通过继承并覆盖此方法，跳过一些不希望处理的 table，定制更加灵活的 table 过滤规则
	 * @return 返回 true 时将跳过当前 tableName 的处理
	 */
	protected boolean isSkipTable(String tableName) {
		if(includedTables.contains(tableName)){
			return false;
		}else{
			return true;
		}
	}
	
	protected void buildTableNames(List<TableMeta> ret) throws SQLException {
		ResultSet rs = getTablesResultSet();
		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			
			if (excludedTables.contains(tableName)) {
				//System.out.println("Skip table :" + tableName);
				continue ;
			}
			if (isSkipTable(tableName)) {
				//System.out.println("Skip table :" + tableName);
				continue ;
			}
			System.out.println("Build table :" + tableName);
			TableMeta tableMeta = new TableMeta();
			tableMeta.name = tableName;
			tableMeta.remarks = rs.getString("REMARKS");
			
			tableMeta.modelName = buildModelName(tableName);
			tableMeta.baseModelName = buildBaseModelName(tableMeta.modelName);
			ret.add(tableMeta);
		}
		rs.close();
	}	
	
	/**
	 * 构造 modelName，mysql 的 tableName 建议使用小写字母，多单词表名使用下划线分隔，不建议使用驼峰命名
	 * oracle 之下的 tableName 建议使用下划线分隔多单词名，无论 mysql还是 oralce，tableName 都不建议使用驼峰命名
	 */
	protected String buildModelName(String tableName) {
		// 移除表名前缀仅用于生成 modelName、baseModelName，而 tableMeta.name 表名自身不能受影响
		if (removedTableNamePrefixes != null) {
			for (String prefix : removedTableNamePrefixes) {
				if (tableName.startsWith(prefix)) {
					tableName = tableName.replaceFirst(prefix, "");
					break;
				}
			}
		}
		
		// 将 oralce 大写的 tableName 转成小写，再生成 modelName
		if (dialect instanceof OracleDialect) {
			tableName = tableName.toLowerCase();
		}
		
		return StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
	}	
}
