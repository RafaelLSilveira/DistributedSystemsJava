import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Rafael LS
 */
public interface IMensagemRMI extends Remote{
    //permite que o cliente RMI envie uma mensagem para o servidor RMI e seja respondido pelo mesmo
    public String enviarMensagemRMI(String msg) throws RemoteException;
}