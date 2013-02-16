package com.jllobera.lugares.map;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.google.android.maps.*;
import com.jllobera.lugares.R;
import com.jllobera.lugares.classes.LugaresDirecciones;

import java.util.List;


/**
 *  Contiene la lista de Overlay items
 *
 * @param <Item>
 */
public abstract class BalloonItemizedOverlay<Item extends BalloonOverlayItem> extends ItemizedOverlay<BalloonOverlayItem> {

    private MapView mapView;
    private BalloonOverlayView<Item> balloonView;
    protected Context context;
    private int viewOffset;
    final MapController mc;
    private BalloonOverlayItem currentFocussedItem;
    private int currentFocussedIndex;

    MapItemizedOverlay nue;

    /**
     * Crea un nuevo overlay para la burbuja
     *
     * @param context       - Contexto
     * @param defaultMarker - Marcador que se dibujará por defecto.
     * @param mapView       - Vista donde se pintará el contenido.
     */
    public BalloonItemizedOverlay(Context context, Drawable defaultMarker, MapView mapView) {
        super(boundCenterBottom(defaultMarker));
        this.context = context;
        this.mapView = mapView;
        viewOffset = 0;
        mc = mapView.getController();
    }

    /**
     * Configura la distancia entre el centro inferior de la burbuja y el marcador.
     *
     * @param pixels - Distancia en pixels entre la burbuja y el marcador.
     */
    public void setBalloonBottomOffset(int pixels) {
        viewOffset = pixels;
    }

    public int getBalloonBottomOffset() {
        return viewOffset;
    }

    /**
     * Si se quiere manejar un evento onTap sobre la burbuja, hay que sobreescribir este método
     *
     * @param index - Índice del elemento del cual se ha pulsado la burbuja
     * @param item  - Elemento pulsado.
     * @return true - Si se ha pulsado, sino devuelve false.
     */
    protected boolean onBalloonTap(int index, BalloonOverlayItem item) {
        return false;
    }


    /**
     * Crea la burbuja sobre el elemento pulsado
     *
     * @param index Índice del elemento pulsado
     * @return true
     */
    @Override
    protected boolean onTap(int index) {
        currentFocussedIndex = index;
        currentFocussedItem = createItem(index);
        createAndDisplayBalloonOverlay();
        mc.animateTo(currentFocussedItem.getPoint());

        return true;
    }


