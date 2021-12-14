import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private static final int port = 12345;
    private DatagramSocket socket;
    private InetAddress vizinho;

    public Client(InetAddress vizinho){
        this.vizinho = vizinho;

        try {
            socket = new DatagramSocket(port);
        } catch (IOException e){
            e.printStackTrace();
        }

        Mensagem m = new Mensagem("ar");
        DatagramPacket packet = new DatagramPacket(m.toBytes(),m.length(), vizinho, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Pedido de ativacao da rota enviado...");
    }

    public void run(){
        new Thread(() -> {
            byte[] messageReceived = new byte[512];
            DatagramPacket pacote = new DatagramPacket(messageReceived,512);

            try {
                socket.receive(pacote);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(new String(ott.trim(messageReceived)));
        });

    }
}
