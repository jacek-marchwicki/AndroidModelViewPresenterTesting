package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class TasksDaoImpl implements TasksDao {

    @Nonnull
    private final DbHelper dbHelper;


    @Inject
    public TasksDaoImpl(@Nonnull Context context) {
        dbHelper = new DbHelper(context);
    }

    @Nonnull
    @Override
    public ImmutableList<Task> getAllTasks() {
        sleepMyLittlePrincess();
        final ArrayList<Task> tasks = new ArrayList<>();
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            final Cursor query = db.query(TaskEntry.TABLE_NAME, new String[]{TaskEntry._ID, TaskEntry.COLUMN_NAME}, null, null, null, null, null);
            try {
                for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
                    final long id = query.getLong(0);
                    final String name = query.getString(1);
                    tasks.add(new Task(id, name));
                }
            } finally {
                query.close();
            }
        } finally {
            db.close();
        }
        return ImmutableList.copyOf(tasks);
    }

    private void sleepMyLittlePrincess() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}
    }

    @Nonnull
    @Override
    public Task addTask(@Nonnull final Task task) {
        sleepMyLittlePrincess();
        checkArgument(task.id() < 0);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            final ContentValues values = new ContentValues();
            values.put(TaskEntry.COLUMN_NAME, task.name());
            final long id = db.insert(TaskEntry.TABLE_NAME, null, values);

            return new Task(id, task.name());
        } finally {
            db.close();
        }
    }

    @Override
    public void deleteTask(int id) {
        sleepMyLittlePrincess();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            db.delete(TaskEntry.TABLE_NAME, TaskEntry._ID + "=?", new String[] {Integer.toString(id)} );
        } finally {
            db.close();
        }
    }

    void clear() {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(TaskEntry.TABLE_NAME, null, null);
        } finally {
            db.close();
        }

    }

    static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME = "name";
    }

    static class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        private static final String TEXT_TYPE = " TEXT";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                        TaskEntry._ID + " INTEGER PRIMARY KEY," +
                        TaskEntry.COLUMN_NAME + TEXT_TYPE +
                        " )";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

        public DbHelper(@Nonnull Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(@Nonnull SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(@Nonnull SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(@Nonnull SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
