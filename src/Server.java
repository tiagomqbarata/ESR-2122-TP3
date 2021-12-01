import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Server {
    private int port;
    private DatagramSocket socket;

    public Server(){
        port = 51510;

        try {
            socket = new DatagramSocket(port);
        } catch (IOException e){
            e.printStackTrace();
        }

        try {
            System.out.println("Ativo em " + InetAddress.getLocalHost().getHostAddress() + ":" + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
/*
    public void run() {
        new Thread(() -> {
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
*/
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

                System.out.println(new String(messageReceived));
                System.out.println(pacote.getAddress().getHostAddress());
                System.out.println(pacote.getPort());

                try {
                    socket.send(new DatagramPacket("HELLO RECEIVED".getBytes(), "HELLO RECEIVED".length(), pacote.getAddress(), pacote.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}

