
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


// import java.net.URLEncoder;
import java.nio.charset.Charset;
// import javax.servlet.http.HttpServletResponse;
import java.net.URL;
// import java.net;
// import org.apache.commons.io.filenameUtils;
// import org.apache.commons.lang3.StringUtils;
import db.*;
import db.OperationState.State;
import db.FileManager.FileType;
// import db.UserManager.UserPermission;

public class DBHandle {
    
    // private boolean loged;
    private boolean admin;
    
    // public OperationState state;
    private DBManager manager;
    
    // filename -> content
    private Map<String, String> fileContent;

    // (boolean) tag of login
    private boolean logined;
    
    private String token;

    /***************************************/
    public DBHandle(){
        System.out.println("debug_info: default handler");
        admin = false;
        logined = false;
        manager = new DBManager(new DBConfig());
        if(fileContent == null)
            fileContent = new HashMap<String,String>();
        
    }
    public DBHandle(String user, String passwd){
        System.out.println("debug_info: input handler");
        admin = false;
        logined = false;
        manager = new DBManager(new DBConfig());
        token = login(user, passwd);
        if(fileContent == null)
            fileContent = new HashMap<String,String>();
    }
    public DBHandle(String token){
        System.out.println("debug_info: token handler");
        admin = false;
        logined = false;
        manager = new DBManager(new DBConfig());
        if(fileContent == null)
            fileContent = new HashMap<String,String>();
        OperationState state = manager.login(token);
        if(state.retState == State.normal){
            logined = true;
            admin = state.retList.get(1).equals("admin");
            System.out.println("debug_info: " + token);
        }
        else {
            System.out.println("debug_error: " + state.msg);
        }
    }

    public boolean getAuthority(){
        return (admin && logined);
    }

    private String login(String user, String passwd){
        if (user == null || passwd == null) {
            return null;
        }
        OperationState state = manager.login(user, passwd);
        if(state.retState == State.normal){
            logined = true;
            admin = state.retList.get(1).equals("admin");
            return state.retList.get(0);
        }
        return null;
    }

    public String gettoken() {
        return token;
    }


    // public String register(String user, String passwd){
    //     OperationState state = manager.register(user, passwd, UserPermission.visitor);
    //     if(state.retState == State.normal)
    //         return this.validate(user, passwd);
    //     return "error";
    // }

    
    /***************************************/
    // 获取文件名列表
    public List<String> filemenu(){
        OperationState state = manager.listFile();
        if(state.retState==State.normal){
            return state.retList;
        }
        return null;
    }
    
    // 获取文件内容
    public String get_document_content(String filename){
        OperationState state = manager.getFile(filename);

        if(fileContent.containsKey(filename)){
            return fileContent.get(filename);
        }

        String content = "";
        if(state.retState==State.normal){
            String url = state.ret;  
            try (InputStream is = new URL(url).openStream();){
                
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                content = sb.toString();
                
            } catch(Exception e) { }
        }


        return content;
    }
                                
    public boolean update_file(String filename, String newcontent){
        if(!logined || !admin)
            return false;
        // ERROR: fileContent is not persistent

            
        
        OperationState state = manager.getFile(filename);
        if(state.retState!=State.normal)
            return false;
        
        try {
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
        if(!logined || !admin)
            return false;
        if (newname.equals(oldname))
            return true;
        return manager.renameFile(oldname, newname).retState == State.normal;
    }
    
    public boolean newfile(String filename){
        if(!logined || !admin)
            return false;

        OperationState state;
        if(filename.substring(filename.length()-2).equals("md"))
            state = manager.addFile(filename, FileType.markdown);    
        else
            state = manager.addFile(filename, FileType.resource);
        
        if(state.retState != State.normal)
            return false;
        
        state = manager.getFile(filename);
        if(state.retState == State.normal)
        {
            String url =  state.ret;
            OutputStream outFile;
            try {
                outFile = new FileOutputStream(url);    
                outFile.close();
            } catch (Exception e) {
                return false;
            }
            // outFile.write("");
            // outFile.flush();
            fileContent.put(filename, "");
            return true;
        }
        return false;
    }
    
    public boolean delfile(String filename){
        if(!logined || !admin)
            return false;
        
            // 获取文件URL
        OperationState state = manager.getFile(filename);
        if(state.retState!=State.normal)
            return false;
        String filePath = state.ret;

        // 删除数据库项
        state = manager.delFile(filename);
        if(state.retState!=State.normal)
            return false;
        // 删除实际文件
        try{
                
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();
            
        }
        catch(Exception e){
            return false;
            // System.out.println("delete failure.");
            // e.printStackTrace();
        }
        
        return true;
    }
};

