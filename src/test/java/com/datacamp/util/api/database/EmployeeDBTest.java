package com.datacamp.util.api.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

class EmployeeDBTest {

    @Test
    void findBy_filtersByColumns() {
        EmployeeDB db = new EmployeeDB();
        db.add("Ada", "Engineering", "ada@businessfactory.com");
        db.add("Grace", "Research", "grace@businessfactory.com");
        db.add("Linus", "Engineering", "linus@businessfactory.com");
        db.add("Mary", "HR", "mary@businessfactory.com");

        var engineers = db.findBy(Column.DEPARTMENT, "Engineering");

        assertThat(engineers, hasSize(2));
    }

    @Test
    void findBy_filtersByColumnsAndSorts() {
        EmployeeDB db = new EmployeeDB();
        db.add("Ada", "Engineering", "ada@businessfactory.com");
        db.add("Grace", "Research", "grace@businessfactory.com");
        db.add("Linus", "Engineering", "linus@businessfactory.com");
        db.add("Mary", "HR", "mary@businessfactory.com");

        var engineersSorted = db.findBy(Column.DEPARTMENT, "Engineering", Column.NAME, true);

        assertThat(engineersSorted, hasSize(2));
    }

    @Test
    void findBy_looksUpById() {
        EmployeeDB db = new EmployeeDB();

        int someId = db.add("Linus", "Engineering", "linus@businessfactory.com"); // grab a valid ID
        Employee emp = db.findBy(Column.ID, someId).getFirst();

        assertThat(emp.getName(), equalTo("Linus"));
        assertThat(emp.getDepartment(), equalTo("Engineering"));
        assertThat(emp.getEmail(), equalTo("linus@businessfactory.com"));
    }
}
