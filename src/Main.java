import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import servicios.SistemaAutoridades;
import vista.PanelInscripcion;
import vista.Panelcharlas;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // si falla se usa el aspecto por defecto de Swing
            }
            SistemaAutoridades sistema = new SistemaAutoridades();
            JTabbedPane pestanias = new JTabbedPane();
            pestanias.addTab("Charlas", new Panelcharlas(sistema.getConvocatoria()));
            pestanias.addTab("Quiero postularme", new PanelInscripcion(sistema));

            JFrame ventana = new JFrame("Portal para autoridades de mesa");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.add(pestanias);
            ventana.setSize(760, 620);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}