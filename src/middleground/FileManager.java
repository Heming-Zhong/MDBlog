import java.net;
import org.apache.commons.io.filenameUtils;

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
    }

    

    // 获取文件名列表
    public List<String> filemenu(){
        List<String> ans;
        OperationState state = manager.listFile();
        if(state.retState==OperationState.State.normal){


            return 
        }

        return ans;
    }

    // // 
    // public List<String> get_documents(){
    // 
    // }
    // get_documens_from_db(){
    //
    // }



    // 获取文件内容
    public String get_document_content(String filename){

    }

    // 
    public boolean update_file(String filename, String newcontent){
        String oldcontent = fileContent[filename]

        
    }

    // 这边需要数据库提供修改文件名的功能renameFile(String url, String newname)
    public boolean rename(String url, String newname){
        String filename = FilenameUtils.getName(url.getPath());
        if (newname==rename)
            return true;
        
        

    }
    public boolean newfile(String filename){


    }
    public boolean delfile(String filename){


    }

    // filename -> url
    private Map<String, String> buffer;


    // private OperationState state;
    private DBmanager manager;

    // 
    private Map<String, String> fileContent;

};
