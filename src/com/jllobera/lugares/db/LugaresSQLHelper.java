package com.jllobera.lugares.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Crea la tabla si no existe
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 22/08/11
 * Time: 10:45
 */
public class LugaresSQLHelper extends SQLiteOpenHelper {
    public LugaresSQLHelper(Context context) {
        super(context, LugaresDB.DB_NAME, null, LugaresDB.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Permitimos que se pueda escribir en la BD
        if (db.isReadOnly()) {
            db = getWritableDatabase();
        }

        /*
           Si no existe la tabla, la creamos cuando ejecutamos la aplicación
           por primera vez
           Sólo dejaremos como opcional la foto y la descripción, los demás campos serán obligatorios
        */
        db.execSQL("CREATE TABLE " + LugaresDB.Lugares.NOMBRE_TABLA + "(" +
                LugaresDB.Lugares._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LugaresDB.Lugares.NOMBRE + " TEXT, " +
                LugaresDB.Lugares.DESCRIPCION + " TEXT, " +
                LugaresDB.Lugares.DIRECCION + " TEXT, " +
                LugaresDB.Lugares.DIRECCIONDET + " TEXT, " +
                LugaresDB.Lugares.LATITUD + " INTEGER, " +
                LugaresDB.Lugares.LONGITUD + " INTEGER, " +
                LugaresDB.Lugares.FOTO_THUMBNAIL + " TEXT, " +
                LugaresDB.Lugares.FOTO + " TEXT" + ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
