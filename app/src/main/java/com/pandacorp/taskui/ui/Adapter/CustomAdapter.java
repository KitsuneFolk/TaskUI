package com.pandacorp.taskui.ui.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final String TAG = "MyLogs";

    private Activity activity;
    private View view;

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private ViewHolder viewHolder;
    protected List<ListItem> listItems;

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
    private void delete_cb_init(final ViewHolder holder, final int position){
        final ListItem listItem = listItems.get(position);
        holder.bindData(listItem);
        holder.delete_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                removeItem(position);
                setCompletedTaskValue(listItem);
                Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), view.getResources().getString(R.string.snackbar_completed), Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(R.id.speed_dial_linearLayout);
                snackbar.setAction(view.getResources().getString(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreItem(listItem, position);
                        //TODO: Сделать отмену.
                        database.delete(DBHelper.COMPLETED_TASKS_TABLE_NAME, DBHelper.KEY_TASK_TEXT + "=" + listItem.getMainText(), null);
//                        database.execSQL("DELETE FROM CompletedTasks WHERE id = (SELECT MAX(id) FROM CompletedTasks)");
                        //Убирает галочку на чекбоксе, когда элемент возвращается.
                        holder.delete_cb.setChecked(false);


                    }
                });
                snackbar.show();
            }
        });

    }
    private void setCompletedTaskValue(ListItem listItem){
        //Setting values of contentValue to set it to COMPLETED_TASKS_DATABASE when clicking CheckBox.
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        Log.d(TAG, "setCompletedTasksValues: listItems.get(position).getMainText() = " + listItem.getMainText());
        database.insert(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, contentValues);
        database.delete(DBHelper.MAIN_TASKS_TABLE_NAME, DBHelper.KEY_TASK_TEXT + "=?", new String[]{listItem.getMainText()});

    }



    @Override
    public int getItemCount() {
         return listItems.size();
    }

    public void removeItem(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listItems.size());
    }

    public void restoreItem (ListItem listItem, int position) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TASK_TEXT, listItem.getMainText());
        database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);

        // notify item added by position
        notifyItemRangeChanged(position, listItems.size());
        notifyItemInserted(position);

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView main_tv;
        private CheckBox delete_cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main_tv = itemView.findViewById(R.id.main_tv);
            delete_cb = itemView.findViewById(R.id.delete_cb);

        }

        int i = 0;

        public void bindData(final ListItem listItem) {
            main_tv.setText(listItem.getMainText());
            i++;
        }

    }
}
