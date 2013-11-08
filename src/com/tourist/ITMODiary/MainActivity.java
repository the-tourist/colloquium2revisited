package com.tourist.ITMODiary;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends Activity {

    private DBAdapter myDBAdapter;
    private Cursor cursor;
    private ListView subjectsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        cursor = myDBAdapter.fetchSubjects();
        startManagingCursor(cursor);
        subjectsView = (ListView) findViewById(R.id.subjects_view);
        registerForContextMenu(subjectsView);
        subjectsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                long subjectID = cursor.getLong(cursor.getColumnIndexOrThrow(DBAdapter.KEY_ID));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.KEY_SUBJECT));
                Intent intent = new Intent(view.getContext(), SubjectActivity.class);
                intent.putExtra(SubjectActivity.EX_ID, "" + subjectID);
                intent.putExtra(SubjectActivity.EX_SUBJECT, subject);
                startActivity(intent);
            }
        });
        showSubjects();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSubjects();
    }

    private void showSubjects() {
        cursor = myDBAdapter.fetchSubjects();
        String[] from = new String[]{DBAdapter.KEY_SUBJECT, DBAdapter.KEY_TOTAL};
        int[] to = new int[]{R.id.subject_row_name, R.id.subject_row_total};
        SimpleCursorAdapter subjects = new SimpleCursorAdapter(this, R.layout.subject_row, cursor, from, to);
        subjectsView.setAdapter(subjects);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.subjects_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            MenuInflater inflater = getMenuInflater();
            cursor.moveToPosition(info.position);
            menu.setHeaderTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.KEY_SUBJECT)));
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        cursor.moveToPosition(info.position);
        long subjectID = cursor.getLong(cursor.getColumnIndexOrThrow(DBAdapter.KEY_ID));
        String subject = cursor.getString(cursor.getColumnIndexOrThrow(DBAdapter.KEY_SUBJECT));
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(this, EditSubjectActivity.class);
                intent.putExtra(EditSubjectActivity.EX_ID, "" + subjectID);
                intent.putExtra(EditSubjectActivity.EX_SUBJECT, subject);
                startActivity(intent);
                return true;
            case R.id.remove:
                myDBAdapter.deleteSubject(subjectID);
                showSubjects();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void addSubject(View view) {
        Intent intent = new Intent(view.getContext(), AddSubjectActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        myDBAdapter.close();
        super.onDestroy();
    }
}
