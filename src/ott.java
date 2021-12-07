import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ott {
    public static void main(String[] args) {
        Map<InetAddress, Integer> vizinhosMap = new HashMap<>();

        switch (args[0]) {
            case "-s" -> {  //TODO - checkar cena dos maps de ips->portas
                            for(int i = 1; i<args.length; i++){
                                String [] content = args[i].split(":");
                                try {
                                    vizinhosMap.put(InetAddress.getByName(content[0]), Integer.parseInt(content[1]));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            }

                            Server s = new Server(vizinhosMap);
                            s.run();
            }
            case "-c" -> {
                            for(int i = 1; i<args.length; i++){
                                String [] content = args[i].split(":");
                                try {
                                    vizinhosMap.put(InetAddress.getByName(content[0]), Integer.parseInt(content[1]));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            }
                            Client c = new Client(vizinhosMap);
                            c.run();
            }
            case "-r" -> {  // encaminhadores - recebem pacotes e encaminham de acordo com o destino e a sua tabela de rotas
                            for(int i = 1; i<args.length; i++){
                                String [] content = args[i].split(":");
                                try {
                                    vizinhosMap.put(InetAddress.getByName(content[0]), Integer.parseInt(content[1]));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            }
                            Router r = new Router(vizinhosMap);
            }
        }
    }

    static byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) { --i; }
        return Arrays.copyOf(bytes, i + 1);
    }
}
