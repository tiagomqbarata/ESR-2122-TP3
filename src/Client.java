import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private static final int port = 12345;
  //  private DatagramSocket socket;
    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final InetAddress vizinho;
    private InetAddress myIp;

    public Client(InetAddress vizinho){
        this.vizinho = vizinho;
        try {
            this.myIp = InetAddress.getLocalHost();
         //   this.socket = new DatagramSocket(port);
            this.tcpSocket = new Socket(vizinho,port);

            Mensagem m = new Mensagem("ar", myIp);

            //DatagramPacket packet = new DatagramPacket(m.toBytes(),m.length(), vizinho, port);
            //socket.send(packet);

            this.tcpSocket = new Socket(vizinho,port);
            out = new DataOutputStream(tcpSocket.getOutputStream());
            in = new DataInputStream(tcpSocket.getInputStream());
            out.write(m.toBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Pedido de ativacao da rota enviado...");

        byte[] data = new byte[512];
        try {
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mensagem m = new Mensagem(data);
    }

    public void run(){
        new Thread(() -> {
            byte[] messageReceived = new byte[512];

            try {
                in.read(messageReceived);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(new String(ott.trim(messageReceived)));
        });

    }
}
