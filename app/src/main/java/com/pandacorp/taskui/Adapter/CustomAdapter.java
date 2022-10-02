package com.pandacorp.taskui.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.pandacorp.taskui.Notifications.NotificationUtils;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.Widget.WidgetProvider;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final String TAG = "MyLogs";

    public final static String MAIN_FRAGMENT = "MAIN_FRAGMENT";
    public final static String COMPLETED_FRAGMENT = "COMPLETED_FRAGMENT";
    public final static String DELETED_FRAGMENT = "DELETED_FRAGMENT";

    private View view;

    private ViewHolder viewHolder;

    private String fragmentName;
    private Activity activity;
    private List<ListItem> listItems;

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase wdb;


    public CustomAdapter(String fragmentName, List<ListItem> listItems, Activity activity) {
        this.fragmentName = fragmentName;
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
        wdb = dbHelper.getWritableDatabase();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        setCompleteButton(holder, position);


    }

    private void setCompleteButton(final ViewHolder holder, final int position) {
        final ListItem listItem = listItems.get(position);
        holder.bindData(listItem);
        holder.complete_button.setOnClickListener(v -> {
            switch (fragmentName){
                case MAIN_FRAGMENT: {
                    removeItem(position);
                    dbHelper.removeById(DBHelper.MAIN_TASKS_TABLE_NAME, position);

                    dbHelper.add(DBHelper.COMPLETED_TASKS_TABLE_NAME, listItem);

                    cancelNotification(listItem);
                    WidgetProvider.Companion.sendRefreshBroadcast(view.getContext());

                    setUndoSnackbar(listItem, position);

                }
                case COMPLETED_FRAGMENT: {
                    break;
                }
                case DELETED_FRAGMENT: {
                    break;
                }
            }


        });

    }

    private void setUndoSnackbar(final ListItem listItem, int position) {

        Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), view.getResources().getString(R.string.snackbar_completed), Snackbar.LENGTH_LONG);
        snackbar.setAnchorView(R.id.speed_dial_linearLayout);

        snackbar.setAction(view.getResources().getString(R.string.snackbar_undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreItem(listItem, DBHelper.MAIN_TASKS_TABLE_NAME);

                //Убирает галочку на чекбоксе, когда элемент возвращается.
                notifyDataSetChanged();


            }
        });
        snackbar.show();


    }

    private void cancelNotification(ListItem deletedModel) {
        SharedPreferences sp = activity.getSharedPreferences("notifications_id", Context.MODE_PRIVATE);
        int notification_id = sp.getInt(deletedModel.getMainText(), 0);

        NotificationUtils.cancelNotification(activity, notification_id);
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


    public void restoreItem(ListItem listItem, String table) {

        listItems.add(listItem);

        dbHelper.add(table, listItem);

        // notify item added by position.
        notifyDataSetChanged();
        // update widget info after restoring the item.
        WidgetProvider.Companion.sendRefreshBroadcast(view.getContext());

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
