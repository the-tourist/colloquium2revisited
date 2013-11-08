package com.tourist.ITMODiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditMarkActivity extends Activity {

    private static final String BAD_NUMBER = "Couldn't add a new mark: the mark should be a real number";
    private static final String EMPTY_NAME = "Couldn't add a new mark: the reason should be non-empty";

    private DBAdapter myDBAdapter;

    private EditText reasonEdit;
    private EditText markEdit;

    private String subject;
    private long subjectID;
    private long markID;
    private String reason;
    private String mark;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_mark);
        myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        Intent intent = getIntent();
        subject = intent.getStringExtra(SubjectActivity.EX_SUBJECT);
        subjectID = Long.parseLong(intent.getStringExtra(SubjectActivity.EX_ID));
        markID = Long.parseLong(intent.getStringExtra(SubjectActivity.EX_MARK_ID));
        reason = intent.getStringExtra(SubjectActivity.EX_REASON);
        mark = intent.getStringExtra(SubjectActivity.EX_MARK);
        TextView editMarkText = (TextView) findViewById(R.id.edit_mark_text);
        editMarkText.setText(editMarkText.getText() + " " + subject);
        reasonEdit = (EditText) findViewById(R.id.edit_reason);
        markEdit = (EditText) findViewById(R.id.edit_mark);
        reasonEdit.setText(reason);
        markEdit.setText(mark);
    }

    public void editMark(View view) {
        try {
            String reason = reasonEdit.getText().toString().replaceAll("\\s+", " ").trim();
            if (reason.isEmpty()) {
                throw new Exception();
            }
            String markStr = markEdit.getText().toString();
            double mark = markStr.isEmpty() ? 0 : Double.parseDouble(markStr);
            myDBAdapter.updateMark(markID, subjectID, reason, mark);
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(this, BAD_NUMBER, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, EMPTY_NAME, Toast.LENGTH_SHORT);
            toast.show();
        }
        myDBAdapter.close();
        this.finish();
    }

    public void remove(View view) {
        myDBAdapter.deleteMark(markID);
        myDBAdapter.close();
        this.finish();
    }
}