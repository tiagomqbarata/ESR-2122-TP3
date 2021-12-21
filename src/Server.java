import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private ServerSocket serverSocket;
    private final List<InetAddress> vizinhos;
    private InetAddress myIp;
    private Map<InetAddress, Streamer> streams;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        this.streams = new HashMap<>();
        try {
            this.myIp = InetAddress.getLocalHost();
            this.serverSocket = new ServerSocket(ott.TCP_PORT);
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
/*
        while (true){
            new Thread(() -> {
                try {
                    Socket tcpSocket = serverSocket.accept();

                    Mensagem msg = ott.recebeMensagemTCP(tcpSocket);

                    //TODO acabar (se o servidor tiver um cliente diretamente ligado...)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

 */
    }

    public void executa(Mensagem msg, InetAddress ip){
        switch (msg.getTipo()) {
            case "ar" -> {
                new Thread(() -> {
                    System.out.println("A iniciar a transmissão...");

                    Streamer streamer = createStreamer(ip);

                    if(streamer==null){
                        System.out.println("ERRO A CRIAR A STREAM PARA " + ip);
                    }else{
                        streams.put(ip,streamer);
                        streamer.startStream();
                    }

                }).start();
            }
            case "dr" -> { //caso da mensagem de "fecho de rota"

                System.out.println("A fechar a transmissao...");
                Streamer streamer = streams.remove(ip);
                if(streamer != null)
                    streamer.stopStream();
                else{
                    System.out.println("---------------ERRO NA STREAM-------------------");
                    System.out.println("Mensagem: " + msg);
                    System.out.println("Ip de onde veio: " + ip);
                }
            }
        }
    }

    public Streamer createStreamer(InetAddress ip) {
        //get video filename to request:

        File f = new File("movie.Mjpeg");
        if (f.exists()) {
            //Create a Main object
            Streamer s = new Streamer(ip);
            //show GUI: (opcional!)
            s.pack();
            s.setVisible(true);

            return s;

        } else
            System.out.println("Ficheiro de video não existe");

        return null;
    }
}

