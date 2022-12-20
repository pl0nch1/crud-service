import DAO.*;
import org.postgresql.ds.PGSimpleDataSource;
import utils.LoggerUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.*;

@WebListener
public class DbInitListener implements ServletContextListener {
    Logger logger;
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger = LoggerUtils.tryGetFileLogger("DBInitListener", "/config.log", Level.CONFIG);

        ServletContext ctx = event.getServletContext();

        DataSource ds;
        try {
            ds = DataSourceCreator.createDataSource();
        }
        catch (IOException e){
            logger.severe(String.format("Failed to retrieve database connection configuration:\n%s", e));
            return;
        }

        logger.config("Database connection pool configured successfully");

        StatusesDAO statusesDAO = new StatusesDAO(ds);
        PersonsDAO personsDAO = new PersonsDAO(ds);
        QueuesDAO queuesDAO = new QueuesDAO(ds);
        TicketsDAO ticketsDAO = new TicketsDAO(ds, queuesDAO, statusesDAO);
        CommentsDAO commentsDAO = new CommentsDAO(ds, personsDAO, ticketsDAO);
        StatusTransitionsDAO statusTransitionsDAO = new StatusTransitionsDAO(ds, ticketsDAO, statusesDAO);

        ctx.setAttribute("statusesDAO", statusesDAO);
        ctx.setAttribute("personsDAO", personsDAO);
        ctx.setAttribute("queuesDAO", queuesDAO);
        ctx.setAttribute("ticketsDAO", ticketsDAO);
        ctx.setAttribute("commentsDAO", commentsDAO);
        ctx.setAttribute("statusTransitionsDAO", statusTransitionsDAO);
        ctx.setAttribute("dataSource", ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    private final static class DataSourceCreator {
        private DataSourceCreator() {}
        final static String dbConfigFileLocalPath = "dbconfig.properties";

        private static Properties getDBProperties() throws IOException {
            InputStream inputStream = DataSourceCreator.class.getResourceAsStream(dbConfigFileLocalPath);
            Properties props = new Properties();
            props.load(inputStream);
            inputStream.close();
            return props;
        }

        public static DataSource createDataSource() throws IOException
        {
            Properties dbProperties = getDBProperties();

            PGSimpleDataSource ds = new PGSimpleDataSource();
            ds.setUrl(dbProperties.getProperty("url"));
            ds.setUser(dbProperties.getProperty("user"));
            ds.setPassword(dbProperties.getProperty("password"));

            return ds;
        }
    }
}
