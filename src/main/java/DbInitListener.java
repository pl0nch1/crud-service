import DAO.*;
import org.postgresql.ds.PGSimpleDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.*;

@WebListener
public class DbInitListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        Logger logger = Logger.getLogger("DBInitListener");
        try {
            logger.addHandler(new FileHandler("%t/startup.log"));
            System.out.println("Handler added");
        }
        catch (IOException e){
            System.out.println("Handler not added");
            logger.warning(String.format("Failed to handle logs with /startup.log file: %s", e));
        }

        logger.setLevel(Level.CONFIG);
        DataSource ds;
        try {
            ds = DataSourceCreator.createDataSource();
        }
        catch (IOException e){
            logger.severe("Failed to retrieve database connection configuration");
            return;
        }

        logger.config("Database connection pool configured successfully");

        StatusesDAO statusesDAO = new StatusesDAO(ds);
        PersonsDAO personsDAO = new PersonsDAO(ds);
        QueuesDAO queuesDAO = new QueuesDAO(ds, personsDAO);
        TicketsDAO ticketsDAO = new TicketsDAO(ds, queuesDAO, statusesDAO);
        CommentsDAO commentsDAO = new CommentsDAO(ds, personsDAO, ticketsDAO);

        ServletContext ctx = event.getServletContext();
        ctx.setAttribute("statusesDAO", statusesDAO);
        ctx.setAttribute("personsDAO", personsDAO);
        ctx.setAttribute("queuesDAO", queuesDAO);
        ctx.setAttribute("ticketsDAO", ticketsDAO);
        ctx.setAttribute("commentsDAO", commentsDAO);
        ctx.setAttribute("dataSource", ds);
    }

    private final static class DataSourceCreator {
        private DataSourceCreator() {}
        final static String dbConfigFilePath = "src/main/resources/dbconfig.properties";

        private static Properties getDBProperties() throws IOException {
            FileInputStream inputStream = new FileInputStream(dbConfigFilePath);
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
