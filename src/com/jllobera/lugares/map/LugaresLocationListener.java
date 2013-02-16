package com.jllobera.lugares.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

/**
 *
 * Obtiene la posición actual
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 6/09/11
 * Time: 2:09
 */
public class LugaresLocationListener implements LocationListener {

    private Context context;
    private MapController mapController;

    public LugaresLocationListener(Context context, MapController mapController) {
        this.context = context;
        this.mapController = mapController;
    }


    public void onLocationChanged(Location location) {
        if (location != null) {
            int lat = (int) (location.getLatitude() * 1E6);
            int lon = (int) (location.getLongitude() * 1E6);
            GeoPoint miPunto = new GeoPoint(lat, lon);
            mapController.animateTo(miPunto);
        }
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }


}
