import DAO.PersonsDAO;
import DAO.QueuesDAO;
import DAO.StatusesDAO;
import DAO.TicketsDAO;
import models.Queue;
import models.Status;
import models.Ticket;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/tickets/create")
public class CreateTicketServlet extends HttpServlet {
    private Logger logger;

    @Override
    public void init() {
        this.logger = LoggerUtils.tryGetFileLogger("CreateTicketServlet", "/index.log", Level.FINER);
    }

    @Override
    public void destroy() {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueuesDAO queuesDAO = (QueuesDAO) getServletContext().getAttribute("queuesDAO");
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/createTicket.jsp");
        try {
            List<Queue> queues = queuesDAO.list();
            logger.info(String.valueOf(queues.size()));
            req.setAttribute("queues", queues);
            dispatcher.forward(req, resp);
        }
        catch (SQLException exception)
        {
            logger.throwing("CreateTicketServlet", "doGet", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // doGet(req, resp);
        QueuesDAO queuesDAO = (QueuesDAO) getServletContext().getAttribute("queuesDAO");
        TicketsDAO ticketsDAO = (TicketsDAO) getServletContext().getAttribute("ticketsDAO");
        StatusesDAO statusesDAO = (StatusesDAO) getServletContext().getAttribute("statusesDAO");
        try {
            Queue queue = queuesDAO.get(Integer.parseInt((String) req.getParameter("queue")));
            String description = req.getParameter("description");
            String title = req.getParameter("title");
            Ticket ticket = Ticket.builder().description(description).title(title).queue(queue)
                    .currentStatus(statusesDAO.get(1)).build();
            ticketsDAO.save(ticket);
        }
        catch (SQLException exception)
        {
            logger.throwing("CreateTicketServlet", "doPost", exception);
        }
        finally {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
