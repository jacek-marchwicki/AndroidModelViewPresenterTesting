package com.example.sample;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.truth.Truth.assert_;

public class UglyAsyncTest {
    static interface Return<T> {
        public void ret(T value);
    }

    static class AddClass {
        public void add(final int x, final int y, final Return<Integer> ret) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ret.ret(x + y);
                }
            }).start();
        }
    }

    @Test
    public void testName() throws Exception {
        final AddClass addClass = new AddClass();

        final BlockingQueue<Integer> integers = new ArrayBlockingQueue<>(1);
        addClass.add(5, 6, new Return<Integer>() {
            @Override
            public void ret(final Integer value) {
                try {
                    integers.put(value);
                } catch (InterruptedException ignore) {}
            }
        });

        final Integer poll = integers.poll(1, TimeUnit.SECONDS);
        assert_().that(poll).isEqualTo(11);
    }
}
