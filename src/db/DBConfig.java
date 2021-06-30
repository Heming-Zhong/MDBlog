package db;

public class DBConfig {
    public Boolean permitAdminReg;
    public Boolean permitVisitorListAll;
    public Integer loginTokenTimeOut;

    public String DBURL;
    public String DBUser;
    public String DBPass;

    public DBConfig() {
        permitAdminReg = true;
        permitVisitorListAll = false;
        loginTokenTimeOut = 1800000; //30min
    }
}
