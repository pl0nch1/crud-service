import DAO.*;
import jdk.swing.interop.SwingInterOpUtils;
import models.*;
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
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/tickets/edit")
public class EditTicketServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TicketsDAO ticketsDAO = (TicketsDAO) getServletContext().getAttribute("ticketsDAO");
        StatusesDAO statusesDAO = (StatusesDAO) getServletContext().getAttribute("statusesDAO");
        CommentsDAO commentsDAO = (CommentsDAO) getServletContext().getAttribute("commentsDAO");
        PersonsDAO personsDAO = (PersonsDAO) getServletContext().getAttribute("personsDAO");
        StatusTransitionsDAO statusTransitionsDAO = (StatusTransitionsDAO) getServletContext().getAttribute("statusTransitionsDAO");
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/editTicket.jsp");
        try {
            Ticket ticket = ticketsDAO.getByFullName(req.getParameter("ticket_name"));
            List<Status> statuses = statusesDAO.list();
            List<StatusTransition> transitions = statusTransitionsDAO.listByTicketId(ticket.getTicketId());
            List<Comment> comments = commentsDAO.listByTicketId(ticket.getTicketId());
            List<Person> persons = personsDAO.list();
            req.setAttribute("statuses", statuses);
            req.setAttribute("ticket", ticket);
            req.setAttribute("transitions", transitions);
            req.setAttribute("comments", comments);
            req.setAttribute("persons", persons);
            dispatcher.forward(req, resp);
        }
        catch (SQLException exception)
        {
            logger.throwing("EditTicketServlet", "doGet", exception);
        }
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
