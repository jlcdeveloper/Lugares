package com.jllobera.lugares.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.google.android.maps.GeoPoint;
import com.jllobera.lugares.R;
import com.jllobera.lugares.activity.EditarLugarActivity;
import com.jllobera.lugares.activity.MapaLugaresActivity;
import com.jllobera.lugares.activity.MostrarLugarActivity;
import com.jllobera.lugares.db.LugaresDB;

public class BalloonOverlayView<Item extends BalloonOverlayItem> extends FrameLayout {
    private static final int DIALOG_ElIMINAR = 1;
    private LinearLayout layout;
    private TextView title;
    private TextView snippet;
    private ImageButton editar;
    private ImageButton abrir;
    private ImageButton eliminar;
    private int id;
    private GeoPoint geo;
    private String direccion;
    private String detalle;


    /**
     * Crea un nuevo BalloonOverlayView.
     *
     * @param context             - Contexto .
     * @param balloonBottomOffset - Distancia en pixels
     */
    public BalloonOverlayView(final Context context, int balloonBottomOffset) {

        super(context);
        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        //Infla la burbuja para pintar el contenido
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_overlay, layout);
        title = (TextView) v.findViewById(R.id.balloon_item_title);
        snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
        editar = (ImageButton) v.findViewById(R.id.BTEditar);
        abrir = (ImageButton) v.findViewById(R.id.BTAbrir);
        eliminar = (ImageButton) v.findViewById(R.id.BTEliminar);

        ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                layout.setVisibility(GONE);
            }
        });

        editar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //pasamos los campos que vamos a editar a la siguiente activity (EditarLugarActivity)
                Intent intent = new Intent(context, EditarLugarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("lat", geo.getLatitudeE6());
                bundle.putInt("lon", geo.getLongitudeE6());
                bundle.putString("direccion", direccion);
                bundle.putString("detalle", detalle);
                intent.putExtras(bundle);
                intent.setClass(context, EditarLugarActivity.class);
                context.startActivity(intent);

            }
        });

        abrir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //pasamos los campos que vamos a editar a la siguiente activity (EditarLugarActivity)
                Intent intent = new Intent(context, EditarLugarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("lat", geo.getLatitudeE6());
                bundle.putInt("lon", geo.getLongitudeE6());
                bundle.putString("direccion", direccion);
                bundle.putString("detalle", detalle);
                intent.putExtras(bundle);
                intent.setClass(context, MostrarLugarActivity.class);
                context.startActivity(intent);
            }
        });
        eliminar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialo = onCreateDialog(DIALOG_ElIMINAR, context, id);
                dialo.show();
            }
        });


        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;

        addView(layout, params);

    }

    /**
     * Establece la vista de datos desde un punto dado
     *
     * @param item - El Overlay item que contiene la información
     */
    public void setData(BalloonOverlayItem item) {

        layout.setVisibility(VISIBLE);
        if (item.getTitle() != null) {
            title.setVisibility(VISIBLE);
            title.setText(item.getTitle());
        } else {
            title.setVisibility(GONE);
        }
        if (item.getSnippet() != null) {
            snippet.setVisibility(VISIBLE);
            snippet.setText(item.getSnippet());
        } else {
            snippet.setVisibility(GONE);
        }

        if (item.getId() == -1) {
            abrir.setVisibility(GONE);
            eliminar.setVisibility(GONE);
            editar.setBackgroundResource(R.drawable.ic_addficha);
        }


        //Se recupera el id del elemento
        id = item.getId();
        geo = item.getGeoPoint();
        direccion = item.getDireccion();
        detalle = item.getDetalle();

    }


    protected Dialog onCreateDialog(int dialogo, final Context context, final int id) {
        Dialog newDialog = null;
        switch (dialogo) {
            case DIALOG_ElIMINAR:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.EliminarLugar);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int dialogo) {

                        Uri uri = LugaresDB.CONTENT_URI;
                        int affectedRows = context.getContentResolver().delete(uri, LugaresDB.Lugares._ID + "=" + id, null);

                        if (affectedRows > 0) {
                            Toast.makeText(context, "El elemento se ha eliminado correctamente", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, MapaLugaresActivity.class);
                            context.startActivity(intent);
                            //TODO: Poner un finish
                        } else {
                            Toast.makeText(context, "Ha habido un problema a la hora de eliminar el elemento", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                newDialog = builder.create();
                break;

        }

        return newDialog;

    }

}
