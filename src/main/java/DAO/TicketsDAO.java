package DAO;

import models.Status;
import models.Ticket;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TicketsDAO implements DAO<Ticket> {
    private final DataSource ds;
    private final QueuesDAO queuesDAO;
    private final StatusesDAO statusesDAO;


    public TicketsDAO(DataSource ds, QueuesDAO queuesDAO, StatusesDAO statusesDAO) {
        this.ds = ds;
        this.queuesDAO = queuesDAO;
        this.statusesDAO = statusesDAO;
    }

    private Ticket getFromResultSet(ResultSet rs) throws SQLException {
        return Ticket.builder()
                .ticketId(rs.getInt("ticket_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .priority(rs.getShort("priority"))
                .queue(queuesDAO.get(rs.getInt("queue_id")))
                .currentStatus(statusesDAO.get(rs.getShort("status_id")))
                .build();
    }

    private List<Ticket> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Ticket> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    private int update(Ticket obj) throws SQLException{
        String query = "UPDATE tickets SET title = ?, description = ?, priority = ?, queue_id = ?, current_status_id = ?  WHERE ticket_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, obj.getTitle());
            statement.setString(2, obj.getDescription());
            statement.setInt(3, obj.getPriority());
            statement.setInt(4, obj.getQueue().getQueueId());
            statement.setShort(5, obj.getCurrentStatus().getStatusId());
            statement.setInt(6, obj.getTicketId());
            statement.executeUpdate();
        }
        return obj.getTicketId();
    }

    private int insert(Ticket obj) throws SQLException{
        String query = "INSERT INTO tickets (title, description, priority, queue_id, current_status_id) VALUES (?, ?, ?, ?, ?)";
        int id = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, obj.getTitle());
            statement.setString(2, obj.getDescription());
            statement.setInt(3, obj.getPriority());
            statement.setInt(4, obj.getQueue().getQueueId());
            statement.setShort(5, obj.getCurrentStatus().getStatusId());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs != null && rs.next()) {
                id = rs.getInt(1);
                rs.close();
            }
        }
        return id;
    }

    @Override
    public Ticket get(int id) throws SQLException {
        String query = "SELECT * FROM tickets WHERE ticket_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return getFromResultSet(rs);
        }
    }

    @Override
    public List<Ticket> list() throws SQLException {
        String query = "SELECT * FROM statuses";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }

    @Override
    public int save(Ticket obj) throws SQLException {
        statusesDAO.save(obj.getCurrentStatus());
        queuesDAO.save(obj.getQueue());
        if (obj.getTicketId() == 0) {
            return insert(obj);
        } else {
            return update(obj);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM tickets WHERE ticket_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate(query);
        }
    }
}
