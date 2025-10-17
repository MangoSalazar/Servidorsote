
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
            salida.writeUTF("Bienvenido cliente " + idCliente
                    + ". Puedes enviar 3 mensajes antes de registrarte.\n"
                    + "Para registrarte o iniciar sesión usa: register nombre o login nombre.");

            while (true) {
                String mensaje = entrada.readUTF();

                if (!autenticado && mensajesEnviados >= 3) {
                    iniciarSesion(mensaje);
                }
                
                // Procesar mensajes
                if (mensaje.startsWith("@")) {
                    String[] partes = mensaje.split(" ", 2);
                    if (partes.length > 1) {
                        String aQuien = partes[0].substring(1);
                        UnCliente cliente = Servidorsote.clientes.get(aQuien);
                        if (cliente != null) {
                            cliente.salida.writeUTF("Directo de " + idCliente + ": " + partes[1]);
                        } else {
                            salida.writeUTF("No existe el cliente con id " + aQuien);
                        }
                    } else {
                        salida.writeUTF("Formato incorrecto. Usa: @id mensaje");
                    }
                } else {
                    for (UnCliente cliente : Servidorsote.clientes.values()) {
                        if (!cliente.idCliente.equals(idCliente)) {
                            cliente.salida.writeUTF("mensaje de " + idCliente + ": " + mensaje);
                        }
                    }
                }

                if (!autenticado) {
                    mensajesEnviados++;
                }

            }

        } catch (IOException e) {
            System.out.println("Cliente " + idCliente + " desconectado.");
        } finally {
            try {
                Servidorsote.clientes.remove(idCliente);
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public boolean iniciarSesion(String mensaje) throws IOException {
        if (mensaje.startsWith("login ") || mensaje.startsWith("register ")) {
            String datos[] = mensaje.split(" ");
            Sesion sesion = new Sesion(datos[0],datos[1],datos[2]);
            salida.writeUTF("Ahora estás autenticado y puedes enviar mensajes ilimitados.");
            autenticado = true;
            return true;
        }
        salida.writeUTF("Límite de mensajes alcanzado. Usa 'login nombre' o 'register nombre'.");
        return false;
    }
}
