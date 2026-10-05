package datos;

import java.time.LocalDate;
 
public class Postulante {
    public final String nombre, apellido, dni, direccion, telefono, correo, distritoElectoral;
    public final LocalDate fechaNacimiento;
    public final boolean fueAutoridadPrevia, cumplioCapacitacion, esAfiliado;
    public final String partido; // null si no es afiliado
 
    public Postulante(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                      String direccion, String telefono, String correo, String distritoElectoral,
                      boolean fueAutoridadPrevia, boolean cumplioCapacitacion,
                      boolean esAfiliado, String partido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.distritoElectoral = distritoElectoral;
        this.fueAutoridadPrevia = fueAutoridadPrevia;
        this.cumplioCapacitacion = cumplioCapacitacion;
        this.esAfiliado = esAfiliado;
        this.partido = partido;
    }
}