package com.tourist.ITMODiary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddSubjectActivity extends Activity {

    private static final String EMPTY_NAME = "Subject name should be non-empty";

    private DBAdapter myDBAdapter;

    private EditText subjectName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject);
        myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        subjectName = (EditText) findViewById(R.id.add_name);
    }

    public void addSubject(View view) {
        String name = subjectName.getText().toString().replaceAll("\\s+", " ").trim();
        if (!name.isEmpty()) {
            myDBAdapter.addSubject(name);
        } else {
            Toast toast = Toast.makeText(this, EMPTY_NAME, Toast.LENGTH_SHORT);
            toast.show();
        }
        myDBAdapter.close();
        this.finish();
    }

    public void cancel(View view) {
        myDBAdapter.close();
        this.finish();
    }
}
