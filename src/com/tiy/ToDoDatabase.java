package com.tiy;

import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by erronius on 1/3/2017.
 */
public class ToDoDatabase {

    private Server server;

    public final static String DB_PATH = "jdbc:h2:./main";

    public void init () throws SQLException {
        server = Server.createWebServer();
        server.start();
        Connection conn = DriverManager.getConnection(DB_PATH);
        Statement statement = conn.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN)");
    }

    public void insertToDo(Connection conn, String text) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, false)");
        statement.setString(1, text);
        statement.execute();
    }

    public void deleteToDo(Connection conn, String text) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM todos where text = ?");
        statement.setString(1, text);
        statement.execute();
    }

    public ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = statement.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone));
        }
        return items;
    }

    public void toggleToDo(Connection conn, String text) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE text = ?");
        statement.setString(1, text);
        statement.execute();
    }

    public ToDoItem retrieveTodo (Connection conn, String text) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("Select * from todos where text = ?");
        statement.setString(1, text);
        ResultSet results = statement.executeQuery();
        results.next();
        int id = results.getInt("id");
        String retrievedText = results.getString("text");
        boolean isDone = results.getBoolean("is_done");
        ToDoItem retrievedTodo = new ToDoItem(id, retrievedText, isDone);
        return retrievedTodo;
    }

    public void closeServer () {
        server.stop();
    }
}