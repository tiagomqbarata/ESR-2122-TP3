import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
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
    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets

    Timer cTimer; //timer used to receive data from the UDP socket
    byte[] cBuf; //buffer used to store data received from the server


    public Router(List<InetAddress> vizinhos) {
        this.vizinhos = vizinhos;
        this.ligados = 0;
        this.routing_table = new HashMap<>();
        try {
            this.myIp = InetAddress.getLocalHost();
            this.streamSocket = new DatagramSocket(ott.PORT);
            this.serverSocket = new ServerSocket(ott.PORT);
            // Streaming
            RTPsocket = new DatagramSocket(RTP_RCV_PORT); //init RTP socket (o mesmo para o cliente e servidor)
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

                    executa(msg, tcpSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() ->{
            while (true) {
                //init para a parte do cliente
                //--------------------------
                cTimer = new Timer(20, new routerTimerListener());
                cTimer.setInitialDelay(0);
                cTimer.setCoalesce(true);
                cBuf = new byte[15000]; //allocate enough memory for the buffer used to receive data from the server
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
                           }else System.out.println("Não tenho vizinhos!");
                       });
                   }
               }
               case "ar" -> {
                   this.addRota(msg.getIpOrigemMensagem(), tcpSocket, msg.getSaltos());
                   this.ligados++;
                   routing_table.get(msg.getIpOrigemMensagem()).ativarRota();

                   Rota r = routing_table.get(ipServidor);

                   ott.enviaMensagemTCP(r.getOrigem(), msg);
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

                // reencaminhar para todas as rotas ativas?
                for(InetAddress ip : routing_table.keySet()){
                    if(routing_table.get(ip).getEstado() && !ip.equals(rcvdp.getAddress()) && !ip.equals(ipServidor)){
                        sendRTPsocket = new DatagramSocket(ott.RTP_PORT,routing_table.get(ip).getOrigem().getInetAddress());
                        sendRTPsocket.send(rcvdp);
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

}
