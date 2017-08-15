package system.controllers;

import java.io.File;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import system.aop.Permission;
import system.core.BaseController;
import system.core.LoginInterceptor;
import system.interceptor.PermissionInterceptor;
import system.models.FileModel;
import system.render.ImageRender;
import system.tools.PdfRender;


public class ImageController extends BaseController{

	/**
	 * 图片预览
	 */
	public void preview(){
		//ImageRender ir = new ImageRender();
		String file_path = getPara("s", "");
		if(StrKit.notBlank(file_path)){
			//todo check if file exist
			//File file = new File(file_path);
			if(FileModel.exists(file_path)){
				render(new ImageRender(file_path));
			}else{
				//System.out.println("file doesnt exists.");
				//String default_file_path = "default/default.png";
				//render(new ImageRender(default_file_path));
				redirect("/public/img/default.png");
			}
		}else{
			renderError(404);
		}
	}
	
	/**
	 * 图片下载
	 */
	public void download(){
		//ImageRender ir = new ImageRender();
		String file_path = getPara("s", "");
		if(StrKit.notBlank(file_path)){
			render(new ImageRender(file_path, "download"));
		}else{
			renderError(404);
		}
	}	
	
}
