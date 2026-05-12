package com.onlinecourse.utils;

public class ComboItem<T> {
    private final int id;
    private final String label;
    private final T value;

    public ComboItem(int id, String label, T value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return label;
    }
}
