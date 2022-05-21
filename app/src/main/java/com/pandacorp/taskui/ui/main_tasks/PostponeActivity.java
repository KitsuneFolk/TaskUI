package com.pandacorp.taskui.ui.main_tasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.os.Bundle;

import com.pandacorp.taskui.R;

public class PostponeActivity extends AppCompatActivity implements SearchManager.OnCancelListener, SearchManager.OnDismissListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpone);
        AlertDialog LDialog = new AlertDialog.Builder(this)
                .setTitle("Title")
                .setMessage("Message")
                .create();
    }


    @Override
    public void onCancel() {
        if (!this.isFinishing()){
            finish();
        }
    }

    @Override
    public void onDismiss() {
        if (!this.isFinishing()){
            finish();
        }
    }
}
