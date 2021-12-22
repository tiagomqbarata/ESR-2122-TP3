import java.io.*;
import java.net.*;
import java.util.*;

public class ott {
    public static final int TCP_PORT = 12345;
    public static final int RTP_PORT = 25000; //port where the client will receive the RTP packets

    public static void main(String[] args) {

        try {
            System.out.println(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        switch (args[0]) {
            case "-s" -> {
                Server s = new Server(makeList(args));
                s.run();
            }
            case "-r" -> {
                Router r = new Router(makeList(args));
                r.run();
            }
            case "-c" -> {
                if(args.length == 2){
                    try {
                        Client c = new Client(InetAddress.getByName(args[1]));
                        c.run();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }else
                    System.out.println("O cliente apenas pode estar ligado a um router.");
            }
        }
    }

    private static List<InetAddress> makeList(String [] args){
        List<InetAddress> vizinhos = new ArrayList<>();
        for(int i = 1; i<args.length; i++){
            try {
                vizinhos.add(InetAddress.getByName(args[i]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return vizinhos;
    }

    public static Mensagem recebeMensagemTCP(Socket s) {
        return recebeMensagemTCP(inCreate(s));
    }

    public static Mensagem recebeMensagemTCP(DataInputStream in){
        byte[] data = new byte[512];
        try {
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Mensagem(data);
    }

    public static void enviaMensagemTCP(Socket s, Mensagem msg){
        enviaMensagemTCP(outCreate(s), msg);
    }

    public static void enviaMensagemTCP(DataOutputStream out, Mensagem msg){
        msg.incSaltos();
        try {
            out.write(msg.toBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataOutputStream outCreate(Socket s){
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static DataInputStream inCreate(Socket s){
        DataInputStream in = null;

        try {
            in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return in;
    }

    public static Socket socketTCPCreate(InetAddress addr){
        Socket s = null;

        try {
            s = new Socket(addr, TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

}
