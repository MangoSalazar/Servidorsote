import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrupoManager {
    public static String crearGrupo(int idCreador, String nombreGrupo) {
        String sqlGrupo = "INSERT INTO grupos (nombre_grupo, id_creador) VALUES (?, ?)";
        String sqlMiembro = "INSERT INTO grupos_miembros (id_grupo, id_usuario) VALUES (?, ?)";
        
        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false); // Transacción
            
            // 1. Crear grupo
            try (PreparedStatement ps = conn.prepareStatement(sqlGrupo, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombreGrupo);
                ps.setInt(2, idCreador);
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGrupo = rs.getInt(1);
                    
                    // 2. Agregar al creador como miembro automáticamente
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlMiembro)) {
                        ps2.setInt(1, idGrupo);
                        ps2.setInt(2, idCreador);
                        ps2.executeUpdate();
                    }
                    conn.commit();
                    return "Grupo '" + nombreGrupo + "' creado exitosamente.";
                }
            }
            conn.rollback();
            return "Error al crear el grupo.";
        } catch (SQLException e) {
            return "Error (probablemente el nombre ya existe): " + e.getMessage();
        }
    }
    public static String unirGrupo(int idUsuario, String nombreGrupo) {
        int idGrupo = obtenerIdGrupo(nombreGrupo);
        if (idGrupo == -1) return "El grupo no existe.";

        String sql = "INSERT INTO grupos_miembros (id_grupo, id_usuario) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
            return "Te has unido al grupo " + nombreGrupo;
        } catch (SQLException e) {
            return "Ya eres miembro de este grupo.";
        }
    }
    public static String salirGrupo(int idUsuario, String nombreGrupo) {
        int idGrupo = obtenerIdGrupo(nombreGrupo);
        if (idGrupo == -1) return "El grupo no existe.";

        String sql = "DELETE FROM grupos_miembros WHERE id_grupo = ? AND id_usuario = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            int filas = ps.executeUpdate();
            return (filas > 0) ? "Saliste del grupo " + nombreGrupo : "No eras miembro del grupo.";
        } catch (SQLException e) {
            return "Error al salir: " + e.getMessage();
        }
    }
    public static String eliminarGrupo(int idUsuario, String nombreGrupo) {
        String sql = "DELETE FROM grupos WHERE nombre_grupo = ? AND id_creador = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreGrupo);
            ps.setInt(2, idUsuario);
            int filas = ps.executeUpdate();
            return (filas > 0) ? "Grupo eliminado." : "No eres el dueño o el grupo no existe.";
        } catch (SQLException e) {
            return "Error al eliminar: " + e.getMessage();
        }
    }
    public static void enviarMensajeGrupo(int idEmisor, String nombreGrupo, String contenido) {
        int idGrupo = obtenerIdGrupo(nombreGrupo);
        if (idGrupo == -1) return;

        String nombreEmisor = Sesion.obtenerNombrePorId(idEmisor);
        List<Integer> miembros = obtenerMiembros(idGrupo);

        String mensajeFormateado = "[Grupo " + nombreGrupo + "] " + nombreEmisor + ": " + contenido;
        Mensaje msgObj = new Mensaje(Mensaje.Tipo.multi, nombreEmisor, nombreGrupo, contenido); 


        for (int idMiembro : miembros) {
            if (idMiembro == idEmisor) continue;

            UnCliente clienteConectado = buscarClienteOnline(idMiembro);

            if (clienteConectado != null) {
                try {
                    clienteConectado.enviarMensajeObject(Protocolo.notificacion(mensajeFormateado));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                guardarMensajePendiente(idMiembro, mensajeFormateado);
            }
        }
    }
    public static List<String> obtenerMensajesPendientes(int idUsuario) {
        List<String> mensajes = new ArrayList<>();
        String sqlSelect = "SELECT id, mensaje_formateado FROM mensajes_pendientes WHERE id_usuario_destino = ?";
        String sqlDelete = "DELETE FROM mensajes_pendientes WHERE id = ?";

        try (Connection conn = ConexionBD.conectar()) {
            // 1. Leer mensajes
            try (PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    mensajes.add(rs.getString("mensaje_formateado"));
                    try (PreparedStatement psDel = conn.prepareStatement(sqlDelete)) {
                        psDel.setInt(1, rs.getInt("id"));
                        psDel.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mensajes;
    }
    private static int obtenerIdGrupo(String nombre) {
        try (Connection conn = ConexionBD.conectar(); 
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM grupos WHERE nombre_grupo = ?")) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    private static List<Integer> obtenerMiembros(int idGrupo) {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT id_usuario FROM grupos_miembros WHERE id_grupo = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(rs.getInt("id_usuario"));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
    private static void guardarMensajePendiente(int idDestino, String mensaje) {
        String sql = "INSERT INTO mensajes_pendientes (id_usuario_destino, mensaje_formateado) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDestino);
            ps.setString(2, mensaje);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    private static UnCliente buscarClienteOnline(int idUsuarioDB) {
        for (UnCliente cliente : Servidorsote.clientes.values()) {
            if (cliente.getIdUsuarioDB() == idUsuarioDB) {
                return cliente;
            }
        }
        return null;
    }
}
