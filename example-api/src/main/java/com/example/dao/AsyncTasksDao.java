package com.example.dao;

import com.example.base.SyncExecutor;
import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class AsyncTasksDao {
    @Nonnull
    private final SyncExecutor syncExecutor;
    @Nonnull
    private final TasksDao tasksDao;

    @Inject
    public AsyncTasksDao(@Nonnull SyncExecutor syncExecutor, @Nonnull TasksDao tasksDao) {
        this.syncExecutor = syncExecutor;
        this.tasksDao = tasksDao;
    }

    public void getAllTasks(@Nonnull SyncExecutor.OnSuccess<ImmutableList<Task>> callback) {
        syncExecutor.executeAndReturn(new Callable<ImmutableList<Task>>() {
            @Override
            public ImmutableList<Task> call() throws Exception {
                return tasksDao.getAllTasks();
            }
        }, callback, null);
    }

    public void addTask(@Nonnull final Task task, @Nonnull SyncExecutor.OnSuccess<Task> callback) {
        syncExecutor.executeAndReturn(new Callable<Task>() {
            @Override
            public Task call() throws Exception {
                return tasksDao.addTask(task);
            }
        }, callback, null);

    }
	
	public void deleteTask(@Nonnull final Task task, @Nonnull SyncExecutor.OnSuccess<Task> callback) {
        syncExecutor.executeAndReturn(new Callable<Task>() {
            @Override
            public Task call() throws Exception {
                return tasksDao.deleteTask(task);
            }
        }, callback, null);

    }
}
