import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Router {
    private final List<InetAddress> vizinhos;
    private static final int port = 12345;
    private Map<InetAddress, Rota> routing_table;
    private InetAddress ipServidor;
    private DatagramSocket socket;
    private ServerSocket serverSocket;
    private Socket tcpSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private int ligados;
    private InetAddress myIp;

    public Router(List<InetAddress> vizinhos) {
        this.vizinhos = vizinhos;
        this.ligados = 0;
        this.routing_table = new HashMap<>();
        try {
            this.myIp = InetAddress.getLocalHost();
            this.socket = new DatagramSocket(port);
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void run(){
        new Thread(() -> {
            while (true) {
                byte[] messageReceived = new byte[512];

                try {
                    tcpSocket = serverSocket.accept();
                    in = new DataInputStream(new BufferedInputStream(tcpSocket.getInputStream()));

                    in.read(messageReceived);
                    Mensagem msg = new Mensagem(ott.trim(messageReceived));

                    //TODO - encaminhar, dependendo do formato da mensagem, tratar os dados e adicionar a rota
                    switch (msg.getTipo()) {
                        case "r" -> {
                            this.ipServidor = msg.getIpOrigemMensagem();
                            if (this.addRota(this.ipServidor, this.ipServidor, msg.getSaltos())) {
                                msg.incSaltos();
                                this.vizinhos.forEach(vizinho -> {
                                    try {
                                        out.write(msg.toBytes());
                                        out.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                        case "ar" -> {
                            this.addRota(msg.getIpOrigemMensagem(), ((InetSocketAddress)tcpSocket.getRemoteSocketAddress()).getAddress(), msg.getSaltos());
                            this.ligados++;
                            msg.incSaltos();
                            try {
                                out.write(msg.toBytes());
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //TODO - pedido de conteúdo
                        }
                        case "" -> { //caso da mensagem de "fecho de rota"

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
