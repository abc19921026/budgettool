package system.tools;
import com.jfinal.plugin.activerecord.Page;
public class PageHandler {

    // 当前页码
    public int pageNumber;
    // 每页数量
    public int pageSize;
    // 总页数
    public int totalPage;
    // 总行数
    public int totalRow;
    // 是否有下一页
    public boolean hasNextPage; 
    // 是否有前一页
    public boolean hasPreviousPage; 
        // 下一页页码
    public int nextPageNumber; 
     // 前一页页码
    public int previousPageNumber; 


    // 页面基础URL，用作翻页事件
    public String basePathUrl;
    // 偏移量，当分页过多时只显示当前页前后偏移部分页码
    public int offSetNum = 3;
    // 需要显示出来的页码范围 - 起始页码
    public int startPageNum;
    // 需要显示出来的页码范围 - 结束页码
    public int endPageNum;

    /**
     * 构造函数，初始化
     * @param pageNumber
     * @param pageSize
     * @param totalPage
     * @param totalRow
     */
    public PageHandler(int pageNumber,int pageSize,int totalPage,int totalRow) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalRow = totalRow;
        this.calculatePage();
        this.calculateShowPage();
    }

    /**
     * 构造函数，根据JFinal内置Page初始化
     * @param pageInfo
     */
    public <T> PageHandler(Page<T> pageInfo){
        this.pageNumber = pageInfo.getPageNumber();//1
        this.pageSize = pageInfo.getPageSize();//5
        this.totalPage =pageInfo.getTotalPage() ;//1
        this.totalRow =pageInfo.getTotalRow();//1
        this.calculatePage();
        this.calculateShowPage();
    }
    /**
     * 计算当前页是否有前后页，并得出前后页码
     */
    private void calculatePage() {
        if ((this.pageNumber - 1) > 0) {
            this.hasPreviousPage = true;
            this.previousPageNumber = this.pageNumber - 1;
        } else {
            this.hasPreviousPage = false;
            this.previousPageNumber = 0;
        }

        if (this.pageNumber >= this.totalPage) {
            this.hasNextPage = false;
            this.nextPageNumber = 0;
        } else {
            this.hasNextPage = true;
            this.nextPageNumber = this.pageNumber + 1;
        }

    }
    /**
     * 计算需要显示出来的页码范围
     */
    private void calculateShowPage(){
        // 前偏移
        int preCalulateNum = this.pageNumber - this.offSetNum;
        // 后偏移
        int nextCalulateNum = this.pageNumber + this.offSetNum;

        if (preCalulateNum > 0){
            this.startPageNum = preCalulateNum;
        }else{
            this.startPageNum = 1;
        }

        if (nextCalulateNum > this.totalPage){
            this.endPageNum = this.totalPage;
        }else{
            this.endPageNum = nextCalulateNum;
        }
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public int getNextPageNumber() {
        return nextPageNumber;
    }

    public void setNextPageNumber(int nextPageNumber) {
        this.nextPageNumber = nextPageNumber;
    }

    public int getPreviousPageNumber() {
        return previousPageNumber;
    }

    public void setPreviousPageNumber(int previousPageNumber) {
        this.previousPageNumber = previousPageNumber;
    }

    public String getBasePathUrl() {
        return basePathUrl;
    }

    public void setBasePathUrl(String basePathUrl) {
        this.basePathUrl = basePathUrl;
    }

    public int getOffSetNum() {
        return offSetNum;
    }

    public void setOffSetNum(int offSetNum) {
        this.offSetNum = offSetNum;
    }

    public int getStartPageNum() {
        return startPageNum;
    }

    public void setStartPageNum(int startPageNum) {
        this.startPageNum = startPageNum;
    }

    public int getEndPageNum() {
        return endPageNum;
    }

    public void setEndPageNum(int endPageNum) {
        this.endPageNum = endPageNum;
    }
}
