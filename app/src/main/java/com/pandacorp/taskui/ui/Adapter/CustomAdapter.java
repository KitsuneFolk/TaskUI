package com.pandacorp.taskui.ui.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final String TAG = "MyLogs";

    private View view;

    private ViewHolder viewHolder;

    private Activity activity;
    private List<ListItem> listItems;

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public CustomAdapter(List<ListItem> listItems, Activity activity) {
        this.listItems = listItems;
        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder(view);
        dbHelper = new DBHelper(view.getContext());
        database = dbHelper.getWritableDatabase();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        delete_cb_init(holder, position);


    }

    private void delete_cb_init(final ViewHolder holder, final int position) {
        final ListItem listItem = listItems.get(position);
        holder.bindData(listItem);
        holder.complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                removeItem(position);
                dbHelper = new DBHelper(view.getContext());
                database = dbHelper.getWritableDatabase();

                removeItem(position, listItem, DBHelper.MAIN_TASKS_TABLE_NAME);

                setCompletedTaskValue(listItem);

                Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), view.getResources().getString(R.string.snackbar_completed), Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(R.id.speed_dial_linearLayout);
                snackbar.setAction(view.getResources().getString(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        deleteTask(listItem.getMainText(), DBHelper.COMPLETED_TASKS_TABLE_NAME);
                        restoreItem(listItem, position);

                        //Убирает галочку на чекбоксе, когда элемент возвращается.
                        notifyDataSetChanged();


                    }
                });
                snackbar.show();
            }
        });

    }

    private void setCompletedTaskValue(ListItem listItem) {
        //Setting values of contentValue to set it to COMPLETED_TASKS_DATABASE when clicking CheckBox.
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        Log.d(TAG, "setCompletedTasksValues: listItems.get(position).getMainText() = " + listItem.getMainText());
        database.insert(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, contentValues);

    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void removeItem(int position, ListItem listItem, String Table) {
        listItems.remove(position);
        notifyItemRemoved(position);


        deleteTask(listItem.getMainText(), Table);


    }
    public void deleteTask(String name, String DataBase) {
        database.execSQL("DELETE FROM " + DataBase + " WHERE " + DBHelper.KEY_TASK_TEXT + "= '" + name + "'");

    }


    public void restoreItem(ListItem listItem, int position) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        listItems.add(listItem);
        database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);
        deleteTask(listItem.getMainText(), DBHelper.COMPLETED_TASKS_TABLE_NAME);

        // notify item added by position
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView main_tv;
        private TextView time_tv;
        private ImageButton complete_button;

        //Needed for drawing red color when swiping.
        public RelativeLayout background;
        public ConstraintLayout foreground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main_tv = itemView.findViewById(R.id.main_tv);
            time_tv = itemView.findViewById(R.id.main_tv);
            complete_button = itemView.findViewById(R.id.complete_button);

            foreground = itemView.findViewById(R.id.foreground);
            background = itemView.findViewById(R.id.background);

        }

        int i = 0;

        public void bindData(final ListItem listItem) {
            main_tv.setText(listItem.getMainText());
            i++;
        }


    }
}
