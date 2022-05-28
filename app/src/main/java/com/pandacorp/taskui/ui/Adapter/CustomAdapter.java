package com.pandacorp.taskui.ui.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.ui.NotificationUtils;

import java.util.List;

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

                try {
                    setCompleteButton(listItem, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void setCompleteButton(final ListItem listItem, int position) {
        cancelNotification(listItem);

        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), view.getResources().getString(R.string.snackbar_completed), Snackbar.LENGTH_LONG);
        snackbar.setAnchorView(R.id.speed_dial_linearLayout);

        snackbar.setAction(view.getResources().getString(R.string.snackbar_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreItem(listItem, DBHelper.COMPLETED_TASKS_TABLE_NAME, DBHelper.MAIN_TASKS_TABLE_NAME);

                //Убирает галочку на чекбоксе, когда элемент возвращается.
                notifyDataSetChanged();


            }
        });
        snackbar.show();

        dbHelper = new DBHelper(view.getContext());
        database = dbHelper.getWritableDatabase();

        removeItem(position, listItem, DBHelper.MAIN_TASKS_TABLE_NAME);

        setCompletedTaskValue(listItem);

    }

    private void cancelNotification(ListItem deletedModel) {
        SharedPreferences sp = activity.getSharedPreferences("notifications_id", Context.MODE_PRIVATE);
        int notification_id = sp.getInt(deletedModel.getMainText(), 0);

        NotificationUtils.cancelNotification(activity, notification_id);
    }

    private void setCompletedTaskValue(ListItem listItem) {
        //Setting values of contentValue to set it to COMPLETED_TASKS_DATABASE when clicking CheckBox.
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        contentValues.put(DBHelper.KEY_TASK_TIME, listItem.getTime());
        contentValues.put(DBHelper.KEY_TASK_PRIORITY, listItem.getPriority());
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


    public void restoreItem(ListItem listItem, String fromTable, String toTable) {

        listItems.add(listItem);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        contentValues.put(DBHelper.KEY_TASK_TIME, listItem.getTime());
        contentValues.put(DBHelper.KEY_TASK_PRIORITY, listItem.getPriority());
        database.insert(toTable, null, contentValues);
        if (fromTable != null) {
            deleteTask(listItem.getMainText(), fromTable);
        }

        // notify item added by position
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView main_tv;
        private TextView time_tv;
        private ImageView priority_image_view;
        private ImageButton complete_button;

        //Needed for drawing red color when swiping.
        public RelativeLayout background;
        public ConstraintLayout foreground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main_tv = itemView.findViewById(R.id.main_tv);
            time_tv = itemView.findViewById(R.id.time_tv);
            priority_image_view = itemView.findViewById(R.id.priority_image_view);

            complete_button = itemView.findViewById(R.id.complete_button);

            foreground = itemView.findViewById(R.id.foreground);
            background = itemView.findViewById(R.id.background);

        }


        public void bindData(final ListItem listItem) {
            main_tv.setText(listItem.getMainText());
            time_tv.setText(listItem.getTime());
            if (listItem.getPriority() == null) {
                return;
                //In case if there is not priority for listItem when you use quick task;
            }
            switch (listItem.getPriority()) {
                case "white":
                    priority_image_view.setBackgroundColor(activity.getResources().getColor(R.color.mdtp_white));
                    break;
                case "yellow":
                    priority_image_view.setBackgroundColor(activity.getResources().getColor(R.color.Yellow));
                    break;
                case "red":
                    priority_image_view.setBackgroundColor(activity.getResources().getColor(R.color.Red));
                    break;
            }


        }


    }
}
