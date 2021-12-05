import java.util.Arrays;

public class ott {
    public static void main(String[] args) {
        switch (args[0]){
            case "-s":  Server s = new Server();
                        s.run();
                        break;
            case "-c":  String[] ipPort = args[1].split(":");
                        Client c = new Client(ipPort[0], Integer.parseInt(ipPort[1]));
                        c.run();
                        break;
            case "-r": // encaminhadores - recebem pacotes e encaminham de acordo com o destino e a sua tabela de rotas
                        Router r = new Router();
                        break;
        }
    }

    static byte[] trim(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0)
        {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
}
