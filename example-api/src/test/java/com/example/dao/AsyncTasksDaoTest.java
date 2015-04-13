package com.example.dao;

import com.example.base.SameThreadScheduler;
import com.example.base.SyncExecutor;
import com.example.model.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AsyncTasksDaoTest {

    @Inject
    AsyncTasksDao asyncDao;
    @Mock
    TasksDao dao;
    @Mock
    SyncExecutor.OnSuccess<Task> taskCallback;
    @Mock
    SyncExecutor.OnSuccess<ImmutableList<Task>> tasksCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ObjectGraph.create(new Module()).inject(this);
    }

    @Test
    public void testAddTaskSuccess_callbackCalled() throws Exception {
        when(dao.addTask(any(Task.class))).thenReturn(new Task(2, "asdf"));

        asyncDao.addTask(new Task(-1L, "test"), taskCallback);

        verify(taskCallback).run(new Task(2, "asdf"));
    }

    @Test
    public void testGetTasksSuccess_callbackCalled() throws Exception {
        when(dao.getAllTasks()).thenReturn(ImmutableList.<Task>of());

        asyncDao.getAllTasks(tasksCallback);

        verify(tasksCallback).run(ImmutableList.<Task>of());
    }

    @dagger.Module(injects = AsyncTasksDaoTest.class)
    class Module{
        @Provides
        TasksDao provideTasksDao() {
            return dao;
        }

        @Provides
        SyncExecutor provideSyncExecutor() {
            return new SyncExecutor(MoreExecutors.sameThreadExecutor(), new SameThreadScheduler());
        }
    }

}