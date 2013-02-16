package com.jllobera.lugares.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.maps.*;

import com.jllobera.lugares.R;
import com.jllobera.lugares.classes.LugaresDirecciones;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;
import com.jllobera.lugares.map.BalloonOverlayItem;
import com.jllobera.lugares.map.LugaresLocationListener;
import com.jllobera.lugares.map.MapItemizedOverlay;

import java.io.IOException;
import java.util.List;

import static android.view.View.OnClickListener;
import static android.view.View.OnKeyListener;

/**
 * Activity basada en el mapa de google que muestra los diferentes lugares marcados en el mapa
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 26/08/11
 * Time: 20:35
 */
public class MapaLugaresActivity extends MapActivity {
    private static final int DIALOG_INFO = 2;
    private MapView mapa;
    private MapController mapController;
    private MapItemizedOverlay itemizedOverlay, itemizedOverlayBusqueda;
    private List<Overlay> mapOverlays;
    public BalloonOverlayItem item;
    private EditText direccion;
    public MapItemizedOverlay localizacion;

    MapItemizedOverlay nue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapalugar);

        //Asignamos las propiedades del mapa
        mapa = (MapView) findViewById(R.id.mapa);
        mapa.setClickable(true);
        mapa.displayZoomControls(true);
        mapa.setBuiltInZoomControls(true);
        mapController = mapa.getController();
        mapController.setZoom(13);
        mapa.setSatellite(true);
        mapOverlays = mapa.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.chinchetaazul);
        itemizedOverlay = new MapItemizedOverlay(getApplicationContext(), drawable, mapa);

        //Buscador
        Button buscar = (Button) findViewById(R.id.BTbuscar);
        buscar.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                buscar();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(direccion.getWindowToken(), 0);
            }
        });

        //Alterna los modos del mapa
        Button toggle = (Button) findViewById(R.id.BTtogglemap);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapa.isSatellite()) {
                    mapa.setSatellite(false);
                } else {
                    mapa.setSatellite(true);
                }

            }
        });

        //Centra la localizaci�n actual
        Button miLocalizacion = (Button) findViewById(R.id.BTmilocalizacion);
        miLocalizacion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CentrarPosicion();
            }
        });

        direccion = (EditText) findViewById(R.id.direccion);

        direccion.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        buscar();
                        return true;
                }
                return false;

            }
        });

        //Obtiene la localizaci�n actual
        obtenerPosicion();

        //Recuperamos todos los lugares que tenemos almacenados en la BD
        Uri uri = LugaresDB.CONTENT_URI;
        LugaresConstants constants = new LugaresConstants();
        Cursor cursor = managedQuery(uri, constants.todasColumnas, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                int valorNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
                int valorDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);
                int valorLat = cursor.getColumnIndex(LugaresDB.Lugares.LATITUD);
                int valorLon = cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD);

                itemizedOverlay.addLocalizacion(cursor.getFloat(valorLat), cursor.getFloat(valorLon), cursor.getString(valorNombre), cursor.getString(valorDescripcion), cursor.getInt(valorID));

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        if (itemizedOverlay.size() > 0) {
            mapOverlays.add(itemizedOverlay);

            //Si existe un ID centramos el mapa en base a �l
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int id = extras.getInt("id");
                int currentItem = -1;
                if (id != -1) {
                    for (int i = 0; i < itemizedOverlay.size(); i++) {
                        if (itemizedOverlay.getItem(i).getId() == id) {
                            currentItem = i;
                        }
                    }
                    if (currentItem != -1) {
                        GeoPoint point = itemizedOverlay.getItem(currentItem).getPoint();
                        mapController.setCenter(point);
                    }
                }
            }
        }


    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    /*
     * Partiendo de un texto busca direcciones geogr�ficas que coincidan con esa direcci�n
     */
    protected void buscar() {
        Geocoder geo = new Geocoder(MapaLugaresActivity.this);
        List<Address> resultados;

        itemizedOverlayBusqueda = new MapItemizedOverlay(getApplicationContext(), getResources().getDrawable(R.drawable.chincheta_add), mapa);
        try {
            resultados = geo.getFromLocationName("" + direccion.getText(), 10);

            for (Address direccion : resultados) {
                itemizedOverlayBusqueda.addLocalizacion(direccion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Finalmente limpiamos el mapa y volvemos a dibujar con los resultados
            mapOverlays = mapa.getOverlays();
            mapOverlays.clear();

            //En el caso que hubiera una b�squeda anterior, la reemplazamos por la nueva
            if (nue != null && nue.size() > 0) {
                nue.hideBalloon();
                if (itemizedOverlay.mOverlays.containsAll(nue.mOverlays)) {
                    itemizedOverlay.mOverlays.removeAll(nue.mOverlays);
                }
            }
            nue = itemizedOverlayBusqueda;

            Drawable add = this.getResources().getDrawable(R.drawable.chincheta_add);

            if (itemizedOverlayBusqueda.size() > 0) {
                for (BalloonOverlayItem over : itemizedOverlayBusqueda.mOverlays) {
                    nue.miBoundCenterBottom(add);
                    over.setMarker(add);
                    itemizedOverlay.mOverlays.add(over);
                }
                itemizedOverlay.miPopulate();
            }

            mapOverlays.add(itemizedOverlay);


            animarMapa();
        }

    }

    protected void animarMapa() {
        super.onStart();
        // Animamos el mapa de punto a punto
        int n = itemizedOverlayBusqueda.size();

        for (int x = 0; x < n; x++) {
            BalloonOverlayItem hito = itemizedOverlayBusqueda.getItem(x);
            mapController.animateTo(hito.getPoint());
        }
    }


    public void obtenerPosicion() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Criteria criteria = new Criteria();
        criteria.getAccuracy();
        criteria.isCostAllowed();
        criteria.isSpeedRequired();
        String bestPRovider = lm.getBestProvider(criteria, true);

        LugaresLocationListener lll = new LugaresLocationListener(getApplicationContext(), mapController);
        lm.requestLocationUpdates(bestPRovider, 0, 0, lll);

        Location location = lm.getLastKnownLocation(bestPRovider);


        LugaresDirecciones ld;
        try {
            String dirDes = "";
            String dir = "";
            if (net) {
                ld = new LugaresDirecciones(getApplicationContext(), location.getLatitude(), location.getLongitude());
                dir = ld.getDireccion();
                dirDes = ld.getDireccionDes();
            }


            if (localizacion != null) {
                if (!itemizedOverlay.mOverlays.containsAll(localizacion.mOverlays)) {

                }
            }

            itemizedOverlay.addLocalizacion(location.getLatitude() * 1e6, location.getLongitude() * 1e6, dir, dirDes, -1);
            localizacion = itemizedOverlay;

            mapController.setZoom(13);
            mapController.setCenter(localizacion.getCenter());

            Drawable drawable = getResources().getDrawable(R.drawable.scrubber_control_disabled_holo);
            localizacion.miBoundCenterBottom(drawable);
            localizacion.mOverlays.get(0).setMarker(drawable);

            mapOverlays.add(localizacion);


            lm.removeUpdates(lll);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void CentrarPosicion() {
        mapController.animateTo(localizacion.getCenter());
    }


    //Controlamos la acci�n los botones b�sicos de android
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Dialog dialog;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                startActivity(new Intent(getApplicationContext(), PrincipalActivity.class));
                finish();
                return true;

            case KeyEvent.KEYCODE_MENU:
                dialog = onCreateDialog(DIALOG_INFO);
                dialog.show();
                return true;

        }

        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog newDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Animation animShow = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        final Animation animHide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        switch (id) {
            case DIALOG_INFO:
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.infomapa, (ViewGroup) findViewById(R.id.layout_root));
                layout.startAnimation(animShow);

                builder.setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        layout.startAnimation(animHide);
                    }
                });
                builder.setTitle(R.string.TituloInfoMapa);
                builder.setView(layout);
                newDialog = builder.create();

                break;

        }

        return newDialog;

    }


}