import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    
    public boolean existeUsuario(String nombre) {
        String sql = "SELECT 1 FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
            return false;
        }
    }
    public boolean validarCredenciales(String nombre, String contrasena) {
        String sql = "SELECT 1 FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, contrasena);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
            return false;
        }
    }
    public boolean registrarUsuario(String nombre, String contrasena) {
        if (existeUsuario(nombre)) return false;
        
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, contrasena);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
            return false;
        }
    }
    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
        }
        return -1;
    }
    public boolean bloquearUsuario(int idUsuario, int idBloqueado) {
        String sql = "INSERT INTO bloqueos (id_usuario, id_bloqueado) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idBloqueado);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public boolean desbloquearUsuario(int idUsuario, int idBloqueado) {
        String sql = "DELETE FROM bloqueos WHERE id_usuario = ? AND id_bloqueado = ?";
        try (Connection conn = ConexionBD.conectar(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ps.setInt(2, idBloqueado);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si borró algo
            
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
            return false;
        }
    }
    public boolean hayBloqueoEntre(int idUsuario1, int idUsuario2) {
        String sql = "SELECT 1 FROM bloqueos WHERE (id_usuario = ? AND id_bloqueado = ?) OR (id_usuario = ? AND id_bloqueado = ?)";
        
        try (Connection conn = ConexionBD.conectar(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario1);
            ps.setInt(2, idUsuario2);
            ps.setInt(3, idUsuario2);
            ps.setInt(4, idUsuario1);
            
            return ps.executeQuery().next();
            
        } catch (SQLException e) {
            System.out.println("No se pudo conectar la base de datos");
            return false;
        }
    }
    public void registrarFinPartida(int idGanador, int idPerdedor) {
        actualizarStat(idGanador, "victorias");
        actualizarStat(idPerdedor, "derrotas");
    }

    private void actualizarStat(int idUsuario, String columna) {
        String sqlInit = "INSERT OR IGNORE INTO estadisticas (id_usuario) VALUES (?)";
        String sqlUpdate = "UPDATE estadisticas SET " + columna + " = " + columna + " + 1 WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.conectar()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlInit)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
        } catch (SQLException e) { System.out.println("No se pudo conectar la base de datos");}
    }
    public String obtenerStats(int idUsuario) {
        String sql = "SELECT victorias, derrotas FROM estadisticas WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return " (W: " + rs.getInt("victorias") + " / L: " + rs.getInt("derrotas") + ")";
        } catch (SQLException e) { return "No se pudo conectar la base de datos"; }
        return " (W: 0 / L: 0)";
    }
    public String obtenerNombrePorId(int id) {
    String sql = "SELECT nombre_usuario FROM usuarios WHERE id = ?";
    try (Connection conn = ConexionBD.conectar(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getString("nombre_usuario");
        }
    } catch (SQLException e) {
        System.out.println("No se pudo conectar la base de datos");
    }
    return null;
}
}
