/*
 * Copyright (C) 2014 Jacek Marchwicki <jacek.marchwicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cachedmodel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cachedmodel.adapters.TasksAdapter;
import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UglyMainActivity extends Activity {

    private TasksAdapter mAdapter;


    private DbHelper dbHelper;
    private boolean activie;
    private View progressView;
    private View errorView;
    private TextView taskTextView;
    private View taskProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(getApplicationContext());
        activie = true;

        errorView = findViewById(R.id.main_error);
        final ListView listView = (ListView) findViewById(R.id.main_list);
        progressView = findViewById(R.id.main_progress);
        taskTextView = (TextView) findViewById(R.id.main_comment_edit_text);
        final View sendButton = findViewById(R.id.main_send_button);
        taskProgress = findViewById(R.id.main_comment_progress);

        mAdapter = new TasksAdapter(this);
        listView.setAdapter(mAdapter);

        setVisibility(progressView, View.VISIBLE, false, 500);
        setVisibility(taskProgress, View.GONE, false, 500);
        setVisibility(errorView, View.GONE, false, 500);

        retrieveData();


        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(progressView, View.VISIBLE, true, 500);
                retrieveData();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setVisibility(taskProgress, View.VISIBLE, true, 500);
                final String taskName = String.valueOf(taskTextView.getText());
                new AsyncTask<Void, Void, Task>() {

                    @Override
                    protected Task doInBackground(final Void[] params) {
                        return addTask(new Task(-1L, taskName));
                    }

                    @Override
                    protected void onPostExecute(final Task o) {
                        super.onPostExecute(o);
                        if (!activie) {
                            return;
                        }
                        setVisibility(taskProgress, View.GONE, true, 500);
                        taskTextView.setText("");
                        retrieveData();

                    }
                }.execute();
            }
        });
    }

    private void retrieveData() {

        new AsyncTask<Void, Void, ImmutableList<Task>>() {

            @Override
            protected ImmutableList<Task> doInBackground(final Void[] params) {
                return getAllTasks();
            }

            @Override
            protected void onPostExecute(final ImmutableList<Task> data) {
                super.onPostExecute(data);
                if (!activie) {
                    return;
                }
                mAdapter.swapData(data);
                setVisibility(errorView, View.GONE, true, 500);
                setVisibility(progressView, View.GONE, true, 500);

            }
        }.execute();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void setVisibility(@Nonnull final View view, final int visibility, boolean withAnimation, int duration) {
        checkNotNull(view);
        checkArgument(duration > 0, "Duration have to be grater than 0");
        cancelAnimationIfSupported(view);
        final boolean visible = visibility == View.VISIBLE;
        if (withAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            final boolean wasVisible = view.getVisibility() == View.VISIBLE;
            if (wasVisible) {
                if (visible) {
                    view.animate()
                            .alpha(1.0f)
                            .setDuration(duration);
                } else {
                    view.animate()
                            .alpha(0.0f)
                            .setDuration(duration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    view.setVisibility(visibility);
                                }
                            });
                }
            } else {
                if (visible) {
                    view.setAlpha(0.0f);
                    view.animate()
                            .alpha(1.0f)
                            .setDuration(duration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    view.setVisibility(visibility);
                                }
                            });
                } else {
                    view.setVisibility(visibility);
                }
            }
        } else {
            if (visible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                view.setAlpha(1.0f);
            }
            view.setVisibility(visibility);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void cancelAnimationIfSupported(@Nonnull View view) {
        checkNotNull(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            view.animate().cancel();
        }
    }

    @Nonnull
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
    protected void onDestroy() {
        activie = false;
        super.onDestroy();
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
