package com.tiy;

import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        statement.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN, user_id INT)");
        statement.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, fullname VARCHAR)");
    }

    public void insertToDo(Connection conn, String text, int userId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, false, ?)");
        statement.setString(1, text);
        statement.setInt(2, userId);
        statement.execute();
    }

    public int insertUser(Connection conn, String username, String fullname) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        statement.setString(1, username);
        statement.setString(2, fullname);
        statement.execute();

        statement = conn.prepareStatement("SELECT id FROM users where username = ?");
        statement.setString(1, username);
        ResultSet results = statement.executeQuery();
        results.next();
        return results.getInt("id");
    }

    public void deleteUser(Connection conn, String username) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM users where username = ?");
        statement.setString(1, username);
        statement.execute();
    }

    public void deleteToDo(Connection conn, String text, int userId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("DELETE FROM todos where text = ? AND user_id = ?");
        statement.setString(1, text);
        statement.setInt(2, userId);
        statement.execute();
    }

    public List<ToDoItem> selectTodos(Connection conn) throws SQLException {
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

    public List<ToDoItem> selectToDosForUser(Connection conn, int userID) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM todos " +
                "INNER JOIN users ON todos.user_id = users.id " +
                "WHERE users.id = ?");
        statement.setInt(1, userID);
        ResultSet results = statement.executeQuery();

        PreparedStatement userNameQueryStatement = conn.prepareStatement("SELECT username from users " +
                "Where id = ?");
        userNameQueryStatement.setInt(1, userID);
        ResultSet userNameResult = userNameQueryStatement.executeQuery();
        userNameResult.next();
        String userName = userNameResult.getString("username");
        //String fullName = userNameResult.getString("fullname");

        User user = retrieveUser(conn, userName);

        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone, user));
        }
        return items;
    }

    public void toggleToDo (Connection conn, String text, int userId) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done " +
                "WHERE text = ? AND user_id = ?");
        statement.setString(1, text);
        statement.setInt(2, userId);
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

    public User retrieveUser (Connection conn, String userName) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("Select * from users where username = ?");
        statement.setString(1, userName);
        ResultSet results = statement.executeQuery();
        results.next();
        int id = results.getInt("id");
        String retrievedUserName = results.getString("username");
        String retrievedFullName = results.getString("fullname");
        return new User(id, retrievedUserName, retrievedFullName);
    }

    public void closeServer () {
        server.stop();
    }
}