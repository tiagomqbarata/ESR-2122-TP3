import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Router {
    private final List<InetAddress> vizinhos;
    private static final int port = 12345;
    private Map<InetAddress, Rota> routing_table;
    private InetAddress ipServidor;
    private DatagramSocket socket;
    private int ligados;

    public Router(List<InetAddress> vizinhos) {
        this.vizinhos = vizinhos;
        this.ligados = 0;
        this.routing_table = new HashMap<>();
    }

    public boolean addRota(InetAddress destino, InetAddress origem, int saltos){
        Rota r = routing_table.get(destino);
        if(r == null || saltos < r.getSaltos()){
            r = new Rota(origem, saltos);
            routing_table.put(destino,r);
            return true;
        }
        return false;
    }

    public void addServidor(InetAddress ipServidor){
        this.ipServidor = ipServidor;
    }

    public void addRota(Rota rota){
        this.routing_table.put(rota.getOrigem(),rota);
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

                //TODO - encaminhar, dependendo do formato da mensagem, tratar os dados e adicionar a rota
                Mensagem msg = new Mensagem(pacote.getData());
                switch(msg.getTipo()){
                    case "r" -> {
                        this.ipServidor = msg.getIpServidor();
                        this.routing_table.put(this.ipServidor,new Rota(this.ipServidor,msg.getSaltos()));
                        msg.incSaltos();
                        this.vizinhos.forEach(vizinho -> {
                            DatagramPacket packet = new DatagramPacket(msg.toBytes(), msg.length(), vizinho, port);
                            try {
                                socket.send(packet);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    case "ar" -> {
                        Rota rota = new Rota(pacote.getAddress(),msg.getSaltos());
                        this.routing_table.put(pacote.getAddress(),rota);
                        this.addRota(pacote.getAddress(),);
                        this.ligados++;
                        msg.incSaltos();
                        for
                        DatagramPacket packet = new DatagramPacket(msg.toBytes(),msg.length(),)
                        socket.send();
                        //TODO - pedido de conteúdo
                    }
                }
            }
        }).start();
    }
}
