
public class Protocolo {
    // Configuración del Servidor
    public static final int PUERTO = 8080;
    public static final int LIMITE_MENSAJES_GUEST = 3;

    // Comandos de Gestión
    public static final String CMD_LOGIN = "login";
    public static final String CMD_REGISTER = "register";
    public static final String CMD_REGISTRAR = "registrar"; 

    // Prefijos de Mensajería
    public static final String PREFIJO_PRIVADO = "@";
    public static final String PREFIJO_GRUPAL = "%";
    public static final String PREFIJO_BLOQUEO = "#";
    
    // Validaciones
    public static final int MIN_LONG_USER = 3;
    public static final int MAX_LONG_USER = 15;
    public static final int MIN_LONG_PASS = 3;
    public static final int MAX_LONG_PASS = 10;
}
