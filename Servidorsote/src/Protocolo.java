
public class Protocolo {
    // Configuracion del Servidor
    public static final int PUERTO = 8080;
    public static final int LIMITE_MENSAJES_GUEST = 3;

    // Comandos de Gestion
    public static final String CMD_LOGIN = "login";
    public static final String CMD_REGISTRAR = "registrar"; 

    // Prefijos de Mensajeria
    public static final String PREFIJO_PRIVADO = "@";
    public static final String PREFIJO_GRUPAL = "%";
    public static final String PREFIJO_BLOQUEO = "#";
    public static final String PREFIJO_DESBLOQUEO = "*";
    public static final String CMD_LISTAR = "listar";
    public static final String CMD_GATO = "/gato";
    
    // Validaciones
    public static final int MIN_LONG_USER = 3;
    public static final int MAX_LONG_USER = 15;
    public static final int MIN_LONG_PASS = 3;
    public static final int MAX_LONG_PASS = 10;
    
    private static final String EMISOR_SISTEMA = "SERVIDOR";
    
    public static final Mensaje ERR_LOGIN = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Usuario o contrasena incorrectos."
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
        "Ya estas autenticado en el sistema."
    );
    
    public static final Mensaje ERR_REQ_SESION = new Mensaje(
        Mensaje.Tipo.sistema, "SERVIDOR", "Necesitas iniciar sesion."
    );
     public static final Mensaje ERR_AUTO_BLOQUEO = new Mensaje(
        Mensaje.Tipo.sistema, "SERVIDOR", "No puedes bloquearte a ti mismo."
    );
    public static final Mensaje ERR_NO_BLOQUEADO = new Mensaje(
        Mensaje.Tipo.sistema, "SERVIDOR", "Ese usuario no estaba bloqueado."
    );
    public static final Mensaje ERR_BLOQUEO_ACTIVO = new Mensaje(
        Mensaje.Tipo.sistema, 
        "SERVIDOR", 
        "No se pudo enviar. Existe un bloqueo activo con este usuario."
    );
    public static Mensaje listaDeUsuarios(String listaNombres) {
        return new Mensaje(
            Mensaje.Tipo.sistema, 
            "SERVIDOR", 
            "Usuarios Conectados:\n" + listaNombres
        );
    }
    public static final Mensaje INFO_LIMITE_ALCANZADO = new Mensaje(
        Mensaje.Tipo.sistema, 
        EMISOR_SISTEMA, 
        "Limite de mensajes invitado alcanzado. Por favor inicia sesion."
    );
    public static Mensaje INFO_LOGIN_EXITOSO() {
        String texto = """
            ¡Autenticacion Exitosa! Hola
            COMANDOS DISPONIBLES (Usuarios Registrados):
            
            [Chat Basico]
            > (Escribir texto)       -> Enviar a todos (Broadcast)
            > @usuario (mensaje)     -> Mensaje Privado
            > #usuario               -> Bloquear a un usuario
            
            [Grupos - Gestion]
            > +crear (nombre)        -> Crear nuevo grupo
            > +unir (nombre)         -> Unirse a grupo existente
            > +salir (nombre)        -> Salirse de un grupo
            > +eliminar (nombre)     -> Borrar grupo (Solo dueno)
            
            [Grupos - Mensajeria]
            > $(nombre del grupo) (mensaje)    -> Enviar mensaje al grupo
              (Ejemplo: $amigos Hola a todos)
            """;
        return new Mensaje(Mensaje.Tipo.sistema, EMISOR_SISTEMA, texto);
    }
    
    public static Mensaje bienvenida() {
        String texto = """
            Bienvenido al Servidor, Cliente
            Comandos disponibles (Sin Autenticacion):
            1. login <usuario> <contrasena>    -> Iniciar sesion
            2. registrar <usuario> <contrasena> -> Crear cuenta nueva
            3. <escribir mensaje>              -> Chat Global (Tienes 3 mensajes de prueba)
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
