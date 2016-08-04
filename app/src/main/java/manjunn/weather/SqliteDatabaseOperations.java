package manjunn.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manjunn on 4/13/2016.
 */

public class SqliteDatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 1;
    public static final String cities_List = "ccc_values";
    public static final String high_scores = "highest_scores_a";
    public static final String settings_values = "s_values";
    public String createINQuery = "CREATE TABLE IF NOT EXISTS " + cities_List + "(city TEXT, temp TEXT, status TEXT)";
    SqliteDatabaseOperations dop;

    public void onCreate(SQLiteDatabase sdb) {

        sdb.execSQL(createINQuery);
    }

    public void onUpgrade(SQLiteDatabase sdb, int arg1, int arg2) {
    }

    public SqliteDatabaseOperations(Context context) {
        super(context, "Weather", null, database_version);
    }

    public void insertCitiesDetails(SqliteDatabaseOperations sqliteDatabaseOperations, String city, String temp, String status) {
        dop = sqliteDatabaseOperations;
        List cityList = getCitiesSaved(sqliteDatabaseOperations);
        if (cityList.contains(city)) {
            updateCitiesDetails(sqliteDatabaseOperations, city, temp, status);
            return;
        }
        SQLiteDatabase sql = sqliteDatabaseOperations.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("city", city);
        contentValues.put("temp", temp);
        contentValues.put("status", status);
        sql.insert(cities_List, null, contentValues);
    }


    public List getCitiesSaved(SqliteDatabaseOperations dop) {
        List cities = new ArrayList();
        SQLiteDatabase sql = dop.getReadableDatabase();
        sql.execSQL(createINQuery);
        String retrievalQuery = "SELECT * FROM " + cities_List;
        Cursor cr = sql.rawQuery(retrievalQuery, null);
        if (cr.moveToFirst() && cr != null) {
            do {
                cities.add(cr.getString(0));
            } while (cr.moveToNext());
        }
        return cities;
    }

    public Cursor getSavedCitiesDetails(SqliteDatabaseOperations dop) {
        SQLiteDatabase sql = dop.getReadableDatabase();
        String retrievalQuery = "SELECT * FROM " + cities_List;
        return sql.rawQuery(retrievalQuery, null);
    }

    public void updateCitiesDetails(SqliteDatabaseOperations sqliteDatabaseOperations, String city, String temp, String status) {
        SQLiteDatabase sql = sqliteDatabaseOperations.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("temp", temp);
        cv.put("status", status);
        sql.update(cities_List, cv, "city=?", new String[]{city});
    }

    public void deleteCity(SqliteDatabaseOperations sqliteDatabaseOperations, String city) {
        SQLiteDatabase sql = sqliteDatabaseOperations.getWritableDatabase();
        sql.delete(cities_List, "city=?", new String[]{city});
    }
}
