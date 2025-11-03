
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sesion {

    String nombre;
    String contraseña;
    
    public Sesion(String tipoDeInicio, String nombre, String contraseña) throws Exception {
        if (!numCaracteres(nombre, contraseña)) {
            throw new Exception("Nombre o contraseña invalidos");
        }
        this.nombre = nombre;
        this.contraseña = contraseña;
        comprobarInicio(tipoDeInicio);
    }

    private boolean numCaracteres(String nombre, String contraseña) {
        if (nombre.length() < 15 && nombre.length() > 3 && contraseña.length() < 10 && contraseña.length() > 3) {
            return true;
        }
        return false;
    }

    private void comprobarInicio(String tipoDeInicio) throws Exception {
        if (tipoDeInicio.equals("login")) {
            if (!buscarUsuario(nombre, contraseña)) {
                throw new Exception("Usuario o contraseña incorrectos");
            }
        }
        if (tipoDeInicio.equals("registrar")) {
            if (!registrarUsuario(nombre, contraseña)) {
                throw new Exception("El usuario ya existe");
            }
        }
    }

    private boolean buscarUsuario(String nombre, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean registrarUsuario(String nombre, String contrasena) {
        String check = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        String insert = "INSERT INTO usuarios (nombre_usuario, contrasena) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement psCheck = conn.prepareStatement(check)) {

            psCheck.setString(1, nombre);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false; // Usuario ya existe
            }
            // Registrar nuevo usuario
            try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                psInsert.setString(1, nombre);
                psInsert.setString(2, contrasena); // En producción, guardar hash
                psInsert.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int obtenerIdPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT id FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener el ID del usuario: " + e.getMessage());
        }
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasena() {
        return contraseña;
    }
}
