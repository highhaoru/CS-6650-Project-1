import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SocketReader {

	private final Properties properties = new Properties();

	private SocketReader()
	   {
	      InputStream in = this.getClass().getClassLoader().getResourceAsStream("tests.properties");
	      System.out.println("Read all properties from file");
	      try {
	          properties.load(in);
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	   }
	   private static class instances
	   {
	      private static final SocketReader INS = new SocketReader();
	   }

	   public static SocketReader getInstance()
	   {
	      return instances.INS;
	   }

	   public String getProperty(String key){
		   System.out.println("Returning key: "+key+ "and Value: " + properties.getProperty(key));
	      return properties.getProperty(key);
	   }
	}

