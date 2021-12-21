import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class Router {
    private final List<InetAddress> vizinhos;
    private Map<InetAddress, Rota> routing_table;
    private InetAddress ipServidor;
    private DatagramSocket streamSocket;
    private ServerSocket serverSocket;
    private int ligados;
    private InetAddress myIp;

    //RTP variables:
    //----------------
    DatagramPacket rcvdp; //UDP packet received from the server (to receive)
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
    DatagramSocket sendRTPsocket; //socket to be used to forward UDP packet

    Timer cTimer; //timer used to receive data from the UDP socket
    byte[] cBuf; //buffer used to store data received from the server


    public Router(List<InetAddress> vizinhos) {
        this.vizinhos = vizinhos;
        this.ligados = 0;
        this.routing_table = new HashMap<>();
        try {
            this.myIp = InetAddress.getLocalHost();
            this.streamSocket = new DatagramSocket(ott.TCP_PORT);
            this.serverSocket = new ServerSocket(ott.TCP_PORT);
            // Streaming
            RTPsocket = new DatagramSocket(ott.RTP_PORT); //init RTP socket (o mesmo para o cliente e servidor)
            sendRTPsocket = new DatagramSocket();
            RTPsocket.setSoTimeout(5000); // setimeout to 5s
        } catch (SocketException e) {
            System.out.println("Cliente: erro no socket: " + e.getMessage());
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

    public void run(){
        new Thread(() ->{
            while (true) {
                try {
                    Socket tcpSocket = serverSocket.accept();

                    Mensagem msg = ott.recebeMensagemTCP(tcpSocket);

                    executaNovo(msg, tcpSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() ->{
            //init para a parte do cliente
            //--------------------------
            cTimer = new Timer(20, new routerTimerListener());
            cTimer.setInitialDelay(0);
            cTimer.setCoalesce(true);
            cBuf = new byte[15000]; //allocate enough memory for the buffer used to receive data from the server
            cTimer.start();
        }).start();

    }

    private void executaNovo(Mensagem msg, Socket tcpSocket) {
       new Thread(()->{
           executa(msg,tcpSocket);
       }).start();
    }

    private void executa(Mensagem msg, Socket tcpSocket) {
        try {
            switch (msg.getTipo()) {
                case "r" -> {
                    if (this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos())) {
                        this.ipServidor = msg.getIpOrigemMensagem();

                        this.vizinhos.forEach(vizinho -> {
                            if (!tcpSocket.getInetAddress().equals(vizinho)) {
                                System.out.println("Sending message to: " + tcpSocket.getInetAddress());

                                comunicaVizinho(msg, vizinho);
                            }
                        });
                    }
                }
                case "ar" -> {
                    System.out.println("Recebido pedido de ativacao de router!");

                    this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos());
                    this.ligados++;

                    System.out.println("AR - Estao ligados " + ligados);
                    if (!existeRotaAtiva()) {
                        System.out.println("Nao existe rota ativaaaaaa");

                        Rota r = routing_table.get(ipServidor);

                        ott.enviaMensagemTCP(r.getOrigem(), msg);

                        System.out.println(routing_table);
                    }

                    routing_table.get(msg.getIpOrigemMensagem()).ativarRota();

                    Mensagem m = ott.recebeMensagemTCP(tcpSocket);
                    executa(m, tcpSocket);

                }
                case "arC" -> {
                    System.out.println("Recebido pedido de ativacao de cliente!");

                    this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos());
                    this.ligados++;

                    System.out.println("ARC - Estao ligados " + ligados);
                    if (!existeRotaAtiva()) {
                        System.out.println("Nao existe rota ativaaaaaa");

                        Rota r = routing_table.get(ipServidor);

                        Mensagem nova = new Mensagem("ar", myIp);

                        ott.enviaMensagemTCP(r.getOrigem(), nova);

                        System.out.println(routing_table);
                    }

                    routing_table.get(msg.getIpOrigemMensagem()).ativarRota();

                    Mensagem m = ott.recebeMensagemTCP(tcpSocket);
                    executa(m, tcpSocket);

                }
                case "dr" -> { //caso da mensagem de "fecho de rota"
                    this.ligados--;

                    try {
                        routing_table.get(msg.getIpOrigemMensagem()).desativarRota();
                    } catch (Exception ex) {
                        System.out.println();
                        System.out.println();
                        System.out.println("----------------------------ERRO QUE EU QUERO------------------------------------------");

                        System.out.println("MAP DOS IPS: " + routing_table);
                        System.out.println("Mensagem: " + msg);
                        System.out.println("MyIp: " + myIp);

                        System.out.println("----------------------------------------------------------------------");
                        System.out.println();
                        System.out.println();
                    }

                    System.out.println("DR - Estao ligados " + ligados);
                    if (ligados == 0) {
                        System.out.println("A enviar " + msg + " para " + tcpSocket.getInetAddress());

                        ott.enviaMensagemTCP(routing_table.get(ipServidor).getOrigem(), msg);
                    }
                    Mensagem m = ott.recebeMensagemTCP(tcpSocket);
                    executa(m, tcpSocket);

                }
                case "drC" -> { //caso da mensagem de "fecho de rota"
                    this.ligados--;

                    try {
                        routing_table.get(msg.getIpOrigemMensagem()).desativarRota();
                    } catch (Exception ex) {
                        System.out.println();
                        System.out.println();
                        System.out.println("----------------------------ERRO QUE EU QUERO------------------------------------------");

                        System.out.println("MAP DOS IPS: " + routing_table);
                        System.out.println("Mensagem: " + msg);
                        System.out.println("MyIp: " + myIp);

                        System.out.println("----------------------------------------------------------------------");
                        System.out.println();
                        System.out.println();
                    }

                    System.out.println("DR - Estao ligados " + ligados);
                    if (ligados == 0) {
                        Mensagem nova = new Mensagem("dr", myIp);
                        System.out.println("A enviar " + nova + " para " + tcpSocket.getInetAddress());
                        ott.enviaMensagemTCP(routing_table.get(ipServidor).getOrigem(), nova);
                    }

                    try {
                        tcpSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                case "EEE" -> {
                    System.out.println("ERRO NA MENSAGEM");
                }
            }
        }catch (Exception e){
            System.out.println();
            System.out.println();
            System.out.println("----------------------------OUTRO ERRO QUE EU QUERO------------------------------------------");

            System.out.println("Mensagem: " + msg);
            System.out.println("MyIp: " + myIp);
            System.out.println("Socket IP: " + tcpSocket.getInetAddress());

            System.out.println("----------------------------------------------------------------------");
            System.out.println();
            System.out.println();

            e.printStackTrace();
        }
    }


    private void comunicaVizinho(Mensagem msg, InetAddress vizinho){
        new Thread(() -> {
            Socket s = ott.socketTCPCreate(vizinho);

            ott.enviaMensagemTCP(s, msg);

            System.out.println("Sou o " + s.getLocalAddress() + " vou receber uma mensagem de: " + s.getInetAddress());

            Mensagem m = ott.recebeMensagemTCP(s);

            executa(m,s);

        }).start();
    }


    private class routerTimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(cBuf, cBuf.length);

            try{
                //receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                //create an RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                //print important header fields of the RTP packet received:
                System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());

                //System.out.println("N Rotas Ativas: " + ligados);

                int i = 0;
                for(InetAddress ip : routing_table.keySet()){
                    if(routing_table.get(ip).getEstado() && !ip.equals(rcvdp.getAddress()) && !ip.equals(ipServidor)){
                        //System.out.println("Rota to " + routing_table.get(ip).getOrigem().getInetAddress());
                        DatagramPacket toSend = new DatagramPacket(cBuf,cBuf.length, routing_table.get(ip).getOrigem().getInetAddress(), ott.RTP_PORT);

                        sendRTPsocket.send(toSend);

                        i++;
                    }
                }
            }
            catch (InterruptedIOException iioe){
                System.out.println("Nothing to read");
            }
            catch (IOException ioe) {
                System.out.println("Exception caught: "+ioe);
            }
        }
    }

    public Boolean existeRotaAtiva(){
        for (Rota r : routing_table.values()){
            System.out.println(r.getOrigem());
            if(r.getEstado()) return true;
        }
        return false;
    }

}
