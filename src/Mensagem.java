import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Mensagem implements Serializable {
    private String tipo;    // r   - reconhecimento;
                            // ar  - ativação de rota;
                            // dr  - desativação de rota;
                            // drC - desativação de rota de um cliente (inclui fecho do socket);
                            // EEE - representa um erro na mensagem
    private int saltos;
    private InetAddress ipOrigemMensagem;

    public Mensagem(String tipo, InetAddress ipOrigemMensagem){
        this.tipo = tipo;
        this.saltos = 1;
        this.ipOrigemMensagem = ipOrigemMensagem;
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
            }
        }
    }

    public byte[] toBytes(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println("Erro na serialização");
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                System.out.println("Erro a fechar a serialização ... ignorado");
            }
        }
        return bytes;
    }

    public void incSaltos(){
        this.saltos += 1;
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
        return "{ tipo: " + this.tipo + " ; ttl: " + this.saltos + " ipOrigem: " + this.ipOrigemMensagem + " }";
    }

}
