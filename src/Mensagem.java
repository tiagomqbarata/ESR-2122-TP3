import java.net.InetAddress;

public class Mensagem {
    private String tipo;
    private int saltos;
    private InetAddress ipServidor;

    public Mensagem(String tipo, InetAddress ip){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipServidor = ip;
    }

    public Mensagem(String tipo){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipServidor = null;
    }

    public Mensagem(byte[] data){ //ISTO É LINDO, OBRIGADO!!!!!!
        String dados = new String(data);
        this.tipo = dados.split("::")[0];
        this.saltos = Integer.parseInt(dados.split("::")[1]);
    }


    public void incSaltos(){
        this.saltos += 1;
    }

    public byte[] toBytes(){
        String data = this.tipo+"::"+this.saltos;
        return data.getBytes();
    }

    public int length(){
        return this.toBytes().length;
    }

    public String getTipo() {
        return tipo;
    }

    public int getSaltos() {
        return saltos;
    }

    public InetAddress getIpServidor(){
        return this.ipServidor;
    }

    public String toString(){
        return "{ tipo: " + this.tipo + " ; informação: " + this.saltos + " }";
    }
}
