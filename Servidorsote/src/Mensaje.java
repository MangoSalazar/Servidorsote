public class Mensaje {
    String mensaje;
    Tipo tipo;
    String uniDestino;
    String[] destinos = {};
    
    public enum Tipo{
        uni,
        multi,
        broad
    }
    
    public Mensaje(Tipo tipo, String mensaje) {
        
        //this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Tipo getTipo() {
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
