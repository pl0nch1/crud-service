package DAO.intefaces;

import java.sql.SQLException;
import java.util.List;

public interface Modifiable<T> {
    int save(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}
