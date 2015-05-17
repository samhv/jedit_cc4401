package core;


import java.io.InputStream;
import java.util.Properties;



public class PropertyManager {
	public static String GetProperty(String propName){
		Properties prop = new Properties();
    	InputStream input = null;
    	
		String filename = "github.props";
		input = PropertyManager.class.getClassLoader().getResourceAsStream(filename);
		if(input==null){
            return null;
		} 
    	try{
    	prop.load(input);
    	}catch(Exception exp){
    		return null;
    	}
    	return prop.getProperty(propName);    	
	}
}
