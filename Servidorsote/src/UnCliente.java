
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnCliente implements Runnable {

    private final Socket socket;
    private final DataOutputStream salida;
    private final DataInputStream entrada;
    final String idCliente;
    private String[] datos;
    private boolean autenticado = false;
    private int mensajesEnviados = 0;

    public UnCliente(Socket s, String idCliente) throws IOException {
        this.socket = s;
        this.idCliente = idCliente;
        
        this.salida = new DataOutputStream(s.getOutputStream());
        this.entrada = new DataInputStream(s.getInputStream());
    }

    @Override
    public void run() {
        try {
            salida.writeUTF("Bienvenido cliente " + idCliente
                    + "\nPuedes enviar 3 mensajes antes de registrarte."
                    + "\nPara registrarte o iniciar sesión usa: 'register nombre contraseña' o 'login nombre contraseña'");

            while (true) {
                String mensaje = entrada.readUTF();
                // Login / registro en cualquier momento
                if (!autenticado && (mensaje.startsWith("login ") || mensaje.startsWith("registrar "))) {
                    iniciarSesion(mensaje);
                    continue;
                }
                if (!autenticado && mensajesEnviados >= 3) {
                    salida.writeUTF("Límite de mensajes alcanzado. Usa 'login nombre contraseña' o 'register nombre contraseña'");
                    continue;
                }
                if (autenticado && mensaje.startsWith("#")) {
                    bloquearUsuario(mensaje);
                    continue;
                }
                // Procesar mensajes directos
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
                    // Mensaje a todos
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
        } catch (Exception ex) {
            Logger.getLogger(UnCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Servidorsote.clientes.remove(idCliente);
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void iniciarSesion(String mensaje) throws IOException {
        try {
            String[] datos = mensaje.split(" ");
            this.datos = new String[]{datos[1], datos[2]};
            if (datos.length != 3) {
                salida.writeUTF("Formato incorrecto. Usa: login nombre contraseña o register nombre contraseña");
                return;
            }
            Sesion sesion = new Sesion(datos[0], datos[1], datos[2]);
            salida.writeUTF("Ahora estás autenticado y puedes enviar mensajes ilimitados.");
            autenticado = true;

        } catch (Exception e) {
            salida.writeUTF("Error al iniciar sesion: " + e.getMessage());
        }
    }

    private void bloquearUsuario(String mensaje) throws IOException {
        int idUsuario = Sesion.obtenerIdPorNombre(this.datos[0]);
        int idBloqueado = Sesion.obtenerIdPorNombre(mensaje.substring(1).trim());
        if (!bloqExiste(idBloqueado)) return;
        
        
    }
    
    private boolean bloqExiste(int idBloqueado) throws IOException{
        if (idBloqueado == -1) {
            salida.writeUTF("El usuario que deseas bloquear no existe");
            return false;
        }
        return true;
    }
}
