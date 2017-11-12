package at.technikum_wien.polzert.stationlist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sebastian on 12.11.2017.
 */

public class StationsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stations.db";
    private static final  int DATABASE_VERSION = 1;

    public StationsDbHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
    final String CREATE_TABLE = "CREATE TABLE "+ StationsContract.StationsTable.TABLE_NAME+"("+
            StationsContract.StationsTable._ID+" INTEGER PRIMARY KEY, "+
            StationsContract.StationsTable.CONLUMN_NAME+" TEXT NOT NULL, "+
            StationsContract.StationsTable.COLUMN_POSITION+ " TEXT NOT NULL, "+
            StationsContract.StationsTable.COLUMN_LINES+ " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ StationsContract.StationsTable.TABLE_NAME);
        onCreate(db);
    }
}
