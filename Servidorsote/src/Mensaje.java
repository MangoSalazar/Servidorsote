
import java.util.List;

public class Mensaje {
    Tipo tipo;
    
    String emisor;
    String destino;
    List<String> destinos ;
    
    String mensaje;
    
    
    public enum Tipo{
        uni,
        multi,
        broad,
        sistema
    }
    //sistema y broad
    public Mensaje(Tipo tipo, String emisor, String mensaje) {
        this.tipo = tipo;
        this.emisor = emisor;
        this.mensaje = mensaje;
    }
    
    //multicast
    public Mensaje(Tipo tipo, String emisor, List<String> destinos, String mensaje) {
        this.tipo = tipo;
        this.emisor = emisor;
        this.destinos = destinos;
        this.mensaje = mensaje;
    }
    //unicast
    public Mensaje(Tipo tipo, String emisor, String destino, String mensaje) {
        this.tipo = tipo;
        this.emisor = emisor;
        this.destino = destino;
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getDestino() {
        return destino;
    }

    public List<String> getDestinos() {
        return destinos;
    }

    public String getEmisor() {
        return emisor;
    }
    
    public String toString() {
        return "[" + tipo + "] " + emisor + ": " + mensaje;
    }
}
