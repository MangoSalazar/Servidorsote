import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UnCliente implements Runnable {
    private final Socket socket;
    private final DataOutputStream salida;
    private final DataInputStream entrada;
    final String idCliente;
    private boolean autenticado = false;
    private int mensajesEnviados = 0;

    UnCliente(Socket s, String idCliente) throws IOException {
        this.socket = s;
        this.idCliente = idCliente;
        this.salida = new DataOutputStream(s.getOutputStream());
        this.entrada = new DataInputStream(s.getInputStream());
    }

    @Override
    public void run() {
        try {
            salida.writeUTF("Bienvenido cliente " + idCliente +
                    ". Puedes enviar 3 mensajes antes de registrarte.\n" +
                    "Para registrarte o iniciar sesión usa: register nombre o login nombre.");

            while (true) {
                String mensaje = entrada.readUTF();

                // Si aún no está autenticado y ya pasó el límite
                if (!autenticado && mensajesEnviados >= 3) {
                    if (mensaje.startsWith("login ") || mensaje.startsWith("register ")) {
                        autenticado = true;
                        salida.writeUTF("Ahora estás autenticado y puedes enviar mensajes ilimitados.");
                    } else {
                        salida.writeUTF("Límite de mensajes alcanzado. Usa 'login nombre' o 'register nombre'.");
                        continue;
                    }
                }


            }

        } catch (IOException e) {
            System.out.println("Cliente " + idCliente + " desconectado.");
        } finally {
            try {
                Servidorsote.clientes.remove(idCliente);
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
