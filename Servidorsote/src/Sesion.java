public class Sesion {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    String nombre;
    String contrasena;
    public Sesion(String tipoDeInicio, String nombre, String contrasena) throws Exception {
            if (!esValido(nombre, contrasena)) {
                throw new Exception("Nombre o contrasena no cumplen con la longitud requerida.");
            }
            this.nombre = nombre;
            this.contrasena = contrasena;
            procesarSolicitud(tipoDeInicio);
    }
    private boolean esValido(String nombre, String contrasena) {
        boolean nombreValido = nombre.length() < Protocolo.MAX_LONG_USER && nombre.length() > Protocolo.MIN_LONG_USER;
        boolean passValido = contrasena.length() < Protocolo.MAX_LONG_PASS && contrasena.length() > Protocolo.MIN_LONG_PASS;
        return nombreValido && passValido;
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
