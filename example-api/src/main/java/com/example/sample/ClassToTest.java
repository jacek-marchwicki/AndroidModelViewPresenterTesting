package com.example.sample;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ClassToTest {
    @Nonnull
    private final MyDatabase myDatabase;

    @Inject
    public ClassToTest(@Nonnull final MyDatabase myDatabase) {
        this.myDatabase = myDatabase;
    }

    public boolean query(@Nonnull String query) {
        final List<String> response = myDatabase.query(query);
        return response == null || response.size() == 0;
    }
}
