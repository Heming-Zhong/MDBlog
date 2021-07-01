package db;

import db.OperationState.State;
import db.UserManager.UserPermission;
import db.FileManager.FileType;

import java.sql.*;

public class DBManager {
    private DBConfig conf;
    private UserManager userDBM;
    private FileManager fileDBM; 
    // private TagManager tagDBM;

    private Boolean isLogined;
    private UserPermission myPermission;
    private String myUserName;
    // private String myUserId;

    private Connection DBConnection;

    public DBManager(DBConfig config) {
        conf = config;
        isLogined = false;
        myPermission = UserPermission.visitor;
        myUserName = "";
        // myUserId = "";

        try {
            Class.forName("org.sqlite.JDBC");
            DBConnection = DriverManager.getConnection("jdbc:sqlite::resource:" + config.sqliteFile);
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        userDBM = new UserManager(DBConnection, config.loginTokenTimeOut);
        fileDBM = new FileManager(DBConnection);
        // tagDBM = new TagManager(DBConnection);
    }

    public OperationState register(String userName, String passwd, UserPermission permission) {
        // 注册一个用户，并指定权限。
        // 默认允许注册一个管理员，在配置文件修改。
        return userDBM.register(userName, passwd, permission);
    }
    public OperationState login(String userName, String passwd) {
        // 登陆。
        // 后续的大部分操作需要登陆才能进行。
        OperationState state = userDBM.login(userName, passwd);
        if (state.retState == State.normal) {
            switch (state.retList.get(1)) {
            case "admin":
                myPermission = UserPermission.admin;
                break;
            case "visitor":
            default:
                myPermission = UserPermission.visitor;
                break;
            }
            myUserName = userName;
            isLogined = true;
        }
        return state;
    }
    public OperationState login(String token) {
        // 登陆。
        // 后续的大部分操作需要登陆才能进行。
        OperationState state = userDBM.login(token);
        if (state.retState == State.normal) {
            switch (state.retList.get(1)) {
            case "admin":
                myPermission = UserPermission.admin;
                break;
            case "visitor":
            default:
                myPermission = UserPermission.visitor;
                break;
            }
            myUserName = state.retList.get(0);
            isLogined = true;
        }
        return state;
    }

    public OperationState changeName(String oldName, String newName) {
        // 更改用户名。
        // 要求登陆
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (oldName.equals(myUserName)) {
            return userDBM.changeName(oldName, newName);
        } else {
            return new OperationState(State.error, "You can't change other's name", "You can't change other's name");
        }
    }
    public OperationState changePasswd(String oldPasswd, String newPasswd) {
        // 更改自己的密码。
        // 要求登陆。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        return userDBM.changePasswd(myUserName, newPasswd);
    }
    public OperationState changePasswd(String userName, String oldPasswd, String newPasswd) {
        // 更改其他用户的密码。
        // 要求管理员登陆。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin) {
            return userDBM.changePasswd(userName, newPasswd);
        } else {
            return new OperationState(State.error, "You can't change other's passwd", "You can't change other's passwd");
        }
    }


    public OperationState addFile(String fileName, FileType fileType) {
        // 新增一个空白文件
        // 要求管理员登陆。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin) {
            return fileDBM.addFile(fileName, fileType, myPermission);
        } else {
            return new OperationState(State.error, "You can't add file", "You can't add file");
        }
    }
    public OperationState delFile(String fileName) {
        // 删除一个文件。
        // 要求管理员登陆。
        // 若文件作为依赖表中的依赖存在，则不允许删除
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin) {
            return fileDBM.delFile(fileName);
        } else {
            return new OperationState(State.error, "You can't del file", "You can't del file");
        }
    }
    public OperationState renameFile(String oldName, String newName) {
        // 重命名一个文件。
        // 并不保证更改**实际**的文件名。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin) {
            return renameFile(oldName, newName);
        } else {
            return new OperationState(State.error, "You can't rename", "You can't rename file");
        }
    }
        public OperationState listFile() {
        // 列出文件列表。
        // 默认允许不允许访客使用，在配置文件中更改。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin || conf.permitVisitorListAll) {
            return fileDBM.listFile();
        } else {
            return new OperationState(State.error, "You can't list", "You can't list");
        }
    }
    public OperationState listFile(FileType fileType) {
        // 列出指定类型的文件列表。
        // 默认只允许访客使用markdown列表，在配置文件中更改。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        if (myPermission == UserPermission.admin || conf.permitVisitorListAll) {
            return fileDBM.listFile(fileType);
        } else {
            return new OperationState(State.error, "You can't list", "You can't list");
        }
    }

    //TODO: OperationState listFile(List<String> tags)
    
    public OperationState getFile(String fileName) {
        // 返回文件的实际URL。
        if (!isLogined) {
            return new OperationState(State.error, "Login First", "Login First");
        }
        return fileDBM.getFile(fileName);
    }

}