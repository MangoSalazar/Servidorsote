
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
    
    private void entradasDeCliente() throws IOException {
        while (true) {
            String rawMensaje = entrada.readUTF();
            // login/registro en cualquier momento
            if (procesarSesion(rawMensaje)) {
                continue;
            }
            // uni, multi, broad, bloqueos
            procesarMensajes(rawMensaje);
            if (!autenticado) {
                mensajesEnviados++;
            }
        }
    }

}
