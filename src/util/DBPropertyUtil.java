package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {
	public static String getConnectionString()  {
		String url=null;
		Properties props=new Properties();
		try(FileInputStream fis=new FileInputStream("db.properties")) {
			props.load(fis);
			url=props.getProperty("url")+"/"+props.getProperty("database")+"?user="+props.getProperty("user")+"&password="+props.getProperty("password");
	
		}
		catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error:  Not able to Properties from File");

		}
		
		return url;
		
		
	}
}
