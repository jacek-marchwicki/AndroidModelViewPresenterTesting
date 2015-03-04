package com.example.dao;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TasksDaoImplTest extends InstrumentationTestCase {

    @dagger.Module(injects = TasksDaoImplTest.class)
    class Module {
        @Provides
        Context provideContext() {
            return getInstrumentation().getTargetContext();
        }

    }

    @Inject
    TasksDaoImpl tasksDao;

    public void setUp() throws Exception {
        super.setUp();
        ObjectGraph.create(new Module()).inject(this);
        tasksDao.clear();
    }

    public void testAtStartPoint_tasksListIsEmpty() throws Exception {
        final ImmutableList<Task> tasks = tasksDao.getAllTasks();

        assertThat(tasks, is(empty()));
    }

    public void testAfterAddingItem_itemIsStored() throws Exception {
        tasksDao.addTask(new Task(-1L, "name"));

        final ImmutableList<Task> tasks = tasksDao.getAllTasks();

        assertThat(tasks, hasSize(1));
    }

    public void testAfterAddingItem_itemWillHaveSetId() throws Exception {
        final Task task = tasksDao.addTask(new Task(-1L, "name"));

        assertThat(task.id(), is(not(equalTo(-1L))));
    }

    public void testAddedItem_haveSameIdAsReceived() throws Exception {
        final Task task = tasksDao.addTask(new Task(-1L, "name"));

        final ImmutableList<Task> tasks = tasksDao.getAllTasks();

        final long addedItemId = task.id();
        final long receivedItemId = tasks.get(0).id();
        assertThat(receivedItemId, is(equalTo(addedItemId)));
    }

    public void testTryToAddItemWithId_illegalArgumentException() throws Exception {
        try {
            tasksDao.addTask(new Task(111L, "name"));
            fail("IllegalArgumentException not thrown");
        } catch (IllegalArgumentException ignore) {}
    }
}