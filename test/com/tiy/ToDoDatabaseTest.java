package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by erronius on 1/3/2017.
 */
public class ToDoDatabaseTest {

    Connection conn;
    static ToDoDatabase toDoDatabase;
    public static final String DB_PATH = "jdbc:h2:./main";

    @Before
    public void setUp() throws Exception {
        conn = DriverManager.getConnection(DB_PATH);
        if (toDoDatabase == null) {
            toDoDatabase = new ToDoDatabase();
            toDoDatabase.init();
        }
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testInit () throws SQLException  {
        // test to make sure we can access the new database
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);
    }

    @Test
    public void testInsertUser () throws SQLException {
        String userName = "paul@dennis";
        String fullName = "Paul Dennis";
        int newUserId = toDoDatabase.insertUser(conn, userName, fullName);
        PreparedStatement statement = conn.prepareStatement("Select * from users where username = ?");
        statement.setString(1, userName);
        ResultSet results = statement.executeQuery();
        assertNotNull(results);
        int numResults = 0;
        int retrievedUserId = -1;
        while (results.next()) {
            numResults++;
            retrievedUserId = results.getInt("id");
        }
        assertEquals(1, numResults);
        assertEquals(newUserId, retrievedUserId);

        toDoDatabase.deleteUser(conn, userName);

        results = statement.executeQuery();

        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testInsertTodo () throws SQLException {
        String todoText = "UnitTest-ToDo";

        String userName = "paul@dennis";
        String fullName = "Paul Dennis";
        int userId = toDoDatabase.insertUser(conn, userName, fullName);

        toDoDatabase.insertToDo(conn, todoText, userId);
        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
        stmt.setString(1, todoText);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(1, numResults);

        ToDoItem retrievedItem = toDoDatabase.retrieveTodo(conn, todoText);
        assertNotNull(retrievedItem);
        assertEquals(todoText, retrievedItem.getText());
        //assertEquals

        toDoDatabase.deleteToDo(conn, todoText, userId);
        toDoDatabase.deleteUser(conn, userName);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testSelectAllToDos() throws Exception {
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        int userId = 5;

        toDoDatabase.insertToDo(conn, firstToDoText, userId);
        toDoDatabase.insertToDo(conn, secondToDoText, userId);

        List<ToDoItem> todos = toDoDatabase.selectTodos(conn);
        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        toDoDatabase.deleteToDo(conn, firstToDoText, userId);
        toDoDatabase.deleteToDo(conn, secondToDoText, userId);
    }

    @Test
    public void testToggleTodo  ()  throws SQLException {
        String todoText = "Unit Test Magic";

        int userId = 5;

        toDoDatabase.insertToDo(conn, todoText, userId);
        toDoDatabase.toggleToDo(conn, todoText, userId);
        ToDoItem retrievedItem = toDoDatabase.retrieveTodo(conn, todoText);
        assertEquals(true, retrievedItem.isDone());
        toDoDatabase.toggleToDo(conn, todoText, userId);
        retrievedItem = toDoDatabase.retrieveTodo(conn, todoText);
        assertEquals(false, retrievedItem.isDone());

        toDoDatabase.deleteToDo(conn, todoText, userId);
    }
    
    @Test
    public void testInsertToDoForUser() throws Exception {
        String todoText = "UnitTest-ToDo";
        String todoText2 = "UnitTest-ToDo2";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = toDoDatabase.insertUser(conn, username, fullName);

        String username2 = "unitester2@tiy.com";
        String fullName2 = "Unit Tester 2";
        int userID2 = toDoDatabase.insertUser(conn, username2, fullName2);

        toDoDatabase.insertToDo(conn, todoText, userID);
        toDoDatabase.insertToDo(conn, todoText2, userID2);

        // make sure each user only has one to-do item
        List<ToDoItem> todosForUser1 = toDoDatabase.selectToDosForUser(conn, userID);
        List<ToDoItem> todosForUser2 = toDoDatabase.selectToDosForUser(conn, userID2);

        assertEquals(1, todosForUser1.size());
        assertEquals(1, todosForUser2.size());

        assertEquals(username, todosForUser1.get(0).getUser().getUserName());

        // make sure each to-do item matches
        ToDoItem todoForUser1 = todosForUser1.get(0);
        assertEquals(todoText, todoForUser1.getText());
        ToDoItem todoForUser2 = todosForUser2.get(0);
        assertEquals(todoText2, todoForUser2.getText());

        toDoDatabase.deleteToDo(conn, todoText, userID);
        toDoDatabase.deleteToDo(conn, todoText2, userID2);
        // make sure we remove the test user we added earlier
        toDoDatabase.deleteUser(conn, username);
        toDoDatabase.deleteUser(conn, username2);

    }

    @Test
    public void testUserRetrieval () throws SQLException {
        //PreparedStatement statement = conn.prepareStatement()
        toDoDatabase.insertUser(conn, "tester@gmail", "Mister Tester");

        String fullName = toDoDatabase.retrieveUser(conn, "tester@gmail").getFullName();

        assertEquals("Mister Tester", fullName);
    }

    @Test
    public void testDeleteWithMultipleUsers () throws SQLException {
        String todoText = "Test-Multi-User-Delete";

        toDoDatabase.insertUser(conn, "User1", "User1");
        toDoDatabase.insertUser(conn, "User2", "User2");

        int userId1 = toDoDatabase.retrieveUser(conn, "User1").getUserId();
        int userId2 = toDoDatabase.retrieveUser(conn, "User2").getUserId();

        toDoDatabase.insertToDo(conn, todoText, userId1);
        toDoDatabase.insertToDo(conn, todoText, userId2);

        toDoDatabase.deleteToDo(conn, todoText, userId1);

        List<ToDoItem> todos = toDoDatabase.selectToDosForUser(conn, userId2);
        assertEquals(1, todos.size());

        toDoDatabase.deleteToDo(conn, todoText, userId2);

        toDoDatabase.deleteUser(conn, "User1");
        toDoDatabase.deleteUser(conn, "User2");
    }

}