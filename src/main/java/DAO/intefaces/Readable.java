package DAO.intefaces;

import java.util.List;
import java.sql.SQLException;

public interface Readable<T> {
    T get(int id) throws SQLException;
    List<T> list() throws SQLException;
}
