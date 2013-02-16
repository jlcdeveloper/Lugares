package com.jllobera.lugares.classes;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import com.jllobera.lugares.activity.MostrarLugarActivity;
import com.jllobera.lugares.constants.LugaresConstants;
import com.jllobera.lugares.db.LugaresDB;

/**
 * Manejador de gestos.
 *
 * Están implementados los desplazamientos en horizontal y vertical
 *
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 16/10/11
 * Time: 0:40
 */
public class ActivitySwipeDetector implements View.OnTouchListener {

    private Activity activity;
    static final int MIN_DISTANCE = 50;
    private float downX;
    private float downY;
    private LugaresConstants constants = new LugaresConstants();
    public ActivitySwipeDetector(Activity activity) {
        this.activity = activity;
    }

    public void onRightToLeftSwipe() {


        Uri uri = LugaresDB.CONTENT_URI;
        Cursor cursor = activity.managedQuery(uri, constants.Id, null, null, null);
        int idRes = -1;
        if (cursor != null) {
            int idAct = activity.getIntent().getExtras().getInt("id");
            while (cursor.moveToNext()) {
                int valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                if (cursor.getInt(valorID) == idAct) {
                    if (cursor.getCount() == 1) {
                        idRes = cursor.getInt(valorID);
                    } else {
                        if (cursor.getPosition() == cursor.getCount() - 1) {
                            cursor.moveToFirst();
                            idRes = cursor.getInt(valorID);
                            break;
                        } else {
                            cursor.moveToNext();
                            idRes = cursor.getInt(valorID);
                            break;
                        }
                    }
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        Intent intent = new Intent();
        intent.setClass(activity.getApplicationContext(), MostrarLugarActivity.class);
        //pasamos los campos que vamos a editar a la siguiente activity (EditarLugarActivity)
        if (idRes != -1) {
            intent.putExtra("id", idRes);
        }
        activity.startActivityForResult(intent, 100);
        activity.overridePendingTransition(com.jllobera.lugares.R.anim.slide_in_right, com.jllobera.lugares.R.anim.slide_out_left);
        activity.finish();
    }

    public void onLeftToRightSwipe() {
        Uri uri = LugaresDB.CONTENT_URI;
        Cursor cursor = activity.managedQuery(uri, constants.Id, null, null, null);


        int idRes = -1;
        if (cursor != null) {
            int idAct = activity.getIntent().getExtras().getInt("id");
            while (cursor.moveToNext()) {
                int valorID = cursor.getColumnIndex(LugaresDB.Lugares._ID);
                if (cursor.getInt(valorID) == idAct) {
                    if (cursor.getCount() == 1) {
                        idRes = cursor.getInt(valorID);
                    } else {
                        if (cursor.getPosition() == 0) {
                            cursor.moveToLast();
                            idRes = cursor.getInt(valorID);
                            break;
                        } else {
                            cursor.moveToPrevious();
                            idRes = cursor.getInt(valorID);
                            break;
                        }
                    }
                }
                //lugares.add(new LugaresObj(cursor.getInt(valorID), cursor.getString(valorNombre), cursor.getString(valorDescripcion), cursor.getString(valorFoto)));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        Intent intent = new Intent();
        intent.setClass(activity.getApplicationContext(), MostrarLugarActivity.class);
        //pasamos los campos que vamos a editar a la siguiente activity (EditarLugarActivity)
        if (idRes != -1) {
            intent.putExtra("id", idRes);
        }
        activity.startActivityForResult(intent, 100);
        activity.overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
        activity.finish();
    }

    public void onTopToBottomSwipe() {

    }

    public void onBottomToTopSwipe() {

    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // izquierda o derecha
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                }

                // swipe vertical
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // arriba o abajo
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                }

                return true;
            }
        }
        return false;
    }

}
