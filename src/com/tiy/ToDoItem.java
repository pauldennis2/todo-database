package com.tiy;

public class ToDoItem {

    private int id;
    private String text;
    private boolean isDone;

    public ToDoItem(int id, String text, boolean isDone) {
        this.id = id;
        this.text = text;
        this.isDone = isDone;
    }

    public ToDoItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (isDone) {
            return text + " (done)";
        } else {
            return text;
        }
        // A one-line version of the logic above:
        // return text + (isDone ? " (done)" : "");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
