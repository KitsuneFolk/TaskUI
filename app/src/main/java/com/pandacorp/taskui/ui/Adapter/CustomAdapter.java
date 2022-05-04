package com.pandacorp.taskui.ui.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.pandacorp.taskui.CompletedItems;
import com.pandacorp.taskui.R;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final String TAG = "MyLogs";

    private Activity activity;

    ViewHolder viewHolder;
    private List<ListItem> listItems;

    public CustomAdapter(List<ListItem> listItems, Activity activity) {
        this.listItems = listItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder: listItems size = " + listItems.size());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        delete_cb_init(holder, position);

    }
    private void delete_cb_init(final ViewHolder holder, final int position){
        Log.d(TAG, "CustomAdapter.onBindViewHolder.onClick: position = " + position);
        final ListItem listItem = listItems.get(position);
        Log.d(TAG, "onBindViewHolder: " + listItem.toString());
        holder.bindData(listItem);
        holder.delete_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
                Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), " Task is completed!", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreItem(listItem, position);
                        holder.delete_cb.setChecked(false);
                        //Убирает галочку на чекбоксе, когда элемент возвращается.
                        CompletedItems.removeCompletedItem(listItems.get(position));

                    }
                });
                snackbar.show();
            }
        });

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

    public void restoreItem(ListItem ListItem, int position) {
        listItems.add(position, ListItem);
        // notify item added by position
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