package com.datacamp.util.api.database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmployeeDB {
    private final Map<Integer, Employee> table = new HashMap<>();
    private final Random rand = new Random();

    /** Insert row; returns the auto‑generated 6‑digit ID. */
    public int add(String name, String dept, String email) {
        int id = generateId();
        table.put(id, new Employee(id, name, dept, email));
        return id;
    }

    private int generateId() {
        int id;
        do id = 100_000 + rand.nextInt(900_000);
        while (table.containsKey(id));
        return id;
    }

    public List<Employee> findBy(
            Column filterCol, Object filterVal, Column sortCol, Boolean ascending) {

        List<Employee> out = new ArrayList<>();
        for (Employee e : table.values()) {
            if (matches(e, filterCol, filterVal)) out.add(e);
        }

        if (sortCol != null && ascending != null) {
            Comparator<Employee> cmp = comparatorFor(sortCol);
            if (!ascending) cmp = cmp.reversed();
            out.sort(cmp);
        }
        return out;
    }

    public List<Employee> findBy(Column filterCol, Object filterVal) {
        return findBy(filterCol, filterVal, null, null);
    }

    private boolean matches(Employee e, Column col, Object val) {
        return switch (col) {
            case ID -> e.getId() == (int) val;
            case NAME -> e.getName().equals(val);
            case DEPARTMENT -> e.getDepartment().equals(val);
            case EMAIL -> e.getEmail().equals(val);
        };
    }

    private Comparator<Employee> comparatorFor(Column col) {
        return switch (col) {
            case ID -> Comparator.comparingInt(Employee::getId);
            case NAME -> Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER);
            case DEPARTMENT ->
                    Comparator.comparing(Employee::getDepartment, String.CASE_INSENSITIVE_ORDER);
            case EMAIL -> Comparator.comparing(Employee::getEmail, String.CASE_INSENSITIVE_ORDER);
        };
    }
}
