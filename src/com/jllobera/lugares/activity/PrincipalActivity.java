package com.jllobera.lugares.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.jllobera.lugares.R;

/**
 * Actividad principal, ejerce como menú para acceder al mapa y a la lista de lugares
 * <p/>
 * * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 26/08/11
 * Time: 20:35
 */
public class PrincipalActivity extends Activity {
    private static final int DIALOG_ALERT = 1;
    private static final int DIALOG_INFO = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button listaLugares = (Button) findViewById(R.id.BTListaLugares);
        Button MapaLugares = (Button) findViewById(R.id.BTMapaLugares);

        TextView tv = (TextView) findViewById(R.id.TVTitulo);
        //Cambiamos la fuente del título
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        tv.setTypeface(face);

        listaLugares.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ListaLugaresActivity.class);
                startActivity(intent);
                finish();
            }
        });

        MapaLugares.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
//                intent.setClass(getApplicationContext(), EditarLugarActivity.class);
                intent.setClass(getApplicationContext(), MapaLugaresActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Dialog dialo;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // Si el valor de onCreateDialog es 1 se muestra el diálogo para salir
                // En el caso que el valor sea 2 se muestra un diálogo de espera
                dialo = onCreateDialog(DIALOG_ALERT);
                dialo.show();
                return true;

            case KeyEvent.KEYCODE_MENU:
                dialo = onCreateDialog(DIALOG_INFO);
                dialo.show();
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
            case DIALOG_ALERT:
                builder.setMessage(R.string.salir);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                         finish();
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

            case DIALOG_INFO:
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.info, (ViewGroup) findViewById(R.id.layout_root));
                layout.startAnimation(animShow);

                builder.setNeutralButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        layout.startAnimation(animHide);
                    }
                });
                builder.setView(layout);
                newDialog = builder.create();

                break;

        }

        return newDialog;

    }


}