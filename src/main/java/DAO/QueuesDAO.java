package DAO;

import models.Person;
import models.Queue;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class QueuesDAO implements DAO<Queue> {
    public static class QueueResponsibles {
        private final Set<Person> responsibles;
        private final HashSet<Person> removed;
        private final HashSet<Person> added;
        public QueueResponsibles(Set<Person> responsibles) {
            removed = new HashSet<>();
            added = new HashSet<>();
            this.responsibles = responsibles;
        }

        public Set<Person> getSet() {
            return responsibles;
        }

        public void add(Person person) {
            added.add(person);
        }

        public void remove(Person person) {
            removed.add(person);
        }

        public HashSet<Person> getAdded() {
            return removed;
        }

        public HashSet<Person> getRemoved() {
            return added;
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
        QueueResponsibles queueResponsibles = new QueueResponsibles(responsiblesSet);

        return Queue.builder()
                .queueId(rs.getInt("queue_id"))
                .name(rs.getString("name"))
                .topCount(rs.getInt("top_count"))
                .responsibles(queueResponsibles)
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
        String query = "SELECT * FROM queues WHERE person_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return getFromResultSet(rs, connection);
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
            obj.getResponsibles().getRemoved().stream()
                    .map(Person::getPersonId)
                    .forEach((personId) -> {
                        try {
                            removeStatement.setInt(1, obj.getQueueId());
                            removeStatement.setInt(2, personId);
                            removeStatement.addBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
            removeStatement.executeBatch();
        }

        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            obj.getResponsibles().getAdded().stream()
                    .map(Person::getPersonId)
                    .forEach((personId) -> {
                        try {
                            insertStatement.setInt(1, obj.getQueueId());
                            insertStatement.setInt(2, personId);
                            insertStatement.addBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
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
