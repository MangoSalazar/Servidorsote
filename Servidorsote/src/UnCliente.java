
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
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private boolean autenticado = false;
    private int mensajesEnviados = 0;
    private String nombreUsuarioAutenticado = null;
    private int idUsuarioDB = -1;

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
            System.out.println("Cliente " + idCliente + " (" + nombreUsuarioAutenticado + ") desconectado.");
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
                    enviarMensajeObject(Protocolo.INFO_LIMITE_ALCANZADO);
                    continue;
                }
                mensajesEnviados++;
            }
            enrutarMensaje(rawMensaje);
        }
    }
    private boolean esComandoSesion(String mensaje) {
        return mensaje.startsWith(Protocolo.CMD_LOGIN + " ") || 
               mensaje.startsWith(Protocolo.CMD_REGISTRAR + " ");
    }
    private void procesarSesion(String mensaje) throws IOException {
        if (autenticado) {
            enviarMensajeObject(Protocolo.INFO_YA_AUTENTICADO);
            return;
        }
        String[] partes = mensaje.split(" ");
        if (partes.length != 3) {
            enviarMensajeObject(Protocolo.ERR_FORMATO);
            return;
        }
        String comando = partes[0];
        String uNombre = partes[1];
        String uPass = partes[2];
        try {
            new Sesion(comando, uNombre, uPass);
            this.autenticado = true;
            this.nombreUsuarioAutenticado = uNombre;
            this.idUsuarioDB = usuarioDAO.obtenerIdPorNombre(uNombre);
            enviarMensajeObject(Protocolo.INFO_LOGIN_EXITOSO(uNombre));
            System.out.println("Usuario autenticado: " + uNombre + " (ID DB: " + idUsuarioDB + ")");
            cargarMensajesPendientes();
        } catch (Exception e) {
            enviarMensajeObject(Protocolo.errorGenerico(e.getMessage()));
        }
    }
    private void cargarMensajesPendientes() throws IOException {
        List<String> pendientes = GrupoManager.obtenerMensajesPendientes(this.idUsuarioDB);
        if (!pendientes.isEmpty()) {
            enviarMensajeObject(Protocolo.notificacion("--- Tienes " + pendientes.size() + " mensajes pendientes de tus grupos ---"));
            for (String msg : pendientes) {
                enviarMensajeObject(Protocolo.notificacion(msg));
            }
            enviarMensajeObject(Protocolo.notificacion("--- Fin de mensajes pendientes ---"));
        }
    }
    private void enrutarMensaje(String rawMensaje) throws IOException {
        if (rawMensaje.isEmpty()) return;
        String primerCaracter = rawMensaje.substring(0, 1);
        if (rawMensaje.startsWith("+")) { 
            if (!autenticado) { enviarMensajeObject(Protocolo.ERR_LOGIN); return; }
            manejarComandoGrupo(rawMensaje);
            return;
        }
        if (rawMensaje.startsWith("$")) {
            if (!autenticado) { enviarMensajeObject(Protocolo.ERR_LOGIN); return; }
            manejarMensajeGrupo(rawMensaje);
            return;
        }
        switch (primerCaracter) {
            case Protocolo.PREFIJO_BLOQUEO:
                manejarBloqueo(rawMensaje);
                break;
            case Protocolo.PREFIJO_PRIVADO:
                manejarPrivado(rawMensaje);
                break;
            case Protocolo.PREFIJO_GRUPAL:
                manejarMulticast(rawMensaje);
                break;
            default:
                manejarBroadcast(rawMensaje);
                break;
        }
    }
    private void manejarComandoGrupo(String rawMensaje) throws IOException {
        String[] partes = rawMensaje.split(" ", 2);
        String comando = partes[0];
        String argumento = (partes.length > 1) ? partes[1] : "";
        
        if (argumento.isEmpty()) {
            enviarMensajeObject(Protocolo.errorGenerico("El comando requiere un nombre de grupo."));
            return;
        }

        String respuesta = switch (comando) {
            case "+crear" -> GrupoManager.crearGrupo(this.idUsuarioDB, argumento);
            case "+unir" -> GrupoManager.unirGrupo(this.idUsuarioDB, argumento);
            case "+salir" -> GrupoManager.salirGrupo(this.idUsuarioDB, argumento);
            case "+eliminar" -> GrupoManager.eliminarGrupo(this.idUsuarioDB, argumento);
            default -> "Comando desconocido.";
        };
        
        enviarMensajeObject(Protocolo.notificacion(respuesta));
    }
    private void manejarMensajeGrupo(String rawMensaje) throws IOException {
        String nombreGrupo = obtenerDestino(rawMensaje).replace("$", ""); 
        String contenido = obtenerContenido(rawMensaje);

        if (contenido.isEmpty()) {
            enviarMensajeObject(Protocolo.errorGenerico("Mensaje vacío. Usa $grupo (mensaje)"));
            return;
        }
        GrupoManager.enviarMensajeGrupo(this.idUsuarioDB, nombreGrupo, contenido);
        enviarMensajeObject(Protocolo.notificacion("[Tú -> Grupo " + nombreGrupo + "]: " + contenido));
    }
    private void manejarBloqueo(String rawMensaje) throws IOException {
        if (!autenticado) {
            enviarMensajeObject(Protocolo.ERR_LOGIN);
            return;
        }
        String nombreABloquear = rawMensaje.substring(1).trim();
        int idEl = usuarioDAO.obtenerIdPorNombre(nombreABloquear);

        if (idEl == -1) {
            enviarMensajeObject(Protocolo.errorGenerico("Usuario no encontrado."));
            return;
        }

        if (usuarioDAO.bloquearUsuario(this.idUsuarioDB, idEl)) {
            enviarMensajeObject(Protocolo.notificacion("Has bloqueado a " + nombreABloquear));
        } else {
            enviarMensajeObject(Protocolo.errorGenerico("Ya estaba bloqueado o error interno."));
        }
    }
    private void manejarPrivado(String rawMensaje) throws IOException {
        String destino = obtenerDestino(rawMensaje);
        if (!clienteEstaConectado(destino)) {
            enviarMensajeObject(Protocolo.errorGenerico("Usuario no conectado o no existe."));
            return;
        }
        String contenido = obtenerContenido(rawMensaje);
        Mensaje msg = new Mensaje(Mensaje.Tipo.uni, idCliente, destino, contenido);
        Servidorsote.clientes.get(destino).enviarMensajeObject(msg);
    }
    private void manejarMulticast(String rawMensaje) throws IOException {
        List<String> destinos = obtenerDestinosLista(rawMensaje);
        String contenido = obtenerContenido(rawMensaje);
        Mensaje msg = new Mensaje(Mensaje.Tipo.multi, idCliente, destinos, contenido);
        for (String dest : destinos) {
            if (clienteEstaConectado(dest)) {
                Servidorsote.clientes.get(dest).enviarMensajeObject(msg);
            }
        }
    }
    private void manejarBroadcast(String rawMensaje) throws IOException {
        Mensaje msg = new Mensaje(Mensaje.Tipo.broad, idCliente, rawMensaje);
        for (UnCliente c : Servidorsote.clientes.values()) {
            if (!c.idCliente.equals(this.idCliente)) {
                c.enviarMensajeObject(msg);
            }
        }
    }
    public void enviarMensajeObject(Mensaje mensaje) throws IOException {
        salida.writeUTF(mensaje.toString());
    }

    private String obtenerDestino(String raw) {
        String[] partes = raw.split(" ");
        return partes[0].substring(1); // Quita el prefijo (@ o $)
    }
    
    private String obtenerContenido(String raw) {
        String[] partes = raw.split(" ", 2);
        return (partes.length == 2) ? partes[1] : "";
    }

    private List<String> obtenerDestinosLista(String raw) {
        String[] partes = raw.split(" ", 2);
        String[] destinos = partes[0].substring(1).split(",");
        return Arrays.asList(destinos);
    }

    private boolean clienteEstaConectado(String idDestino) {
        return Servidorsote.clientes.containsKey(idDestino);
    }

    private void limpiarConexion() {
        try {
            Servidorsote.clientes.remove(idCliente);
            socket.close();
        } catch (IOException ignored) {}
    }
    
    public int getIdUsuarioDB() {
        return this.idUsuarioDB;
    }
}
