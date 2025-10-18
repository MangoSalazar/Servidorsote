
public class Sesion {

    String nombre;
    String contraseña;

    public Sesion(String tipoDeInicio, String nombre, String contraseña) {
        if (numCaracteres(nombre, contraseña)) {
            this.nombre = nombre;
            this.contraseña = contraseña;
        }
        if (tipoDeInicio.equals("login")) {
            buscarUsuario(nombre, contraseña);
        }
        if (tipoDeInicio.equals("registrar")) {
            registrarUsuario(nombre, contraseña);
        }
    }

    private boolean numCaracteres(String nombre, String contraseña) {
        if (nombre.length() < 15 && nombre.length() > 3 && contraseña.length() < 10 && contraseña.length() > 3) {
            return true;
        }
        return false;
    }

    public void buscarUsuario(String nombre, String contraseña) {

    }

    public void registrarUsuario(String nombre, String contraseña) {

    }
}
