package utils;

import org.apache.juli.OneLineFormatter;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtils {
    public static Logger tryGetFileLogger(String loggerName, String localPath, Level level) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
        try {
            FileHandler handler = new FileHandler("%t" + localPath, true);
            handler.setFormatter(new OneLineFormatter());
            logger.addHandler(handler);
            logger.config(String.format("Handling logs with temp%s file\n", localPath));
        }
        catch (IOException e){
            logger.warning(String.format("Failed to handle logs with temp%s file:\n%s", localPath, e));
        }
        return logger;
    }
}
