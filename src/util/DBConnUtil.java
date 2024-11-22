package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnUtil {
	public static Connection getCon() {
		Connection con=null;
		try {
			String url=DBPropertyUtil.getConnectionString();
			con=DriverManager.getConnection(url);
		}
		catch(SQLException e){
			e.printStackTrace();
			System.out.println("Not Connected to Database");

		}
		return con;
}
}
