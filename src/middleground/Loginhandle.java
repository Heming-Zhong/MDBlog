
/**登陆控制
 * 首先实例化一个Loginhandle,
 * 使用validate(user, passname)来登陆
 * register(user, passwd)来注册
 * 使用文件管理类的时候,
 * 需要通过该类的实例来使用
 * 
 */
import FileManager;

public class DBHandle extends FileManager{
    DBHandle(){
        loged = false;
    }
    //
    public boolean validate(String user, String passwd){
        // DBmanager manager = new DBmanager();
        OperationState state = super.manager.login(user, passwd);
        loged = state.retState == OperationState.State.normal;
        return loged;
    }

    // to do
    public boolean register(String user, String passwd){
        // manager = new DBmanager();
        // OperationState state = manager.register(user, passwd, permission);
        // loged = state.retState == OperationState.State.normal;
        // return loged;
        return false;
    }



    public List<String> filemenu(){
        return super.filemenu();
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
        return super.get_document_content(filename);
    }

    // 
    public boolean update_file(String filename, String newcontent){
        if(loged)
            return super.update_file(filename, newcontent);

        return false;
    }

    // 这边需要数据库提供修改文件名的功能renameFile(String filename, String newname)
    public boolean rename(String filename, String newname){
        if(loged)
            return super.rename(filename, newcontent);

        return false;

    }
    public boolean newfile(String filename){
        if(loged)
            return super.newfile(filename);

        return false;
    }
    public boolean delfile(String filename){
        if(loged)
            return super.delfile(filename);

        return false;
    }
    
    private boolean loged;
};

