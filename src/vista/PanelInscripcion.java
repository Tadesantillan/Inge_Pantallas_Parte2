package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import datos.Charla;
import datos.Convocatoria;
import datos.EstadoConvocatoria;
import servicios.SistemaAutoridades;
 
/** Caso de uso "Registrarse como postulante" (RF-07). */
public class PanelInscripcion extends JPanel {
	private static final long serialVersionUID = 1L;
	
    private final SistemaAutoridades sistema;
    private final List<Charla> charlas;
    private final JPanel form = new JPanel(new GridBagLayout());
    private int fila = 0;
 
    private final JComboBox<String> cbDistrito = new JComboBox<>();
    private final JTextField txtNombre = new JTextField(22), txtApellido = new JTextField(22),
            txtDni = new JTextField(22), txtNacimiento = new JTextField(22), txtDireccion = new JTextField(22),
            txtTelefono = new JTextField(22), txtCorreo = new JTextField(22), txtPartido = new JTextField(22);
    private final ButtonGroup gAut = new ButtonGroup(), gCap = new ButtonGroup(), gAfi = new ButtonGroup();
    private final JRadioButton autSi = new JRadioButton("Sí"), autNo = new JRadioButton("No"),
            capSi = new JRadioButton("Sí"), capNo = new JRadioButton("No"),
            afiSi = new JRadioButton("Sí"), afiNo = new JRadioButton("No");
    private final List<JCheckBox> checks = new ArrayList<>();
 
    public PanelInscripcion(SistemaAutoridades sistema) {
        super(new BorderLayout());
        this.sistema = sistema;
        this.charlas = sistema.getConvocatoria().charlas;
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
 
        cbDistrito.addItem("Elegí una opción");
        for (String d : SistemaAutoridades.DISTRITOS) cbDistrito.addItem(d);
        gAut.add(autSi); gAut.add(autNo);
        gCap.add(capSi); gCap.add(capNo);
        gAfi.add(afiSi); gAfi.add(afiNo);
 
        Convocatoria conv = sistema.getConvocatoria();
        String plazo = conv.estado() == EstadoConvocatoria.ABIERTA
                ? "Inscripción abierta hasta el " + conv.fechaLimite.format(SistemaAutoridades.FECHA)
                : "La convocatoria está cerrada";
        agregar(new JLabel(plazo), null);
        agregar("Distrito electoral", cbDistrito);
        agregar("Nombre", txtNombre);
        agregar("Apellido", txtApellido);
        agregar("DNI (sin puntos)", txtDni);
        agregar("Fecha de nacimiento (dd/mm/aaaa)", txtNacimiento);
        agregar("Dirección actual", txtDireccion);
        agregar("Teléfono", txtTelefono);
        agregar("Correo de contacto", txtCorreo);
        agregar("¿Ya fuiste autoridad de mesa?", siNo(autSi, autNo));
        agregar("¿Cumpliste la capacitación?", siNo(capSi, capNo));
        agregar("¿Sos afiliado a una agrupación?", siNo(afiSi, afiNo));
        agregar("Partido (solo si sos afiliado)", txtPartido);
 
        JPanel panelCharlas = new JPanel(new GridBagLayout());
        int i = 0;
        for (Charla c : charlas) {
            JCheckBox cb = new JCheckBox(c.nombre + " — " + c.fecha.format(SistemaAutoridades.FECHA) + ", " + c.horario + " hs");
            checks.add(cb);
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = i++;
            gc.anchor = GridBagConstraints.WEST;
            panelCharlas.add(cb, gc);
        }
        agregar("Charlas de interés (opcional)", panelCharlas);
 
        JButton enviar = new JButton("Enviar inscripción");
        enviar.addActionListener(e -> enviar());
        agregar(enviar, null);
 
        JPanel alineado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        alineado.add(form);
        add(new JScrollPane(alineado), BorderLayout.CENTER);
    }
 
    private JPanel siNo(JRadioButton si, JRadioButton no) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.add(si);
        p.add(no);
        return p;
    }
 
    private void agregar(String etiqueta, JComponent campo) { agregar(new JLabel(etiqueta), campo); }
 
    /** Agrega una fila al formulario: etiqueta a la izquierda y campo a la derecha (si hay). */
    private void agregar(JComponent izquierda, JComponent derecha) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = fila++;
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0;
        form.add(izquierda, gc);
        if (derecha != null) {
            gc.gridx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            form.add(derecha, gc);
        }
    }
 
    private String valor(JRadioButton si, JRadioButton no) { return si.isSelected() ? "SI" : no.isSelected() ? "NO" : ""; }
 
    private void enviar() {
        Map<String, String> d = new HashMap<>();
        d.put("distrito", cbDistrito.getSelectedIndex() > 0 ? (String) cbDistrito.getSelectedItem() : "");
        d.put("nombre", txtNombre.getText());
        d.put("apellido", txtApellido.getText());
        d.put("dni", txtDni.getText());
        d.put("fechaNacimiento", txtNacimiento.getText());
        d.put("direccion", txtDireccion.getText());
        d.put("telefono", txtTelefono.getText());
        d.put("correo", txtCorreo.getText());
        d.put("fueAutoridad", valor(autSi, autNo));
        d.put("capacitacion", valor(capSi, capNo));
        d.put("afiliado", valor(afiSi, afiNo));
        d.put("partido", txtPartido.getText());
 
        List<Charla> interes = new ArrayList<>();
        for (int i = 0; i < checks.size(); i++) if (checks.get(i).isSelected()) interes.add(charlas.get(i));
 
        List<String> errores = sistema.registrar(d, interes);
        if (errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inscripción registrada. Tu solicitud quedó en estado PENDIENTE.",
                    "Listo", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } else {
            JOptionPane.showMessageDialog(this, "- " + String.join("\n- ", errores),
                    "Revisá estos puntos", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void limpiar() {
        cbDistrito.setSelectedIndex(0);
        for (JTextField t : new JTextField[] { txtNombre, txtApellido, txtDni, txtNacimiento, txtDireccion, txtTelefono, txtCorreo, txtPartido })
            t.setText("");
        gAut.clearSelection();
        gCap.clearSelection();
        gAfi.clearSelection();
        for (JCheckBox cb : checks) cb.setSelected(false);
    }
}
 