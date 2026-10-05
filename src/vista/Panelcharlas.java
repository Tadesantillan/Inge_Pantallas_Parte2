package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import datos.Charla;
import datos.Convocatoria;
import datos.Sede;
import servicios.ServicioMapa;
import servicios.SistemaAutoridades;
 
/** Caso de uso "Consultar charlas" + "Ver ubicación de sede" (RF-06 y RF-14). */
public class Panelcharlas extends JPanel {
	private static final long serialVersionUID = 1L;
	
    private final MapaPanel mapa = new MapaPanel();
    private final JLabel lblSede = new JLabel("Seleccioná una charla para ver su sede.");
    private final Map<Sede, double[]> cacheCoordenadas = new HashMap<>();
 
    public Panelcharlas(Convocatoria convocatoria) {
        super(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));
 
        DefaultTableModel modelo = new DefaultTableModel(new String[] {"Charla", "Tema", "Fecha", "Horario", "Sede"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        for (Charla c : convocatoria.charlas)
            modelo.addRow(new Object[] { c.nombre, c.tema, c.fecha.format(SistemaAutoridades.FECHA), c.horario + " hs", c.sede.nombre });
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
 
        JButton mas = new JButton("Acercar (+)"), menos = new JButton("Alejar (-)");
        mas.addActionListener(e -> mapa.cambiarZoom(1));
        menos.addActionListener(e -> mapa.cambiarZoom(-1));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(lblSede);
        barra.add(mas);
        barra.add(menos);
 
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(barra, BorderLayout.NORTH);
        abajo.add(mapa, BorderLayout.CENTER);
 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new java.awt.Dimension(640, 130));
        add(scroll, BorderLayout.NORTH);
        add(abajo, BorderLayout.CENTER);
 
        // El listener se registra al final, cuando todos los componentes ya existen (evita NullPointerException)
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) mostrarSede(convocatoria.charlas.get(fila).sede);
        });
    }
 
    private void mostrarSede(Sede sede) {
        lblSede.setText(sede.nombre + " — " + sede.direccion);
        double[] guardadas = cacheCoordenadas.get(sede);
        if (guardadas != null) {
            mapa.mostrar(guardadas[0], guardadas[1]);
            return;
        }
        mapa.mostrarMensaje("Buscando la ubicación...");
        new SwingWorker<double[], Void>() {
            @Override
            protected double[] doInBackground() throws Exception {
                return ServicioMapa.geocodificar(sede.direccion);
            }
 
            @Override
            protected void done() {
                try {
                    double[] coord = get();
                    cacheCoordenadas.put(sede, coord);
                    mapa.mostrar(coord[0], coord[1]);
                } catch (Exception e) {
                    // RNF-07: si el servicio de mapas falla, igual se muestra la información de texto de la sede
                    mapa.mostrarMensaje("No se pudo ubicar la sede en el mapa. Dirección: " + sede.direccion);
                }
            }
        }.execute();
    }
}
 