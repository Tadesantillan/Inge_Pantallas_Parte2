package datos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class Convocatoria {
    public final LocalDate fechaInicio;
    public final LocalDate fechaLimite;
    public final List<Charla> charlas = new ArrayList<>(); // Convocatoria "1" -> "1..*" Charla
 
    public Convocatoria(LocalDate fechaInicio, LocalDate fechaLimite) {
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
    }
 
    /** El estado se deduce de la fecha: pasada la fecha límite, la convocatoria queda cerrada (RF-08). */
    public EstadoConvocatoria estado() {
        return LocalDate.now().isAfter(fechaLimite) ? EstadoConvocatoria.CERRADA : EstadoConvocatoria.ABIERTA;
    }
}