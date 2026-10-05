package datos;
import java.time.LocalDate;
import java.util.List;
 
public class Solicitud {
    public final Postulante postulante;            // Solicitud "1" -> "1" Postulante
    public final List<Charla> charlasDeInteres;    // Solicitud "0..*" -- "0..*" Charla
    public final LocalDate fechaRegistro = LocalDate.now();
    public Estadosolicitud estado = Estadosolicitud.PENDIENTE;
    public String motivoRechazo;                   // se usará en la evaluación (TP posterior)
 
    public Solicitud(Postulante postulante, List<Charla> charlasDeInteres) {
        this.postulante = postulante;
        this.charlasDeInteres = charlasDeInteres;
    }
}
 