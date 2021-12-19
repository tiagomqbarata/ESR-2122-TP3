import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private final List<InetAddress> vizinhos;
    private InetAddress myIp;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        try {
            this.myIp = InetAddress.getLocalHost();
            this.serverSocket = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        /*
         * Envio de mensagem de resposta dos vizinhos, via tcp
         */
        for(InetAddress vizinho : vizinhos) {
            new Thread(() -> {
                Mensagem m = new Mensagem("r", myIp);

                Socket s = ott.socketTCPCreate(vizinho);
                ott.enviaMensagemTCP(s,m);

                Mensagem msg = ott.recebeMensagemTCP(s);

                executa(msg,s.getInetAddress());
            }).start();
        }

        /*
         * Receção de mensagens de ativação de rota e consequente transmissão de stream ( em udp )
         */

    }

    public void executa(Mensagem msg, InetAddress ip){
        switch (msg.getTipo()) {
            case "ar" -> {
                new Thread(() -> {
                    while(true){
                        Streamer.run(msg.getDados(),ip,msg.getIpOrigemMensagem());
                    }
                });
            }
            case "" -> { //caso da mensagem de "fecho de rota"

            }
        }
    }
}

