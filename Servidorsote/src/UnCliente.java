
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
                String rawMensaje = entrada.readUTF();
                // Login / registro en cualquier momento
                if (!autenticado && (rawMensaje.startsWith("login ") || rawMensaje.startsWith("registrar "))) {
                    iniciarSesion(rawMensaje);
                    continue;
                }
                if (!autenticado && mensajesEnviados >= 3) {
                    salida.writeUTF("Límite de mensajes alcanzado. Usa 'login nombre contraseña' o 'register nombre contraseña'");
                    continue;
                }
                if (autenticado && rawMensaje.startsWith("#")) {
                    bloquearUsuario(rawMensaje);
                    continue;
                }
                // mensajes directos
                if (rawMensaje.startsWith("@")) {
                    enviarMensajePrivado(rawMensaje);
                    continue;
                }
                //mensajes grupales
                if (rawMensaje.startsWith("%")) {
                    enviarMensajeGrupal(rawMensaje);
                    continue;
                }
                //mensajes globales
                enviarBroadCast(rawMensaje);

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

    private void enviarMensajeGrupal(String rawMensaje) throws IOException {
        List<String> destinos = obtenerDestinos(rawMensaje);
        String contenido = obtenerContenido(rawMensaje);
        if (contenido.equals("")) {
            salida.writeUTF("Formato incorrecto. Usa: @id1,id2...idx (mensaje)");
            return;
        }
        Mensaje mensaje = new Mensaje(Mensaje.Tipo.multi, idCliente, destinos, contenido);
        enviarMensajes(mensaje);
    }

    private void enviarMensajes(Mensaje mensaje) throws IOException {
        for (String destino : mensaje.getDestinos()) {
            UnCliente cliente = Servidorsote.clientes.get(destino);
            if (cliente != null && !destino.equals(idCliente)) {
                cliente.salida.writeUTF(mensaje.toString());
                continue;
            }
            salida.writeUTF("No se encontro al cliente: " + destino);
        }
    }

    private List<String> obtenerDestinos(String rawMensaje) {
        String[] partes = rawMensaje.split(" ", 2);
        String[] destinos = partes[0].substring(1).split(",");
        return Arrays.asList(destinos);
    }

    private void enviarBroadCast(String rawMensaje) throws IOException {
        Mensaje mensaje = new Mensaje(Mensaje.Tipo.broad, idCliente, rawMensaje);
        for (UnCliente cliente : Servidorsote.clientes.values()) {
            if (!cliente.idCliente.equals(idCliente)) {
                cliente.salida.writeUTF(mensaje.toString());
            }
        }
    }

    private void enviarMensajePrivado(String rawMensaje) throws IOException {
        String destino = obtenerDestino(rawMensaje);
        if (!destinoExiste(destino)) {
            salida.writeUTF("El destino no existe");
            return;
        }
        String contenido = obtenerContenido(rawMensaje);
        if (contenido.equals("")) {
            salida.writeUTF("Formato incorrecto. Usa: @id (mensaje)");
            return;
        }
        Mensaje mensaje = new Mensaje(Mensaje.Tipo.uni, idCliente, destino, contenido);
        UnCliente cliente = Servidorsote.clientes.get(mensaje.getDestino());
        cliente.salida.writeUTF(mensaje.toString());
    }

    private String obtenerDestino(String mensaje) {
        String destino = "";
        String[] partes = mensaje.split(" ");
        destino = partes[0].substring(1);
        return destino;
    }

    private String obtenerContenido(String rawMensaje) {
        String contenido = "";
        String[] partes = rawMensaje.split(" ", 2);
        if (partes.length == 2) {
            return contenido = partes[1];
        }
        return contenido;
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
            salida.writeUTF("Ahora estas autenticado y puedes enviar mensajes ilimitados.");
            autenticado = true;

        } catch (Exception e) {
            salida.writeUTF("Error al iniciar sesion: " + e.getMessage());
        }
    }

    private void bloquearUsuario(String mensaje) throws IOException {
        try {
            int idUsuario = Sesion.obtenerIdPorNombre(this.datos[0]);
            int idBloqueado = Sesion.obtenerIdPorNombre(mensaje.substring(1).trim());
            if (!bloqExiste(idBloqueado)) {
                return;
            }

            try (Connection conn = ConexionBD.conectar()) {
                String sql = "INSERT INTO bloqueos (id_usuario, id_bloqueado) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, idUsuario);
                ps.setInt(2, idBloqueado);
                ps.executeUpdate();
            }

            salida.writeUTF("Has bloqueado a " + mensaje.substring(1).trim());

        } catch (SQLIntegrityConstraintViolationException e) {
            salida.writeUTF("Ya habías bloqueado a ese usuario.");
        } catch (Exception e) {
            salida.writeUTF("Error al bloquear usuario: " + e.getMessage());
        }
    }

    private boolean bloqExiste(int idBloqueado) throws IOException {
        if (idBloqueado == -1) {
            salida.writeUTF("El usuario que deseas bloquear no existe");
            return false;
        }
        return true;
    }

    private boolean destinoExiste(String destino) {
        UnCliente cliente = Servidorsote.clientes.get(destino);
        if (cliente != null) {
            return true;
        }
        return false;
    }

}
