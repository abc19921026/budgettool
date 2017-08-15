package system.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import system.core.BaseController;

import system.models.FileModel;
import system.models.UserModel;
import system.render.ImageRender;
import system.tools.DateTools;


public class FileController extends BaseController{

	public void index(){
		
	}
	
	/**
	 * 
	 */
	
	public void upload(){
		String callback = getPara("callback", "");//回调
		int refresh = getParaToInt("refresh", 0);//保存后是否刷新页面
		String upload_path = getPara("upload_path", "default");//上传路径
		String type = getPara("type", "default");//文件上传类型default,private,public,tmp
		String multi_selection = getPara("multi", "0");//是否允许多个上传
		String max_files = getPara("max_files", "10");//最大同时上传文件个数
		if(multi_selection.equals("1")){
			multi_selection = "true";
		}else{
			multi_selection = "false";
		}
		
		if(StrKit.isBlank(callback)){
			renderText("<p class='alert'>请设置回调路由.</p>");
			return;
		}
		
		setAttr("callback", callback);
		setAttr("upload_path", upload_path);
		setAttr("type", type);
		setAttr("multi_selection", multi_selection);
		setAttr("max_files", max_files);
		String max_file_size = "20m";
		setAttr("max_file_size", max_file_size);
		
		
		setAttr("refresh", refresh);
		
		//允许的文件类型
		String extensions = getPara("extensions", "");
		setAttr("extensions", extensions);
	}
	

	public void do_upload(){
		UploadFile upload_file = null;
		List<UploadFile> upload_files = null;
		
		//这个upload_path参数必须是url传参/file/do_upload?upload_path=foo，否则获取不到
		String upload_path = getPara("upload_path", "default");//默认目录
		String type = getPara("type", "default");//类型 public 公开, private 不公开, tmp 临时文件
		if(StrKit.isBlank(upload_path)){
			upload_files = getFiles();
		}else{
			String default_path = upload_path + "/" + DateTools.getDate();//default目录按日期分类存放
			upload_files = getFiles(default_path);
		}
		int uid  = current_user_id();
		int fid = 0;
		Map<String, Object> re = new HashMap<String, Object>();

		//目前都是单文件上传
		if(upload_files.size() > 0){
			for(int i = 0; i < upload_files.size(); i++){
				upload_file = upload_files.get(i);
				fid = FileModel.save(upload_file, uid);
				re.put("fid", fid);
			}
		}
		String msg = "File(s) uploaded successfully.";

		render_success_message(msg, re);
	}	
	
	public void image(){
		//ImageRender ir = new ImageRender();
		String file_path = getPara(0, "");
		render(new ImageRender("www.123.PNG"));
	}
	
	//**************************************************************************
	
	public void delete() throws Exception {
		Integer fid;
		String checkedIds = getPara("checkedIds");
		boolean succeed = false;
		if(StrKit.notBlank(checkedIds)){
			String[] vids = checkedIds.split(",");
			for(String id : vids){
				fid = Integer.valueOf(id);
				succeed = FileModel.fileDelete(fid);
			}
		}else{
			renderError(404);
		}
		if(succeed){
			renderText("删除成功！");
		}else{
			renderText("删除失败！");
		}
	}
	
	public void deleteOnlyData() throws Exception {
		Integer fid = getParaToInt("fid");
		if(fid != null){
			boolean succeed = system.dao.File.dao.deleteById(fid);
			if(succeed){
				renderText("删除成功！");
			}else{
				renderText("删除失败！");
			}
		}
	}
	
	public void findSrc() throws Exception {
		Integer fid = getParaToInt(0);
		int uid = current_user_id();
		if(fid != null){
			system.dao.File file = system.dao.File.dao.findById(fid);
			//String path = "/"+file.getFilePath() + "/" + file.getFileName();
			String path = file.getFilePath();
			if(UserModel.headshotUpdate(path, uid)){
				renderText("保存成功");
			}else{
				renderText("保存失败");
			}
			/*File srcFile = new File(PathKit.getWebRootPath() +"/"+ file.getFilePath() + "/" + file.getFileName());
			renderFile(srcFile);*/
		}else{
			renderText("");
		}
	}
	
	/**
	 * 文件下载
	 * @throws Exception
	 */
	public void download() throws Exception{
	    int fid = getParaToInt("fid",0);
		system.dao.File sFile = system.dao.File.dao.findById(fid);
		String file_path = sFile.getFilePath();
		String absoulte_path = FileModel.web_root_path+"/"+FileModel.baseUploadPath+"/"+file_path;
		File file = new File(absoulte_path);
	    if (file.isFile()) {
	    	String file_src_name = sFile.getFileSrcName();
	    	//含有源文件名的按源文件名下载
	    	if(StrKit.notBlank(file_src_name))
	    		renderSrcFile(file, file_src_name);
	    	else
	    		renderFile(file);
	        return;
	    } 
	    renderNull();
	}
}
