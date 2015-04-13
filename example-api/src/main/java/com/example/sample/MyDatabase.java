package com.example.sample;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MyDatabase {

    @Nullable
    public List<String> query(@Nonnull String query) {
        final int i = new Random().nextInt() + query.length() % 3;
        switch (i) {
            case 0:
                return new ArrayList<>();
            case 1:
                return null;
            default:
                return ImmutableList.of("response");
        }
    }

}
