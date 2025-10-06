
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class UnCliente implements Runnable{
    final DataOutputStream salida;
    final BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    final DataInputStream entrada;
    String idCliente;
    UnCliente(Socket s, String idCliente) throws IOException{
        this.idCliente = idCliente;
        salida = new DataOutputStream(s.getOutputStream());
        entrada = new DataInputStream(s.getInputStream());
                
    }
    
    @Override
    public void run() {
        String mensaje;
        while (true) {            
            try {
                mensaje = entrada.readUTF();
                if (mensaje.startsWith("@")) {
                    String[] partes= mensaje.split(" ");
                    String aQuien = partes[0].substring(1);
                    UnCliente cliente = Servidorsote.clientes.get(aQuien);
                    if (cliente != null) {
                        cliente.salida.writeUTF("Mensjae directo de " + idCliente + ": " + partes[1]);
                    }
                }else{
                    for (UnCliente cliente : Servidorsote.clientes.values()) {
                        cliente.salida.writeUTF("Mensjae directo de " + idCliente + ": " + mensaje);
                    }
                }
            } catch (Exception ex) {
            }                               
        }
    }
    
}
