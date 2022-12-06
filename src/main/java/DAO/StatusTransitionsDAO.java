package DAO;

import DAO.intefaces.Readable;
import models.StatusTransition;
import models.Ticket;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StatusTransitionsDAO implements Readable<StatusTransition> {
    private final DataSource ds;
    private final StatusesDAO statusesDAO;
    private final TicketsDAO ticketsDAO;


    public StatusTransitionsDAO(DataSource ds, TicketsDAO ticketsDAO, StatusesDAO statusesDAO) {
        this.ds = ds;
        this.ticketsDAO = ticketsDAO;
        this.statusesDAO = statusesDAO;
    }

    private StatusTransition getFromResultSet(ResultSet rs) throws SQLException {
        return StatusTransition.builder()
                .transitionId(rs.getInt("transition_id"))
                .ticket(ticketsDAO.get(rs.getInt("ticket_id")))
                .statusTo(statusesDAO.get(rs.getInt("status_to")))
                .statusFrom(statusesDAO.get(rs.getInt("status_from")))
                .transitionTime(rs.getTime("transition_time"))
                .build();
    }

    private List<StatusTransition> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<StatusTransition> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    @Override
    public StatusTransition get(int id) throws SQLException {
        String query = "SELECT * FROM status_transitions WHERE status_transition_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getFromResultSet(rs);
            else
                return null;
        }
    }

    public List<StatusTransition> listByTicketId(int id) throws SQLException {
        String query = "SELECT * FROM status_transitions WHERE ticket_id = ? ORDER BY transition_time DESC";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }

    @Override
    public List<StatusTransition> list() throws SQLException {
        String query = "SELECT * FROM status_transitions";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }
}
