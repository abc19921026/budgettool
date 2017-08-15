package system.render;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.FileRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderFactory;

/**
 * 文件以源文件名下载
 * @author Labmp0916
 *
 */
public class FileSrcRender extends Render {
	
	private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	
	private File file;
	private String sourceName;
	private static String baseDownloadPath;
	private static ServletContext servletContext;
	
	public FileSrcRender(File file, String sourceName) {
		if (file == null) {
			throw new IllegalArgumentException("file can not be null.");
		}
		if (StrKit.isBlank(sourceName)) {
			throw new IllegalArgumentException("sourceName can not be blank.");
		}
		this.file = file;
		this.sourceName = sourceName;
		this.servletContext =  JFinal.me().getServletContext();
	}
	
	public FileSrcRender(String fileName, String sourceName) {
		if (StrKit.isBlank(fileName)) {
			throw new IllegalArgumentException("fileName can not be blank.");
		}

		if (StrKit.isBlank(sourceName)) {
			throw new IllegalArgumentException("sourceName can not be blank.");
		}
		
		String fullFileName;
		fileName = fileName.trim();
		if (fileName.startsWith("/") || fileName.startsWith("\\")) {
			if (baseDownloadPath.equals("/")) {
				fullFileName = fileName;
			} else {
				fullFileName = baseDownloadPath + fileName;	
			}
		} else {
			fullFileName = baseDownloadPath + File.separator + fileName;
		}
		
		this.file = new File(fullFileName);
		this.sourceName = sourceName;
		this.servletContext =  JFinal.me().getServletContext();
	}
	
	static void init(String baseDownloadPath, ServletContext servletContext) {
		FileSrcRender.baseDownloadPath = baseDownloadPath;
		FileSrcRender.servletContext = servletContext;
	}
	
	public void render() {
		if (file == null || !file.isFile()) {
			RenderFactory.me().getErrorRender(404).setContext(request, response).render();
			return ;
        }
		
		// ---------
		response.setHeader("Accept-Ranges", "bytes");
		response.setHeader("Content-disposition", "attachment; filename=" + encodeFileName(sourceName));
        String contentType = servletContext.getMimeType(file.getName());
        response.setContentType(contentType != null ? contentType : DEFAULT_CONTENT_TYPE);
        
        // ---------
        if (StrKit.isBlank(request.getHeader("Range")))
        	normalRender();
        else
        	rangeRender();
	}
	
	protected String encodeFileName(String fileName) {
		try {
			// return new String(fileName.getBytes("GBK"), "ISO8859-1");
			return new String(fileName.getBytes(getEncoding()), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			return fileName;
		}
	}
	
	private void normalRender() {
		response.setHeader("Content-Length", String.valueOf(file.length()));
		InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
        	if (getDevMode()) {
        		throw new RenderException(e);
        	}
        } catch (Exception e) {
        	throw new RenderException(e);
        } finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
            if (outputStream != null)
            	try {outputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
        }
	}
	
	private void rangeRender() {
		Long[] range = {null, null};
		processRange(range);
		
		String contentLength = String.valueOf(range[1].longValue() - range[0].longValue() + 1);
		response.setHeader("Content-Length", contentLength);
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);	// status = 206
		
		// Content-Range: bytes 0-499/10000
		StringBuilder contentRange = new StringBuilder("bytes ").append(String.valueOf(range[0])).append("-").append(String.valueOf(range[1])).append("/").append(String.valueOf(file.length()));
		response.setHeader("Content-Range", contentRange.toString());
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
        try {
        	long start = range[0];
        	long end = range[1];
            inputStream = new BufferedInputStream(new FileInputStream(file));
            if (inputStream.skip(start) != start)
                	throw new RuntimeException("File skip error");
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            long position = start;
            for (int len; position <= end && (len = inputStream.read(buffer)) != -1;) {
            	if (position + len <= end) {
            		outputStream.write(buffer, 0, len);
            		position += len;
            	}
            	else {
            		for (int i=0; i<len && position <= end; i++) {
            			outputStream.write(buffer[i]);
                    	position++;
            		}
            	}
            }
            outputStream.flush();
        }
        catch (IOException e) {
        	if (getDevMode())	throw new RenderException(e);
        }
        catch (Exception e) {
        	throw new RenderException(e);
        }
        finally {
            if (inputStream != null)
                try {inputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
            if (outputStream != null)
            	try {outputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
        }
	}
	
	/**
	 * Examples of byte-ranges-specifier values (assuming an entity-body of length 10000):
	 * The first 500 bytes (byte offsets 0-499, inclusive): bytes=0-499
	 * The second 500 bytes (byte offsets 500-999, inclusive): bytes=500-999
	 * The final 500 bytes (byte offsets 9500-9999, inclusive): bytes=-500
	 * 															Or bytes=9500-
	 */
	private void processRange(Long[] range) {
		String rangeStr = request.getHeader("Range");
		int index = rangeStr.indexOf(',');
		if (index != -1)
			rangeStr = rangeStr.substring(0, index);
		rangeStr = rangeStr.replace("bytes=", "");
		
		String[] arr = rangeStr.split("-", 2);
		if (arr.length < 2)
			throw new RuntimeException("Range error");
		
		long fileLength = file.length();
		for (int i=0; i<range.length; i++) {
			if (StrKit.notBlank(arr[i])) {
				range[i] = Long.parseLong(arr[i].trim());
				if (range[i] >= fileLength)
					range[i] = fileLength - 1;
			}
		}
		
		// Range format like: 9500-
		if (range[0] != null && range[1] == null) {
			range[1] = fileLength - 1;
		}
		// Range format like: -500
		else if (range[0] == null && range[1] != null) {
			range[0] = fileLength - range[1];
			range[1] = fileLength - 1;
		}
		
		// check final range
		if (range[0] == null || range[1] == null || range[0].longValue() > range[1].longValue())
			throw new RuntimeException("Range error");
	}

}
