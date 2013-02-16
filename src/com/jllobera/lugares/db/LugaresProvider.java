package com.jllobera.lugares.db;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 24/08/11
 * Time: 0:02
 */

/**
 * Creamos un proveedor de contenidos para implementar las acciones básicas para manejar los datos de la BD;
 * Insert, update, delete...
 */
public class LugaresProvider extends ContentProvider {
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "lugares", LugaresDB.Lugares.LUGARES);
        uriMatcher.addURI(LugaresDB.PROVIDER_NAME, "lugares/#", LugaresDB.Lugares.LUGARES_ID);
    }

    private SQLiteDatabase lugaresDB;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        LugaresSQLHelper dbHelper = new LugaresSQLHelper(context);
        lugaresDB = dbHelper.getWritableDatabase();
        return (lugaresDB != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        sqlBuilder.setTables(LugaresDB.Lugares.NOMBRE_TABLA);
        if (uriMatcher.match(uri) == LugaresDB.Lugares.LUGARES_ID) {
            sqlBuilder.appendWhere(LugaresDB.Lugares._ID + " = " + uri.getPathSegments().get(1));
        }
        if (sortOrder == null || "".equals(sortOrder)) {
            sortOrder = LugaresDB.Lugares.NOMBRE;
        }

        Cursor c = sqlBuilder.query(
                lugaresDB, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case LugaresDB.Lugares.LUGARES:
                return "vnd.android.cursor.dir/vnd.jllobera.lugares.lugares";
            case LugaresDB.Lugares.LUGARES_ID:
                return "vnd.android.cursor.item/vnd.jllobera.lugares.lugares/#";
            default:
                throw new IllegalAccessError("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = lugaresDB.insert(LugaresDB.Lugares.NOMBRE_TABLA, "", values);

        //Si todo ha ido bien devuelve la uri
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(LugaresDB.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        return null;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
       int rows = lugaresDB.delete(LugaresDB.Lugares.NOMBRE_TABLA, selection, selectionArgs);
        if(rows>0){
            return rows;
        }
        return 0;
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rows = lugaresDB.update(LugaresDB.Lugares.NOMBRE_TABLA, contentValues, selection, selectionArgs);
        //Si todo ha ido bien devuelve el número de filas afectadas
        if (rows > 0) {
            return rows;
        }
        return 0;
    }

}
