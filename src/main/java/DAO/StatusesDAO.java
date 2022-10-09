package DAO;

import DAO.intefaces.Modifiable;
import DAO.intefaces.Readable;
import models.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StatusesDAO implements Readable<Status>, Modifiable<Status> {
    private final DataSource ds;

    public StatusesDAO(DataSource ds){
        this.ds = ds;
    }

    private Status getFromResultSet(ResultSet rs) throws SQLException {
        return Status.builder()
                .statusId(rs.getShort("status_id"))
                .name(rs.getString("name"))
                .build();
    }

    private List<Status> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Status> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    private int update(Status obj) throws SQLException{
        String query = "UPDATE statuses SET name = ? WHERE status_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, obj.getName());
            statement.setShort(2, obj.getStatusId());
            statement.executeUpdate();
        }
        return obj.getStatusId();
    }

    private int insert(Status obj) throws SQLException{
        String query = "INSERT INTO statuses (name) VALUES (?)";
        int id = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, obj.getName());
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
    public Status get(int id) throws SQLException {
        String query = "SELECT * FROM statuses WHERE status_id = ?";
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

    @Override
    public List<Status> list() throws SQLException {
        String query = "SELECT * FROM statuses";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }

    @Override
    public int save(Status obj) throws SQLException {
        if (obj.getStatusId() == 0) {
            return insert(obj);
        } else {
            return update(obj);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM statuses WHERE status_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }
}
