
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnCliente implements Runnable {
    private final Socket socket;
    private final DataOutputStream salida;
    private final DataInputStream entrada;
    final String idCliente;
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Instancia del DAO
    private boolean autenticado = false;
    private String nombreUsuarioAutenticado = null;
    
    public UnCliente(Socket s, String idCliente) throws IOException {
        this.socket = s;
        this.idCliente = idCliente;
        this.salida = new DataOutputStream(s.getOutputStream());
        this.entrada = new DataInputStream(s.getInputStream());
    }
    @Override
    public void run() {
            try {
                enviarMensajeObject(Protocolo.bienvenida(idCliente));

                cicloPrincipal();

            } catch (IOException e) {
                System.out.println("Cliente " + idCliente + " desconectado.");
            } catch (Exception ex) {
                Logger.getLogger(UnCliente.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                limpiarConexion();
            }
    }
    
    private void cicloPrincipal() throws IOException {
        while (true) {
            String rawMensaje = entrada.readUTF();
            
            if (esComandoSesion(rawMensaje)) {
                procesarSesion(rawMensaje);
                continue;
            }

            if (!autenticado) {
                if (mensajesEnviados >= Protocolo.LIMITE_MENSAJES_GUEST) {
                    // USANDO CONSTANTE DE PROTOCOLO
                    enviarMensajeObject(Protocolo.INFO_LIMITE_ALCANZADO);
                    continue;
                }
                mensajesEnviados++;
            }
            enrutarMensaje(rawMensaje);
        }
    }
    


}
