/*
 * This file is generated by jOOQ.
 */
package info.galudisu.pg.jooq.tables.records;


import info.galudisu.pg.jooq.tables.Courses;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CoursesRecord extends UpdatableRecordImpl<CoursesRecord> implements Record3<String, String, Integer> {

    private static final long serialVersionUID = -1226606749;

    /**
     * Setter for <code>public.courses.c_no</code>.
     */
    public void setCNo(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.courses.c_no</code>.
     */
    public String getCNo() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.courses.title</code>.
     */
    public void setTitle(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.courses.title</code>.
     */
    public String getTitle() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.courses.hours</code>.
     */
    public void setHours(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.courses.hours</code>.
     */
    public Integer getHours() {
        return (Integer) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<String, String, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Courses.COURSES.C_NO;
    }

    @Override
    public Field<String> field2() {
        return Courses.COURSES.TITLE;
    }

    @Override
    public Field<Integer> field3() {
        return Courses.COURSES.HOURS;
    }

    @Override
    public String component1() {
        return getCNo();
    }

    @Override
    public String component2() {
        return getTitle();
    }

    @Override
    public Integer component3() {
        return getHours();
    }

    @Override
    public String value1() {
        return getCNo();
    }

    @Override
    public String value2() {
        return getTitle();
    }

    @Override
    public Integer value3() {
        return getHours();
    }

    @Override
    public CoursesRecord value1(String value) {
        setCNo(value);
        return this;
    }

    @Override
    public CoursesRecord value2(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public CoursesRecord value3(Integer value) {
        setHours(value);
        return this;
    }

    @Override
    public CoursesRecord values(String value1, String value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CoursesRecord
     */
    public CoursesRecord() {
        super(Courses.COURSES);
    }

    /**
     * Create a detached, initialised CoursesRecord
     */
    public CoursesRecord(String cNo, String title, Integer hours) {
        super(Courses.COURSES);

        set(0, cNo);
        set(1, title);
        set(2, hours);
    }
}
