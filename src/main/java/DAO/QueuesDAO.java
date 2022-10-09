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
    public static class MemorizedResponsibles {
        private final Set<Person> responsibles;
        private final HashSet<Person> removed;
        private final HashSet<Person> added;
        public MemorizedResponsibles(Set<Person> responsibles) {
            removed = new HashSet<>();
            added = new HashSet<>();
            this.responsibles = responsibles;
        }

        public Set<Person> getSet() {
            return responsibles;
        }

        public void add(@NonNull Person person) {
            responsibles.add(person);
            added.add(person);
        }

        public void remove(@NonNull Person person) {
            responsibles.remove(person);
            removed.add(person);
        }

        public HashSet<Person> getAdded() {
            return added;
        }

        public HashSet<Person> getRemoved() {
            return removed;
        }

        private void clearHistory() {
            removed.clear();
            added.clear();
        }
    }
    private final DataSource ds;
    private final PersonsDAO personsDAO;
    public QueuesDAO(DataSource ds, PersonsDAO personsDAO){
        this.ds = ds;
        this.personsDAO = personsDAO;
    }

    private Queue getFromResultSet(ResultSet rs, Connection connection) throws SQLException {
        String responsiblesQuery = "SELECT * FROM queue_responsibles WHERE queue_id = ?";
        Set<Person> responsiblesSet = new HashSet<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(responsiblesQuery)) {
            preparedStatement.setInt(1, rs.getInt("queue_id"));
            ResultSet responsiblesResultSet = preparedStatement.executeQuery();
            while (responsiblesResultSet.next()) {
                responsiblesSet.add(personsDAO.get(responsiblesResultSet.getInt("person_id")));
            }
        }
        MemorizedResponsibles memorizedResponsibles = new MemorizedResponsibles(responsiblesSet);

        return Queue.builder()
                .queueId(rs.getInt("queue_id"))
                .name(rs.getString("name"))
                .topCount(rs.getInt("top_count"))
                .responsibles(memorizedResponsibles)
                .build();
    }

    private List<Queue> getListFromResultSet(ResultSet rs, Connection connection) throws SQLException {
        ArrayList<Queue> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs, connection));
        }
        rs.close();
        return list;
    }

    @Override
    public Queue get(int id) throws SQLException {
        String query = "SELECT * FROM queues WHERE queue_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getFromResultSet(rs, connection);
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
            return getListFromResultSet(rs, connection);
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

    private void updateResponsibles(Queue obj, Connection connection) throws SQLException {
        String insertSql = "INSERT INTO queue_responsibles(queue_id, person_id) VALUES (?, ?)";
        String removeSql = "DELETE FROM queue_responsibles WHERE queue_id = ? AND person_id = ?";

        try (PreparedStatement removeStatement = connection.prepareStatement(removeSql)) {
            for (Person person: obj.getResponsibles().getRemoved()) {
                int personId = person.getPersonId();
                try {
                    removeStatement.setInt(1, obj.getQueueId());
                    removeStatement.setInt(2, personId);
                    removeStatement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            removeStatement.executeBatch();
        }

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            for (Person person: obj.getResponsibles().getAdded()) {
                int personId = person.getPersonId();
                try {
                    insertStatement.setInt(1, obj.getQueueId());
                    insertStatement.setInt(2, personId);
                    insertStatement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            insertStatement.executeBatch();
        }

        obj.getResponsibles().clearHistory();
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

            updateResponsibles(obj, connection);
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
            statement.executeUpdate(query);
        }
    }
}
