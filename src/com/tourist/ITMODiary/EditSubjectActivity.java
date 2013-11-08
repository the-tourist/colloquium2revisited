package com.tourist.ITMODiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditSubjectActivity extends Activity {

    private static final String EMPTY_NAME = "Subject name should be non-empty";

    public static final String EX_SUBJECT = "name";
    public static final String EX_ID = "id";

    private DBAdapter myDBAdapter;

    private EditText subjectName;
    private long subjectID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_subject);
        myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        Intent intent = getIntent();
        subjectName = (EditText) findViewById(R.id.edit_name);
        subjectName.setText(intent.getStringExtra(EX_SUBJECT));
        subjectID = Long.parseLong(intent.getStringExtra(EX_ID));
    }

    public void renameSubject(View view) {
        String newName = subjectName.getText().toString().replaceAll("\\s+", " ").trim();
        if (!newName.isEmpty()) {
            myDBAdapter.updateSubject(subjectID, newName);
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