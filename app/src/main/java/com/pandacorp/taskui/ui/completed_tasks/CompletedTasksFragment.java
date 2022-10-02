package com.pandacorp.taskui.ui.completed_tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.Widget.WidgetProvider;

import java.util.ArrayList;

public class CompletedTasksFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private final String TAG = "MyLogs";
    private final String table = DBHelper.COMPLETED_TASKS_TABLE_NAME;

    private RecyclerView recyclerView;
    public CustomAdapter adapter;
    private ArrayList<ListItem> tasksList = new ArrayList<>();

    private FloatingActionButton delete_fab_completed;
    private FloatingActionButton delete_forever_fab_completed;

    private View root;

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        new Thread(this::initViews).start();

        return root;
    }

    private void initViews() {

        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, null, null, null, null, null);

        delete_fab_completed = root.findViewById(R.id.delete_fab_completed);
        delete_forever_fab_completed = root.findViewById(R.id.delete_forever_fab_completed);
        delete_fab_completed.post(() -> delete_fab_completed.setOnClickListener(v -> {
            dbHelper = new DBHelper(getContext());
            database = dbHelper.getWritableDatabase();
            setDeletedTasksValues();
            database.delete(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, null);
            databaseGetTasks();
            adapter.notifyDataSetChanged();
        }));
        delete_forever_fab_completed.post(() -> delete_forever_fab_completed.setOnClickListener(v -> {
            dbHelper = new DBHelper(getContext());
            database = dbHelper.getWritableDatabase();
            database.delete(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, null);
            databaseGetTasks();
            adapter.notifyDataSetChanged();
        }));

        setRecyclerView();

    }


    private void setDeletedTasksValues() {
        for (int i = 0; i < tasksList.size(); i++) {
            dbHelper.add(DBHelper.DELETED_TASKS_TABLE_NAME, tasksList.get(i));
        }


    }

    private void setRecyclerView() {
        databaseGetTasks();

        adapter = new CustomAdapter(CustomAdapter.COMPLETED_FRAGMENT, tasksList, getActivity());
        recyclerView = root.findViewById(R.id.completed_rv);
        recyclerView.post(() -> {
            recyclerView.setHasFixedSize(false);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        });


        enableSwipe();
        recyclerView.post(() -> registerForContextMenu(recyclerView));

    }

    private void databaseGetTasks() {
        tasksList.clear();
        //Here is recreating DataBase objects for getting new tasks that came from SetTaskActivity
        //from user.
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.COMPLETED_TASKS_TABLE_NAME, null, null, null, null, null, null);

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
                tasksList.add(new ListItem(
                        cursor.getString(keyTaskTextIndex),
                        cursor.getString(keyTaskTimeIndex),
                        cursor.getString(keyTaskPriorityIndex)
                ));

            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");


    }


    private void enableSwipe() {
        //Attached the ItemTouchHelper
        recyclerView.post(() ->
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL)));

        //Attached the ItemTouchHelper
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        recyclerView.post(() -> new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        Log.d(TAG, "onSwiped: onSwiped");
        if (viewHolder instanceof CustomAdapter.ViewHolder) {
            final ListItem deletedListItem = tasksList.get(position);
            adapter.removeItem(position);
            dbHelper.removeById(DBHelper.COMPLETED_TASKS_TABLE_NAME, position);
            dbHelper.add(DBHelper.DELETED_TASKS_TABLE_NAME, deletedListItem);

            WidgetProvider.Companion.sendRefreshBroadcast(getContext());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), getResources().getText(R.string.snackbar_removed), Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(R.id.fabs_constraintLayout_completed);
            snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
            snackbar.setAction(getResources().getText(R.string.snackbar_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedListItem, DBHelper.COMPLETED_TASKS_TABLE_NAME);

                }
            });
            snackbar.show();
        }


    }
}