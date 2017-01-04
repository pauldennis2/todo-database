package com.tiy;

import java.sql.SQLException;

/**
 * Created by Paul Dennis on 1/4/2017.
 */
public class DatabaseRunner {

    public static void main(String[] args) {
        ToDoDatabase db = new ToDoDatabase();
        try {
            db.init();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
