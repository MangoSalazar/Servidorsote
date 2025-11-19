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
}
