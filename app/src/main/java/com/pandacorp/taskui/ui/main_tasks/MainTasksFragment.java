package com.pandacorp.taskui.ui.main_tasks;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.DBHelper;
import com.pandacorp.taskui.R;
import com.pandacorp.taskui.ui.Adapter.CustomAdapter;
import com.pandacorp.taskui.ui.Adapter.ListItem;
import com.pandacorp.taskui.ui.SetTaskActivity;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

public class MainTasksFragment extends Fragment {
    //Variable needed for ActivityOnResult to understand where data came from.
    private final int REQUEST_CODE_SET_TASK = 0;

    private final String TAG = "MyLogs";

    //RecyclerView objects.
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<ListItem> arrayItemList = new ArrayList<>();

    private EditText speed_dial_editText;
    private Button speed_dial_button;

    private FloatingActionButton add_fab;
    private FloatingActionButton clear_fab;

    private View root;

    //SQLite database objects.
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main_tasks, container, false);
        Log.d(TAG, "onSwiped: theme = " + getActivity().getTheme().toString());


        initViews();


        return root;
    }

    private void initViews() {
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null);

        setRecyclerView();

        speed_dial_editText = root.findViewById(R.id.speed_dial_editText);
        speed_dial_button = root.findViewById(R.id.speed_dial_button);
        speed_dial_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(speed_dial_editText.getText());
                if (!text.isEmpty()) {
                    Log.d(TAG, "onClick: not empty");

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_TASK_TEXT, text);

                    database.insert(DBHelper.MAIN_TASKS_TABLE_NAME, null, contentValues);

                    databaseGetTasks();
                    fillArrayItemList();
                    adapter.notifyDataSetChanged();
                    speed_dial_editText.setText("");
                }
                Log.d(TAG, "onClick: empty");

            }
        });

        add_fab = root.findViewById(R.id.add_fab);
        clear_fab = root.findViewById(R.id.clear_fab);

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SET_TASK);
                adapter.notifyDataSetChanged();
            }
        });
        clear_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(getContext());
                database = dbHelper.getWritableDatabase();
                setDeletedTasksValues();
                database.delete(DBHelper.MAIN_TASKS_TABLE_NAME, null, null);
                databaseGetTasks();
                fillArrayItemList();
                adapter.notifyDataSetChanged();
                //Очистка текста.
                speed_dial_editText.setText("");
            }
        });
    }

    private void setDeletedTasksValues() {
        //Setting values of contentValue to set it to DELETED_TASKS_DATABASE when clicking clear_fab
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < itemList.size(); i++) {
            contentValues.put(DBHelper.KEY_TASK_TEXT, itemList.get(i));
            database.insert(DBHelper.DELETED_TASKS_TABLE_NAME, null, contentValues);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "MainTaskFragment.onActivityResult: RESULT_OK");
            //Updating the data for the RecyclerView and the RecyclerView.
            databaseGetTasks();
            fillArrayItemList();
            adapter.notifyDataSetChanged();

        }
    }

    private void setRecyclerView() {
        databaseGetTasks();
        fillArrayItemList();

        adapter = new CustomAdapter(arrayItemList, getActivity());
        recyclerView = root.findViewById(R.id.main_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipe();
        registerForContextMenu(recyclerView);
    }

    private void databaseGetTasks() {
        itemList.clear();
        //Here is recreating DataBase objects for getting new tasks that came from SetTaskActivity
        //from user.
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        cursor = database.query(DBHelper.MAIN_TASKS_TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int keyTaskIndex = cursor.getColumnIndex(DBHelper.KEY_TASK_TEXT);
            do {
                Log.d("MyLogs", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(keyTaskIndex));
                itemList.add(cursor.getString(keyTaskIndex));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");


    }

    private void fillArrayItemList() {
        arrayItemList.clear();
        for (int i = 0; i < itemList.size(); i++) {
            ListItem current = new ListItem(itemList.get(i));
            arrayItemList.add(current);

        }
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ListItem deletedModel = arrayItemList.get(position);
                final int deletedPosition = position;
                adapter.removeItem(position);
                // deleting database item and pushing it to DELETED_DATABASE
                final SQLiteDatabase WritableDatabase = dbHelper.getWritableDatabase();
                WritableDatabase.delete(DBHelper.MAIN_TASKS_TABLE_NAME, DBHelper.KEY_TASK_TEXT + "=?", new String[]{deletedModel.getMainText()});
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_TASK_TEXT, (String) deletedModel.getMainText());
                Log.d(TAG, "setCompletedTasksValues: viewHolder.main_tv.getText() = " + deletedModel.getMainText());
                database.insert(DBHelper.DELETED_TASKS_TABLE_NAME, null, contentValues);
                adapter.notifyItemRemoved(position);
                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), getResources().getText(R.string.snackbar_removed), Snackbar.LENGTH_LONG);
                snackbar.setAnchorView(R.id.speed_dial_linearLayout);
                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                snackbar.setAction(getResources().getText(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // undo is selected, restore the deleted item
                        adapter.restoreItem(deletedModel, deletedPosition);



                    }
                });
                Log.d(TAG, "onSwiped: 6");
                snackbar.show();
                Log.d(TAG, "onSwiped: 7");
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.Red))
                        .addActionIcon(R.drawable.ic_recyclerview_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        database.close();
    }

}