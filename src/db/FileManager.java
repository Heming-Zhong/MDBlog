package db;

import db.UserManager.UserPermission;
import db.OperationState.State;
import java.sql.*;
import java.util.*;

public class FileManager {
    public enum FileType {
        markdown,   // 1 in db
        resource    // 2 in db
    };

    private String pathPrefix = "./resources/";
    private Connection myConnection;

    public FileManager(Connection dbConnection) {
        myConnection = dbConnection;
    }

    // TODO: Prove File Table
    // TODO: OperationState uploadFile(FileItem file, String fileName, FileType fileType);

    public OperationState addFile(String fileName, FileType fileType, UserPermission permission) {
        int typeInt = 0;
        switch (fileType) {
        case markdown:
            typeInt = 1;
            break;
        case resource:
        default:
            typeInt = 2;
            break;
        }

        int permissionInt = 0;
        if (permission == UserPermission.admin) {
            permissionInt = 1;
        }

        String query = String.format("INSERT INTO File (FileName, FilePath, FileType, Permission) VALUES (?, ?, ?, ?)");
        Long fileKey = -1L;
        String filePath = pathPrefix + fileName;
        try {
            PreparedStatement stmt = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, fileName);
            stmt.setString(2, filePath);
            stmt.setInt(3, typeInt);
            stmt.setInt(4, permissionInt);
            stmt.executeUpdate();
            
            ResultSet fileKeys = stmt.getGeneratedKeys();
            while (fileKeys.next()) {
                fileKey = fileKeys.getLong(1);
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (fileKey == -1L) {
            return new OperationState(State.error, "DB can't add file", "DB can't add file");
        }
        return new OperationState(State.normal, String.format("Success create file %s with ID %d", fileName, fileKey), "Success");
    }

    public OperationState delFile(String fileName) {
        int affectLine = 0;

        String query = String.format("DELETE from File WHERE FileName = ?");

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, fileName);
            stmt.executeUpdate(query);
            
            affectLine = stmt.executeUpdate();

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (affectLine > 0) {
            return new OperationState(State.error, "DB can't delete file", "DB can't delete file");
        }
        return new OperationState(State.normal, String.format("Success delete file %s", fileName), "Success");
    }

    public OperationState renameFile(String oldName, String newName) {
        int affectLine = 0;

        String query = String.format("UPDATE File set FileName = ? WHERE FileName = ?");

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            stmt.executeUpdate(query);
            
            affectLine = stmt.executeUpdate();

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (affectLine > 0) {
            return new OperationState(State.error, "DB can't rename file", "DB can't reanme file");
        }
        return new OperationState(State.normal, String.format("Success rename file %s to %s", oldName, newName), "Success");
    }

    public OperationState listFile() {
        Boolean flag = false;
        String query = String.format("SELECT FileName FROM File");
        List<String> ret = new ArrayList<String>();

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(rs.getString("FileName"));
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (flag) {
            return new OperationState(State.normal, "List success", ret);
        } else {
            return new OperationState(State.error, "List failed", "List Failed");
        }
    }

    public OperationState listFile(FileType fileType) {
        int typeInt = 0;
        switch (fileType) {
        case markdown:
            typeInt = 1;
            break;
        case resource:
        default:
            typeInt = 2;
            break;
        }

        Boolean flag = false;
        String query = String.format("SELECT FileName FROM File WHERE FileType = ?");
        List<String> ret = new ArrayList<String>();

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setInt(1, typeInt);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(rs.getString("FileName"));
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (flag) {
            return new OperationState(State.normal, "List success", ret);
        } else {
            return new OperationState(State.error, "List failed", "List Failed");
        }
    }

    //TODO: OperationState listFile(List<String> Tags) {};

    public OperationState getFile(String fileName) {
        Boolean flag = false;
        String query = String.format("SELECT FilePath FROM File WHERE FileName = ?");
        String ret = "";

        try {
            PreparedStatement stmt = myConnection.prepareStatement(query);
            stmt.setString(1, fileName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ret = rs.getString("FilePath");
            }

            stmt.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        if (flag) {
            return new OperationState(State.normal, "Get success", ret);
        } else {
            return new OperationState(State.error, "Get failed", "List Failed");
        }
    }
}
