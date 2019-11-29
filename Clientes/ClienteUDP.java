package Clientes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael LS
 */
public class ClienteUDP {

    public static DatagramSocket socket;
    public long time;
    
    public static void main(String[] args) {
        Thread x = null;
        if (socket == null) {
            try {
                socket = new DatagramSocket();
                System.out.println("CLIENTE UDP Inicializado...");
            } catch (SocketException ex) {
                System.out.println("Erro ao criar SOCKET");
            }

            //Fica recebendo as msgs que o servidor manda
            x = new Thread() {
                public void run() {
                    while (true) {
                        //instanciar um pacote datagrama "vazio" de até 10 0bytes
                        DatagramPacket pacoteRecebido = new DatagramPacket(new byte[100], 100);
                        try {
                            //servidor começa a aguardar mensagens pela rede por referência
                            socket.receive(pacoteRecebido);
                        } catch (IOException ex) {
                            Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //retiramos do pacote os bytes da mensagem
                        byte buffer[] = pacoteRecebido.getData();
                        //convertemos os bytes em string
                        String mensagemRecebida = new String(buffer, 0, buffer.length);
                        System.out.println(mensagemRecebida);
                    }
                }
            };
            x.start();
            
        }
        int cont = 0;
        while (cont < 10) {try {
            //mude conforme a quantidade de mensagens para o teste quera
            Thread.sleep(50); //50 mili-seconds para poder atualizar a interface a tempo < que isso ele pula as mensagens na tela, esse valor vai ser levado em conta na hora de dar o tempo de comunicação
            } catch (InterruptedException ex) {
                Logger.getLogger(ClienteUDP.class.getName()).log(Level.SEVERE, null, ex);
            }
            enviaMensagem("Mensagem Cliente UDP nº " + cont+"#_"+System.nanoTime());
            cont++;
        }
        enviaMensagem("TERMINEI");
    }

    public static void enviaMensagem(String mensagemEnviar) {
        try {
            System.out.println(mensagemEnviar);
            //monto um pacote datagrama para enviar
            DatagramPacket pacoteEnviar = new DatagramPacket(
                    mensagemEnviar.getBytes(),
                    mensagemEnviar.getBytes().length,
                    InetAddress.getByName("localhost"), //DEVE SER MUDADO PARA TESTE EM MÁQUINAS DIFERENTES, TROCANDO localhost pelo IP do SERVIDOR DE TESTES
                    1234);
            //envia para o servidor
            socket.send(pacoteEnviar);
        } catch (UnknownHostException ex) {
            System.out.println("ERRO AO ENVIAR MENSAGEM " + ex.toString());
        } catch (IOException ex) {
            System.out.println("ERRO AO ENVIAR MENSAGEM" + ex.toString());
        }
    }
}
