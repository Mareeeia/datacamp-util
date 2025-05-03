package com.datacamp.util.api.database;

/* ───────── 1. Row object ───────── */
public class Employee {
    private final int id;
    private final String name;
    private final String department;
    private final String email;

    public Employee(int id, String name, String dept, String email) {
        this.id = id;
        this.name = name;
        this.department = dept;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%d | %-10s | %-12s | %s", id, name, department, email);
    }
}
