package com.jllobera.lugares.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import java.util.ArrayList;

/**
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 2/09/11
 * Time: 19:51
 */
public class MapItemizedOverlay extends BalloonItemizedOverlay<BalloonOverlayItem> {
    public ArrayList<BalloonOverlayItem> mOverlays = new ArrayList<BalloonOverlayItem>();
    public BalloonOverlayItem item;

    public MapItemizedOverlay(Context context, Drawable defaultMarker, MapView mapView) {
        super(context, boundCenterBottom(defaultMarker),mapView);
    }

    /**
     * Se utiliza cuando se hace la búsqueda en el mapa
     *
     * @param direccion Direccion
     */
    public void addLocalizacion(Address direccion) {
        GeoPoint punto = new GeoPoint((int) (direccion.getLatitude() * 1E6), (int) (direccion.getLongitude() * 1E6));

        item = new BalloonOverlayItem(punto, direccion.getAddressLine(0), null, -1);

        mOverlays.add(item);
        populate();
    }




    /**
     * Se utiliza cuando se cargan los lugares desde BD
     *
     * @param lat           - Latitud
     * @param lon           - Longitud
     * @param etiqueta1     - Direccion
     * @param etiqueta2     - Detalle Direccion
     * @param id            - Id
     */
    public void addLocalizacion(double lat, double lon, String etiqueta1, String etiqueta2, int id) {
        int lt = (int) (lat);
        int ln = (int) (lon);

        GeoPoint punto = new GeoPoint(lt, ln);
        item = new BalloonOverlayItem(punto, etiqueta1, etiqueta2, id);
        mOverlays.add(item);
        populate();
    }


    public void addLocalizacion(GeoPoint punto, String etiqueta1, String etiqueta2) {
        item = new BalloonOverlayItem(punto, etiqueta1, etiqueta2, -1);
        mOverlays.add(item);
        populate();
    }


    @Override
    protected BalloonOverlayItem createItem(int i) {
        return mOverlays.get(i);

    }

    @Override
    public int size() {
        return mOverlays.size();
    }

}


