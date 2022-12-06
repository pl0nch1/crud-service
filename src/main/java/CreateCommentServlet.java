import DAO.QueuesDAO;
import DAO.StatusTransitionsDAO;
import DAO.StatusesDAO;
import DAO.TicketsDAO;
import models.Status;
import models.StatusTransition;
import models.Ticket;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/comments/create")
public class CreateCommentServlet extends HttpServlet {
    private Logger logger;

    @Override
    public void init() {
        this.logger = LoggerUtils.tryGetFileLogger("EditTicketServlet", "/index.log", Level.FINER);
    }

    @Override
    public void destroy() {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueuesDAO queuesDAO = (QueuesDAO) getServletContext().getAttribute("queuesDAO");
        TicketsDAO ticketsDAO = (TicketsDAO) getServletContext().getAttribute("ticketsDAO");
        StatusesDAO statusesDAO = (StatusesDAO) getServletContext().getAttribute("statusesDAO");
        try {
            Ticket ticket = ticketsDAO.getByFullName(req.getParameter("full_name"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            short priority = Short.parseShort(req.getParameter("priority"));
            Status status = statusesDAO.get(Integer.parseInt(req.getParameter("status")));
            ticket.setCurrentStatus(status);
            ticket.setDescription(description);
            ticket.setTitle(title);
            ticket.setPriority(priority);
            ticketsDAO.save(ticket);
        }
        catch (SQLException exception)
        {
            logger.throwing("EditTicketServlet", "doPost", exception);
        }
        finally {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
