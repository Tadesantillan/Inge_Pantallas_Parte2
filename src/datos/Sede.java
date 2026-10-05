package datos;
public class Sede {
    public final String nombre;
    public final String direccion; // es el texto que se le envía al servicio de mapas
 
    public Sede(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }
}
 