import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String,String> routing_table = new HashMap<>(); //string,string -> ipadress destino, ipadress prox salto

    public Router(){

    }

    /*
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

}