    /**
     * @param p       - Variable con la info de las coordenadas
     * @param mapView - Vista del mapa
     * @return true   - Si se ha pulsado sobre el marcador
     */
    public boolean onTap(final GeoPoint p, final MapView mapView) {
        boolean tapped = super.onTap(p, mapView);

        MapItemizedOverlay ant;
        if (tapped) {
            return true;
        } else {
            try {
                //Chincheta que va a añadir
                MapItemizedOverlay miPos = new MapItemizedOverlay(context, context.getResources().getDrawable(R.drawable.chincheta_add), mapView);
                List<Overlay> mapOverlays = mapView.getOverlays();
                //Recupera la dirección de a partir de las coordenadas para pintarlo en la burbuja
                LugaresDirecciones ld = new LugaresDirecciones(context, p.getLatitudeE6(), p.getLongitudeE6());

                //introduce el valor en el item
                miPos.addLocalizacion(p, ld.getDireccion(), ld.getDireccionDes());

                hideBalloon();

                miPos.setFocus(miPos.item);
                mc.animateTo(p);

                ant = (MapItemizedOverlay) mapOverlays.get(0);
                if(nue != null && nue.size() > 0){
                    nue.hideBalloon();
                    if(ant.mOverlays.containsAll(nue.mOverlays)){
                        ant.mOverlays.removeAll(nue.mOverlays);
                    }
                }

                nue = miPos;

                nue.setDrawFocusedItem(false);

                Drawable add = context.getResources().getDrawable(R.drawable.chincheta_add);
                boundCenterBottom(add);
                nue.mOverlays.get(0).setMarker(add);
                mapOverlays.clear();
                ant.mOverlays.add(nue.item);
                mapOverlays.add(ant);
                populate();

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    /**
     * Crea la vista de la burbuja
     *
     * @return BalloonOverlayView
     */
    protected BalloonOverlayView<Item> createBalloonOverlayView() {
        setBalloonBottomOffset(65);
        return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
    }

    /**
     * Obtiene el mapa
     *
     * @return MapView
     */

    public MapView getMapView() {
        return mapView;
    }

    /**
     * Oculta la burbuja
     */
    public void hideBalloon() {
        if (balloonView != null) {
            balloonView.setVisibility(View.GONE);
        }
    }

    /**
     * Oculta las burbujas que se estén mostrando en el MapView
     *
     * @param overlays - lista de overlays.
     */
    private void hideOtherBalloons(List<Overlay> overlays) {

        for (Overlay overlay : overlays) {
            if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
                ((BalloonItemizedOverlay<?>) overlay).hideBalloon();
            }
        }

    }

    /**
     * Para configurar el onTouchListener de la burbuja que se muestra, hay que
     * sobrescribir el método {@link #onBalloonTap}
     *
     * @return OnTouchListener
     */
    private OnTouchListener createBalloonTouchListener() {
        return new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                try{
                View l = ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
                Drawable d = l.getBackground();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int[] states = {android.R.attr.state_pressed};
                    if (d.setState(states)) {
                        d.invalidateSelf();
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int newStates[] = {};
                    if (d.setState(newStates)) {
                        d.invalidateSelf();
                    }
                    // call overridden method
                    onBalloonTap(currentFocussedIndex, currentFocussedItem);
                    return true;
                } else {
                    return false;
                }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return false;

            }

        };
    }

    /**
     * Elemento Seleccionado
     *
     * @see com.google.android.maps.ItemizedOverlay#getFocus()
     */
    @Override
    public BalloonOverlayItem getFocus() {
        return currentFocussedItem;
    }

    /**
     * Configura el elemento que se le pasa como el elemento seleccionado
     *
     * @see com.google.android.maps.ItemizedOverlay#setFocus(com.google.android.maps.OverlayItem)(Item);
     */
    @Override
    public void setFocus(BalloonOverlayItem item) {
        currentFocussedItem = item;

        if (currentFocussedItem == null) {
            hideBalloon();
        } else {
            createAndDisplayBalloonOverlay();
        }
    }


    /**
     * Crea y muestra la burbuja reutilizando el actual o inflándolo desde xml
     * true si la burbuja se ha reutilizado, sino devuelve false
     *
     * @return true if the balloon was recycled false otherwise
     */
    public boolean createAndDisplayBalloonOverlay() {
        boolean isRecycled;
        if (balloonView == null) {
            balloonView = createBalloonOverlayView();
            View clickRegion = balloonView.findViewById(R.id.balloon_inner_layout);
            clickRegion.setOnTouchListener(createBalloonTouchListener());
            isRecycled = false;  //cambiado 18/10/2011
        } else {
            isRecycled = true;
        }

        balloonView.setVisibility(View.GONE);

        List<Overlay> mapOverlays = mapView.getOverlays();
        if (mapOverlays.size() > 1) {
            hideOtherBalloons(mapOverlays);
        }

        if (currentFocussedItem != null)
            balloonView.setData(currentFocussedItem);

        GeoPoint point = null;
        if (currentFocussedItem != null) {
            point = currentFocussedItem.getPoint();
        }
        MapView.LayoutParams params = new MapView.LayoutParams(
                MapView.LayoutParams.WRAP_CONTENT, MapView.LayoutParams.WRAP_CONTENT, point,
                MapView.LayoutParams.BOTTOM_CENTER);
        params.mode = MapView.LayoutParams.MODE_MAP;

        balloonView.setVisibility(View.VISIBLE);

        if (isRecycled) {
            balloonView.setLayoutParams(params);
        } else {
            mapView.addView(balloonView, params);
        }

        return isRecycled;
    }

    public void miPopulate(){
        populate();
    }

    public void miBoundCenterBottom(Drawable drawable){
        boundCenterBottom(drawable);
    }
}
