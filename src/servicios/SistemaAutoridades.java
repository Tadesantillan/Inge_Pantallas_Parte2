package servicios;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import datos.Charla;
import datos.Convocatoria;
import datos.EstadoConvocatoria;
import datos.Postulante;
import datos.Sede;
import datos.Solicitud;
 
/** Lógica de negocio de la prueba de concepto: datos de ejemplo, consulta de charlas e inscripción. */
public class SistemaAutoridades {
 
    public static final String[] DISTRITOS = {
        "Buenos Aires", "Catamarca", "Chaco", "Chubut", "Ciudad Autónoma de Buenos Aires", "Córdoba",
        "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones",
        "Neuquén", "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe",
        "Santiago del Estero", "Tierra del Fuego", "Tucumán" };
 
    public static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
 
    private static final String[][] REQUERIDOS = {
        {"distrito", "El distrito electoral"}, {"nombre", "El nombre"}, {"apellido", "El apellido"},
        {"dni", "El DNI"}, {"fechaNacimiento", "La fecha de nacimiento"}, {"direccion", "La dirección"},
        {"telefono", "El teléfono"}, {"correo", "El correo"} };
 
    private static final String[][] SI_NO = {
        {"fueAutoridad", "si fue autoridad de mesa"}, {"capacitacion", "si cumplió la capacitación"},
        {"afiliado", "si es afiliado a una agrupación"} };
 
    private final Convocatoria convocatoria = cargarDatosDeEjemplo();
    private final List<Solicitud> solicitudes = new ArrayList<>();
 
    public Convocatoria getConvocatoria() { return convocatoria; }
 
    /** Representa lo que cargaría el administrador. La 1ª charla abre la inscripción y la última la cierra. */
    private static Convocatoria cargarDatosDeEjemplo() {
        LocalDate hoy = LocalDate.now();
        Sede ungs = new Sede("UNGS - Campus", "Juan María Gutiérrez 1150, Los Polvorines, Buenos Aires, Argentina");
        Sede cabildo = new Sede("Cabildo de Buenos Aires", "Bolívar 65, Ciudad Autónoma de Buenos Aires, Argentina");
        Sede teatro = new Sede("Teatro Municipal de San Miguel", "Av. Presidente Perón 1301, San Miguel, Buenos Aires, Argentina");
        Convocatoria c = new Convocatoria(hoy, hoy.plusDays(30));
        c.charlas.add(new Charla("Charla inaugural", "Qué es una autoridad de mesa", hoy, LocalTime.of(18, 0), ungs));
        c.charlas.add(new Charla("Capacitación práctica", "Cómo se arma y se cierra una mesa", hoy.plusDays(10), LocalTime.of(17, 30), cabildo));
        c.charlas.add(new Charla("Preguntas frecuentes", "Dudas sobre el día de la elección", hoy.plusDays(20), LocalTime.of(19, 0), teatro));
        c.charlas.add(new Charla("Charla de cierre", "Último llamado para postularse", hoy.plusDays(30), LocalTime.of(18, 0), ungs));
        return c;
    }
 
    /**
     * Registra una inscripción. Devuelve la lista de errores; si está vacía, la solicitud quedó guardada.
     * Se valida TODO antes de guardar, así nunca queda una inscripción incompleta (RNF-05).
     */
    public List<String> registrar(Map<String, String> d, List<Charla> interes) {
        List<String> errores = new ArrayList<>();
        if (convocatoria.estado() != EstadoConvocatoria.ABIERTA) {
            errores.add("La convocatoria está cerrada: ya no se aceptan inscripciones.");
            return errores;
        }
        for (String[] r : REQUERIDOS)
            if (d.getOrDefault(r[0], "").trim().isEmpty()) errores.add(r[1] + " es obligatorio.");
        for (String[] r : SI_NO)
            if (d.getOrDefault(r[0], "").isEmpty()) errores.add("Indicá " + r[1] + ".");
 
        String dni = d.getOrDefault("dni", "").trim();
        if (!dni.isEmpty() && !dni.matches("\\d{7,8}")) errores.add("El DNI debe tener 7 u 8 números, sin puntos.");
        String correo = d.getOrDefault("correo", "").trim();
        if (!correo.isEmpty() && !correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) errores.add("El correo no tiene un formato válido.");
 
        LocalDate nacimiento = null;
        String texto = d.getOrDefault("fechaNacimiento", "").trim();
        if (!texto.isEmpty()) {
            try {
                nacimiento = LocalDate.parse(texto, FECHA);
                if (Period.between(nacimiento, LocalDate.now()).getYears() < 18)
                    errores.add("Tenés que ser mayor de 18 años para postularte.");
            } catch (DateTimeParseException e) {
                errores.add("La fecha de nacimiento debe tener el formato dd/mm/aaaa.");
            }
        }
        boolean afiliado = "SI".equals(d.get("afiliado"));
        String partido = d.getOrDefault("partido", "").trim();
        if (afiliado && partido.isEmpty()) errores.add("Indicá el partido o agrupación al que estás afiliado.");
        if (!dni.isEmpty() && solicitudes.stream().anyMatch(s -> s.postulante.dni.equals(dni)))
            errores.add("Ya existe una inscripción con ese DNI.");
 
        if (errores.isEmpty()) {
            Postulante p = new Postulante(d.get("nombre").trim(), d.get("apellido").trim(), dni, nacimiento,
                    d.get("direccion").trim(), d.get("telefono").trim(), correo, d.get("distrito"),
                    "SI".equals(d.get("fueAutoridad")), "SI".equals(d.get("capacitacion")),
                    afiliado, afiliado ? partido : null);
            solicitudes.add(new Solicitud(p, new ArrayList<>(interes)));
        }
        return errores;
    }
}