package com.tourist.ITMODiary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class SubjectActivity extends Activity {

    public static final String EX_SUBJECT = "name";
    public static final String EX_ID = "id";
    public static final String EX_MARK_ID = "markID";
    public static final String EX_REASON = "reason";
    public static final String EX_MARK = "mark";

    private DBAdapter myDBAdapter;
    private Cursor cursor;
    private ListView marksView;

    private TextView subjectName;
    private TextView subjectTotal;

    private String subject;
    private long subjectID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject);
        Intent intent = getIntent();
        subject = intent.getStringExtra(EX_SUBJECT);
        subjectID = Long.parseLong(intent.getStringExtra(EX_ID));
        myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        subjectName = (TextView) findViewById(R.id.subject_name);
        subjectName.setText(subject);
        subjectTotal = (TextView) findViewById(R.id.subject_total);
        subjectTotal.setText(getTotalMark(subjectID));
        cursor = myDBAdapter.fetchSubjects();
        startManagingCursor(cursor);
        marksView = (ListView) findViewById(R.id.marks_view);
        marksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                long markID = cursor.getLong(cursor.getColumnIndexOrThrow(DBAdapter.KEY_ID));
                String reason = cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.KEY_REASON));
                String mark = cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.KEY_MARK));
                Intent intent = new Intent(view.getContext(), EditMarkActivity.class);
                intent.putExtra(SubjectActivity.EX_ID, "" + subjectID);
                intent.putExtra(SubjectActivity.EX_SUBJECT, subject);
                intent.putExtra(SubjectActivity.EX_MARK_ID, "" + markID);
                intent.putExtra(SubjectActivity.EX_REASON, reason);
                intent.putExtra(SubjectActivity.EX_MARK, mark);
                startActivity(intent);
            }
        });
        showMarks();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMarks();
    }

    public String getTotalMark(long subjectID) {
        Cursor cur = myDBAdapter.fetchTotalMark(subjectID);
        cur.moveToFirst();
        return cur.getString(0);
    }

    private void showMarks() {
        subjectTotal.setText(getTotalMark(subjectID));
        cursor = myDBAdapter.fetchMarks(subjectID);
        String[] from = new String[]{DBAdapter.KEY_REASON, DBAdapter.KEY_MARK};
        int[] to = new int[]{R.id.mark_row_reason, R.id.mark_row_mark};
        SimpleCursorAdapter marks = new SimpleCursorAdapter(this, R.layout.mark_row, cursor, from, to);
        marksView.setAdapter(marks);
    }

    public void addMark(View view) {
        Intent intent = new Intent(view.getContext(), AddMarkActivity.class);
        intent.putExtra(EX_ID, "" + subjectID);
        intent.putExtra(EX_SUBJECT, subject);
        startActivity(intent);
    }
}