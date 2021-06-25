package db;

public class DBConfig {
    public Boolean permitAdminReg;
    public Boolean permitVisitorListAll;

    public String DBURL;
    public String DBUser;
    public String DBPass;

    public DBConfig() {
        permitAdminReg = true;
        permitVisitorListAll = false;
    }
}
