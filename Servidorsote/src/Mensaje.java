public class Mensaje {
    String mensaje;
    Tipo tipo;
    public enum Tipo{
        uni,
        multi,
        broad
    }
    
    public Mensaje(Tipo tipo, String mensaje) {
        obtenerDestinos(tipo, mensaje);
        //this.mensaje = mensaje;
    }
    
    public static String obtenerDestinos(Tipo tipo, String mensaje){
        
        
        return "";
    }
}
