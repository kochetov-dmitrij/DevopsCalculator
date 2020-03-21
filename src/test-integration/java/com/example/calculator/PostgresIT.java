package com.example.calculator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Integration test for calculator.
 *
 * @author GreenD
 */
public class PostgresIT {

    private Connection db;

    @Before
    public void connectPostgres() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            fail("'org.postgresql.Driver' not found.");
        }
        try {
            db = DriverManager.getConnection(
                    "jdbc:postgresql:postgres",
                    "admin",
                    "pass");
        } catch (SQLException e) {
            fail("SQL exception\n" + e.getMessage());
        }
    }

    @After
    public void closePostgres() {
        try {
            db.close();
        } catch (SQLException e) {
            fail("Could not close Postgres connection.");
        }
    }

    @Test
    public void testDropTestTable() {
        try {
            PreparedStatement pst = db.prepareStatement("DROP TABLE test");
            pst.executeUpdate();
        } catch (SQLException e) {
            fail("Could not drop Postgres table." + e.getMessage());
        }
    }

    @Test
    public void testCreateTestTable() {
        try {
            PreparedStatement pst = db.prepareStatement(
                    "CREATE TABLE test (ts TIMESTAMP PRIMARY KEY, record VARCHAR (50) NOT NULL)");
            pst.executeUpdate();
        } catch (SQLException e) {
            fail("Could not create Postgres table." + e.getMessage());
        }
    }

    @Test
    public void testInsert() {
        try {
            PreparedStatement pst = db.prepareStatement("INSERT INTO test(ts, record) VALUES(?, ?)");
            pst.setTimestamp(1, new Timestamp(641423311));
            pst.setString(2, "2) 2 + 2 = 4");
            pst.executeUpdate();
        } catch (SQLException e) {
            fail("Could not insert into Postgres table." + e.getMessage());
        }
    }

    @Test
    public void testSelect() {
        try {
            PreparedStatement pst = db.prepareStatement("SELECT * FROM test");
            ResultSet rs = pst.executeQuery();
            rs.next();
            Timestamp ts = rs.getTimestamp(1);
            String record = rs.getString(2);
            assertEquals(ts, new Timestamp(641423311));
            assertEquals(record, "2) 2 + 2 = 4");
        } catch (SQLException e) {
            fail("Could not select Postgres table." + e.getMessage());
        }
    }
}
