package DAO;

import java.util.List;
import java.sql.SQLException;

public interface DAO<T> {
    T get(int id) throws SQLException;
    List<T> list() throws SQLException;
    int save(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}
