import middleground.*;
import db.*;
import db.FileManager.FileType;

DBManager dbm = new DBManager(new DBConfig());
dbm.login("admin", "123");
DBHandle dbh = new DBHandle("admin", "123");