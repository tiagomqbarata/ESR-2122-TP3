import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rota {
    private InetAddress servidor;
    private int porta_servidor;
    private String origem;
    private int saltos;
    private List<String> destinos = new ArrayList<>(); //TODO - vizinhos?
    private int estado; //0 - inativa; 1 - ativa

    /* TODO - checkar esta classe
    identificação do servidor, identificação do fluxo, valor da métrica, etc;
    cada nó deve atualizar a tabela de rotas com: Servidor/Fluxo, Origem, Métrica, Destinos, Estado da Rota
     */

    public Rota(InetAddress servidor, int porta_servidor, String origem, int saltos, List<String> destinos){
        this.servidor = servidor;
        this.porta_servidor = porta_servidor;
        this.origem = origem;
        this.saltos = saltos;
        this.destinos = destinos;
        this.estado = 0;
    }

    public InetAddress getServidor() {
        return servidor;
    }

    public void setServidor(InetAddress servidor) {
        this.servidor = servidor;
    }

    public int getPorta_servidor() {
        return porta_servidor;
    }

    public void setPorta_servidor(int porta_servidor) {
        this.porta_servidor = porta_servidor;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public int getSaltos() {
        return saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public List<String> getDestinos() {
        return destinos;
    }

    public void setDestinos(List<String> destinos) {
        this.destinos = destinos;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void activate() {
        this.estado = 1;
    }

    @Override
    public String toString() {
        return "Rota{" +
                "servidor=" + servidor +
                ", porta_servidor=" + porta_servidor +
                ", origem='" + origem + '\'' +
                ", saltos=" + saltos +
                ", destinos=" + destinos +
                ", estado=" + estado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rota rota = (Rota) o;
        return porta_servidor == rota.porta_servidor && saltos == rota.saltos && estado == rota.estado && servidor.equals(rota.servidor) && origem.equals(rota.origem) && destinos.equals(rota.destinos);
    }
}
