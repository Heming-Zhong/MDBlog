public class Loginhandle{
    Loginhandle(){
        manager = new DBmanager();
    }
    public boolean validate(string user, string passwd){
        state = manager.login(user, passwd);
        return state == ;
    }

    public boolean register(string username, string passwd){
        //to do

    }

    private DBmanager manager;
    private OperationState state;
};

