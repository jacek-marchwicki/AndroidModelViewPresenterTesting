package com.example.model;

import javax.annotation.Nonnull;

public class Task {
    private final long id;

    private int cid;
    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Nonnull
    private final String name;

    public Task(final long id, @Nonnull final String name) {
        this.id = id;
        this.name = name;
    }
	public Task(final long id, @Nonnull final String name, int cid) {
        this.id = id;
        this.name = name;
        this.cid = cid;
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
