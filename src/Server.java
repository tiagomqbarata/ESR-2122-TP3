import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private DatagramSocket streamSocket;
    private List<InetAddress> vizinhos;
    private InetAddress myIp;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        try {
            this.myIp = InetAddress.getLocalHost();
            this.streamSocket = new DatagramSocket(ott.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        /**
         * Envio de mensagem de resposta dos vizinhos, via tcp
         * */
        for(InetAddress vizinho : vizinhos) {
            new Thread(() -> {
                Mensagem m = new Mensagem("r", myIp);

                Socket s = ott.socketTCPCreate(vizinho);
                ott.enviaMensagemTCP(s,m);
                while(true) {
                    Mensagem pedido = ott.recebeMensagemTCP(s);

                    // STREAM
                    Mensagem aEnviar = new Mensagem("d", myIp, pedido.getIpOrigemMensagem(), "Hello");

                    ott.enviaMensagemUDP(pedido.getIpOrigemMensagem(), streamSocket, aEnviar);
                }
            }).start();
        }

        /**
         * Espera de pedidos UDP
         * */
/*        new Thread(() -> {
            while (true){
                byte[] messageReceived = new byte[512];
                DatagramPacket pacote = new DatagramPacket(messageReceived,512);

                try {
                    socket.receive(pacote);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Mensagem m = new Mensagem(messageReceived);

                switch (m.getTipo()) {
                    case "ar" -> {
                        // Mensagem recebida
                        System.out.println(new String(ott.trim(messageReceived)));
                        //TODO - FAZER A STREAM AQUI
                    }
                    case "" -> { //caso da mensagem de "fecho de rota"

                    }
                }
            }
        }).start();
*/
    }
}

