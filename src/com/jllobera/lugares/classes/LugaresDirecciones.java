package com.jllobera.lugares.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Clase que se encarga de devolver la dirección y el detalle de la dirección tan solo pasando el contexto y
 * las coordenadas
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 12/10/11
 * Time: 19:42
 */
public class LugaresDirecciones {

    Context context;
    float lat;
    float lon;
    String[] direcciones = new String[2];

    public LugaresDirecciones(Context context, float lat, float lon) throws IOException {
        this.context = context;
        this.lat = lat;
        this.lon = lon;
        String add = "";
        String addDes = "";


        Geocoder geoCoder = new Geocoder(
        context, Locale.getDefault());
        List<Address> addresses = geoCoder.getFromLocation(lat / 1E6, lon / 1E6, 1);
        if (addresses.size() > 0) {
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();
                 i++)
                if (i == 0) {
                    add += addresses.get(0).getAddressLine(i);
                } else {
                    addDes += addresses.get(0).getAddressLine(i) + "\n";
                }
        }


        direcciones[0] = add;
        direcciones[1] = addDes;

    }


    public LugaresDirecciones(Context context, double lat, double lon) throws IOException {
           this.context = context;
           this.lat = (float)lat;
           this.lon = (float) lon;
           String add = "";
           String addDes = "";




           Geocoder geoCoder = new Geocoder(
           context, Locale.getDefault());
           List<Address> addresses = geoCoder.getFromLocation(lat, lon, 1);
           if (addresses.size() > 0) {
               for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();
                    i++)
                   if (i == 0) {
                       add += addresses.get(0).getAddressLine(i);
                   } else {
                       addDes += addresses.get(0).getAddressLine(i) + "\n";
                   }
           }


           direcciones[0] = add;
           direcciones[1] = addDes;

       }

    public String getDireccion() {
        return direcciones[0];
    }

    public String getDireccionDes() {
        return direcciones[1];
    }


}
