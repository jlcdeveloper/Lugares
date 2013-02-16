package com.jllobera.lugares.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Clase que extiende del OverlayItem para la burbuja
 *
 */

public class BalloonOverlayItem extends OverlayItem {
    int id;
    GeoPoint geo;
    String direccion;
    String detalle;

    public BalloonOverlayItem(GeoPoint geoPoint, String dir, String detail, int itemId) {
        super(geoPoint, dir, detail);

        id = itemId;
        geo = geoPoint;
        direccion = dir;
        detalle = detail;
        

    }


    public int getId(){
        return id;
    }
    
    public GeoPoint getGeoPoint(){
        return geo;
    }

    public String getDireccion(){
        return direccion;
    }

    public String getDetalle(){
        return detalle;
    }

    
    
    
}

