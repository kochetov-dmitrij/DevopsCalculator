/**
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import java.util.LinkedList;
import java.sql.*;

/**
 * @author GreenD
 */
public class CalculateController extends ParameterizableViewController {

    private LinkedList<String> history = new LinkedList<>();
    private int historyCounter = 1;
    private Calculator calculator;
    private Connection db;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        char button = request.getParameter("button").charAt(0);
        if ((button >= '0') && (button <= '9')) {
            calculator.digit(Integer.parseInt("" + button));
        } else {
            switch (button) {
                case '+':
                    calculator.add();
                    break;
                case '-':
                    calculator.subtract();
                    break;
                case '*':
                    calculator.multiply();
                    break;
                case '/':
                    calculator.divide();
                    break;
                case '=':
                    calculator.equals();
                    break;
                case 'C':
                    calculator.clear();
                    break;
            }
        }

        ModelAndView mav = super.handleRequestInternal(request, response);
        mav.addObject("displayAmount", calculator.getDisplayAmount());
        appendRecord(calculator.getLastRecord());
        mav.addObject("records", getHistory());

        return mav;
    }

    public void setCalculator(Calculator calculator) {
        this.calculator = calculator;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.print("org.postgresql.Driver not found");
            return;
        }
        try {
            db = DriverManager.getConnection(
                    "jdbc:postgresql:postgres",
                    "admin",
                    "pass");
        } catch (SQLException e) {
            System.out.print("SQL exception\n" + e.getMessage());
        }
    }

    private String wrapRecord(String equation) {
        return String.format("<div class=\"record\">%s</div>", equation);
    }

//    private void appendRecord(String equation) {
//        if (equation == null) return;
//        if (history.size() == 5) history.removeLast();
//        history.add(0, String.format("%d) %s", historyCounter++, equation));
//    }
//
//    private String getHistory() {
//        StringBuilder sb = new StringBuilder();
//        for (String s : history) {
//            sb.append(wrapRecord(s));
//        }
//        return sb.toString();
//    }

    private String getHistory() {
        try {
            PreparedStatement pst = db.prepareStatement("SELECT record FROM history ORDER BY ts DESC");
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append(wrapRecord(rs.getString(1)));
            }
            return sb.toString();
        } catch (SQLException e) {
            System.out.print("SQL exception. Could not append.\n" + e.getMessage());
            return "";
        }
    }

    private void appendRecord(String equation) {
        if (equation == null) return;

        try {
            PreparedStatement pst;
            ResultSet rs;

            pst = db.prepareStatement("SELECT COUNT(*) FROM history");
            rs = pst.executeQuery();
            rs.next();
            int length = rs.getInt(1);

            if (length == 5) {
                pst = db.prepareStatement("SELECT ts FROM history ORDER BY ts ASC LIMIT 1");
                rs = pst.executeQuery();
                rs.next();
                Timestamp ts = rs.getTimestamp(1);

                pst = db.prepareStatement("DELETE FROM history WHERE ts = (?)");
                pst.setTimestamp(1, ts);
                pst.executeUpdate();
            }

            pst = db.prepareStatement("INSERT INTO history(ts, record) VALUES(?, ?)");
            pst.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pst.setString(2, String.format("%d) %s", historyCounter++, equation));
            pst.executeUpdate();

        } catch (SQLException e) {
            System.out.print("SQL exception. Could not append.\n" + e.getMessage());
        }
    }
}
