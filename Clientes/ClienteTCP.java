package Clientes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteTCP {

    public static Socket socket;
    public long time;
    public static DataOutputStream out;
    public static String mensagemRecebida;

    public static void main(String[] args) {
        Thread x = null;
        if (socket == null) {
            try {
                socket = new Socket("localhost", 1235);//Mudar para IP do servidor
                out = new DataOutputStream(socket.getOutputStream());
                System.out.println("CLIENTE TCP Inicializado...");
            } catch (SocketException ex) {
                System.out.println("Erro ao criar SOCKET");
            } catch (IOException ex) {
                System.out.println("Erro ao criar SOCKET II");
            }

            //Fica recebendo as msgs que o servidor manda para confirmar CONEXAO com o SERVIDOR
            x = new Thread() {
                public void run() {
                    while (true) {
                        //instanciar um pacote datagrama "vazio" de até 10 0bytes
                        DataInputStream in = null;
                        try {
                            in = new DataInputStream(socket.getInputStream());
                        } catch (IOException ex) {
                            Logger.getLogger(ClienteTCP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            //servidor começa a aguardar mensagens pela rede por referência
                            mensagemRecebida = in.readUTF();
                        } catch (IOException ex) {
                            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println(mensagemRecebida);
                    }
                }
            };
            x.start();

        }
        int cont = 0;
        while (cont < 10) {
            try {
                //mude conforme a quantidade de mensagens para o teste queira
                Thread.sleep(50); //50 mili-seconds para poder atualizar a interface a tempo < que isso ele pula as mensagens na tela, esse valor vai ser levado em conta na hora de dar o tempo de comunicação
            } catch (InterruptedException ex) {
                Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
            enviaMensagem("Mensagem Cliente TCP nº " + cont + "#_" + System.nanoTime());
            cont++;
        }

        enviaMensagem("TERMINEI");
    }

    public static void enviaMensagem(String mensagemEnviar) {
        try {
            out.writeUTF(mensagemEnviar);
        } catch (IOException ex) {
            System.out.println("ERRO AO ENVIAR MENSAGEM " + ex.toString());
        }
    }
}
