
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mecanismos {

    //declaração das váriaveis da classe
    //UDP
    private DatagramSocket socket;
    private DatagramPacket pacoteRecebido;
    private String ipPCTestado;
    private int portaPCTestado;
    private String mensagemRecebida;
    private byte buffer[];
    private long tempo;
    private long tempo2;
    private String somaUDp = "";
    private String somaTCp = "";
    private TelaPrincipal tela;
    //TCP
    private ServerSocket servidor;
    private Socket cliente;
    private DataInputStream in;
    //RMI

    private long somaTotal = 0;
    private String somaRMI = "";
    private String[] soma;
    private String mensagemRMI = "hello world";

    //Multicast
    private MulticastSocket s;
    private InetAddress grupo;
    private String somaMult = "";
    private long tempo3;
    private byte[] buffered;
    private DatagramPacket pacoteRecebidoMult;

    /**
     * Método para pegar a instância da tela principal para poder fazer
     * modificação na interface da mesma
     *
     * @param tela recebe um objeto da classe TelaPrincipal
     */
    public void setTela(TelaPrincipal tela) {
        this.tela = tela;
    }

    public String getSomaUDp() {
        return somaUDp;
    }

    /**
     * * Esse Método executa um teste Socket UDP
     *
     * @param porta tipo Integer*
     */
    public int testeUDP(int porta) {
        int auxiliar = 0;
        //cria socket é executada somente uma vez
        try {
            socket = new DatagramSocket(porta);
            auxiliar = 1;
        } catch (SocketException ex) {
            auxiliar = 0;
        }
        new Thread(() -> {
            while (true) {
                pacoteRecebido = new DatagramPacket(new byte[100], 100);
                try {
                    socket.receive(pacoteRecebido);

                    buffer = pacoteRecebido.getData();
                    mensagemRecebida = new String(buffer, 0, buffer.length).trim();
                    if (mensagemRecebida.equals("TERMINEI")) {
                        String[] soma = somaUDp.split(",");
                        int qtd = soma.length;
                        long somaTotal = 0;
                        for (int i = 0; i < qtd; i++) {
                            somaTotal += Integer.parseInt(soma[i]);
                        }

                        tela.MostraTela("UDP," + somaTotal / qtd);
                    } else {
                        String msgRec[] = mensagemRecebida.split("#_");
                        long duracao = System.nanoTime() - Long.parseLong(msgRec[1]);
                        tempo = duracao;
                        somaUDp = somaUDp + tempo + ",";
                        ipPCTestado = pacoteRecebido.getAddress().getHostAddress();
                        portaPCTestado = pacoteRecebido.getPort();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Mecanismos.class.getName()).log(Level.SEVERE, null, ex);
                }
                DatagramPacket pacoteEnviar;
                try {
                    pacoteEnviar = new DatagramPacket("LISTEN...".getBytes(), "LISTEN...".getBytes().length,
                            InetAddress.getByName(ipPCTestado), portaPCTestado);
                    socket.send(pacoteEnviar);

                } catch (IOException ex) {
                    Logger.getLogger(Mecanismos.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            //}
        }).start();
        return auxiliar;
    }

    /**
     * Método cria um servidor TCP que fica aguardando conexões e calcula o
     * tempo levado de um cliente até ele em nano segundos
     *
     * @param porta porta aberta na máquina local por onde o servidor pode rodar
     * @return 1 ou 0 se 1 servidor criado com sucesso se 0 algum problema no
     * servidor
     */
    public int testeTCP(int porta) {
        int auxiliarTCP = 0;
        try {
            //criação do servidor na porta 1234
            servidor = new ServerSocket(porta);
            //System.out.println("Inicializando TCP...");
            auxiliarTCP = 1;
        } catch (IOException ex) {
            //Logger.getLogger(Mecanismos.class.getName()).log(Level.SEVERE, null, ex);
            auxiliarTCP = 0;
            System.out.println("PORTA TCP JÁ EM USO!!");
        }

        new Thread(() -> {

            //para aceitar mais de uma conexão para poder ser possível testar mais de um cliente TCP
            while (true) {
                try {
                    //aguarda a conexão de um cliente
                    cliente = servidor.accept();
                } catch (IOException ex) {
                    System.out.println("Erro ao conectar com o cliente " + ex);
                }

                new Thread(() -> {
                    Socket meuCliente = cliente;
                    try {
                        in = new DataInputStream(meuCliente.getInputStream());
                        while (true) {
                            //aguarda mensagem do cliente junto com o Tempo de envio
                            //e faz o cálculo para ver a diferença de tempo
                            String mensagemRecebida = in.readUTF();
                            if (mensagemRecebida.equals("TERMINEI")) {
                                String[] soma = somaTCp.split(",");
                                int qtd = soma.length;
                                long somaTotal = 0;
                                for (int i = 0; i < qtd; i++) {
                                    somaTotal += Integer.parseInt(soma[i]);
                                }

                                tela.MostraTela("TCP," + somaTotal / qtd);
                            } else {
                                String msgRec[] = mensagemRecebida.split("#_");
                                //cálculo do TEMPO que levou para chegar a mensagem no SERVIDOR TCP
                                long duracao = (long) System.nanoTime() - Long.parseLong(msgRec[1]);
                                tempo2 = duracao;
                                somaTCp = somaTCp + tempo2 + ",";
                                //spy = msgRec[0]+" TEMPO (nano-segundos):"+tempo;
                                DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
                                out.writeUTF("LISTEN...");
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println("Erro ao conectar com o Cliente " + ex);
                    }

                }).start();
            }

        }).start();
        return auxiliarTCP;
    }

    public int testeRPC(int porta) {
        int auxiliar = 0;

        return auxiliar;
    }

    /**
     * Cria um Servidor RMI
     *
     * @return 1 ou 0 se 1 servidor criado com sucesso se 0 algum problema no
     * servidor
     */
    public int testeRMI() {
        int auxiliar = 0;
        String HOST_URL = "rmi://localhost/Torpedo";

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            MensagemRMI objetoRemoto = new MensagemRMI();
            objetoRemoto.setTp(tela);
            Naming.bind(HOST_URL, objetoRemoto);
            auxiliar = 1;
        } catch (RemoteException | AlreadyBoundException | MalformedURLException ex) {
            System.out.println("Erro ao criar Servidor " + ex);
            auxiliar = 0;
        }
        return auxiliar;
    }

    /**
     * Cria um Cliente/Servidor Multicast
     *
     * @return 1 ou 0 se 1 servidor criado com sucesso se 0 algum problema no
     * servidor
     */
    public int testeMulticast() {
        int auxiliar = 0;
        int inspetor = 0;

        //inicia o multicast
        try {
            //Configuração padrão
            grupo = InetAddress.getByName("239.1.2.3");
            s = new MulticastSocket(3456);
            s.joinGroup(grupo);
            auxiliar = 1;
            //para guardar o IP do servidor Multicast no cliente
            enviarMulticast("LISTEN..");
        } catch (Exception e) {
            System.out.println("Erro na criacao do Multicast " + e);
            auxiliar = 0;
        }
        //Criação da thread para recebimento de mensagens
        new Thread() {
            @Override
            public void run() {
                int cod = -1;
                while (true) {
                    try {
                        buffered = new byte[10000];
                        pacoteRecebidoMult = new DatagramPacket(buffered, buffered.length);
                        try {
                            s.receive(pacoteRecebidoMult);//receive bloqueante
                        } catch (IOException ex) {
                            System.out.println("Erro ao recber pacote " + ex);
                        }

                        String mensagemRecebida = new String(pacoteRecebidoMult.getData(), 0, pacoteRecebidoMult.getLength());

                        //String ipAddress = (pacoteRecebidoMult.getAddress()).getHostAddress();//ip do cliente de recebimento da mensagem
                        String[] msg = mensagemRecebida.split("_#");

                        if (mensagemRecebida.equals("TERMINEI")) {
                            cod = 1;
                            String[] soma = somaMult.split(",");
                            int qtd = soma.length;
                            long somaTotal = 0;
                            for (int i = 0; i < qtd; i++) {
                                somaTotal += Long.parseLong(soma[i]);
                            }

                            tela.MostraTela("Multicast," + somaTotal / qtd);
                            enviarMulticast("OBRIGADO!");
                        } else if (!mensagemRecebida.equals("") && !mensagemRecebida.equals("LISTEN..")&& !mensagemRecebida.equals("OBRIGADO!")) {
                            cod = 2;
                            String msgRec[] = mensagemRecebida.split("#_");
                            //cálculo do TEMPO que levou para chegar a mensagem no SERVIDOR Multicast
                            long duracao = (long) System.nanoTime() - Long.parseLong(msgRec[1]);
                            tempo3 = duracao;
                            somaMult = somaMult + tempo3 + ",";
                            //responde o cliente
                            enviarMulticast("LISTEN..");
                        }
                    } catch (Exception e) {
                        System.out.println("Pacote entregue Vazio " + e+ ", cod = "+cod);
                    }
                }
            }
        }.start();

        return auxiliar;
    }

    public void enviarMulticast(String aux) {

        try {
            aux = new String(aux.getBytes("ISO-8859-1"), "ISO-8859-1");
            System.out.println(aux);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Erro no encoding " + ex);
        }
        DatagramPacket pacote = null;
        pacote = new DatagramPacket(aux.getBytes(), aux.length(), grupo, 3456);

        try {
            s.send(pacote);//send não bloqueante
        } catch (IOException ex) {
            System.out.println("Erro ao enviar pacote " + ex);
        }
    }
}
