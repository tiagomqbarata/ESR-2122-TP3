import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Mensagem implements Serializable {
    private String dados;
    private String tipo; //r - reconhecimento; ar - ativação de rota; sr - saltos de rota
    private int saltos;
    private InetAddress ipOrigemMensagem;
    private InetAddress ipDestino;

    public Mensagem(String tipo, InetAddress ipOrigemMensagem){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipOrigemMensagem = ipOrigemMensagem;
    }

    public Mensagem(String tipo, InetAddress ipOrigemMensagem, InetAddress ipDestino, String dados){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipOrigemMensagem = ipOrigemMensagem;
        this.ipDestino = ipDestino;
        this.dados = dados;
    }


    public Mensagem(){
        this.tipo = "EEE";
        this.saltos = -1;
    }

    public Mensagem(byte[] data){
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Mensagem m = (Mensagem) in.readObject();

            this.tipo = m.getTipo();
            this.saltos = m.getSaltos();
            this.ipOrigemMensagem = m.getIpOrigemMensagem();

        } catch (IOException e) {
            System.out.println("Erro ao criar o objeto");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Erro ao encontrar a classe");
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                System.out.println("Erro a fechar a deserialização... ignorado");
                // ignore close exception
            }
        }
    }


    public void incSaltos(){
        this.saltos += 1;
    }

    public byte[] toBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            yourBytes = bos.toByteArray();

        } catch (IOException e) {
            System.out.println("Erro na serialização");
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
                System.out.println("Erro a fechar a serialização ... ignorado");
            }
        }
        return yourBytes;
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

    public InetAddress getIpDestino(){
        return this.ipDestino;
    }

    public String toString(){
        return "{ tipo: " + this.tipo + " ; ttl: " + this.saltos + " ipOrigem: " + this.ipOrigemMensagem + " }";
    }

    byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) { --i; }
        return Arrays.copyOf(bytes, i + 1);
    }

}
