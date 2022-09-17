package com.pandacorp.taskui.Adapter;

public class ListItem {
    private String mainText;
    private String time;
    private String priority;

    public ListItem(String mainText, String time, String priority) {
        this.mainText = mainText;
        this.time = time;
        this.priority = priority;
    }

    public String getMainText() {
        return mainText;
    }
    public String getTime() {
        return time;
    }
    public String getPriority() {
        return priority;
    }

    public static String white = "white";
    public static String yellow = "yellow";
    public static String red = "red";

}
