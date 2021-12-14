import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Server {
    private DatagramSocket socket;
    private List<Socket> tcpSockets;
    private DataInputStream in;
    private DataOutputStream out;
    private static final int port = 12345;
    private List<InetAddress> vizinhos;
    private InetAddress myIp;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        try {
            this.myIp = InetAddress.getLocalHost();
            this.socket = new DatagramSocket(port);

            Mensagem m = new Mensagem("r", myIp);

            for(InetAddress vizinho : vizinhos) {
                //DatagramPacket packet = new DatagramPacket(m.toBytes(), m.length(), vizinho, port);
                //socket.send(packet);
                // TODO - checkar envio tcp
                this.tcpSockets.add(new Socket(vizinho,port));
                for (Socket socket: tcpSockets) {
                    out = new DataOutputStream(socket.getOutputStream());
                    out.write(m.toBytes());
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Mensagens enviadas...");

    }

    public void run(){
        new Thread(() -> {
            while (true){
                byte[] messageReceived = new byte[512];
                DatagramPacket pacote = new DatagramPacket(messageReceived,512);

                try {//TODO - passar para tcp
                    socket.receive(pacote);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Mensagem m = new Mensagem(messageReceived);

                switch (m.getTipo()) {
                    case "ar" -> {
                        // Mensagem recebida
                        System.out.println(new String(ott.trim(messageReceived)));

                        // IP de onde veio a mensagem (proximo salto para a stream)
                        System.out.println(pacote.getAddress().getHostAddress());

                        // Porta de onde veio a mensagem (sempre '12345')
                        System.out.println(pacote.getPort());

                        //TODO - FAZER A STREAM AQUI
                    }
                    case "" -> { //caso da mensagem de "fecho de rota"

                    }
                }
            }
        }).start();

    }
}

