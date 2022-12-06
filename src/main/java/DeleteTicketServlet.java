import DAO.TicketsDAO;
import models.Ticket;
import utils.LoggerUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/tickets/delete")
public class DeleteTicketServlet extends HttpServlet {
    private Logger logger;

    @Override
    public void init() {
        this.logger = LoggerUtils.tryGetFileLogger("EditTicketServlet", "/index.log", Level.FINEST);
    }

    @Override
    public void destroy() {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TicketsDAO ticketsDAO = (TicketsDAO) getServletContext().getAttribute("ticketsDAO");
        try {
            Ticket ticket = ticketsDAO.getByFullName(req.getParameter("full_name"));
            logger.info(String.valueOf(ticket.getTicketId()));
            ticketsDAO.delete(ticket.getTicketId());
        }
        catch (SQLException exception)
        {
            logger.info(exception.toString());
            logger.throwing("DeleteTicketServlet", "doPost", exception);
        }
        finally {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
