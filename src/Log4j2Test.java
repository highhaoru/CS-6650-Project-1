import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*
* This is a test file to test functions of log4j2.jar
*/
public class Log4j2Test {
    private static final Logger LOGGER = LogManager.getLogger(Log4j2Test.class);

    public static void main(String[] args) {
        LOGGER.debug("This is a debug message");
        LOGGER.info("This is an info message");
        LOGGER.error("This is an error message");
    }
}
