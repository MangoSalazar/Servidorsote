public class Mensaje {
    String mensaje;
    public enum Tipo{
        unicast,
        multicast,
        broadcast
    }
    
    public Mensaje(String mensaje) {
        //this.mensaje = mensaje;
    }
    
}
