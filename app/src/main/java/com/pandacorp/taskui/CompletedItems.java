package com.pandacorp.taskui;

import com.pandacorp.taskui.ui.Adapter.ListItem;

import java.util.ArrayList;

public class CompletedItems {
    private static ArrayList<ListItem> listItems = new ArrayList<>();

    public static void addCompletedItem(ListItem completedItem){
        listItems.add(completedItem);
    }
    public static void removeCompletedItem(ListItem completedItem){
        listItems.remove(completedItem);
    }
    public static int getSize(){
        return listItems.size();
    }
    public static ArrayList<ListItem> getListItems(){
        return listItems;
    }
}
