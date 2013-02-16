package com.jllobera.lugares.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.*;
import com.jllobera.lugares.R;
import com.jllobera.lugares.classes.ActivitySwipeDetector;
import com.jllobera.lugares.classes.LugaresObj;
import com.jllobera.lugares.classes.PreCarga;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;
import com.jllobera.lugares.map.MapItemizedOverlay;

import java.util.ArrayList;
import java.util.List;

/**
 * Muestra la ficha en formato de sólo lectura
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 26/08/11
 * Time: 20:35
 */


/**
 * Extendemos de un MapActivity debido a que queremos un frame con un mapa que muestre la ubicación en la ficha
 */
public class MostrarLugarActivity extends MapActivity {
    private static final int DIALOG_INFO = 2;
    int ID;
    Uri fotoUri;
    Context context = this;
    LugaresConstants constants = new LugaresConstants();
    String extraTitulo, extraDetalle;
    float extraLon = 0, extraLat = 0;
    String extraDireccion = null, extraFoto;
    Cursor cursor;
    ArrayList<LugaresObj> lugares = new ArrayList<LugaresObj>();

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrarlugar);


        //recuperamos los elementos de la plantilla
        Button BTEditar = (Button) findViewById(R.id.BTEditarLugar);
        Button BTMapa = (Button) findViewById(R.id.BTVerMapa);
        TextView TVNombre = (TextView) findViewById(R.id.TVNombre);
        TextView TVDireccion = (TextView) findViewById(R.id.TVDireccion);
        TextView TVDetalle = (TextView) findViewById(R.id.TVDetalle);
        TextView TVDescripcion = (TextView) findViewById(R.id.TVDescripcion);
        ImageView IVFoto = (ImageView) findViewById(R.id.IVFoto);


        PreCarga preCarga = new PreCarga(context);
        preCarga.execute();
        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
        LinearLayout lowestLayout = (LinearLayout) this.findViewById(R.id.MostrarLugar);
        lowestLayout.setOnTouchListener(activitySwipeDetector);


        //Datos para el mapa
        MapView mapa = (MapView) findViewById(R.id.FLMapView);
        mapa.displayZoomControls(true);
        mapa.setBuiltInZoomControls(true);
        MapController mapController = mapa.getController();
        mapController.setZoom(16);
        mapa.setSatellite(false);
        mapa.setClickable(false);


        List<Overlay> mapOverlays = mapa.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.chinchetaazul);
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(context, drawable, mapa);


        //Recuperamos el ID del item seleccionado
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get("id") == null || extras.getInt("id") == -1) {
                extraTitulo = extras.getString("titulo");
                extraLat = extras.getInt("lat");
                extraLon = extras.getInt("lon");
                extraDireccion = extras.getString("direccion");
                extraDetalle = extras.getString("detalle");

                extraFoto = constants.defImg;

            } else {
                ID = extras.getInt("id");
            }


            //Lanzamos la query para recoger la información del lugar
            if (extras.get("id") != null && extras.getInt("id") != -1) {
                LugaresConstants constants = new LugaresConstants();
                cursor = managedQuery(LugaresDB.CONTENT_URI, constants.todasColumnas, LugaresDB.Lugares._ID + "=" + ID, null, null);

                //Recuperamos los valores desde la BD

                if (cursor != null) {
                    cursor.moveToFirst();
                    int valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                    int valorNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
                    int valorDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);
                    int valorDireccion = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCION);
                    int valorDireccionDet = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCIONDET);
                    int valorLat = cursor.getColumnIndex(LugaresDB.Lugares.LATITUD);
                    int valorLon = cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD);
                    int valorFoto = cursor.getColumnIndex(LugaresDB.Lugares.FOTO);
                    int valorFotoThumb = cursor.getColumnIndex(LugaresDB.Lugares.FOTO_THUMBNAIL);

                    lugares.add(new LugaresObj(cursor.getInt(valorID), cursor.getString(valorNombre),
                            cursor.getString(valorDescripcion), cursor.getString(valorDireccion),
                            cursor.getString(valorDireccionDet), cursor.getString(valorFoto),
                            cursor.getString(valorFotoThumb),
                            cursor.getFloat(valorLat), cursor.getFloat(valorLon)));

                    itemizedOverlay.addLocalizacion(cursor.getFloat(valorLat), cursor.getFloat(valorLon),
                            cursor.getString(valorDireccion), cursor.getString(valorDireccionDet),
                            valorID);
                }

                //Si hay un cursor abierto, se cierra
                if (cursor != null) {
                    cursor.close();
                }
            } else {

                lugares.add(new LugaresObj(extraDireccion, extraDetalle, extraLat, extraLon));
                itemizedOverlay.addLocalizacion(extraLat, extraLon, extraDireccion, extraDetalle, -1);

            }
        }
        //Como sólo tenemos un elemento, el índice siempre será 0
        //Empezamos a añadir los valores a la ficha
        TVNombre.setText(lugares.get(0).getNombre());
        TVDireccion.setText(lugares.get(0).getDireccion());
        TVDetalle.setText(lugares.get(0).getDireccionDetalle());
        TVDescripcion.setText(lugares.get(0).getDescripcion());

        if (lugares.get(0).getFoto() != null) {
            fotoUri = Uri.parse(lugares.get(0).getFotoThumb());
        } else {
            fotoUri = Uri.parse(constants.defImg);
        }
        IVFoto.setImageURI(fotoUri);

        mapOverlays.clear();
        mapOverlays.add(itemizedOverlay);
        GeoPoint punto = new GeoPoint((int) lugares.get(0).getLat(), (int) lugares.get(0).getLon());
        mapController.animateTo(punto);
        mapController.setZoom(14);

        //Botones
        BTEditar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditarLugarActivity.class);
                //pasamos los campos que vamos a editar a la siguiente activity (EditarLugarActivity)
                intent.putExtra("id", ID);
                startActivityForResult(intent, 100);
                finish();


            }
        });


        BTMapa.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MapaLugaresActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", ID);
                intent.putExtras(bundle);
                context.startActivity(intent);
                finish();

            }
        });


    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Dialog dialog;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                startActivity(new Intent(context, PrincipalActivity.class));
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
                final View layout = inflater.inflate(R.layout.infomostrarlugar, (ViewGroup) findViewById(R.id.layout_root));
                layout.startAnimation(animShow);

                builder.setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        layout.startAnimation(animHide);
                    }
                });
                builder.setTitle(R.string.TituloInfoMostrar);
                builder.setView(layout);
                newDialog = builder.create();


                break;

        }

        return newDialog;

    }


}
