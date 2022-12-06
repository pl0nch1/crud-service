import DAO.*;
import models.*;
import org.apache.tomcat.jni.Time;
import utils.LoggerUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
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
        CommentsDAO commentsDAO = (CommentsDAO) getServletContext().getAttribute("commentsDAO");
        PersonsDAO personsDAO = (PersonsDAO) getServletContext().getAttribute("personsDAO");
        TicketsDAO ticketsDAO = (TicketsDAO) getServletContext().getAttribute("ticketsDAO");
        String ticket_name = req.getParameter("full_name");
        try {
            String content = req.getParameter("contents");
            Person person = personsDAO.get(Integer.parseInt(req.getParameter("person_id")));
            logger.info(person.toString());
            logger.info(req.getParameter("person_id"));
            Ticket ticket = ticketsDAO.getByFullName(ticket_name);
            Comment comment = Comment.builder()
                    .creationTime(new java.sql.Time(System.currentTimeMillis()))
                    .author(person)
                    .contents(content)
                    .ticket(ticket)
                    .build();

            commentsDAO.save(comment);
            resp.sendRedirect(req.getContextPath() + "/tickets/edit?ticket_name=" + ticket_name);
        }
        catch (SQLException exception)
        {
            logger.throwing("EditTicketServlet", "doPost", exception);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }
}
