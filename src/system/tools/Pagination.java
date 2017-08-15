package system.tools;

import java.util.List;

public class Pagination<T> {
	private int currentPage;//第几页
	
	private int totalRows;//总的记录数
	
	private int pageRows=10;//每页的记录数 默认是10条
	
	private List<T> rowList;
	
	/**
	 * 构造器
	 * @param currentPage
	 * @param totalRows
	 * @param pageRows
	 * @param rowList
	 */
	public Pagination(int currentPage,int totalRows,int pageRows,List<T> rowList){
		this.currentPage=currentPage;
		this.totalRows=totalRows;
		this.pageRows=pageRows;
		this.rowList=rowList;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getPageRows() {
		return pageRows;
	}

	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}

	public List<T> getRowList() {
		return rowList;
	}

	public void setRowList(List<T> rowList) {
		this.rowList = rowList;
	}
}
