import DAO.PersonsDAO;
import DAO.QueuesDAO;
import models.Person;
import models.Queue;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/person/create")
public class CreatePersonsServlet extends HttpServlet {
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
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/createPerson.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonsDAO personsDAO = (PersonsDAO) getServletContext().getAttribute("personsDAO");
        Person person = Person.builder()
                .firstName(req.getParameter("fname"))
                .lastName(req.getParameter("lname"))
                .mail(req.getParameter("mail"))
                .build();
        try {
            personsDAO.save(person);
        } catch (SQLException e) {
            logger.throwing("", "", e);
        }
        finally{
            resp.sendRedirect(req.getContextPath() + "/persons");
        }
    }
}
