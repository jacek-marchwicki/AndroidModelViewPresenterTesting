package com.example.presenter;

import com.example.base.SyncExecutor;
import com.example.dao.AsyncTasksDao;
import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class TasksPresenterTest {

    @Inject
    TasksPresenter presenter;
    @Mock
    TasksPresenter.Listener listener;
    @Mock
    AsyncTasksDao tasksDao;
    @Captor
    ArgumentCaptor<SyncExecutor.OnSuccess<ImmutableList<Task>>> callbackCaptor;
    @Captor
    ArgumentCaptor<SyncExecutor.OnSuccess<Task>> taskCallbackCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ObjectGraph.create(new Module()).inject(this);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testAfterStart_showProgress() throws Exception {
        presenter.register(listener);

        verify(listener).showProgress(true);
        verify(listener, never()).showProgress(false);
    }

    @Test
    public void testAfterStart_hideTaskProgress() throws Exception {
        presenter.register(listener);

        verify(listener).showTaskProgress(false);
        verify(listener, never()).showTaskProgress(true);
    }

    @Test
    public void testAfterStart_hideError() throws Exception {
        presenter.register(listener);

        verify(listener).showError(false);
        verify(listener, never()).showError(true);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testAfterGettingTasks_swapData() throws Exception {
        presenter.register(listener);

        getAllTasksCallable().run(ImmutableList.<Task>of());

        verify(listener).swapData(ImmutableList.<Task>of());
    }

    @Test
    public void testWhenErrorClick_refreshData() throws Exception {
        presenter.register(listener);
        reset(tasksDao);

        presenter.errorClick();
        verify(tasksDao).getAllTasks(callbackCaptor.capture());
    }

    @Test
    public void testWhenErrorClick_showProgressWhileRefreshing() throws Exception {
        presenter.register(listener);
        reset(listener);

        presenter.errorClick();
        verify(listener).showProgress(true);
    }

    // --------------------------------------------------------------------------------------------
    @Test
    public void testWhenReceiveTasksAfterUnregister_nothingHappen() throws Exception {
        presenter.register(listener);
        presenter.unregister();
        reset(listener);

        getAllTasksCallable().run(ImmutableList.<Task>of());

        verifyZeroInteractions(listener);
    }

    @Test
    public void testWhenSendClick_callAddTask() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);

        presenter.sendClick();

        verify(tasksDao).addTask(eq(new Task(-1L, "sampleTask")), notNull(SyncExecutor.OnSuccess.class));
    }

    @Test
    public void testWhenSendClick_showTasksProgress() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);

        presenter.sendClick();

        verify(listener).showTaskProgress(true);
    }

    @Test
    public void testWhenSendSuccess_hideTasksProgress() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);
        presenter.sendClick();
        reset(listener);

        getAddTaskCallable().run(new Task(3, "sampleTask"));

        verify(listener).showTaskProgress(false);
    }

    @Test
    public void testWhenSendSuccessAfterUnregister_doNotNotifyListener() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);
        presenter.sendClick();
        presenter.unregister();
        reset(listener);

        getAddTaskCallable().run(new Task(3, "sampleTask"));

        verifyZeroInteractions(listener);
    }

    @Test
    public void testWhenSendSuccess_clearTaskTextView() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);
        presenter.sendClick();
        reset(listener);

        getAddTaskCallable().run(new Task(3, "sampleTask"));

        verify(listener).clearTaskTextView();
    }

    @Test
    public void testWhenSendSuccess_refreshListData() throws Exception {
        when(listener.getTaskName()).thenReturn("sampleTask");
        presenter.register(listener);
        reset(tasksDao);
        presenter.sendClick();

        getAddTaskCallable().run(new Task(3, "sampleTask"));

        verify(tasksDao).getAllTasks(any(SyncExecutor.OnSuccess.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testSendClickAfterUnregister_throwIllegalStateException() throws Exception {
        presenter.register(listener);
        presenter.unregister();

        presenter.sendClick();
    }


    @Test(expected = IllegalStateException.class)
    public void testErrorClickAfterUnregister_throwIllegalStateException() throws Exception {
        presenter.register(listener);
        presenter.unregister();

        presenter.errorClick();
    }

//    @Test
    public void testAfterRotation_dataIsRestored() throws Exception {
        presenter.register(listener);
        presenter.unregister();
        getAllTasksCallable().run(ImmutableList.<Task>of());
        reset(listener);

        presenter.register(listener);

        verify(listener).swapData(ImmutableList.<Task>of());
    }

//    @Test
    public void testAfterRestoreData_progressIsHidden() throws Exception {
        presenter.register(listener);
        presenter.unregister();
        getAllTasksCallable().run(ImmutableList.<Task>of());
        reset(listener);

        presenter.register(listener);

        verify(listener).showProgress(false);
    }

//    @Test
    public void testAfterRestoreData_errorIsHidden() throws Exception {
        presenter.register(listener);
        presenter.unregister();
        getAllTasksCallable().run(ImmutableList.<Task>of());
        reset(listener);

        presenter.register(listener);

        verify(listener).showError(false);
    }

    private SyncExecutor.OnSuccess<ImmutableList<Task>> getAllTasksCallable() {
        verify(tasksDao).getAllTasks(callbackCaptor.capture());
        return callbackCaptor.getValue();
    }

    private SyncExecutor.OnSuccess<Task> getAddTaskCallable() {
        verify(tasksDao).addTask(any(Task.class), taskCallbackCaptor.capture());
        return taskCallbackCaptor.getValue();
    }

    @Test
    public void testAfterGettingTasks_hideProgress() throws Exception {
        presenter.register(listener);
        reset(listener);

        getAllTasksCallable().run(ImmutableList.<Task>of());

        verify(listener).showProgress(false);
    }

    @dagger.Module(injects = TasksPresenterTest.class)
    class Module {
        @Provides
        AsyncTasksDao provideAsyncTasksDao() {
            return tasksDao;
        }

    }
}