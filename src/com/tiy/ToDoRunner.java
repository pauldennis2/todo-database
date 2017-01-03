package com.tiy;

import org.h2.tools.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by erronius on 12/19/2016.
 */
public class ToDoRunner {

    Scanner scanner;

    ToDoDatabase toDoDatabase;
    Connection conn;

    public static final String DB_PATH = "jdbc:h2:./main";



    public static void main(String[] args) {
        try {
            new ToDoRunner().startInterface();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("Done main");
    }

    public ToDoRunner () throws SQLException {
        //Server.createWebServer().start();
        conn = DriverManager.getConnection(DB_PATH);
        scanner = new Scanner(System.in);
        toDoDatabase = new ToDoDatabase();
    }

    public void startInterface() throws SQLException {
        System.out.println("Welcome to T0-D0.");
        toDoDatabase.init();
        mainMenu();
        toDoDatabase.closeServer();
    }

    public void mainMenu () throws SQLException {
        List<ToDoItem> todos = toDoDatabase.selectToDos(conn);
        int index = 0;
        System.out.println("List of to-dos");
        for (ToDoItem todoItem : todos) {
            index++;
            System.out.println(index + ". " + todoItem);
        }

        System.out.println("Options:");
        System.out.println("1. Add a todo");
        System.out.println("2. Remove or change a todo");
        System.out.println("3. Exit");

        int userChoice = Integer.parseInt(scanner.nextLine());
        switch (userChoice) {
            case 1:
                System.out.println("Todo item to add?");
                toDoDatabase.insertToDo(conn, scanner.nextLine());
                mainMenu();
                break;
            case 2:
                System.out.println("Index of item to remove/change?");
                int userIndex = Integer.parseInt(scanner.nextLine());
                userIndex--;
                System.out.println("Remove, or change status? (r/c)");
                String actionChoice = scanner.nextLine().toLowerCase();
                if (actionChoice.contains("r")) {
                    toDoDatabase.deleteToDo(conn, todos.get(userIndex).getText());
                } else if (actionChoice.contains("c")) {
                    toDoDatabase.toggleToDo(conn, todos.get(userIndex).getText());
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
