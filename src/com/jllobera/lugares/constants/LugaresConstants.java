package com.jllobera.lugares.constants;

import com.jllobera.lugares.R;
import com.jllobera.lugares.db.LugaresDB;

/**
 *
 * Clase que contiene objetos que se utilizan como constantes
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 3/09/11
 * Time: 18:32
 */
public class LugaresConstants {
    public final String[] ColumnasListado = new String[]{
            LugaresDB.Lugares._ID, //0
            LugaresDB.Lugares.NOMBRE, //1
            LugaresDB.Lugares.DIRECCION, //2
            LugaresDB.Lugares.DIRECCIONDET,//3
            LugaresDB.Lugares.FOTO_THUMBNAIL, //4
    };

    public final String[] Id = new String[]{
                LugaresDB.Lugares._ID, //0
        };

    public final String[] todasColumnas = new String[]{
            LugaresDB.Lugares._ID, //0
            LugaresDB.Lugares.NOMBRE, //1
            LugaresDB.Lugares.DESCRIPCION, //2
            LugaresDB.Lugares.DIRECCION, //3
            LugaresDB.Lugares.DIRECCIONDET, //4
            LugaresDB.Lugares.FOTO, //5
            LugaresDB.Lugares.FOTO_THUMBNAIL, //6
            LugaresDB.Lugares.LATITUD, //7
            LugaresDB.Lugares.LONGITUD, //8
    };

    public String defImg = "android.resource://com.jllobera.lugares/"+ R.drawable.icolista;

}
