import DAO.PersonsDAO;
import DAO.QueuesDAO;
import DAO.StatusesDAO;
import DAO.TicketsDAO;
import models.Ticket;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/dashboard")
public class IndexServlet extends HttpServlet {
    private Logger logger;

    @Override
    public void init() {
        this.logger = LoggerUtils.tryGetFileLogger("IndexServlet", "/index.log", Level.FINER);
    }

    @Override
    public void destroy() {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        RequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/index.jsp");
        TicketsDAO ticketsDAO = (TicketsDAO) context.getAttribute("ticketsDAO");

        try {
            List<Ticket> tickets = ticketsDAO.list();
            req.setAttribute("tickets", tickets);
            dispatcher.forward(req, resp);
        } catch (SQLException e) {
            logger.throwing("IndexServlet", "doGet", e);
        }
    }
}
