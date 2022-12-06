import DAO.QueuesDAO;
import models.Queue;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/queue")
public class QueueServlet extends HttpServlet {
    private Logger logger;
    private QueuesDAO queuesDAO;

    @Override
    public void init() {
        logger = LoggerUtils.tryGetFileLogger("CreateQueueServlet", "/index.log", Level.FINER);
        queuesDAO = (QueuesDAO) getServletContext().getAttribute("queuesDAO");
    }

    @Override
    public void destroy() {
        //Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/WEB-INF/createQueue.jsp");

        try {
            int key = Integer.parseInt(req.getParameter("key"));
            Queue queue = queuesDAO.get(key);
            req.setAttribute("queue", queue);
            dispatcher.forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("err","key must be a valid integer\n");
            dispatcher.forward(req, resp);
        } catch (SQLException e) {
            logger.throwing("","", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Something went wrong :\n%s", e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QueuesDAO queuesDAO = (QueuesDAO) getServletContext().getAttribute("queuesDAO");
        Queue queue = Queue.builder()
                .name(req.getParameter("name"))
                .build();
        try {
            queuesDAO.save(queue);
        } catch (SQLException e) {
            logger.throwing("", "", e);
        }

        resp.sendRedirect(getServletContext().getRealPath(getServletContext().getServletContextName()));
    }
}
