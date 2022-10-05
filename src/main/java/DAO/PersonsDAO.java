package DAO;

import models.Person;
import models.Status;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PersonsDAO implements DAO<Person> {
    private final DataSource ds;

    public PersonsDAO(DataSource ds){
        this.ds = ds;
    }

    private Person getFromResultSet(ResultSet rs) throws SQLException {
        return Person.builder()
                .personId(rs.getInt("person_id"))
                .mail(rs.getString("mail"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .build();
    }

    private List<Person> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Person> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    private int update(Person obj) throws SQLException{
        String query = "UPDATE persons SET mail = ?, firstname = ?, lastname = ? WHERE person_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, obj.getMail());
            statement.setString(2, obj.getFirstName());
            statement.setString(3, obj.getLastName());
            statement.setInt(4, obj.getPersonId());
            statement.executeUpdate();
        }
        return obj.getPersonId();
    }

    private int insert(Person obj) throws SQLException{
        String query = "INSERT INTO persons (mail, firstname, lastname) VALUES (?, ?, ?)";
        int id = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, obj.getMail());
            statement.setString(2, obj.getFirstName());
            statement.setString(3, obj.getLastName());
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
    public Person get(int id) throws SQLException {
        String query = "SELECT * FROM persons WHERE person_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return getFromResultSet(rs);
        }
    }

    @Override
    public List<Person> list() throws SQLException {
        String query = "SELECT * FROM persons";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }

    @Override
    public int save(Person obj) throws SQLException {
        if (obj.getPersonId() == 0) {
            return insert(obj);
        } else {
            return update(obj);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM persons WHERE person_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }
}
