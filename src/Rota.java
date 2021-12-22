import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Rota {
    private final Socket origem;
    private final int saltos;
    private Boolean estado; //false - inativa; true - ativa

    public Rota(Socket origem, int saltos){
        this.origem = origem;
        this.saltos = saltos;
        this.estado = false;
    }

    public Socket getOrigem() {
        return this.origem;
    }

    public int getSaltos() {
        return this.saltos;
    }

    public Boolean getEstado() {
        return this.estado;
    }

    public void ativarRota() {
        this.estado = true;
    }

    public void desativarRota() {this.estado = false;}
}
