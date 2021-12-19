import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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

                while(true){
                    Mensagem msg = ott.recebeMensagemTCP(s);
                    System.out.println(msg);
                    executa(msg,s.getInetAddress());
                }

            }).start();
        }
    }

    public void executa(Mensagem msg, InetAddress ip){
        System.out.println("Cheguei aqui!!!!!!!");
        switch (msg.getTipo()) {
            case "ar" -> {
                new Thread(() -> {
                    System.out.println("Recebi pedido de ativacao e vou mandar conteudo!");
                    runStreamer(ip);
                }).start();
            }
            case "" -> { //caso da mensagem de "fecho de rota"

            }
        }
    }

    public  void runStreamer(InetAddress ip) {
        //get video filename to request:

        File f = new File("movie.Mjpeg");
        if (f.exists()) {
            //Create a Main object
            Streamer s = new Streamer(ip);
            //show GUI: (opcional!)
            s.pack();
            s.setVisible(true);
        } else
            System.out.println("Ficheiro de video não existe");
    }
}

