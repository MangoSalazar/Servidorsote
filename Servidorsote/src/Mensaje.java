public class Mensaje {
    Tipo tipo;
    
    String emisor;
    String uniDestino;
    String[] destinos = {};
    
    String mensaje;
    
    
    public enum Tipo{
        uni,
        multi,
        broad
    }
    
    public Mensaje(Tipo tipo, String mensaje) {
        tipo = getTipo(mensaje);
        //this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Tipo getTipo(String mensaje) {
        if (mensaje.startsWith("@")) ;
        if (mensaje.startsWith("#")) ;
        if (mensaje.startsWith("@")) ;
        return tipo;
    }

    public String getUniDestino() {
        return uniDestino;
    }

    public String[] getDestinos() {
        if (Tipo.multi == this.tipo) {
            
        }
        return destinos;
    }
    
}
