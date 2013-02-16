package com.jllobera.lugares.classes;

/**
 *
 * Clase utilizada para devolver ciertos campos según sean necesarios
 *
 * Ciertos métodos están comentados ya que de momento no se utilizan
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 27/08/11
 * Time: 3:21
 */
public class LugaresObj {
    int ID;
    String nombre;
    String descripcion;
    String direccion;
    String direccionDetalle;
    public String foto;
    public String fotoThumb;
    float lat;
    float lon;

    private LugaresObj group;

    public LugaresObj(int ID, String nombre, String descripcion, String direccion, String direccionDetalle, String foto, String fotoThumb, float lat, float lon) {
        super();
        this.ID = ID;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.direccionDetalle = direccionDetalle;
        this.lat = lat;
        this.lon = lon;
        this.foto = foto;
        this.fotoThumb = fotoThumb;

    }

    public LugaresObj(String nombre, String descripcion, float lat, float lon) {
        super();

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.lat = lat;
        this.lon = lon;
    }
    public LugaresObj(String nombre, String direccion, String direccionDet, float lat, float lon) {
        super();

        this.nombre = nombre;
        this.direccion = direccion;
        this.direccionDetalle = direccionDet;
        this.lat = lat;
        this.lon = lon;
    }


    public LugaresObj(int ID, String nombre, String direccion, String direccionDet, String fotoThumb) {
        super();
        this.ID = ID;
        this.nombre = nombre;
        this.direccion = direccion;
        this.direccionDetalle = direccionDet;
        this.fotoThumb = fotoThumb;
    }


    //Setters y getters

  public int getID() {
        return ID;
    }

    public String getNombre() {
        return nombre;

    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDireccionDetalle() {
        return direccionDetalle;
    }

    public String getFoto() {
        return foto;
    }
    public String getFotoThumb() {
        return fotoThumb;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public LugaresObj getGroup() {
        return group;
    }

    public void setGroup(LugaresObj group) {
        this.group = group;
    }

    //De momento no se utilizan para nada, así que se comentan

    /*public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setDireccionDetalle(String direccionDetalle) {
        this.direccionDetalle = direccionDetalle;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
*/

}
