
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
    
    private static final String EMISOR_SISTEMA = "SERVIDOR";
    
    public static final Mensaje ERR_LOGIN = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Usuario o contraseña incorrectos."
    );

    public static final Mensaje ERR_REGISTRO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "El usuario ya existe o hubo un error."
    );

    public static final Mensaje ERR_FORMATO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Formato incorrecto. Revisa tu comando."
    );
    
    public static final Mensaje INFO_YA_AUTENTICADO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Ya estás autenticado en el sistema."
    );

    public static final Mensaje INFO_LIMITE_ALCANZADO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Límite de mensajes invitado alcanzado. Por favor inicia sesión."
    );

    public static final Mensaje INFO_LOGIN_EXITOSO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Autenticación exitosa. Ahora tienes mensajes ilimitados."
    );
    
    public static Mensaje bienvenida(String idCliente) {
        String texto = """
            Bienvenido al Servidor, Cliente: %s
            Comandos disponibles (Sin Autenticación):
            1. login <usuario> <contraseña>    -> Iniciar sesión
            2. register <usuario> <contraseña> -> Crear cuenta nueva
            3. <escribir mensaje>              -> Chat Global (Tienes %d mensajes de prueba)
            """;

        return new Mensaje(Mensaje.Tipo.sistema, EMISOR_SISTEMA, texto);
    }

    public static Mensaje notificacion(String texto) {
        return new Mensaje(Mensaje.Tipo.sistema, EMISOR_SISTEMA, texto);
    }

    public static Mensaje errorGenerico(String detalle) {
        return new Mensaje(
            Mensaje.Tipo.sistema, 
            EMISOR_SISTEMA, 
            "Error: " + detalle
        );
    }
}
