
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {
    private static final String URL = "jdbc:sqlite:db/usuarios.db"; // Base de datos dentro del proyecto

    // Retorna la conexión
    public static Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Conexión a SQLite establecida.");
            return conn;
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return null;
        }
    }

    // Crea las tablas si no existen
    public static void inicializarBD() {
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL
            );
        """;

        String sqlBloqueos = """
            CREATE TABLE IF NOT EXISTS bloqueos (
                id_usuario INTEGER NOT NULL,
                id_bloqueado INTEGER NOT NULL,
                FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
                FOREIGN KEY (id_bloqueado) REFERENCES usuarios(id) ON DELETE CASCADE,
                UNIQUE (id_usuario, id_bloqueado)
            );
        """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlBloqueos);
            System.out.println("Tablas creadas o verificadas correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
}

