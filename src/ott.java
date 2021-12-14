import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class ott {
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
                    Client c = null;
                    try {
                        c = new Client(InetAddress.getByName(args[1]));
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    c.run();
                }else
                    System.out.println("O cliente apenas pode estar ligado a um router.");
            }
        }
    }

    static byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) { --i; }
        return Arrays.copyOf(bytes, i + 1);
    }

    static List<InetAddress> makeList(String [] args){
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
}
