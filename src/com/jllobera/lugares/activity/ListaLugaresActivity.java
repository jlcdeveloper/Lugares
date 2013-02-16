package com.jllobera.lugares.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import com.jllobera.lugares.R;
import com.jllobera.lugares.adapter.LugaresExpandableListAdapter;
import com.jllobera.lugares.classes.LugaresObj;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;

import java.util.ArrayList;

/**
 * Activity que lista los diferentes lugares guardados en la BD
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 25/08/11
 * Time: 1:56
 */


public class ListaLugaresActivity extends Activity {
    private static final int DIALOG_INFO = 2;
    LugaresExpandableListAdapter adapter;
    Context context;
    int lastExpandedGroupPosition = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lugaresexpanlist);
        context = this;

        Uri uri = LugaresDB.CONTENT_URI;
        LugaresConstants constants = new LugaresConstants();
        ArrayList<LugaresObj> lugares = new ArrayList<LugaresObj>();

        Cursor cursor = managedQuery(uri, constants.ColumnasListado, null, null, null);

        final ExpandableListView elv = (ExpandableListView) findViewById(android.R.id.list);
        elv.setGroupIndicator(null);


        if (cursor != null) {
            while (cursor.moveToNext()) {
                int valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                int valorNombre = cursor.getColumnIndex(LugaresDB.Lugares.NOMBRE);
                int valorDireccion = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCION);
                int valorDireccionDet = cursor.getColumnIndex(LugaresDB.Lugares.DIRECCIONDET);
                int valorFotoThumb = cursor.getColumnIndex(LugaresDB.Lugares.FOTO_THUMBNAIL);
                lugares.add(new LugaresObj(cursor.getInt(valorID), cursor.getString(valorNombre), cursor.getString(valorDireccion), cursor.getString(valorDireccionDet), cursor.getString(valorFotoThumb)));
            }
        }

        //Si hay un cursor abierto, se cierra
        if (cursor != null) {
            cursor.close();
        }
        //Añadimos un objeto ExpandableList para mostrar los elementos
        adapter = new LugaresExpandableListAdapter(this, new ArrayList<LugaresObj>(), new ArrayList<ArrayList<LugaresObj>>());

        //En caso que no haya elementos se mostrará un mensaje de lista vacía
        if (adapter.isEmpty()) {
            View emptyView = findViewById(android.R.id.empty);
            if (emptyView != null) {
                elv.setEmptyView(emptyView);
            }
        }

        for (LugaresObj lugar : lugares) {
            LugaresObj lug = new LugaresObj(lugar.getID(), lugar.getNombre(), lugar.getDireccion(), lugar.getDireccionDetalle(), lugar.getFotoThumb());
            lug.setGroup(lug);
            adapter.addItem(lug);
        }

        elv.setAdapter(adapter);
        elv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != lastExpandedGroupPosition) {
                    elv.collapseGroup(lastExpandedGroupPosition);
                }
                lastExpandedGroupPosition = groupPosition;

            }
        });


        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                return false;
            }
        });

        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
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
