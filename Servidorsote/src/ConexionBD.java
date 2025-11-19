
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {
    
    private static final String URL = "jdbc:sqlite:db/usuarios.db"; 

    // --- SENTENCIAS SQL (Constantes para limpieza visual) ---
    
    private static final String TBL_USUARIOS = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre_usuario TEXT NOT NULL UNIQUE,
            contrasena TEXT NOT NULL
        );
    """;

    private static final String TBL_BLOQUEOS = """
        CREATE TABLE IF NOT EXISTS bloqueos (
            id_usuario INTEGER NOT NULL,
            id_bloqueado INTEGER NOT NULL,
            FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
            FOREIGN KEY (id_bloqueado) REFERENCES usuarios(id) ON DELETE CASCADE,
            UNIQUE (id_usuario, id_bloqueado)
        );
    """;

    // Nuevas tablas necesarias para GrupoManager
    private static final String TBL_GRUPOS = """
        CREATE TABLE IF NOT EXISTS grupos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre_grupo TEXT NOT NULL UNIQUE,
            id_creador INTEGER NOT NULL,
            FOREIGN KEY (id_creador) REFERENCES usuarios(id)
        );
    """;

    private static final String TBL_GRUPOS_MIEMBROS = """
        CREATE TABLE IF NOT EXISTS grupos_miembros (
            id_grupo INTEGER NOT NULL,
            id_usuario INTEGER NOT NULL,
            FOREIGN KEY (id_grupo) REFERENCES grupos(id) ON DELETE CASCADE,
            FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
            UNIQUE (id_grupo, id_usuario)
        );
    """;

    private static final String TBL_MSJ_PENDIENTES = """
        CREATE TABLE IF NOT EXISTS mensajes_pendientes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_usuario_destino INTEGER NOT NULL,
            mensaje_formateado TEXT NOT NULL,
            FOREIGN KEY (id_usuario_destino) REFERENCES usuarios(id) ON DELETE CASCADE
        );
    """;

    // --- MÉTODOS ---

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error crítico de conexion BD: " + e.getMessage());
            return null;
        }
    }

    public static void inicializarBD() {
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            
            if (conn == null) return;

            // Ejecutar creacion de tablas
            stmt.execute(TBL_USUARIOS);
            stmt.execute(TBL_BLOQUEOS);
            stmt.execute(TBL_GRUPOS);
            stmt.execute(TBL_GRUPOS_MIEMBROS);
            stmt.execute(TBL_MSJ_PENDIENTES);

            System.out.println("Base de datos verificada y lista.");
            
        } catch (SQLException e) {
            System.out.println("Error al inicializar tablas: " + e.getMessage());
        }
    }
}

