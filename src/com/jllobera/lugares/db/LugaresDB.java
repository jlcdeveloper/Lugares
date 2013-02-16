package com.jllobera.lugares.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 22/08/11
 * Time: 10:49
 */
public class LugaresDB {
    //Nombre de la BD
    public static final String DB_NAME = "lugaresdb.db";
    //Versión de la BD
    public static final int DB_VERSION = 1;

    public static final String PROVIDER_NAME = "com.jllobera.lugares.lugaresdb";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/lugares");

    private LugaresDB(){}

    public static final class Lugares implements BaseColumns{
        private Lugares(){}

        //Orden de los elementos por ID
        public static final String DEFAULT_SORT ="_ID DESC";

        //Abstraemos el nombre real de las columnas de la BD, de esta forma
        //si alguna vez tocamos la BD, sólo tendremos que cambiar esta clase.

        public static final String NOMBRE_TABLA = "lugares";
        public static final String _ID = "_id";
        public static final String NOMBRE = "nombre";
        public static final String DESCRIPCION = "descripcion";
        public static final String DIRECCION = "direccion";
        public static final String DIRECCIONDET = "direcciondetalle";
        public static final String LATITUD = "latitud";
        public static final String LONGITUD = "longitud";
        public static final String FOTO = "foto";
        public static final String FOTO_THUMBNAIL = "fotothumbnail";

        //Con estas constantes controlamos si se solicita un registro o varios
        public static final int LUGARES = 1;
        public static final int LUGARES_ID = 2;
    }
}
