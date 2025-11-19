public class Sesion {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    String nombre;
    String contrasena;
    public Sesion(String tipoDeInicio, String nombre, String contrasena) throws Exception {
            if (true) {
                throw new Exception("Nombre o contrasena no cumplen con la longitud requerida.");
            }
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
