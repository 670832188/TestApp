package com.dev.kit.testapp.dbAndProvider.providerTest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.dbAndProvider.dbTest.TestDbHelper;

/**
 * Created by cuiyan on 2018/12/13.
 */

public class TestProvider extends ContentProvider {
    private static final String AUTHORITY = "com.dev.kit.testapp.TestProvider";
    private static final String STUDENT_TABLE_NAME = "studentInfo";
    private static final int STUDENT_TABLE_CODE = 1;
    public static final String STUDENT_INFO_URI = "content://com.dev.kit.testapp.TestProvider/studentInfo";
    private Context context;
    private UriMatcher uriMatcher;
    private TestDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        init();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        uriMatcher.addURI(AUTHORITY, STUDENT_TABLE_NAME, STUDENT_TABLE_CODE);
        switch (uriMatcher.match(uri)) {
            case STUDENT_TABLE_CODE: {
                return dbHelper.getReadableDatabase().query(TestDbHelper.STUDENT_TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null);
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("No external getType");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case STUDENT_TABLE_CODE: {
                long id = dbHelper.getWritableDatabase().insert(TestDbHelper.STUDENT_TABLE_NAME, null, values);
                LogUtil.e("mytag", "insertRowId: " + id);
                return uri;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case STUDENT_TABLE_CODE: {
                return dbHelper.getWritableDatabase().delete(TestDbHelper.STUDENT_TABLE_NAME, selection, selectionArgs);
            }
            default: {
                return 0;
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case STUDENT_TABLE_CODE: {
                return dbHelper.getWritableDatabase().update(TestDbHelper.STUDENT_TABLE_NAME, values, selection, selectionArgs);
            }
            default: {
                return 0;
            }
        }
    }

    private void init() {
        context = getContext();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        dbHelper = new TestDbHelper(context);
    }

    public static Uri getStudentInfoUri() {
        return Uri.parse(STUDENT_INFO_URI);
    }
}
