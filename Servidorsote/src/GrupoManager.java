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
}
