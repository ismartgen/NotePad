package com.paytravel.notepad;

import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Вадим on 09.02.2018.
 */
public class NoteDao {
    public static final String SELECT_NOTES = "SELECT DATE,NOTE FROM NOTES";
    private static final java.lang.String INSERT_NOTE = "INSERT INTO NOTES (DATE,NOTE) VALUES (?,?);";

    public NoteDao() {
    }

    public ArrayList<Note> getNotes() {
        try (PreparedStatement stat = ConnectionManager.getConnection().prepareStatement(SELECT_NOTES)) {
            try (ResultSet resultSet = stat.executeQuery()) {
                ArrayList<Note> notes = new ArrayList<>();
                while (resultSet.next()) {
                    notes.add(new Note(resultSet.getTimestamp(1).toLocalDateTime(), resultSet.getString(2)));
                }
                return notes;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertNote(Timestamp timestamp, String text) {
        Connection con = null;
        try (PreparedStatement stat = (con = ConnectionManager.getConnection()).prepareStatement(INSERT_NOTE)) {
            stat.setTimestamp(1,timestamp);
            stat.setString(2, text);
            stat.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.rollback();
            } catch (SQLException e) {
                //empty
            }
        }
    }
}
