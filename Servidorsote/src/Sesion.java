public class Sesion {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    String nombre;
    String contraseña;
    public Sesion(String tipoDeInicio, String nombre, String contraseña) throws Exception {
            if (!esValido(nombre, contraseña)) {
                throw new Exception("Nombre o contraseña no cumplen con la longitud requerida.");
            }
            this.nombre = nombre;
            this.contraseña = contraseña;
            procesarSolicitud(tipoDeInicio);
    }
    private boolean esValido(String nombre, String contraseña) {
        boolean nombreValido = nombre.length() < Protocolo.MAX_LONG_USER && nombre.length() > Protocolo.MIN_LONG_USER;
        boolean passValido = contraseña.length() < Protocolo.MAX_LONG_PASS && contraseña.length() > Protocolo.MIN_LONG_PASS;
        return nombreValido && passValido;
    }

    private void procesarSolicitud(String tipoDeInicio) throws Exception {
        if (tipoDeInicio.equals(Protocolo.CMD_LOGIN)) {
            if (!usuarioDAO.validarCredenciales(nombre, contraseña)) {
                throw new Exception("Usuario o contraseña incorrectos");
            }
        } else if (tipoDeInicio.equals(Protocolo.CMD_REGISTRAR)) {
            if (!usuarioDAO.registrarUsuario(nombre, contraseña)) {
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
