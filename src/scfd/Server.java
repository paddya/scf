package scfd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.Reconnect;
import scf.parser.Parser;



public class Server
{

    public static void main(String[] args) throws IOException
    {
        final ExecutorService pool;
        final ServerSocket serverSocket;
        int port = 13370;
        Thread serverThread;


        // Initialize listening socket and handler
        pool = Executors.newCachedThreadPool();
        serverSocket = new ServerSocket(port);
        serverThread = new Thread(new Listener(pool, serverSocket));
        serverThread.start();


        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                pool.shutdown();

                try {
                    pool.awaitTermination(2L, TimeUnit.SECONDS);
                    if (!serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                } catch (InterruptedException e) {
                }
            }
        });
    }
}



class Listener implements Runnable
{

    private final ServerSocket serverSocket;
    private final ExecutorService pool;



    public Listener(ExecutorService pool, ServerSocket serverSocket)
    {
        this.pool = pool;
        this.serverSocket = serverSocket;
    }



    public void run()
    {
        try {
            // Accept loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client");
                
                
                // Initialize
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String s = br.readLine();
                Command cmd = Parser.parse(s);
                
                try {
                    ClientHello clientHello = (ClientHello) cmd;
                    PlayerThread playerThread = new PlayerThread(clientSocket, clientHello.getPlayerID());
                    pool.execute(playerThread);
                } catch (ClassCastException e) {
                    try {
                        Reconnect reconnect = (Reconnect) cmd;
                        
                    } catch (ClassCastException e2) {
                        System.out.println("First message from client to server MUST be CLIENTHELLO or RECONNECT");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to handle server socket");
        } finally {
            pool.shutdown();

            try {
                pool.awaitTermination(4L, TimeUnit.SECONDS);
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    }
}