
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidorsote{
    static HashMap<String,UnCliente> clientes = new HashMap<String,UnCliente>();
    
    public static void main (String[] args) throws IOException{
        
        System.out.println("--- Iniciando Servidor ---");
        ConexionBD.inicializarBD();
        try (ServerSocket servidorSocket = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Servidor abiertote en el puerto: " + Protocolo.PUERTO); 
            int idCliente = 0;
            while (true) {            
                try {
                    Socket s = servidorSocket.accept();
                    s.setKeepAlive(true); 
                    UnCliente unCliente = new UnCliente(s, Integer.toString(idCliente));
                    Thread hilo = new Thread(unCliente);
                    clientes.put(Integer.toString(idCliente), unCliente);
                    hilo.start();
                    System.out.println("Nueva people conectandose. ID: " + idCliente);
                    idCliente++;
                } catch (IOException e) {
                    System.out.println("Error al aceptar una conexion");
                }
            }
        } catch (IOException e) {
            System.out.println("Error critico del servidor, verifica los puertos");
        }
    }
    
}
 
