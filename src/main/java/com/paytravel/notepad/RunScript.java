package com.paytravel.notepad;

/**
 * Created by Вадим on 09.02.2018.
 */

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.h2.tools.RunScript.execute;

public class RunScript {
    static ResultSet runScript(Connection connection, String resourceName) throws SQLException {
        try {
            ResultSet result = execute(connection, new InputStreamReader(
                    ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName + ".sql"), Charset.forName("UTF8")));
            connection.commit();
            return result;
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("failed script execution " + resourceName, e);
        }
    }
}