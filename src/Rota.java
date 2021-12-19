import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Rota {
    private InetAddress servidor;
    private Socket origem; //
    private int saltos;
    private Boolean estado; //false - inativa; true - ativa

    public Rota(Socket origem, int saltos){
        this.origem = origem;
        this.saltos = saltos;
        this.estado = false;
    }

    public InetAddress getServidor() {
        return this.servidor;
    }

    public void setServidor(InetAddress servidor) {
        this.servidor = servidor;
    }

    public Socket getOrigem() {
        return this.origem;
    }

    public void setOrigem(Socket origem) {
        this.origem = origem;
    }

    public int getSaltos() {
        return this.saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public Boolean getEstado() {
        return this.estado;
    }

    public void ativarRota() {
        this.estado = true;
    }

    public void desativarRota() {this.estado = false;}
}
