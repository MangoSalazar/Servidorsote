
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
    private static boolean tieneInvitacion(int idAcepta, int idInvito) {
        List<Integer> lista = invitaciones.get(idAcepta);
        return lista != null && lista.contains(idInvito);
    }
    private static String validarInvitacion(int idEmisor, int idDestino) {
        if (idDestino == -1) return "Usuario no existe.";
        if (idEmisor == idDestino) return "No puedes jugar contra ti mismo.";
        if (dao.hayBloqueoEntre(idEmisor, idDestino)) return "Bloqueo activo con este usuario.";
        if (buscarClienteOnline(idDestino) == null) return "Usuario no conectado.";
        if (partidasActivas.containsKey(generarKey(idEmisor, idDestino))) return "Ya están jugando.";
        return null;
    }
    private static String generarKey(int id1, int id2) {
        return Math.min(id1, id2) + "-" + Math.max(id1, id2);
    }

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
    private static UnCliente buscarClienteOnline(int idDB) {
        for (UnCliente c : Servidorsote.clientes.values()) if (c.getIdUsuarioDB() == idDB) return c;
        return null;
    }
}
