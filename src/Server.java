import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Server {
    private DatagramSocket socket;
    private static final int port = 12345;
    private List<InetAddress> vizinhos;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;

        try {
            socket = new DatagramSocket(port);
        } catch (IOException e){
            e.printStackTrace();
        }

        Mensagem m = new Mensagem("r");

        for(InetAddress addr : vizinhos){
            DatagramPacket packet = new DatagramPacket(m.toBytes(),m.length(), addr, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Mensagens enviadas...");

    }

    public void run(){
        new Thread(() -> {
            while (true){
                byte[] messageReceived = new byte[512];
                DatagramPacket pacote = new DatagramPacket(messageReceived,512);

                try {
                    socket.receive(pacote);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Mensagem recebida
                System.out.println(new String(ott.trim(messageReceived)));

                // IP de onde veio a mensagem (proximo salto para a stream)
                System.out.println(pacote.getAddress().getHostAddress());

                // Porta de onde veio a mensagem (sempre '12345')
                System.out.println(pacote.getPort());

               //TODO - FAZER A STREAM AQUI
            }
        }).start();

    }
}

