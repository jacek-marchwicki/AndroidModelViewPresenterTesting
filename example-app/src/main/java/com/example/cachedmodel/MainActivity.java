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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anim.FadeAnimationHelper;
import com.example.cachedmodel.adapters.TasksAdapter;
import com.example.model.Task;
import com.example.presenter.TasksPresenter;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

public class MainActivity extends Activity {

    private TasksAdapter mAdapter;

    private TasksPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FadeAnimationHelper fadeAnimationHelper = new FadeAnimationHelper(getResources());

        presenter = restoreRetainedFragment();

        final View errorView = findViewById(R.id.main_error);
        final ListView listView = (ListView) findViewById(R.id.main_list);
        final View progressView = findViewById(R.id.main_progress);
        final TextView taskTextView = (TextView) findViewById(R.id.main_comment_edit_text);
        final View sendButton = findViewById(R.id.main_send_button);
        final View taskProgress = findViewById(R.id.main_comment_progress);

        mAdapter = new TasksAdapter(this);
        listView.setAdapter(mAdapter);

        presenter.register(new TasksPresenter.Listener() {

            @Override
            public void swapData(@Nonnull final ImmutableList<Task> tasks) {
                mAdapter.swapData(tasks);
            }

            @Override
            public void showProgress(final boolean showProgress) {
                fadeAnimationHelper.setVisibility(progressView, showProgress ? View.VISIBLE : View.GONE);
            }

            @Override
            public String getTaskName() {
                return String.valueOf(taskTextView.getText());
            }

            @Override
            public void showTaskProgress(final boolean showProgress) {
                fadeAnimationHelper.setVisibility(taskProgress, showProgress ? View.VISIBLE : View.GONE);
            }

            @Override
            public void showError(final boolean showError) {
                fadeAnimationHelper.setVisibility(errorView, showError ? View.VISIBLE : View.GONE);
            }

            @Override
            public void clearTaskTextView() {
                taskTextView.setText("");
            }

        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        "Name: " + ((TextView) view).getText() + ", Row number: " + position+1, Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                presenter.deleteClick((int)id);
                Toast.makeText(getApplicationContext(),
                        "Deleted " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
		
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.errorClick();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                presenter.sendClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.unregister();
        super.onDestroy();
    }

    private TasksPresenter restoreRetainedFragment() {
        final FragmentManager fm = getFragmentManager();
        final RetainedFragment retainedFragment = (RetainedFragment) fm.findFragmentByTag("data");
        if (retainedFragment != null) {
            return retainedFragment.getData();
        }

        final RetainedFragment newRetainedFragment = new RetainedFragment();
        fm.beginTransaction().add(newRetainedFragment, "data").commit();
        final TasksPresenter presenter = MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .get(TasksPresenter.class);
        newRetainedFragment.setData(presenter);
        return presenter;
    }

    public static class RetainedFragment extends Fragment {

        private TasksPresenter data;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setData(TasksPresenter data) {
            this.data = data;
        }

        public TasksPresenter getData() {
            return data;
        }
    }

    @dagger.Module(
            injects = {
                    TasksPresenter.class
            },
            addsTo = MainApplication.Module.class
    )
    class Module {

    }

}
