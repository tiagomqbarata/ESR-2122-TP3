import java.net.InetAddress;

public class Mensagem {
    private String tipo; //r - reconhecimento; ar - ativação de rota; sr - saltos de rota
    private int saltos;
    private InetAddress ipOrigemMensagem;

    public Mensagem(String tipo, InetAddress ipOrigemMensagem){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipOrigemMensagem = ipOrigemMensagem;
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

    public InetAddress getIpOrigemMensagem(){
        return this.ipOrigemMensagem;
    }

    public String toString(){
        return "{ tipo: " + this.tipo + " ; informação: " + this.saltos + " }";
    }
}
