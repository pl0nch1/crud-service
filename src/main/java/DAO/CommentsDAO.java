package DAO;

import models.Comment;
import models.Person;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CommentsDAO implements DAO<Comment> {
    private final DataSource ds;
    private final PersonsDAO personsDAO;
    private final TicketsDAO ticketsDAO;

    public CommentsDAO(DataSource ds, PersonsDAO personsDAO, TicketsDAO ticketsDAO){
        this.ds = ds;
        this.personsDAO = personsDAO;
        this.ticketsDAO = ticketsDAO;
    }

    private Comment getFromResultSet(ResultSet rs) throws SQLException {
        return Comment.builder()
                .commentId(rs.getInt("comment_id"))
                .author(personsDAO.get(rs.getInt("author_id")))
                .contents(rs.getString("contents"))
                .creationTime(rs.getTime("creation_time"))
                .ticket(ticketsDAO.get(rs.getInt("ticket_id")))
                .build();
    }

    private List<Comment> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Comment> list = new ArrayList<>();
        while (rs.next()){
            list.add(getFromResultSet(rs));
        }
        rs.close();
        return list;
    }

    private int update(Comment obj) throws SQLException{
        String query = "UPDATE comments SET ticket_id = ?, contents = ?, creation_time = ?, author_id = ? WHERE comment_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, obj.getTicket().getTicketId());
            statement.setString(2, obj.getContents());
            statement.setTime(3, obj.getCreationTime());
            statement.setInt(4, obj.getAuthor().getPersonId());
            statement.setInt(5, obj.getCommentId());
            statement.executeUpdate();
        }
        return obj.getCommentId();
    }

    private int insert(Comment obj) throws SQLException{
        String query = "INSERT INTO comments (ticket_id, contents, creation_time, author_id) VALUES (?, ?, ?, ?)";
        int id = 0;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, obj.getTicket().getTicketId());
            statement.setString(2, obj.getContents());
            statement.setTime(3, obj.getCreationTime());
            statement.setInt(4, obj.getAuthor().getPersonId());
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
    public Comment get(int id) throws SQLException {
        String query = "SELECT * FROM comments WHERE comment_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            return getFromResultSet(rs);
        }
    }

    @Override
    public List<Comment> list() throws SQLException {
        String query = "SELECT * FROM persons";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){
            ResultSet rs = statement.executeQuery();
            return getListFromResultSet(rs);
        }
    }

    @Override
    public int save(Comment obj) throws SQLException {
        ticketsDAO.save(obj.getTicket());
        personsDAO.save(obj.getAuthor());
        if (obj.getCommentId() == 0) {
            return insert(obj);
        } else {
            return update(obj);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM comments WHERE comment_id = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate(query);
        }
    }
}
