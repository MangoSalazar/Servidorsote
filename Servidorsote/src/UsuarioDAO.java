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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
