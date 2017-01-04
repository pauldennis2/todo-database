package com.tiy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by erronius on 12/19/2016.
 */
public class ToDoRunner {

    Scanner scanner;

    ToDoDatabase toDoDatabase;
    Connection conn;

    String activeUserName;

    public static final String DB_PATH = "jdbc:h2:./main";



    public static void main(String[] args) {
        try {
            new ToDoRunner().startInterface();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ToDoRunner () throws SQLException {
        //Server.createWebServer().start();
        conn = DriverManager.getConnection(DB_PATH);
        scanner = new Scanner(System.in);
        toDoDatabase = new ToDoDatabase();
    }

    public void startInterface() throws SQLException {
        System.out.println("Welcome to T0-D0.");
        System.out.println("Please log in. Enter username:");
        activeUserName = scanner.nextLine();
        toDoDatabase.init();
        mainMenu();
        toDoDatabase.closeServer();
    }

    public void mainMenu () throws SQLException {
        //List<ToDoItem> todos = toDoDatabase.selectTodos(conn);
        int userId;
        try {
            userId = toDoDatabase.retrieveUser(conn, activeUserName).getUserId();
            System.out.println("Welcome back " + activeUserName);
        } catch (SQLException ex) {
            System.out.println("New user detected. Please full name:");
            String fullName = scanner.nextLine();
            userId = toDoDatabase.insertUser(conn, activeUserName, fullName);
        }
        List<ToDoItem> userTodos = toDoDatabase.selectToDosForUser(conn, userId);
        int printIndex = 1;
        System.out.println("List of to-dos");
        for (ToDoItem todoItem : userTodos) {
            System.out.println(printIndex + ". " + todoItem);
            printIndex++;
        }

        System.out.println("Options:");
        System.out.println("1. Add a todo");
        System.out.println("2. Remove or change a todo");
        System.out.println("3. Exit");

        int userChoice = Integer.parseInt(scanner.nextLine());
        switch (userChoice) {
            case 1:
                System.out.println("Todo item to add?");
                toDoDatabase.insertToDo(conn, scanner.nextLine(), userId);
                mainMenu();
                break;
            case 2:
                System.out.println("Index of item to remove/change?");
                int userIndex = Integer.parseInt(scanner.nextLine());
                userIndex--;
                System.out.println("Remove, or change status? (r/c)");
                String actionChoice = scanner.nextLine().toLowerCase();
                if (actionChoice.contains("r")) {
                    toDoDatabase.deleteToDo(conn, userTodos.get(userIndex).getText(), userId);
                } else if (actionChoice.contains("c")) {
                    toDoDatabase.toggleToDo(conn, userTodos.get(userIndex).getText(), userId);
                } else {
                    System.out.println("You dun messed up good, kid.");
                }
                mainMenu();
                break;
            case 3:
                break;
        }
    }
}
