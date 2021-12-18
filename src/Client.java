import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private DatagramSocket streamSocket;
    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final InetAddress vizinho;
    private InetAddress myIp;

    public Client(InetAddress vizinho){
        this.vizinho = vizinho;

        try {
            this.myIp = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            this.streamSocket = new DatagramSocket(ott.PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.tcpSocket = ott.socketTCPCreate(vizinho);

        Mensagem m = new Mensagem("ar", myIp);

        ott.enviaMensagemTCP(this.tcpSocket, m);

        System.out.println("Pedido de ativacao da rota enviado...");
    }

    public void run(){
        Mensagem msg = ott.recebeMensagemUDP(streamSocket);
        System.out.println(msg);
    }
}
