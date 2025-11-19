
import java.util.ArrayList;
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
    public static String aceptar(int idAcepta, String nombreInvito) {
        int idInvito = dao.obtenerIdPorNombre(nombreInvito);
        if (!tieneInvitacion(idAcepta, idInvito)) return "No tienes invitación de este usuario.";
        String key = generarKey(idAcepta, idInvito);
        PartidaGato partida = new PartidaGato(idInvito, idAcepta);
        partidasActivas.put(key, partida);
        
        invitaciones.get(idAcepta).remove((Integer) idInvito);
        notificarAmbos(partida, "¡Partida Iniciada! " + nombreInvito + " (X) vs Tú (O).\n" + partida.dibujarTablero());
        return "Partida aceptada.";
    }

    public static String jugar(int idJugador, String nombreOponente, String casillaStr) {
        try {
            int idOponente = dao.obtenerIdPorNombre(nombreOponente);
            PartidaGato partida = partidasActivas.get(generarKey(idJugador, idOponente));
            if (partida == null) return "No estás jugando contra " + nombreOponente;

            String estado = partida.realizarJugada(idJugador, Integer.parseInt(casillaStr));
            verificarEstadoPartida(partida, idJugador, idOponente, estado);
            return "Jugada realizada.";
        } catch (Exception e) { return "Error: " + e.getMessage(); }
    }
    private static void verificarEstadoPartida(PartidaGato partida, int idJugador, int idOponente, String tablero) {
        if (partida.hayGanador()) {
            finalizarPartida(idJugador, idOponente, "¡Ganaste contra " + dao.obtenerNombrePorId(idOponente) + "!", "Perdiste contra " + dao.obtenerNombrePorId(idJugador));
        } else if (partida.esEmpate()) {
            notificarAmbos(partida, "Juego Empatado.\n" + tablero);
            partidasActivas.remove(generarKey(idJugador, idOponente));
        } else {
            notificarAmbos(partida, tablero);
            notificarUsuario(partida.getOponente(idJugador), Protocolo.notificacion("Tu turno contra " + dao.obtenerNombrePorId(idJugador)));
        }
    }

    public static void manejarDesconexion(int idDesconectado) {
        partidasActivas.forEach((key, partida) -> {
            if (partida.idJugadorX == idDesconectado || partida.idJugadorO == idDesconectado) {
                int idGanador = (partida.idJugadorX == idDesconectado) ? partida.idJugadorO : partida.idJugadorX;
                finalizarPartida(idGanador, idDesconectado, "Tu oponente se desconectó. ¡Ganaste!", null);
            }
        });
        invitaciones.remove(idDesconectado);
    }

    private static void finalizarPartida(int idGanador, int idPerdedor, String msgGanador, String msgPerdedor) {
        dao.registrarFinPartida(idGanador, idPerdedor);
        partidasActivas.remove(generarKey(idGanador, idPerdedor));
        
        notificarUsuario(idGanador, Protocolo.notificacion(msgGanador + dao.obtenerStats(idGanador)));
        if (msgPerdedor != null) notificarUsuario(idPerdedor, Protocolo.notificacion(msgPerdedor + dao.obtenerStats(idPerdedor)));
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
