package com.example.cachedmodel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.model.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class CommentsAdapter extends BaseArrayAdapter<Task> {

    @Nonnull
    private final Context mContext;

    public CommentsAdapter(@Nonnull Context context) {
        mContext = checkNotNull(context);
    }

    @Override
    protected long getItemId(int position,
                             @Nonnull Task item) {
        return item.id();
    }

    @Override
    protected View getView(int position,
                           @Nonnull Task item,
                           @Nullable View convertView,
                           @Nonnull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(mContext)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        final TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(item.name());
        return convertView;
    }
}
