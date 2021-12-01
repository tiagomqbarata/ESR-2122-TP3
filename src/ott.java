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
            case "-r":
        }
    }
}
