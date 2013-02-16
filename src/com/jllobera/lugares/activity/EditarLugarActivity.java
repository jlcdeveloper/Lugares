package com.jllobera.lugares.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.google.android.maps.*;

import com.jllobera.lugares.R;
import com.jllobera.lugares.classes.LugaresDirecciones;
import com.jllobera.lugares.classes.LugaresObj;
import com.jllobera.lugares.classes.PreCarga;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;
import com.jllobera.lugares.map.MapItemizedOverlay;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * Activity que permite editar los campos de las fichas guardadas y crear una ficha con
 * datos nuevos.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 26/08/11
 * Time: 20:36
 */

public class EditarLugarActivity extends MapActivity {
    private static final int DIALOG_INFO = 2;
    int ID;
    int FOTO = 100;
    int affectedRows;
    float extraLon = 0, extraLat = 0;
    String extraTitulo, extraDetalle;
    String src;
    Cursor cursor;
    Bundle extras;
    Context context;
    ImageView IVFoto;
    String extraDireccion = null, extraFoto;
    LugaresConstants constants = new LugaresConstants();
    ArrayList<LugaresObj> lugares = new ArrayList<LugaresObj>();
    LugaresDirecciones ld = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarlugar);

        context = this;
        //Recogemos los valores que llegan desde MostrarLugarActivity
        extras = getIntent().getExtras();
        int valorID;
        int valorNombre;
        int valorDescripcion;
        int valorFoto, valorFotoThumb;
        int valorLat;
        int valorLon;

        //Elementos de la plantilla
        final EditText ETNombre = (EditText) findViewById(R.id.ETNombre);
        final TextView TVDireccion = (TextView) findViewById(R.id.TVDireccion);
        final TextView TVDetalle = (TextView) findViewById(R.id.TVDetalle);
        final EditText ETDescripcion = (EditText) findViewById(R.id.ETDescripcion);
        Button btGuardar = (Button) findViewById(R.id.BTGuardar);
        Button btEliminar = (Button) findViewById(R.id.BTEliminar);
        MapView mapa = (MapView) findViewById(R.id.FLMapView);

        if (extras.get("id") == null || extras.getInt("id") == -1) {
            btEliminar.setVisibility(View.INVISIBLE);
            btGuardar.setText(R.string.Crear);
        }

        IVFoto = (ImageView) findViewById(R.id.IVFoto);

        PreCarga preCarga = new PreCarga(context);
        preCarga.execute();

        MapController mapController = mapa.getController();
        mapa.displayZoomControls(true);
        mapa.setBuiltInZoomControls(true);
        mapa.setSatellite(false);
        mapController.setZoom(16);
        mapa.setClickable(false);
        List<Overlay> mapOverlays = mapa.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.chinchetaazul);
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(context, drawable, mapa);

        //Si llega un id diferente a -1, es que estamos tratando un elemento existente
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

            if (extras.get("id") != null && extras.getInt("id") != -1) {
                LugaresConstants constants = new LugaresConstants();
                cursor = managedQuery(LugaresDB.CONTENT_URI, constants.todasColumnas, LugaresDB.Lugares._ID + "=" + ID, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                    valorNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
                    valorDescripcion = cursor.getColumnIndex(LugaresDB.Lugares.DESCRIPCION);
                    int valorDireccion = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCION);
                    int valorDireccionDet = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCIONDET);
                    valorFoto = cursor.getColumnIndex(LugaresDB.Lugares.FOTO);
                    valorFotoThumb = cursor.getColumnIndex(LugaresDB.Lugares.FOTO_THUMBNAIL);
                    valorLat = cursor.getColumnIndex(LugaresDB.Lugares.LATITUD);
                    valorLon = cursor.getColumnIndex(LugaresDB.Lugares.LONGITUD);

                    lugares.add(new LugaresObj(cursor.getInt(valorID), cursor.getString(valorNombre),
                            cursor.getString(valorDescripcion), cursor.getString(valorDireccion),
                            cursor.getString(valorDireccionDet), cursor.getString(valorFoto),
                            cursor.getString(valorFotoThumb),
                            cursor.getLong(valorLat), cursor.getLong(valorLon)));

                    itemizedOverlay.addLocalizacion(cursor.getFloat(valorLat), cursor.getFloat(valorLon),
                            cursor.getString(valorDireccion), cursor.getString(valorDireccionDet),
                            valorID);

                    if (lugares.get(0).getFotoThumb() != null) {
                        IVFoto.setImageURI(Uri.parse(lugares.get(0).getFotoThumb()));
                    }


                }
                //Si hay un cursor abierto, se cierra
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                lugares.add(new LugaresObj(extraDireccion, extraDireccion, extraDetalle, extraLat, extraLon));
                itemizedOverlay.addLocalizacion(extraLat, extraLon, extraDireccion, extraDetalle, -1);


            }
        }
        mapOverlays.clear();
        mapOverlays.add(itemizedOverlay);
        //GeoPoint punto = new GeoPoint((int) lugares.get(0).getLat(), (int) lugares.get(0).getLon());

        if (lugares.size() > 0) {
            extraLat = lugares.get(0).getLat();
            extraLon = lugares.get(0).getLon();
        }
        GeoPoint punto = new GeoPoint((int) extraLat, (int) extraLon);
        mapController.animateTo(punto);
        mapController.setZoom(14);
        mapController.setCenter(punto);

        if (ld != null) {
            extraDireccion = ld.getDireccion();
            extraDetalle = ld.getDireccionDes();
        }

        //Rellenamos los campos
        ETNombre.setText(lugares.size() > 0 ? lugares.get(0).getNombre() : extraDireccion);
        TVDireccion.setText((lugares.size() > 0 && lugares.get(0).getDireccion() != null) ? lugares.get(0).getDireccion() : extraDireccion);
        TVDetalle.setText((lugares.size() > 0 && lugares.get(0).getDireccionDetalle() != null) ? lugares.get(0).getDireccionDetalle() : extraDetalle);
        ETDescripcion.setText(lugares.size() > 0 ? lugares.get(0).getDescripcion() : null);


        //LISTENERS
        btGuardar.setOnClickListener(new View.OnClickListener() {
            Uri uri;

            public void onClick(View v) {
                String nombre = ETNombre.getText().toString();
                String descripcion = ETDescripcion.getText().toString();
                String direccion = TVDireccion.getText().toString();
                String direccionDet = TVDetalle.getText().toString();
                //ImageView foto = IVFoto.get
                //String foto = "a";
                //La latitud y la longitud tambi�n hay que guardarla, se le pasa desde el extra

                if (extras.getInt("id") == -1) {
                    uri = insertar(nombre, descripcion, direccion, direccionDet, extraLat, extraLon);
                    if (uri != null) {
                        ID = parseInt(uri.getLastPathSegment());
                    }
                } else {
                    affectedRows = guardar(ID, nombre, descripcion, direccion, direccionDet, lugares.get(0).getLat(), lugares.get(0).getLon());
                }

                if (affectedRows > 0 || uri != null) {
                    Toast.makeText(v.getContext(), R.string.guardadoOK, Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MostrarLugarActivity.class);
                intent.putExtra("id", ID);
                startActivityForResult(intent, 100);
                finish();
            }
        });


        btEliminar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Uri uri = LugaresDB.CONTENT_URI;
                ContentValues contentValues = new ContentValues();
                contentValues.put(LugaresDB.Lugares._ID, ID);

                int affectedRows = getContentResolver().delete(uri, LugaresDB.Lugares._ID + "=" + ID, null);

                if (affectedRows > 0) {
                    Toast.makeText(getApplicationContext(), R.string.eliminadoOK, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ListaLugaresActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.eliminadoKO, Toast.LENGTH_LONG).show();

                }


            }
        });

        IVFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Si no se ha guardado la ficha, no deber�a poder cambiarse la imagen ya que no estar�a vinculada
                //a ninguna ficha
                if (extras.getInt("id") == -1) {
                    final CharSequence cam = getString(R.string.camara);
                    final CharSequence gal = getString(R.string.galeria);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.AntesGuardar)
                            .setCancelable(true)
                            .setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }

                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    final CharSequence cam = getString(R.string.camara);
                    final CharSequence gal = getString(R.string.galeria);
                    final CharSequence[] items = {cam, gal};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            builder.setTitle(getString(R.string.menutituloimg));
                            try {
                                if (items[item].equals(cam)) {
                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                                    src = "cam";
                                    i.putExtra("output", uri);
                                    startActivityForResult(intent, FOTO);
                                } else if (items[item].equals(gal)) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    src = "gal";
                                    startActivityForResult(intent, FOTO);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent respuesta) {
        super.onActivityResult(requestCode, resultCode, respuesta);
        String uriThumb = null;
        PreCarga preCarga = new PreCarga(context);
        preCarga.execute();
        if (resultCode == Activity.RESULT_OK && requestCode == FOTO) {
            IVFoto = (ImageView) findViewById(R.id.IVFoto);

            if ("cam".equals(src)) {
                extras = respuesta.getExtras();
                Bitmap bm = (Bitmap) respuesta.getExtras().get("data");

                String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bm, "" + System.currentTimeMillis(), "");
                //imageUri ser� la uri de la imagen que vamos a obtener el thumb
                Uri imageUri = Uri.parse(uri);
                //Obtenemos el �ltimo segmento de la uri que es el que nos indica el ID
                long uriThumbId = Long.parseLong(imageUri.getLastPathSegment());
                //M�s info sobre esta funci�n en
                // http://developer.android.com/reference/android/provider/MediaStore.Images.Thumbnails.html
                Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                        getContentResolver(), uriThumbId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null);
                //Comprobamos que no tengamos un cursor nulo y que hayamos obtenido almenos un resultado
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst(); //Como en este caso s�lo nos interesa una imagen vamos al primer registro

                    //cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
                    //Obtiene el �ndice de la columna que queremos obtener el valor, en nuestro caso obtenemos el campo data
                    //cursor.getString()
                    //Obtenemos el valor de la columna seleccionada
                    uriThumb = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                }

                if (uriThumb != null) {
                    IVFoto.setImageURI(Uri.parse(uriThumb));
                    guardar(ID, uri, uriThumb);
                }

            } else if ("gal".equals(src)) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(respuesta.getData(), projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    IVFoto.setImageBitmap(bm);

                    Uri imageThumbUri = respuesta.getData();
                    long uriThumbId = Long.parseLong(imageThumbUri.getLastPathSegment());

                    Cursor cursorThumb = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                            getContentResolver(), uriThumbId,
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null);
                    if (cursorThumb != null && cursorThumb.getCount() > 0) {
                        cursorThumb.moveToFirst();
                        uriThumb = cursorThumb.getString(cursorThumb.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    }


                    guardar(ID, respuesta.getDataString(), uriThumb);
                }
            }
        }
    }

    protected int guardar(int ID, String nombre, String descripcion, String direccion, String direccionDet, float lat, float lon) {
        Uri uri = LugaresDB.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(LugaresDB.Lugares._ID, ID);
        contentValues.put(LugaresDB.Lugares.NOMBRE, nombre);
        contentValues.put(LugaresDB.Lugares.DESCRIPCION, descripcion);
        contentValues.put(LugaresDB.Lugares.DIRECCION, direccion);
        contentValues.put(LugaresDB.Lugares.DIRECCIONDET, direccionDet);
        contentValues.put(LugaresDB.Lugares.LATITUD, lat);
        contentValues.put(LugaresDB.Lugares.LONGITUD, lon);

        return getContentResolver().update(uri, contentValues, LugaresDB.Lugares._ID + "=" + ID, null);


    }

    protected int guardar(int ID, String foto, String fotoThumb) {
        Uri uri = LugaresDB.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(LugaresDB.Lugares._ID, ID);
        contentValues.put(LugaresDB.Lugares.FOTO, foto);
        contentValues.put(LugaresDB.Lugares.FOTO_THUMBNAIL, fotoThumb);

        return getContentResolver().update(uri, contentValues, LugaresDB.Lugares._ID + "=" + ID, null);
    }


    protected Uri insertar(String nombre, String descripcion, String direccion, String direccionDet, float lat, float lon) {
        Uri uri = LugaresDB.CONTENT_URI;
        ContentValues contentValues = new ContentValues();

        contentValues.put(LugaresDB.Lugares.NOMBRE, nombre);
        contentValues.put(LugaresDB.Lugares.DESCRIPCION, descripcion);
        contentValues.put(LugaresDB.Lugares.DIRECCION, direccion);
        contentValues.put(LugaresDB.Lugares.DIRECCIONDET, direccionDet);
        contentValues.put(LugaresDB.Lugares.LATITUD, lat);
        contentValues.put(LugaresDB.Lugares.LONGITUD, lon);

        return getContentResolver().insert(uri, contentValues);


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
                final View layout = inflater.inflate(R.layout.infoeditarlugar, (ViewGroup) findViewById(R.id.layout_root));
                layout.startAnimation(animShow);

                builder.setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        layout.startAnimation(animHide);
                    }
                });
                builder.setTitle(R.string.TituloInfoEditar);
                builder.setView(layout);
                newDialog = builder.create();

                break;

        }

        return newDialog;

    }


}
