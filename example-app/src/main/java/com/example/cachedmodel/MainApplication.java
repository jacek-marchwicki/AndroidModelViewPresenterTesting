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

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.base.Scheduler;
import com.example.base.SyncExecutor;
import com.example.dao.TasksDao;
import com.example.dao.TasksDaoImpl;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.ObjectGraph;
import dagger.Provides;

public class MainApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new Module());
        objectGraph.inject(this);

    }

    @Nonnull
    public static MainApplication fromApplication(@Nonnull Application application) {
        return (MainApplication) application;
    }

    @Nonnull
    public ObjectGraph objectGraph() {
        return objectGraph;
    }

    @dagger.Module(
            injects = MainApplication.class,
            library = true
    )
    class Module {

        @Provides
        @Singleton
        Scheduler provideScheduler() {
            final Handler handler = new Handler(Looper.getMainLooper());
            return new Scheduler() {

                @Override
                public void schedule(@Nonnull Runnable runnable) {
                    handler.post(runnable);
                }
            };
        }

        @Provides
        @Singleton
        SyncExecutor provideSyncExecutor(final Scheduler scheduler) {
            final ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

            return new SyncExecutor(MoreExecutors.listeningDecorator(executor), scheduler);
        }

        @Provides
        @Singleton
        TasksDao provideTasksDao() {
            return new TasksDaoImpl(MainApplication.this);
        }

    }
}
