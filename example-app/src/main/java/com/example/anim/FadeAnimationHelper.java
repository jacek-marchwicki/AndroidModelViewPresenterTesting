/*
 * Copyright 2015 Jacek Marchwicki <jacek.marchwicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class FadeAnimationHelper {

    private final int mShortAnimationDuration;

    @Inject
    public FadeAnimationHelper(@Nonnull Resources resources) {
        checkNotNull(resources);
        mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime);
        checkState(mShortAnimationDuration > 0);
    }

    public void setVisibility(@Nonnull final View view, final int visibility) {
        checkNotNull(view);
        setVisibility(view, visibility, isAfterLayout(view));
    }

    private boolean isAfterLayout(@Nonnull View view) {
        checkNotNull(view);
        return view.getWidth() > 0 && view.getHeight() > 0;
    }

    public void setVisibility(@Nonnull final View view, final int visibility, boolean withAnimation) {
        checkNotNull(view);
        setVisibility(view, visibility, withAnimation, mShortAnimationDuration);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void setVisibility(@Nonnull final View view, final int visibility, boolean withAnimation, int duration) {
        checkNotNull(view);
        checkArgument(duration > 0, "Duration have to be grater than 0");
        cancelAnimationIfSupported(view);
        final boolean visible = visibility == View.VISIBLE;
        if (withAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            final boolean wasVisible = view.getVisibility() == View.VISIBLE;
            if (wasVisible) {
                if (visible) {
                    view.animate()
                            .alpha(1.0f)
                            .setDuration(duration);
                } else {
                    view.animate()
                            .alpha(0.0f)
                            .setDuration(duration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    view.setVisibility(visibility);
                                }
                            });
                }
            } else {
                if (visible) {
                    view.setAlpha(0.0f);
                    view.animate()
                            .alpha(1.0f)
                            .setDuration(duration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    view.setVisibility(visibility);
                                }
                            });
                } else {
                    view.setVisibility(visibility);
                }
            }
        } else {
            if (visible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                view.setAlpha(1.0f);
            }
            view.setVisibility(visibility);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void cancelAnimationIfSupported(@Nonnull View view) {
        checkNotNull(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            view.animate().cancel();
        }
    }

}
