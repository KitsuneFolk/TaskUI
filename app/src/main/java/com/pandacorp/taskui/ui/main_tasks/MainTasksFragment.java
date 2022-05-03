package com.pandacorp.taskui.ui.main_tasks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.CustomAdapter;
import com.pandacorp.taskui.ListItem;
import com.pandacorp.taskui.R;

import java.util.ArrayList;
import java.util.List;

public class MainTasksFragment extends Fragment {

    private final String TAG = "MyLogs";
    private final String ALERT_DIALOG_TITLE = "Задача";
    private final String ALERT_DIALOG_MESSAGE = "Введите название";
    private final String ALERT_DIALOG_POSITIVE = "Принять";
    private final String ALERT_DIALOG_NEGATIVE = "Назад";

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<String> itemList;
    private ArrayList<ListItem> arrayItemList;

    private FloatingActionButton add_fab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_tasks, container, false);

        itemList = new ArrayList();
        arrayItemList = new ArrayList();

        itemList.add("Temp1");
        itemList.add("Temp2");
        itemList.add("Temp3");
        itemList.add("Temp4");
        itemList.add("Temp5");

        for (int i = 0; i < itemList.size(); i++) {
            ListItem current = new ListItem(itemList.get(i));
            arrayItemList.add(current);

        }
        adapter = new CustomAdapter(arrayItemList, getActivity());

        recyclerView = root.findViewById(R.id.main_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipe();

        add_fab = root.findViewById(R.id.add_fab);

        registerForContextMenu(recyclerView);

        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        return root;
    }
    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(ALERT_DIALOG_TITLE);
        alertDialog.setMessage(ALERT_DIALOG_MESSAGE);
        final EditText alertDialog_et = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                100,
                20
//                LinearLayout.LayoutParams.MATCH_PARENT

        );
        alertDialog_et.setLayoutParams(lp);
        alertDialog.setView(alertDialog_et);
        alertDialog.setPositiveButton(ALERT_DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
            String text;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text = String.valueOf(alertDialog_et.getText());
                addNewItem(text);

            }
        });
        alertDialog.setNegativeButton(ALERT_DIALOG_NEGATIVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    final ListItem deletedModel = arrayItemList.get(position);
                    final int deletedPosition = position;
                    adapter.removeItem(position);
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), " removed from Recyclerview!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // undo is selected, restore the deleted item
                            adapter.restoreItem(deletedModel, deletedPosition);
                        }
                    });
                    snackbar.show();
                } else {
                    final ListItem deletedListItem = arrayItemList.get(position);
                    final int deletedPosition = position;
                    adapter.removeItem(position);
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), " Task is removed!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // undo is selected, restore the deleted item
                            adapter.restoreItem(deletedListItem, deletedPosition);
                        }
                    });
                    snackbar.show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void addNewItem(String item){
        itemList.add(item);
        arrayItemList.add(new ListItem(itemList.get(itemList.size()-1)));

        adapter.notifyDataSetChanged();
    }
}