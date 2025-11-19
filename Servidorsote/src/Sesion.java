public class Sesion {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    String nombre;
    String contrasena;
    public Sesion(String tipoDeInicio, String nombre, String contrasena) throws Exception {
        if (!validarFormatoNombre(nombre)) throw new Exception("Nombre inválido: Solo letras y números.");
        if (esPalabraReservada(nombre)) throw new Exception("Ese nombre está reservado por el sistema.");
        if (!validarLongitud(nombre, contrasena)) throw new Exception("Longitud incorrecta (User: 3-15, Pass: 3-10).");
            this.nombre = nombre;
            this.contrasena = contrasena;
            procesarSolicitud(tipoDeInicio);
    }

    private boolean validarFormatoNombre(String nombre) {
        return nombre.matches(Protocolo.PATRON_NOMBRE);
    }

    private boolean esPalabraReservada(String nombre) {
        return Protocolo.NOMBRES_PROHIBIDOS.contains(nombre.toLowerCase());
    }

    private boolean validarLongitud(String nombre, String contraseña) {
        boolean nombreOk = nombre.length() >= Protocolo.MIN_LONG_USER && nombre.length() <= Protocolo.MAX_LONG_USER;
        boolean passOk = contraseña.length() >= Protocolo.MIN_LONG_PASS && contraseña.length() <= Protocolo.MAX_LONG_PASS;
        return nombreOk && passOk;
    }
    private void procesarSolicitud(String tipoDeInicio) throws Exception {
        if (tipoDeInicio.equals(Protocolo.CMD_LOGIN)) {
            if (!usuarioDAO.validarCredenciales(nombre, contrasena)) {
                throw new Exception("Usuario o contrasena incorrectos");
            }
        } else if (tipoDeInicio.equals(Protocolo.CMD_REGISTRAR)) {
            if (!usuarioDAO.registrarUsuario(nombre, contrasena)) {
                throw new Exception("El usuario ya existe o error en base de datos");
            }
        } else {
            throw new Exception("Comando de sesion desconocido");
        }
    }
    public String getNombre() {
        return nombre; 
    }
}
