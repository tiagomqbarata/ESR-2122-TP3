import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private InetAddress ipServer;
    private int portServer;
    private DatagramSocket socket;
    private Map<InetAddress,Integer> vizinhos = new HashMap<>();

    public Client(Map<InetAddress,Integer> vizinhos){
        this.vizinhos = vizinhos;
    }

    /*
    public Client(String ip, int port){
        try {
            this.ipServer = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.portServer = port;
    }*/

    public void run(){
        new Thread(() -> {
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            // TODO - checkar
            vizinhos.forEach((ip,port) ->{
                try {
                    socket.send(new DatagramPacket("HELLO".getBytes(), "HELLO".length(), ip, port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        /*
        try {
            socket.send(new DatagramPacket("HELLO".getBytes(), "HELLO".length(), ipServer, portServer));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

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
