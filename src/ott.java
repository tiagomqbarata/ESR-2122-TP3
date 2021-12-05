import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ott {
    public static void main(String[] args) {
        List<String> vizinhos;

        switch (args[0]) {
            case "-s" -> {  //TODO - checkar cena dos maps de ips->portas
                            vizinhos = Arrays.stream(args[1].split(",")).toList();
                            Map<InetAddress, Integer> ip_vizinhos = new HashMap<>();
                            vizinhos.forEach(vizinho -> {
                                try {
                                    ip_vizinhos.put(InetAddress.getByName(vizinho.split(":")[0]), Integer.parseInt(vizinho.split(":")[1]));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            });
                            Server s = new Server(ip_vizinhos);
                            s.run();
            }
            case "-c" -> {
                            vizinhos = Arrays.stream(args[1].split(",")).toList();
                            Map<InetAddress, Integer> ip_vizinhos = new HashMap<>();
                            vizinhos.forEach(vizinho -> {
                                try {
                                    ip_vizinhos.put(InetAddress.getByName(vizinho.split(":")[0]), Integer.parseInt(vizinho.split(":")[1]));
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                }
                            });
                            Client c = new Client(ip_vizinhos);
                            c.run();
            }
            case "-r" -> {  // encaminhadores - recebem pacotes e encaminham de acordo com o destino e a sua tabela de rotas
                            vizinhos = Arrays.stream(args[1].split(",")).toList();
                            Router r = new Router(vizinhos);
            }
        }
    }

    static byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) { --i; }
        return Arrays.copyOf(bytes, i + 1);
    }
}
