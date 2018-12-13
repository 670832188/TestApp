package com.dev.kit.testapp.dbAndProvider.dbTest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.dbAndProvider.StudentInfo;

import java.util.List;

/**
 * Created by cuiyan on 2018/12/12.
 */

public class TestDbHelper extends SQLiteOpenHelper {
    private static final int version = 3;
    private static final String DB_NAME = "myTestDb.db";
    public static final String STUDENT_TABLE_NAME = "studentInfo";
    private static final String studentInfoTableStruct = "create table if not exists " +
            STUDENT_TABLE_NAME + "(id integer primary key autoincrement, studentNumber integer, name text, math integer, chinese integer, physics integer, chemistry  integer)";

    public static final String REPORT_COLUMN_NO = "studentNumber";
    public static final String REPORT_COLUMN_NAME = "name";
    public static final String REPORT_COLUMN_MATH = "math";
    public static final String REPORT_COLUMN_CHINESE = "chinese";
    public static final String REPORT_COLUMN_PHYSICS = "physics";
    public static final String REPORT_COLUMN_CHEMISTRY = "chemistry";

    public static final String[] REPORT_COLUMNS = {REPORT_COLUMN_NO, REPORT_COLUMN_NAME, REPORT_COLUMN_MATH, REPORT_COLUMN_CHINESE, REPORT_COLUMN_PHYSICS, REPORT_COLUMN_CHEMISTRY};

    public TestDbHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(studentInfoTableStruct);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 测试使用，真实不要这样写哦
        db.execSQL("drop table " + STUDENT_TABLE_NAME);
        db.execSQL(studentInfoTableStruct);
    }

    public void insertOrUpdateStudent(StudentInfo info) {
        synchronized (this) {
            ContentValues values = new ContentValues();
            values.put(REPORT_COLUMN_NO, info.getStudentNumber());
            values.put(REPORT_COLUMN_NAME, info.getName());
            values.put(REPORT_COLUMN_MATH, info.getGradeMathematics());
            values.put(REPORT_COLUMN_CHINESE, info.getGradeChinese());
            values.put(REPORT_COLUMN_PHYSICS, info.getGradePhysics());
            values.put(REPORT_COLUMN_CHEMISTRY, info.getGradeChemistry());
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.query(STUDENT_TABLE_NAME, null, "studentNumber=?", new String[]{String.valueOf(info.getStudentNumber())}, null, null, null);
            if (cursor.getCount() > 0) {
                db.update(STUDENT_TABLE_NAME, values, "studentNumber=?", new String[]{String.valueOf(info.getStudentNumber())});
            } else {
                db.insert(STUDENT_TABLE_NAME, null, values);
            }
            cursor.close();
        }
    }

    public void insertOrUpdateStudent(List<StudentInfo> infoList) {
        synchronized (this) {
            for (StudentInfo info : infoList) {
                ContentValues values = new ContentValues();
                values.put(REPORT_COLUMN_NO, info.getStudentNumber());
                values.put(REPORT_COLUMN_NAME, info.getName());
                values.put(REPORT_COLUMN_MATH, info.getGradeMathematics());
                values.put(REPORT_COLUMN_CHINESE, info.getGradeChinese());
                values.put(REPORT_COLUMN_PHYSICS, info.getGradePhysics());
                values.put(REPORT_COLUMN_CHEMISTRY, info.getGradeChemistry());
                SQLiteDatabase db = getWritableDatabase();
                Cursor cursor = db.query(STUDENT_TABLE_NAME, null, "studentNumber=?", new String[]{String.valueOf(info.getStudentNumber())}, null, null, null);
                if (cursor.getCount() > 0) {
                    db.update(STUDENT_TABLE_NAME, values, "studentNumber=?", new String[]{String.valueOf(info.getStudentNumber())});
                } else {
                    db.insert(STUDENT_TABLE_NAME, null, values);
                }
                cursor.close();
            }
        }
    }

    public Cursor queryAllStudent() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(STUDENT_TABLE_NAME, null, null, null, null, null, null);
    }
}
