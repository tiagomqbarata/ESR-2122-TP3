import java.net.InetAddress;
import java.util.*;

public class Rota {
    private InetAddress servidor;
    private static final int porta = 12345;
    private InetAddress origem;
    private int saltos;
    private int estado; //0 - inativa; 1 - ativa

    /*
    identificação do servidor, identificação do fluxo, valor da métrica, etc;
    cada nó deve atualizar a tabela de rotas com: Servidor/Fluxo, Origem, Métrica, Destinos, Estado da Rota
     */

    public Rota(InetAddress origem, int saltos){
        this.origem = origem;
        this.saltos = saltos;
        this.estado = 0;
    }

    public InetAddress getServidor() {
        return this.servidor;
    }

    public void setServidor(InetAddress servidor) {
        this.servidor = servidor;
    }

    public int getPortaOrigem() {
        return this.porta;
    }

    public InetAddress getOrigem() {
        return this.origem;
    }

    public void setOrigem(InetAddress origem) {
        this.origem = origem;
    }

    public int getSaltos() {
        return this.saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public int getEstado() {
        return this.estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
