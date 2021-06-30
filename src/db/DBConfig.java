package db;

public class DBConfig {
    public Boolean permitAdminReg;
    public Boolean permitVisitorListAll;
    public Long loginTokenTimeOut;
    public String sqliteFile;

    public String DBURL;
    public String DBUser;
    public String DBPass;

    public DBConfig() {
        permitAdminReg = true;
        permitVisitorListAll = false;
        loginTokenTimeOut = 1800000L; //30min
        sqliteFile = "mdblog.db";
    }
}
