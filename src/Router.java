import java.io.*;
import java.net.*;
import java.util.*;

public class Router {
    private final List<InetAddress> vizinhos;
    private Map<InetAddress, Rota> routing_table;
    private InetAddress ipServidor;
    private DatagramSocket streamSocket;
    private ServerSocket serverSocket;
    private int ligados;
    private InetAddress myIp;

    public Router(List<InetAddress> vizinhos) {
        this.vizinhos = vizinhos;
        this.ligados = 0;
        this.routing_table = new HashMap<>();
        try {
            this.myIp = InetAddress.getLocalHost();
            this.streamSocket = new DatagramSocket(ott.PORT);
            this.serverSocket = new ServerSocket(ott.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addRota(InetAddress destino, Socket from, int saltos){
        Rota r = routing_table.get(destino);
        if(r == null || saltos < r.getSaltos()){
            r = new Rota(from, saltos);
            routing_table.put(destino,r);
            return true;
        }
        return false;
    }

    public void addServidor(InetAddress ipServidor){
        this.ipServidor = ipServidor;
    }

    public void run(){
        new Thread(() ->{
            while (true) {
                try {
                    Socket tcpSocket = serverSocket.accept();

                    Mensagem msg = ott.recebeMensagemTCP(tcpSocket);

                    executa(msg, tcpSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() ->{
            while (true) {
                Mensagem msg = ott.recebeMensagemUDP(streamSocket);

                executa(msg);

            }
        }).start();

    }

    private void executa(Mensagem msg, Socket tcpSocket) {
       new Thread(()->{
           switch (msg.getTipo()){
               case "r" ->{
                   if (this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos())) {
                       this.ipServidor = msg.getIpOrigemMensagem();

                       this.vizinhos.forEach(vizinho -> {
                           if (!tcpSocket.getInetAddress().equals(vizinho)) {
                               System.out.println("IP anterior: " + tcpSocket.getInetAddress());
                               System.out.println("Vizinhos: " + vizinhos);

                               comunicaVizinho(msg,vizinho);
                           }
                       });
                   }
               }
               case "ar" -> { //TODO - nao falta dizer que a rota esta ativa??
                   this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos());
                   this.ligados++;

                   // escrever a mensagem no canal do gajo mais próximo
                   Rota r = routing_table.get(ipServidor);
                   r.ativarRota();

                   ott.enviaMensagemTCP(r.getOrigem(), msg);


                   //TODO - pedido de conteúdo
               }
               case "" -> { //caso da mensagem de "fecho de rota"
                   this.ligados--;


                   Rota r = routing_table.get(ipServidor);
                   r.ativarRota();

                   ott.enviaMensagemTCP(r.getOrigem(), msg);
               }
               case "EEE" -> {
                   System.out.println("ERRO NA MENSAGEM");
               }
           }

       }).start();
    }

    private void executa(Mensagem msg) {
        new Thread(()->{
            switch (msg.getTipo()){
                case "d" -> {
                    Rota r = routing_table.get(msg.getIpDestino());

                    ott.enviaMensagemUDP(r.getOrigem().getInetAddress(), streamSocket, msg);

                }
                case "" -> { //caso da mensagem de "fecho de rota"
                    this.ligados--;


                    Rota r = routing_table.get(ipServidor);
                    r.ativarRota();

                    ott.enviaMensagemTCP(r.getOrigem(), msg);
                }
                case "EEE" -> {
                    System.out.println("ERRO NA MENSAGEM");
                }
            }

        }).start();
    }

    private void comunicaVizinho(Mensagem msg, InetAddress vizinho){
        new Thread(() -> {
            Socket s = ott.socketTCPCreate(vizinho);

            ott.enviaMensagemTCP(s, msg);

            Mensagem m = ott.recebeMensagemTCP(s);

            executa(m,s);

        }).start();
    }


}
