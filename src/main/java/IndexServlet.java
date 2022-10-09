import DAO.PersonsDAO;
import DAO.QueuesDAO;
import DAO.StatusesDAO;
import DAO.TicketsDAO;
import models.Queue;
import utils.LoggerUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/")
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
        TicketsDAO ticketsDAO = (TicketsDAO) context.getAttribute("ticketsDAO");
        PersonsDAO personsDAO = (PersonsDAO) context.getAttribute("personsDAO");
        StatusesDAO statusesDAO = (StatusesDAO) context.getAttribute("statusesDAO");
        QueuesDAO queuesDAO = (QueuesDAO) context.getAttribute("queuesDAO");
        PrintWriter writer = resp.getWriter();
        try {
            Queue queue = queuesDAO.get(3);
            QueuesDAO.MemorizedResponsibles responsibles = queue.getResponsibles();
            logger.severe(personsDAO.get(1).toString());
            responsibles.add(personsDAO.get(1));
            responsibles.add(personsDAO.get(2));
            responsibles.add(personsDAO.get(3));
            queuesDAO.save(queue);

        } catch (Exception e) {
            logger.throwing("", "", e);
            resp.setStatus(500);
        }
    }
}
