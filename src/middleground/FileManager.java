import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net;
import org.apache.commons.io.filenameUtils;
import org.apache.commons.lang3.StringUtils;
import db.OperationState;
import db.OperationState.State;
import db.FileManager.FileType;
/**文件管理类
 * 使用者能获得文件名列表
 * 能通过文件名获得文件内容
 * 能修改文件名
 * 能删除、增加、更新文件
 * 均通过文件名来操作
 * 
 * 该类私有文件名到url的映射,以及文件内容的缓存
 */

public class FileManager{
    public FileManager(){
        manager = new DBmanager();
        fileContent = new HashMpa<String,String>();
    }

    

    // 获取文件名列表
    protected List<String> filemenu(){
        // List<String> urlList, ans;

        OperationState state = manager.listFile();
        if(state.retState==State.normal){
            // urlList = state.retList;
            for(String url : urlList){
                // String filename = FilenameUtils.getName(url.getPath());
                // ans.add(filename);
                // buffer.put(filename, url);
            }
            
            return state.retList;
        }
        return ans;
    }

    // // 
    // protected List<String> get_documents(){
    // 
    // }
    // get_documens_from_db(){
    //
    // }



    // 获取文件内容
    protected String get_document_content(String filename){
        OperationState state = manager.getFile(filename);
        // String url = buffer.get();
        String content;
        String url = state.ret;


        // if(){

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            
        } finally {
            is.close();
        }
        content = sb.toString();
        // }
        // else{

        // }

        return content;
    }

    public static void writeFile(BufferedInputStream bis,boolean bis_closeFlag,
            String fileName,HttpServletResponse response) 
            throws IOException {
		if(bis==null) {
			return ;
		}
		int len=0;
		byte[ ] bs=new byte[2048];
		if(StringUtils.isNotBlank(fileName)) {
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
		}
		
		while((len=bis.read(bs))!=-1) {
			response.getOutputStream().write(bs,0,len);
		}
		if(bis_closeFlag) {
			bis.close();
		}
		response.getOutputStream().flush();
	}

    // 
    protected boolean update_file(String filename, String newcontent){
        // String oldcontent = fileContent

        if(fileContent.containsKey(filename)){

            try {
                OperationState state = manager.getFile(filename);
                String url = state.ret;
                OutputStream outFile = new FIleOutputStream(url);
                outFile.write(newcontent);
                outFile.flush();
                outFile.close();

                fileContent.set(filename, newcontent);

            } catch (Exception e) {
                
                return false;
            }
            return true;
        }
        return false;
    }

    // 这边需要数据库提供修改文件名的功能renameFile(String url, String newname)
    protected boolean rename(String oldname, String newname){
        // String filename = FilenameUtils.getName(url.getPath());
        if (newname==oldname)
            return true;

        return manager.renameFile(oldname, newname).retState == State.normal;
    }

    protected boolean newfile(String filename){
        OperationState state;
        if(filename.substring(filename.length()-2)!="md")
            state = manager.addFile(filename, FileType.resource);    
        else
            state = manager.addFile(filename, FileType.markdown);

        return state.retState == State.normal;
    }

    protected boolean delfile(String filename){
        // 删除数据库项
        OperationState state = manager.delFile(filename);
        // 删除实际文件
        // TODO:
        
        return state.retState == State.normal;
    }

    // // filename -> url
    // protected Map<String, String> buffer;


    // protected OperationState state;
    protected DBmanager manager;

    // filename -> content
    protected Map<String, String> fileContent;

};
