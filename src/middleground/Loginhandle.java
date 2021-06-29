
/**登陆控制
 * 首先实例化一个Loginhandle,
 * 使用validate(user, passname)来登陆
 * register(user, passwd)来注册
 * 使用文件管理类的时候,
 * 需要通过该类的实例来使用
 * 
 */
import FileManager;

public class Loginhandle{
    public boolean validate(String user, String passwd){
        // DBmanager manager = new DBmanager();
        OperationState state = obj.manager.login(user, passwd);
        loged = state.retState == OperationState.State.normal;
        return loged;
    }

    public boolean register(String user, String passwd){
        //to do
        // manager = new DBmanager();
        // OperationState state = manager.register(user, passwd, permission);
        // loged = state.retState == OperationState.State.normal;
        // return loged;
        return false;
    }

    
    private FileManager obj;
    public String message;
    private boolean loged;
};

