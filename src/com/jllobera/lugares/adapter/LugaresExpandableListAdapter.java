package com.jllobera.lugares.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.jllobera.lugares.R;
import com.jllobera.lugares.activity.EditarLugarActivity;
import com.jllobera.lugares.activity.ListaLugaresActivity;
import com.jllobera.lugares.activity.MapaLugaresActivity;
import com.jllobera.lugares.activity.MostrarLugarActivity;
import com.jllobera.lugares.classes.LugaresObj;
import com.jllobera.lugares.classes.PreCarga;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;

import java.util.ArrayList;

/**
 * Creamos nuestro propio ExpandableListAdapter
 * 
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 27/09/11
 * Time: 20:43
 */
public class LugaresExpandableListAdapter extends BaseExpandableListAdapter {
    private static final int DIALOG_ElIMINAR = 1;

    public ArrayList<LugaresObj> groups;
    public ArrayList<ArrayList<LugaresObj>> children;
    private Context context;
    public int grupoSel;
    public LugaresConstants lc = new LugaresConstants();

    public LugaresExpandableListAdapter(Context context, ArrayList<LugaresObj> groups,
                                        ArrayList<ArrayList<LugaresObj>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    public void addItem(LugaresObj lugar) {
        //Si no contiene grupo, lo asignará al elemento en cuestión
        if (!groups.contains(lugar.getGroup())) {
            groups.add(lugar.getGroup());
        }
        int index = groups.indexOf(lugar.getGroup());
        if (children.size() < index + 1) {
            children.add(new ArrayList<LugaresObj>());
        }
        children.get(index).add(lugar);
    }


    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LugaresObj grupo = (LugaresObj) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lugaresadapter, null);
        }

        TextView TVNombre = (TextView) convertView.findViewById(R.id.idNombre);
        TVNombre.setText(grupo.getNombre());
        ImageView IVFoto = (ImageView) convertView.findViewById(R.id.idFoto);
        TextView TVDir = (TextView) convertView.findViewById(R.id.idDireccion);
        TextView TVDesc = (TextView) convertView.findViewById(R.id.idDescripcionDir);

        TVDir.setText(grupo.getDireccion());
        TVDesc.setText(grupo.getDireccionDetalle());


        try {
            if (grupo.getFotoThumb() != null) {
                Uri uri = Uri.parse(grupo.getFotoThumb());
                //Uri uriThumb = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, uri.getLastPathSegment());
                IVFoto.setImageURI(uri);
            } else {
                IVFoto.setImageURI(Uri.parse(lc.defImg));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return convertView;

    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        grupoSel = groupPosition;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listaacciones, null);
        }

        Animation animation = AnimationUtils.loadAnimation(convertView.getContext(), android.R.anim.fade_in);

        animation.setDuration(500);
        convertView.startAnimation(animation);

        ImageButton abrirFicha = (ImageButton) convertView.findViewById(R.id.BTAbrir);
        ImageButton editarFicha = (ImageButton) convertView.findViewById(R.id.BTEditar);
        ImageButton eliminarFicha = (ImageButton) convertView.findViewById(R.id.BTEliminar);
        ImageButton verMapa = (ImageButton) convertView.findViewById(R.id.BTVerMapa);


        abrirFicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LugaresObj grupo = (LugaresObj) getGroup(grupoSel);
                Intent intent = new Intent(context, MostrarLugarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", grupo.getID());
                intent.putExtras(bundle);
                context.startActivity(intent);

                //TODO: Poner un finish
                PreCarga preCarga = new PreCarga(context);
                preCarga.execute();

            }
        });
        editarFicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LugaresObj grupo = (LugaresObj) getGroup(grupoSel);
                Intent intent = new Intent(context, EditarLugarActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", grupo.getID());
                intent.putExtras(bundle);
                //TODO: Poner un finish
                context.startActivity(intent);

            }
        });
        eliminarFicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialo = onCreateDialog(DIALOG_ElIMINAR);
                dialo.show();

            }
        });

        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LugaresObj grupo = (LugaresObj) getGroup(grupoSel);
                Intent intent = new Intent(context, MapaLugaresActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", grupo.getID());
                intent.putExtras(bundle);
                //TODO: Poner un finish
                context.startActivity(intent);

            }
        });
        return convertView;
    }


    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public int getGroupCount() {
        return groups.size();
    }

    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public boolean hasStableIds() {
        return true;
    }


    protected Dialog onCreateDialog(int id) {
        Dialog newDialog = null;
        switch (id) {
            case DIALOG_ElIMINAR:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.EliminarLugar);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                        LugaresObj grupo = (LugaresObj) getGroup(grupoSel);
                        Uri uri = LugaresDB.CONTENT_URI;
                        int affectedRows = context.getContentResolver().delete(uri, LugaresDB.Lugares._ID + "=" + grupo.getID(), null);

                        if (affectedRows > 0) {
                            Toast.makeText(context, R.string.DeleteOK, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, ListaLugaresActivity.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, R.string.DeleteKO, Toast.LENGTH_LONG).show();
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
