public class Loginhandle{
    public boolean validate(string user, string passwd){
        //DBmanager manager = new DBmanager();
        OperationState state = obj.manager.login(user, passwd);
        message = state.msg;
        // switch (state.retState) {
        //     case OperationState.State.normal:
        //         message = state.msg;
        //         break;
        //     case OperationState.State.warning:
        
        //         break;

        //     default:
        //         break;
        // }
        
        return state.retState == OperationState.State.normal;
    }

    public boolean register(string user, string passwd){
        //to do

    }

    
    private FileManager obj;
    public String message;
};

