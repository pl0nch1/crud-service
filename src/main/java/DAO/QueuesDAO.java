package DAO;

import DAO.intefaces.Modifiable;
import DAO.intefaces.Readable;
import lombok.NonNull;
import models.Person;
import models.Queue;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class QueuesDAO implements Readable<Queue>, Modifiable<Queue> {
    private final DataSource ds;
    public QueuesDAO(DataSource ds){
        this.ds = ds;
    }

    private Queue getFromResultSet(ResultSet rs) throws SQLException {
        return Queue.builder()
                .queueId(rs.getInt("queue_id"))
                .name(rs.getString("name"))
                .topCount(rs.getInt("top_count"))
                .build();
    }

    private List<Queue> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Queue> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    public Queue getByName(String name) throws SQLException {
        String query = "SELECT * FROM queues WHERE name = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getFromResultSet(rs);
            else
                return null;
        }
    }

    @Override
    public Queue get(int id) throws SQLException {
        String query = "SELECT * FROM queues WHERE queue_id = ?";
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
    public List<Queue> list() throws SQLException {
        String query = "SELECT * FROM queues";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }
    private int update(Queue obj, Connection connection) throws SQLException{
        String query = "UPDATE queues SET name = ?, top_count = ? WHERE queue_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, obj.getName());
            statement.setInt(2, obj.getTopCount());
            statement.setInt(3, obj.getQueueId());
            statement.executeUpdate();
        }
        return obj.getQueueId();
    }

    private int insert(Queue obj, Connection connection) throws SQLException{
        String query = "INSERT INTO queues (name, top_count) VALUES (?, ?)";
        int id = 0;
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, obj.getName());
            statement.setInt(2, obj.getTopCount());
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
    public int save(Queue obj) throws SQLException {

        try (Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            int result;
            if (obj.getQueueId() == 0) {
                result = insert(obj, connection);
            } else {
                result = update(obj, connection);
            }

            connection.commit();

            return result;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM queues WHERE queue_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
