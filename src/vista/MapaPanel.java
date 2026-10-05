package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import servicios.ServicioMapa;
 
/** Panel que dibuja el mapa de una ubicación. La descarga se hace en segundo plano (SwingWorker). */
public class MapaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private BufferedImage imagen;
    private String mensaje = "Elegí una charla para ver su sede en el mapa.";
    private double lat, lon;
    private int zoom = 16;
    private boolean hayUbicacion = false;
 
    public MapaPanel() {
        setPreferredSize(new Dimension(640, 320));
        setBackground(new Color(0xE8EBEF));
    }
 
    public void mostrar(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.hayUbicacion = true;
        recargar();
    }
 
    public void cambiarZoom(int delta) {
        zoom = Math.max(3, Math.min(18, zoom + delta));
        if (hayUbicacion) recargar();
    }
 
    public void mostrarMensaje(String texto) {
        imagen = null;
        mensaje = texto;
        hayUbicacion = false;
        repaint();
    }
 
    private void recargar() {
        final int w = Math.max(getWidth(), 320), h = Math.max(getHeight(), 200);
        final double la = lat, lo = lon;
        final int z = zoom;
        imagen = null;
        mensaje = "Cargando mapa...";
        repaint();
        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return ServicioMapa.armarMapa(la, lo, z, w, h);
            }
 
            @Override
            protected void done() {
                try {
                    imagen = get();
                } catch (Exception e) {
                    imagen = null;
                    mensaje = "No se pudo cargar el mapa. Verificá la conexión a internet.";
                }
                repaint();
            }
        }.execute();
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (imagen == null) {
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(mensaje, 20, getHeight() / 2);
            return;
        }
        g2.drawImage(imagen, 0, 0, null);
        int cx = imagen.getWidth() / 2, cy = imagen.getHeight() / 2; // la ubicación siempre queda al centro
        g2.setColor(new Color(0xD93025));
        g2.fillOval(cx - 8, cy - 8, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(cx - 8, cy - 8, 16, 16);
    }
}
 