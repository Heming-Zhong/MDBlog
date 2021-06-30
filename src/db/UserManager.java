package db;

import java.sql.*;
import java.util.*;

import db.OperationState.State;

public class UserManager {
    public enum UserPermission {
        admin,
        visitor
    };

    Connection myConnection;
    Long loginTimeOut;

    //TODO: Prove User Table
    public UserManager(Connection dbConnection, Long timeout) {
        myConnection = dbConnection;
        loginTimeOut = timeout;
    }
    public OperationState register(String userName, String passwd, UserPermission permission) {
        int tmp = 0;
        if (permission == UserPermission.admin) {
            tmp = 1;
        }
        String query = String.format("INSERT INTO User (UserName, Passwd, Permission) VALUES (?, ?, ?)");
        Long userKey = -1L;

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, userName);
            stmt.setString(2, passwd);
            stmt.setInt(3, tmp);
            stmt.executeUpdate();
            
            ResultSet userKeys = stmt.getGeneratedKeys();
            while (userKeys.next()) {
                userKey = userKeys.getLong(1);
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        if (userKey == -1L) {
            return new OperationState(State.error, "DB can't add user", "DB can't add user");
        }
        return new OperationState(State.normal, String.format("Success create user %s with ID %d", userName, userKey), "Success");
    }

    public OperationState login(String userName, String passwd) {
        Boolean flag = false;
        String query = String.format("SELECT * FROM User WHERE UserName = ? and Passwd = ?;");
        Integer permissionInt = 2;
        String permissionStr = "";
        Integer userId = 0;
        String token = "";

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setString(2, passwd);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                flag = true;
                permissionInt = rs.getInt("Permission");
                userId = rs.getInt("ID");
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        switch (permissionInt) {
        case 0:
            permissionStr = "admin";
            break;
        case 1:
        default:
            permissionStr = "visitor";
            break;
        }

        if (flag) {
            query = "INSERT INTO Login (UserID, Token, TimeStamp) VALUES (?, ?, ?)";
            try {
                token = UUID.randomUUID().toString();
                PreparedStatement stmt = myConnection.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, token);
                stmt.setLong(3, System.currentTimeMillis());
                Integer affLine = stmt.executeUpdate();
    
                if (affLine < 1) {
                    flag = false;
                }
    
                stmt.close();
            } catch (Exception e) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                // System.exit(0);
            }
        }

        if (flag) {
            return new OperationState(State.normal, String.format("User %s login success", userName), new ArrayList<String>(Arrays.asList(token, permissionStr)));
        } else {
            return new OperationState(State.error, String.format("User %s login failed", userName), "Login Failed");
        }
    }

    public OperationState login(String token) {
        String userName = "";
        Boolean flag = false;
        String query = "SELECT * FROM Login WHERE Token = ?";
        Integer permissionInt = 2;
        String permissionStr = "";
        Integer userId = 0;

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long timeStamp = rs.getLong("TimeStamp");
                if (timeStamp + loginTimeOut <= System.currentTimeMillis()) {
                // Token Out-of-Date
                    // TODO: Delete Token
                    continue;
                }

                flag = true;
                userId = rs.getInt("UserID");
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        if (flag) {
            try {
                query = "SELECT * FROM User WHERE ID = ?";
                PreparedStatement stmt = myConnection.prepareStatement(query);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    userName = rs.getString("UserName");
                    permissionInt = rs.getInt("Permission");
                }
    
                stmt.close();
            } catch (Exception e) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                // System.exit(0);
            }
        }

        switch (permissionInt) {
        case 0:
            permissionStr = "admin";
            break;
        case 1:
        default:
            permissionStr = "visitor";
            break;
        }

        if (flag) {
            return new OperationState(State.normal, String.format("User %s login success", userName), new ArrayList<String>(Arrays.asList(userName, permissionStr)));
        } else {
            return new OperationState(State.error, String.format("Token %s login failed", token), "Login Failed");
        }
    }

    public OperationState changeName(String oldName, String newName) {
        Integer affectLine = 0;
        String query = "UPDATE User SET UserName = ? WHERE UserName = ?";
        
        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            affectLine = stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        if (affectLine > 0) {
            return new OperationState(State.normal, String.format("User %s rename to %s success, affecting %d User", oldName, newName, affectLine), "Success");
        } else {
            return new OperationState(State.normal, String.format("User %s rename failed", oldName), "Rename Failed");
        }
    }

    public OperationState changePasswd(String userName, String newPasswd) {
        Integer affectLine = 0;
        String query = "UPDATE User set Passwd = ? WHERE UserName = ?";
        
        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setString(1, newPasswd);
            stmt.setString(2, userName);
            affectLine = stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            // System.exit(0);
        }

        if (affectLine > 0) {
            return new OperationState(State.normal, String.format("User %s's passwd change success, affecting %d User", userName, affectLine), "Success");
        } else {
            return new OperationState(State.normal, String.format("User %s rename failed", userName), "Change Passwd Failed");
        }
    }
}
