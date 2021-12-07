import java.net.InetAddress;
import java.util.*;

public class Rota {
    private InetAddress servidor;
    private int portaOrigem;
    private InetAddress origem;
    private int saltos;
    private int estado; //0 - inativa; 1 - ativa

    /* TODO - checkar esta classe
    identificação do servidor, identificação do fluxo, valor da métrica, etc;
    cada nó deve atualizar a tabela de rotas com: Servidor/Fluxo, Origem, Métrica, Destinos, Estado da Rota
     */

    public Rota(int porta, InetAddress origem, int saltos){
        this.portaOrigem = porta;
        this.origem = origem;
        this.saltos = saltos;
        this.estado = 0;
    }

    public InetAddress getServidor() {
        return servidor;
    }

    public void setServidor(InetAddress servidor) {
        this.servidor = servidor;
    }

    public int getPortaOrigem() {
        return portaOrigem;
    }

    public void setPortaOrigem(int portaOrigem) {
        this.portaOrigem = portaOrigem;
    }

    public InetAddress getOrigem() {
        return origem;
    }

    public void setOrigem(InetAddress origem) {
        this.origem = origem;
    }

    public int getSaltos() {
        return saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
