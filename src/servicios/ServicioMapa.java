package servicios;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
* Interacción con el sistema externo de información geográfica (OpenStreetMap):
*  - Nominatim: convierte una dirección en coordenadas (geocodificación).
*  - Servidor de tiles: devuelve las imágenes de 256x256 px con las que se arma el mapa.
*/
public class ServicioMapa {

   private static final HttpClient HTTP = HttpClient.newHttpClient();
   private static final String USER_AGENT = "PoC-AutoridadesMesa-UNGS/1.0 (trabajo practico universitario)";

   /** Devuelve {latitud, longitud}. Lanza excepción si la dirección no se encuentra o no hay conexión. */
   public static double[] geocodificar(String direccion) throws Exception {
       String url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q="
               + URLEncoder.encode(direccion, StandardCharsets.UTF_8);
       HttpRequest pedido = HttpRequest.newBuilder(URI.create(url)).header("User-Agent", USER_AGENT).build();
       String json = HTTP.send(pedido, HttpResponse.BodyHandlers.ofString()).body();
       Matcher lat = Pattern.compile("\"lat\":\"(-?[0-9.]+)\"").matcher(json);
       Matcher lon = Pattern.compile("\"lon\":\"(-?[0-9.]+)\"").matcher(json);
       if (!lat.find() || !lon.find()) throw new Exception("Dirección no encontrada");
       return new double[] { Double.parseDouble(lat.group(1)), Double.parseDouble(lon.group(1)) };
   }

   /** Arma una imagen de w x h píxeles centrada en (lat, lon) uniendo los tiles necesarios. */
   public static BufferedImage armarMapa(double lat, double lon, int zoom, int w, int h) throws Exception {
       double mundo = 256.0 * (1 << zoom); // ancho del mapa del mundo, en píxeles, a este zoom
       double px = (lon + 180.0) / 360.0 * mundo;
       double rad = Math.toRadians(lat);
       double py = (1.0 - Math.log(Math.tan(rad) + 1.0 / Math.cos(rad)) / Math.PI) / 2.0 * mundo;
       double x0 = px - w / 2.0, y0 = py - h / 2.0; // esquina superior izquierda de la imagen
       int maxTile = (1 << zoom) - 1;

       BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
       Graphics2D g = img.createGraphics();
       g.setColor(new Color(0xE8EBEF));
       g.fillRect(0, 0, w, h);
       int descargados = 0;
       for (int tx = (int) Math.floor(x0 / 256); tx <= (int) Math.floor((x0 + w) / 256); tx++) {
           for (int ty = (int) Math.floor(y0 / 256); ty <= (int) Math.floor((y0 + h) / 256); ty++) {
               if (tx < 0 || ty < 0 || tx > maxTile || ty > maxTile) continue;
               try {
                   g.drawImage(descargarTile(zoom, tx, ty), (int) Math.round(tx * 256 - x0),
                           (int) Math.round(ty * 256 - y0), null);
                   descargados++;
               } catch (Exception e) {
                   // si un tile falla se deja el fondo gris y se sigue con los demás
               }
           }
       }
       if (descargados == 0) throw new Exception("No se pudo descargar el mapa");
       g.setColor(new Color(255, 255, 255, 200));
       g.fillRect(w - 190, h - 18, 190, 18);
       g.setColor(Color.DARK_GRAY);
       g.dispose();
       return img;
   }

   private static BufferedImage descargarTile(int z, int x, int y) throws Exception {
       HttpRequest pedido = HttpRequest.newBuilder(
               URI.create("https://tile.openstreetmap.org/" + z + "/" + x + "/" + y + ".png"))
               .header("User-Agent", USER_AGENT).build();
       HttpResponse<java.io.InputStream> r = HTTP.send(pedido, HttpResponse.BodyHandlers.ofInputStream());
       if (r.statusCode() != 200) throw new Exception("HTTP " + r.statusCode());
       return ImageIO.read(r.body());
   }


}
