package com.tourist.ITMODiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "ITMODiary";
    private static final int DATABASE_VERSION = 5;
    private static final String SUBJECTS_TABLE = "subjects";
    private static final String MARKS_TABLE = "marks";

    public static final String KEY_ID = "_id";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_SUBJECT_ID = "subjectID";
    public static final String KEY_REASON = "reason";
    public static final String KEY_MARK = "mark";
    public static final String KEY_TOTAL = "total";

    private static final String INIT_SUBJECTS =
            "create table if not exists " + SUBJECTS_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_SUBJECT + " text not null)";

    private static final String INIT_MARKS =
            "create table if not exists " + MARKS_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_SUBJECT_ID + " integer not null, "
                    + KEY_REASON + " text not null, "
                    + KEY_MARK + " real not null)";

    private static final String REMOVE_SUBJECTS =
            "drop table if exists " + SUBJECTS_TABLE;

    private static final String REMOVE_MARKS =
            "drop table if exists " + MARKS_TABLE;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INIT_SUBJECTS);
            db.execSQL(INIT_MARKS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(REMOVE_SUBJECTS);
            db.execSQL(REMOVE_MARKS);
            onCreate(db);
        }
    }

    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDb.close();
        mDbHelper.close();
    }

    public long addSubject(String subject) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SUBJECT, subject);
        return mDb.insert(SUBJECTS_TABLE, null, initialValues);
    }

    public boolean updateSubject(long rowID, String subject) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SUBJECT, subject);
        return mDb.update(SUBJECTS_TABLE, newValues, KEY_ID + "=" + rowID, null) > 0;
    }

    public boolean deleteSubject(long rowID) {
        return mDb.delete(SUBJECTS_TABLE, KEY_ID + "=" + rowID, null) > 0;
    }

    public long addMark(long subjectID, String reason, double mark) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SUBJECT_ID, subjectID);
        initialValues.put(KEY_REASON, reason);
        initialValues.put(KEY_MARK, mark);
        return mDb.insert(MARKS_TABLE, null, initialValues);
    }

    public boolean updateMark(long rowID, long subjectID, String reason, double mark) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SUBJECT_ID, subjectID);
        newValues.put(KEY_REASON, reason);
        newValues.put(KEY_MARK, mark);
        return mDb.update(MARKS_TABLE, newValues, KEY_ID + "=" + rowID, null) > 0;
    }

    public boolean deleteMark(long rowID) {
        return mDb.delete(MARKS_TABLE, KEY_ID + "=" + rowID, null) > 0;
    }

    public Cursor fetchSubjects() {
        return mDb.query(SUBJECTS_TABLE, new String[] {KEY_ID, KEY_SUBJECT,
                "ifNull((select sum(" + KEY_MARK + ") from " + MARKS_TABLE +
                        " where " + KEY_SUBJECT_ID + "=" + SUBJECTS_TABLE + "." + KEY_ID + "), 0) as " + KEY_TOTAL},
                null, null, null, null, null);
    }

    public Cursor fetchMarks(long subjectID) {
        return mDb.query(MARKS_TABLE, new String[] {KEY_ID, KEY_REASON, KEY_MARK},
                KEY_SUBJECT_ID + "=" + subjectID, null, null, null, null);
    }

    public Cursor fetchTotalMark(long subjectID) {
        return mDb.query(MARKS_TABLE, new String[] {"ifNull(sum(" + KEY_MARK + "), 0) as " + KEY_TOTAL},
                KEY_SUBJECT_ID + "=" + subjectID, null, null, null, null);
    }
}
