/*
 * Copyright 2010 David Green
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
public class PostgresIntegrationTest {

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
