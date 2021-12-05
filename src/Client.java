import java.io.*;
import java.net.*;

public class Client {
    private InetAddress ipServer;
    private int portServer;
    private DatagramSocket socket;

    public Client(String ip, int port){
        try {
            this.ipServer = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.portServer = port;
    }

    public void run(){

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            socket.send(new DatagramPacket("HELLO".getBytes(), "HELLO".length(), ipServer, portServer));
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] messageReceived = new byte[512];
        DatagramPacket pacote = new DatagramPacket(messageReceived,512);

        try {
            socket.receive(pacote);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new String(ott.trim(messageReceived)));

    }
}
