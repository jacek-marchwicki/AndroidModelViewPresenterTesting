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

package com.example.cachedmodel.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.model.Task;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseArrayAdapter<T> extends BaseAdapter {

    @Nonnull
    protected ImmutableList<T> mItems = ImmutableList.of();

    public BaseArrayAdapter() {
    }

    @Override
    public final int getCount() {
        return mItems.size();
    }

    @Override
    @Nonnull
    public final T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public final long getItemId(int position) {
        // We need to allow for querying for id after notifyDataSetInvalidated
        if (position >= mItems.size()) {
            return 0;
        }
        return getItemId(position, getItem(position));
    }

    @Override
    @Nonnull
    public final View getView(int position,
                              @Nonnull View convertView,
                              @Nonnull ViewGroup parent) {
        return getView(position, getItem(position), convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        if (getViewTypeCount() != 1) {
            final T item = getItem(position);
            return getItemViewType(position, item);
        } else {
            return super.getItemViewType(position);
        }
    }

    protected int getItemViewType(int position,
                                  @Nonnull T item) {
        return 0;
    }

    public void swapData(@Nonnull ImmutableList<T> items) {
        mItems = checkNotNull(items);
        notifyDataSetChanged();
    }

    protected abstract long getItemId(int position,
                                      @Nonnull T item);


    protected abstract View getView(int position,
                                    @Nonnull T item,
                                    @Nullable View convertView,
                                    @Nonnull ViewGroup parent);

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
