
import java.util.List;

public class Mensaje {
    Tipo tipo;
    
    String emisor;
    String uniDestino;
    List<String> destinos ;
    
    String mensaje;
    
    
    public enum Tipo{
        uni,
        multi,
        broad,
        Sistema
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
    public Mensaje(Tipo tipo, String emisor, String uniDestino, String mensaje) {
        this.tipo = tipo;
        this.emisor = emisor;
        this.uniDestino = uniDestino;
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Tipo getTipo(String mensaje) {
        return tipo;
    }

    public String getUniDestino() {
        return uniDestino;
    }

    public List<String> getDestinos() {
        return destinos;
    }
    
}
