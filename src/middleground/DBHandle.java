
/**登陆控制
 * 首先实例化一个DBHandle,
 * 使用validate(user, passname)来登陆
 * register(user, passwd)来注册
 * 使用文件管理类的时候,
 * 需要通过该类的实例来使用
 */
package middleground;

import java.io.*;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
// import javax.servlet.http.HttpServletResponse;
import java.net.URL;
// import java.net;
// import org.apache.commons.io.filenameUtils;
// import org.apache.commons.lang3.StringUtils;
import db.*;
import db.OperationState.State;
import db.FileManager.FileType;
import db.UserManager.UserPermission;

public class DBHandle {
    
    // private boolean loged;
    private boolean admin;
    
    // public OperationState state;
    private DBManager manager;
    
    // filename -> content
    /***************************************/
    private Map<String, String> fileContent;
    public DBHandle(){
        admin = false;
        manager = new DBManager(new DBConfig());
        fileContent = new HashMap<String,String>();
    }

    public boolean getAuthority(){
        return admin;
    }

    public boolean validate(String user, String passwd){
        OperationState state = manager.login(user, passwd);
        admin = state.ret.equals("admin");
        return state.retState == State.normal;
    }

    // to do
    public boolean register(String user, String passwd){
        OperationState state = manager.register(user, passwd, UserPermission.visitor);
        if(state.retState == State.normal)
            return this.validate(user, passwd);
        return state.retState == State.normal;
    }

    // // 
    // public List<String> get_documents(){
    // 
    // }
    // get_documens_from_db(){
    //
    // }

    

    // 
    // public boolean update_file(String filename, String newcontent){
    //     if(admin)
    //         return update_file(filename, newcontent);

    //     return false;
    // }

    // 这边需要数据库提供修改文件名的功能renameFile(String filename, String newname)
    // public boolean rename(String filename, String newname){
    //     if(admin)
    //         return rename(filename, newcontent);

    //     return false;

    // }
    // public boolean newfile(String filename){
    //     if(admin)
    //         return newfile(filename);

    //     return false;
    // }
    // public boolean delfile(String filename){
    //     if(admin)
    //         return delfile(filename);

    //     return false;
    // }

    /***************************************/
    // 获取文件名列表
    public List<String> filemenu(){
        List<String> ans = null;

        OperationState state = manager.listFile();
        if(state.retState==State.normal){
            return state.retList;
        }
        return ans;
    }
    
    // 获取文件内容
    public String get_document_content(String filename){
        OperationState state = manager.getFile(filename);
        // String url = buffer.get();
        String content = "";
        String url = state.ret;


        // if(){

        try (InputStream is = new URL(url).openStream();){
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            content = sb.toString();
            
        } catch(Exception e) {

        }
        // }
        // else{

        // }

        return content;
    }

    // public static void writeFile(BufferedInputStream bis,boolean bis_closeFlag,
    //         String fileName,HttpServletResponse response) 
    //         throws IOException {
	// 	if(bis==null) {
	// 		return ;
	// 	}
	// 	int len=0;
	// 	byte[ ] bs=new byte[2048];
	// 	if(StringUtils.isNotBlank(fileName)) {
	// 		response.setContentType("application/octet-stream; charset=utf-8");
	// 		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "utf-8"));
	// 	}
		
	// 	while((len=bis.read(bs))!=-1) {
	// 		response.getOutputStream().write(bs,0,len);
	// 	}
	// 	if(bis_closeFlag) {
	// 		bis.close();
	// 	}
	// 	response.getOutputStream().flush();
	// }

    // 
    public boolean update_file(String filename, String newcontent){
        // String oldcontent = fileContent
        if(!admin)
            return false;

        if(!fileContent.containsKey(filename))
            return false;
        

        try {
            OperationState state = manager.getFile(filename);
            String url = state.ret;
            OutputStream outFile = new FileOutputStream(url);
            byte[] bytesArr = newcontent.getBytes();
            outFile.write(bytesArr);
            outFile.flush();
            outFile.close();

            fileContent.put(filename, newcontent);

        } catch (Exception e) {  
            return false;
        }

        return true;
    }

    // 这边需要数据库提供修改文件名的功能renameFile(String url, String newname)
    public boolean rename(String oldname, String newname){
        // String filename = FilenameUtils.getName(url.getPath());
        if(!admin)
            return false;
        if (newname.equals(oldname))
            return true;

        return manager.renameFile(oldname, newname).retState == State.normal;
    }

    public boolean newfile(String filename){
        // if(!admin)
        //     return false;
        OperationState state;
        if(!filename.substring(filename.length()-2).equals("md"))
            state = manager.addFile(filename, FileType.resource);    
        else
            state = manager.addFile(filename, FileType.markdown);

        if(state.retState == State.normal)
        {
            state = manager.getFile(filename);
            if(state.retState == State.normal)
            {
                String url =  state.ret;
                OutputStream outFile;
                try {
                    outFile = new FileOutputStream(url);    
                    outFile.close();
                } catch (Exception e) {
                    //TODO: handle exception
                }
                // outFile.write("");
                // outFile.flush();
                String newcontent = "";
                fileContent.put(filename, newcontent);
                return true;
            }
            return true;
        }

        return false;
    }

    public boolean delfile(String filename){
        if(!admin)
            return false;
        // 删除数据库项
        OperationState state = manager.delFile(filename);
        // 删除实际文件
        // TODO:
        try{
            String filePath = manager.getFile(filename).ret;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
        }
        catch(Exception e){
            System.out.println("delete failure.");
            e.printStackTrace();
        }

        return state.retState == State.normal;
    }
};

