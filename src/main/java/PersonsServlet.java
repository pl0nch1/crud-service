import DAO.PersonsDAO;
import models.Person;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/persons")
public class PersonsServlet extends HttpServlet {
    private Logger logger;
    private PersonsDAO personsDAO;

    @Override
    public void init() {
        logger = LoggerUtils.tryGetFileLogger("CreateQueueServlet", "/index.log", Level.FINER);
        personsDAO = (PersonsDAO) getServletContext().getAttribute("personsDAO");
    }

    @Override
    public void destroy() {
        //Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Person> persons = personsDAO.list();
            req.setAttribute("persons", persons);
        }
        catch (SQLException e){
            resp.sendError(500, "Failed to get persons " + e);
        }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/persons.jsp");
        dispatcher.forward(req, resp);
    }
}
