package com.example.presenter;

import com.example.base.SyncExecutor;
import com.example.dao.AsyncTasksDao;
import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkState;

public class TasksPresenter {

    @Nonnull
    private final AsyncTasksDao tasksDao;
    @Nullable
    private Listener listener;
//    @Nullable
//    private ImmutableList<Task> storedData;

    @Inject
    public TasksPresenter(final @Nonnull AsyncTasksDao tasksDao) {
        this.tasksDao = tasksDao;
    }

    public void register(@Nonnull final Listener listener) {
        this.listener = listener;
//        if (storedData == null) {
        listener.showProgress(true);
//        } else {
//            listener.showProgress(false);
//            listener.swapData(storedData);
//        }
        listener.showTaskProgress(false);
        listener.showError(false);

        retrieveData();
    }

    private void retrieveData() {
        tasksDao.getAllTasks(new SyncExecutor.OnSuccess<ImmutableList<Task>>() {
            @Override
            public void run(final ImmutableList<Task> data) {
                final Listener baseListener = TasksPresenter.this.listener;
                if (baseListener != null) {
                    baseListener.swapData(data);
                    baseListener.showError(false);
                    baseListener.showProgress(false);
                }
//                storedData = data;
            }
        });
    }


    public void unregister() {
        this.listener = null;
    }

    public void errorClick() {
        checkState(listener != null, "Called after unregister");
        listener.showProgress(true);
        retrieveData();
    }

    public void sendClick() {
        checkState(listener != null, "Called after unregister");
        listener.showTaskProgress(true);
        final String taskName = listener.getTaskName();
        tasksDao.addTask(new Task(-1, taskName), new SyncExecutor.OnSuccess<Task>() {
            @Override
            public void run(final Task data) {
                if (listener != null) {
                    listener.showTaskProgress(false);
                    listener.clearTaskTextView();
                }
                retrieveData();
            }
        });

    }

    public void deleteClick(Task item) {
        checkState(listener != null, "Called after unregister");
        listener.showTaskProgress(true);
        tasksDao.deleteTask((int) item.id(), new SyncExecutor.OnSuccess<Task>() {
            @Override
            public void run(final Task data) {
                if (listener != null) {
                    listener.showTaskProgress(false);
                }
                retrieveData();
            }
        });
    }

    public static interface Listener {

        void swapData(@Nonnull ImmutableList<Task> tasks);

        void showProgress(boolean showProgress);

        String getTaskName();

        long getIdByPosition(int position);

        void showTaskProgress(boolean showProgress);

        void showError(boolean showError);

        void clearTaskTextView();
    }
}
