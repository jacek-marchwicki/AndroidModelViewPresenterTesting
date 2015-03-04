package com.example.sample;

import com.example.base.SameThreadScheduler;
import com.example.base.Scheduler;
import com.example.base.SyncExecutor;
import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NiceAsyncTest {
    static class AddClass {
        private final SyncExecutor syncExecutor;

        public AddClass(final SyncExecutor syncExecutor) {
            this.syncExecutor = syncExecutor;
        }

        public void add(final int x, final int y, final SyncExecutor.OnSuccess<Integer> ret) {
            syncExecutor.executeAndReturn(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return x + y;
                }
            }, ret, null);
        }
    }

    @Mock
    SyncExecutor.OnSuccess<Integer> callback;

    @Test
    public void testName() throws Exception {
        final AddClass addClass = new AddClass(new SyncExecutor(MoreExecutors.sameThreadExecutor(), new SameThreadScheduler()));

        addClass.add(5, 6, callback);
        verify(callback).run(11);
    }
}
