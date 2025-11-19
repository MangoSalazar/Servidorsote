
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class GatoManager {
    private static final ConcurrentHashMap<String, PartidaGato> partidasActivas = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, List<Integer>> invitaciones = new ConcurrentHashMap<>();
    private static final UsuarioDAO dao = new UsuarioDAO();
    
    public static String invitar(int idEmisor, String nombreDestino) {
        int idDestino = dao.obtenerIdPorNombre(nombreDestino);
        if (validarInvitacion(idEmisor, idDestino) != null) return validarInvitacion(idEmisor, idDestino);

        invitaciones.computeIfAbsent(idDestino, k -> new ArrayList<>()).add(idEmisor);
        notificarUsuario(idDestino, Protocolo.notificacion("¡" + dao.obtenerNombrePorId(idEmisor) + " te invitó a jugar Gato! Usa: /gato aceptar " + dao.obtenerNombrePorId(idEmisor)));
        return "Invitación enviada a " + nombreDestino;
    }


    // Método duplicado por necesidad de acceso estático, idealmente en una clase Utils o Servidorsote
    private static void notificarUsuario(int idUsuario, Mensaje msg) {
        for (UnCliente c : Servidorsote.clientes.values()) {
            if (c.getIdUsuarioDB() == idUsuario) {
                try { c.enviarMensajeObject(msg); } catch (Exception e) {}
            }
        }
    }

    private static void notificarAmbos(PartidaGato p, String texto) {
        notificarUsuario(p.idJugadorX, Protocolo.notificacion(texto));
        notificarUsuario(p.idJugadorO, Protocolo.notificacion(texto));
    }
    
    // Necesitamos este helper aquí también
    private static UnCliente buscarClienteOnline(int idDB) {
        for (UnCliente c : Servidorsote.clientes.values()) if (c.getIdUsuarioDB() == idDB) return c;
        return null;
    }
}
