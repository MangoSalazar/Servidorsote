
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidorsote{
    static HashMap<String,UnCliente> clientes = new HashMap<String,UnCliente>();
    
    public static void main (String[] args) throws IOException{
    ServerSocket servidorSocker = new ServerSocket (8080);
    int idCliente = 0;
        while (true) {            
            Socket s = servidorSocker.accept();
            UnCliente unCliente = new UnCliente(s, Integer.toString(idCliente));
            Thread hilo = new Thread(unCliente);
            clientes.put(Integer.toString(idCliente),unCliente);
            hilo.start();
            System.out.println("Se conecto el wey n: "+idCliente);
            idCliente++;
        }
    }
    
}
 
