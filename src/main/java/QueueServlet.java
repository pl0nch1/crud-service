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
        dispatcher.forward(req, resp);
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

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
