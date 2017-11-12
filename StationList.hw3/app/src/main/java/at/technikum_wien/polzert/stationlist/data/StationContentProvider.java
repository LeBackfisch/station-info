package at.technikum_wien.polzert.stationlist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sebastian on 12.11.2017.
 */

public class StationContentProvider extends ContentProvider {
    private StationsDbHelper mStationsDbHelper;
    public static final int STATIONS = 100;
    public static final int STATION_WITH_ID = 101;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(StationsContract.CONTENT_AUTHORITY, StationsContract.PATH_STATIONS, STATIONS);
        uriMatcher.addURI(StationsContract.CONTENT_AUTHORITY, StationsContract.PATH_STATIONS + "/#", STATION_WITH_ID);
        return  uriMatcher;
    }
    public UriMatcher mUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mStationsDbHelper = new StationsDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mStationsDbHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case STATIONS:
                retCursor = db.query(StationsContract.StationsTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return  retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mStationsDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STATIONS:
                long id = db.insert(StationsContract.StationsTable.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(StationsContract.StationsTable.CONTENT_URI, id);
                else
                    throw new SQLException("Failed to insert row");
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mStationsDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {
            case STATION_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(StationsContract.StationsTable.TABLE_NAME,
                        StationsContract.StationsTable._ID + " = ?",
                        new String[] {id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (tasksDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
