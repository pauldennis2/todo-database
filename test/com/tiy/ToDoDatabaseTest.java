package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by erronius on 1/3/2017.
 */
public class ToDoDatabaseTest {

    static ToDoDatabase toDoDatabase;

    @Before
    public void setUp() throws Exception {
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
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);
    }

    @Test
    public void testInsertTodo () throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";
        toDoDatabase.insertToDo(conn, todoText);
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

        toDoDatabase.deleteToDo(conn, todoText);

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
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        toDoDatabase.insertToDo(conn, firstToDoText);
        toDoDatabase.insertToDo(conn, secondToDoText);

        ArrayList<ToDoItem> todos = toDoDatabase.selectToDos(conn);
        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        toDoDatabase.deleteToDo(conn, firstToDoText);
        toDoDatabase.deleteToDo(conn, secondToDoText);
    }

    @Test
    public void testToggleTodo  ()  throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "Unit Test Magic";

        toDoDatabase.insertToDo(conn, todoText);
        toDoDatabase.toggleToDo(conn, todoText);
        ToDoItem retrievedItem = toDoDatabase.retrieveTodo(conn, todoText);
        assertEquals(true, retrievedItem.isDone());
        toDoDatabase.toggleToDo(conn, todoText);
        retrievedItem = toDoDatabase.retrieveTodo(conn, todoText);
        assertEquals(false, retrievedItem.isDone());

        toDoDatabase.deleteToDo(conn, todoText);
    }
}