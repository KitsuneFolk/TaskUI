package com.pandacorp.taskui.ui.main_tasks;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.Adapter.CustomAdapter;
import com.pandacorp.taskui.Adapter.ListItem;
import com.pandacorp.taskui.Adapter.RecyclerItemTouchHelper;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.Notifications.NotificationUtils;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.SetTaskActivity;
import com.pandacorp.taskui.Widget.WidgetProvider;

import java.util.ArrayList;

public class MainTasksFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    //Variable needed for ActivityOnResult to understand where data came from.
    private final int REQUEST_CODE_SET_TASK = 0;

    private final String TAG = "MyLogs";

    //RecyclerView objects.
    private RecyclerView recyclerView;
    public CustomAdapter adapter;
    private ArrayList<ListItem> arrayItemList = new ArrayList<>();

    private EditText speed_dial_editText;
    private Button speed_dial_button;

    private FloatingActionButton add_fab;
    private FloatingActionButton delete_fab;
    private FloatingActionButton delete_forever_fab;

    private View root;

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main_tasks, container, false);

        new Handler().post(this::initViews);

        return root;
    }

    private void initViews() {
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null);

        databaseGetTasks();
        setRecyclerView();

        speed_dial_editText = root.findViewById(R.id.speed_dial_editText);
        speed_dial_button = root.findViewById(R.id.speed_dial_button);

        add_fab = root.findViewById(R.id.add_fab);
        delete_fab = root.findViewById(R.id.delete_fab);
        delete_forever_fab = root.findViewById(R.id.delete_forever_fab);

        speed_dial_button.setOnClickListener(v -> {
            String text = String.valueOf(speed_dial_editText.getText());
            if (!text.isEmpty()) {

                ListItem listItem = new ListItem(text, null, null);

                dbHelper.add(DBHelper.MAIN_TASKS_TABLE_NAME, listItem);
                adapter.notifyDataSetChanged();
                speed_dial_editText.setText("");

                databaseGetTasks();
                updateWidget();

            }
        });
        add_fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SetTaskActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SET_TASK);
            adapter.notifyDataSetChanged();
        });
        delete_fab.setOnClickListener(v -> {
            dbHelper = new DBHelper(getContext());
            database = dbHelper.getWritableDatabase();
            setDeletedTasksValues();
            database.delete(DBHelper.MAIN_TASKS_TABLE_NAME, null, null);

            //Without this expression tasks won't be updated.
            databaseGetTasks();

            adapter.notifyDataSetChanged();
            //Очистка текста.
            speed_dial_editText.setText("");
            updateWidget();
        });
        delete_forever_fab.setOnClickListener(v -> {
            dbHelper = new DBHelper(getContext());
            database = dbHelper.getWritableDatabase();
            database.delete(DBHelper.MAIN_TASKS_TABLE_NAME, null, null);

            //Without this expression tasks won't be updated.
            databaseGetTasks();

            adapter.notifyDataSetChanged();
            //Очистка текста.
            speed_dial_editText.setText("");
            updateWidget();
        });

    }

    private void updateWidget() {
        WidgetProvider.Companion.sendRefreshBroadcast(requireContext());

    }

    private void setDeletedTasksValues() {
        //Setting values of contentValue to set it to DELETED_TASKS_DATABASE when clicking delete_fab
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < arrayItemList.size(); i++) {
            contentValues.put(DBHelper.KEY_TASK_TEXT, arrayItemList.get(i).getMainText());
            database.insert(DBHelper.DELETED_TASKS_TABLE_NAME, null, contentValues);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Updating the data for RecyclerView and the RecyclerView.
            databaseGetTasks();
            adapter.notifyDataSetChanged();

        }
    }

    private void setRecyclerView() {

        adapter = new CustomAdapter(arrayItemList, getActivity());

        recyclerView = root.findViewById(R.id.main_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        enableSwipe();

        registerForContextMenu(recyclerView);

    }

    private void databaseGetTasks() {
        //Here is recreating DataBase objects for getting new tasks that came from SetTaskActivity
        //from user.
        arrayItemList.clear();
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int keyTaskTextIndex = cursor.getColumnIndex(DBHelper.KEY_TASK_TEXT);
            int keyTaskTimeIndex = cursor.getColumnIndex(DBHelper.KEY_TASK_TIME);
            int keyTaskPriorityIndex = cursor.getColumnIndex(DBHelper.KEY_TASK_PRIORITY);
            do {
                Log.d("MyLogs", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(keyTaskTextIndex) +
                        ", time = " + cursor.getString(keyTaskTimeIndex) +
                        ", priority = " + cursor.getString(keyTaskPriorityIndex));
                arrayItemList.add(new ListItem(cursor.getString(keyTaskTextIndex), cursor.getString(keyTaskTimeIndex), cursor.getString(keyTaskPriorityIndex)));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");


    }

    private void enableSwipe() {
        //Attached the ItemTouchHelper
        DividerItemDecoration recyclerViewDivider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(recyclerViewDivider);

        //Attached the ItemTouchHelper
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CustomAdapter.ViewHolder) {
            Log.d(TAG, "onSwiped: position = " + position);
            final ListItem deletedListItem = arrayItemList.get(position);
            adapter.removeItem(position);
            dbHelper.removeById(DBHelper.MAIN_TASKS_TABLE_NAME, position);

            dbHelper.add(DBHelper.DELETED_TASKS_TABLE_NAME, deletedListItem);
            cancelNotification(deletedListItem);
            updateWidget();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), getResources().getText(R.string.snackbar_removed), Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(R.id.fabs_constraintLayout);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setAction(getResources().getText(R.string.snackbar_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedListItem, DBHelper.MAIN_TASKS_TABLE_NAME);

                }
            });
            snackbar.show();
        }


    }

    private void cancelNotification(ListItem deletedModel) {
        SharedPreferences sp = getContext().getSharedPreferences("notifications_id", Context.MODE_PRIVATE);
        int notification_id = sp.getInt(deletedModel.getMainText(), 0);

        NotificationUtils.cancelNotification(getContext(), notification_id);
    }


}