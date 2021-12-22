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
    private final List<InetAddress> vizinhos;
    private InetAddress myIp;
    Streamer s;

    public Server(List<InetAddress> vizinhos){
        this.vizinhos = vizinhos;
        try {
            this.myIp = InetAddress.getLocalHost();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File("movie.Mjpeg");
        if (f.exists()) {
            //Create a Main object
            this.s = new Streamer();
            //show GUI: (opcional!)
            s.pack();
            s.setVisible(true);
        } else
            System.out.println("Ficheiro de video não existe");
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
        switch (msg.getTipo()) {
            case "ar" -> {
                System.out.println("A iniciar a transmissão...");
                s.addIp(ip);
                if(s.getNumberIps()==1){
                    s.startStream();
                }
            }
            case "dr" -> { //caso da mensagem de "fecho de rota"
                System.out.println("A fechar a transmissao...");
                s.remIp(ip);
                if(s.getNumberIps()==0){
                    s.stopStream();
                }
            }
        }
    }
}

