package com.pandacorp.taskui.ui.Adapter;

public class ListItem {
    private String mainText;
    private String time;

    public ListItem(String mainText, String time) {
        this.mainText = mainText;
        this.time = time;
    }

    public String getMainText() {
        return mainText;
    }
    public String getTime() {
        return time;
    }

}
