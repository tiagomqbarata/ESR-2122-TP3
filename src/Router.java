import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/*
    TODO - LER ESTA PORRA QUE DEU TRABALHO FAZER/PENSAR
   enunciado resumido:
       servidor vai enviar mensagem de controlo para criar rotas - inclui identificação do servidor, identificação do fluxo, valor da métrica, etc;
       cada nó deve atualizar a tabela de rotas com: Servidor/Fluxo, Origem, Métrica, Destinos, Estado da Rota;
       todos os nós vão difundir a mensagem com o valor da métrica atualizado (+1 salto) para todos os vizinhos menos de onde a receberam;
       estado da rota é inicialmente inativo
       são ativadas pelo cliente através de uma mensagem de ativação de rota pelo percurso inverso
       cada nó deve reencaminhar uma mensagem de pedido de ativação seguindo o campo "origem" da rota

   o que acho que devemos fazer:
       servidor envia mensagem para nó1
       nó1 regista rota para o servidor
       nó1 envia mensagem para nó2 que indica que se quer ir ao servidor é para ele que envia e que o valor da métrica é 1
       nó2 repete o que nó1 fez mas adiciona 1 à métrica
       e assim sucessivamente
       quando um cliente inicia e quiser pedir dados ao servidor deve enviar uma mensgaem de pedido de rota aos seus vizinhos
       cada vizinho deve responder com "esta é a rota que eu tenho e tem este valor de saltos"
       o cliente decide qual a melhor de acordo com o menor nº de saltos
       e envia essa informação ao vizinho selecionado que adiciona o cliente à rota e ativa a rota em questão enviando ainda mensagem ao servidor com o pedido de dados
   */

public class Router {
    private Map<InetAddress, Integer> vizinhos;
    private Map<InetAddress, Rota> routing_table;
    private InetAddress ipServidor;
    private DatagramSocket socket;

    public Router(Map<InetAddress, Integer> vizinhos) {
        this.vizinhos = vizinhos;
        this.routing_table = new TreeMap<>();
    }

    public boolean addRoute(InetAddress ip, int porta, InetAddress origem, int saltos){
        Rota r = routing_table.get(ip);
        if(r == null || saltos < r.getSaltos()){
            r = new Rota(porta, origem, saltos);
            routing_table.put(ip,r);

            return true;
        }
        return false;
    }

    public void addServidor(InetAddress ipServidor){
        this.ipServidor = ipServidor;
    }

    public void addRoute(Rota rota){
    //    this.routing_table.add(rota);
    }

    public void run(){
        new Thread(() -> {
            while (true){
                byte[] messageReceived = new byte[512];
                DatagramPacket pacote = new DatagramPacket(messageReceived,512);

                try {
                    socket.receive(pacote);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String data = Arrays.toString(pacote.getData());
                //TODO - dependendo do formato da mensagem, tratar os dados e adicionar rota

                /* reencaminhar a mensagem para todos os vizinhos, não esquecer adicionar um salto à métrica
                try {
                    socket.send(new DatagramPacket());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }).start();

    }



}
