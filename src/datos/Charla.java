package datos;
import java.time.LocalDate;
import java.time.LocalTime;
 
public class Charla {
    public final String nombre;
    public final String tema;
    public final LocalDate fecha;
    public final LocalTime horario;
    public final Sede sede; // Charla "0..*" -> "1" Sede
 
    public Charla(String nombre, String tema, LocalDate fecha, LocalTime horario, Sede sede) {
        this.nombre = nombre;
        this.tema = tema;
        this.fecha = fecha;
        this.horario = horario;
        this.sede = sede;
    }
}
 
