import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private DatagramSocket socket;
    private static final int port = 12345;
    private List<InetAddress> vizinhos;
    private InetAddress myIp;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        try {
            this.myIp = InetAddress.getLocalHost();
            this.socket = new DatagramSocket(port);
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
                try {
                    Socket vizinhoSocket = new Socket(vizinho,port);
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(vizinhoSocket.getOutputStream()));
                    DataInputStream in = new DataInputStream(new BufferedInputStream(vizinhoSocket.getInputStream()));

                    out.write(m.toBytes());
                    out.flush();

                    byte[] data = new byte[512];
                    in.read(data);

                    Mensagem pedido = new Mensagem(data);

                    //TODO - Responder ao pedido


                } catch (IOException e) {
                    e.printStackTrace();
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

