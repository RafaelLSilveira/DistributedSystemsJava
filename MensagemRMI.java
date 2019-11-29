
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/**
 *
 * @author Rafael LS
 */
 //implements IMensagemRMI

public class MensagemRMI extends UnicastRemoteObject implements IMensagemRMI
{
    private long somaTotal=0;
    private String somaRMI = "";
    private String[] soma;
    private long resultado=0;
    private TelaPrincipal tp;
    private Mecanismos mecanismo = new Mecanismos();
    
    public MensagemRMI() throws RemoteException{
        
    }
    /**
     * Método para pegar a instância da tela principal para poder fazer
     * modificação na interface da mesma
     *
     */
    public void setTp(TelaPrincipal tp) {
        this.tp = tp;
    }


    public long getResultado() {
        return resultado;
    }

//    @Override
    public String enviarMensagemRMI(String msg) throws RemoteException {
        
        long tempo;
        String msgRMI = "LISTEN..";
        if (msg.equals("TERMINEI")) {
             soma = somaRMI.split(",");
             System.out.println(Arrays.toString(soma));
             int qtd = soma.length;
            //zera as variaveis globais para um novo usario usar
            System.out.println("QUANTIDADE = "+qtd);
           for (int x = 0; x < qtd; x++) {
             somaTotal = Long.parseLong(soma[x])+somaTotal;
            }
            System.out.println(somaTotal);
            long resultado = somaTotal/qtd;
            System.out.println("Resultado"+resultado);

            this.resultado = resultado;
            
            tp.MostraTela("RMI,"+resultado);
            
            msgRMI =String.valueOf(resultado);
            soma = null;
            somaRMI = "";
        } else {
            String msgRec[] = msg.split("#_");
            //cálculo do TEMPO que levou para chegar a mensagem no SERVIDOR TCP
            long duracao = (long) System.nanoTime() - Long.parseLong(msgRec[1]);
            tempo = duracao;
            somaRMI = somaRMI + tempo + ",";
        }
        return msgRMI;
    }
}
