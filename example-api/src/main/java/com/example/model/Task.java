package com.example.model;

import javax.annotation.Nonnull;

public class Task {
    private final long id;
    @Nonnull
    private final String name;

    public Task(final long id, @Nonnull final String name) {
        this.id = id;
        this.name = name;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        final Task task = (Task) o;

        return id == task.id && name.equals(task.name);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }
}
