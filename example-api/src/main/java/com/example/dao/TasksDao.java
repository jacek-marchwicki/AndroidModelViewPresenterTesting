package com.example.dao;

import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

public interface TasksDao {
    @Nonnull
    public ImmutableList<Task> getAllTasks();
    @Nonnull
    public Task addTask(@Nonnull final Task task);

    @Nonnull
    public Task taskChangeItemOnListView(@Nonnull final Task task);
}
