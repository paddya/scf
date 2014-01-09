package scfd;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.Reconnect;
import scf.parser.Parser;
import scf.parser.exception.ParserException;



public class Server
{

    public static void main(String[] args) throws IOException
    {
        final ExecutorService pool;
        final ServerSocket serverSocket;
        int port = 13370 + (int)(Math.random() * 10);
        System.out.println("Server port: " + port);
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



    @Override
    public void run()
    {
        try {
            // Accept loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client");
                
                
                // Either create a new thread (CLIENTHELLO) or unfreeze an old one (RECONNECT)
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String s = br.readLine();
                Command cmd;
                
                try {
                    cmd = Parser.parse(s);
                } catch (ParserException ex) {
                    System.out.println("Parse error on first message. Client is not even trying.");
                    continue;
                }
                
                try {
                    // CLIENTHELLO
                    ClientHello clientHello = (ClientHello) cmd;
                    PlayerThread playerThread = new PlayerThread(clientSocket, clientHello.getPlayerID());
                    
                    
                    // Save playerThread in PlayerThreadMap for future reconnects
                    PlayerThreadMap.getInstance().put(clientHello.getPlayerID(), playerThread);
                    
                    // Run playerThread
                    pool.execute(playerThread);
                } catch (ClassCastException e) {
                    try {
                        // RECONNECT
                        Reconnect reconnect = (Reconnect) cmd;
                        PlayerThread playerThread;
                        
                        
                        // Retrieve old playerThread for this player
                        playerThread = PlayerThreadMap.getInstance().get(reconnect.getPlayerID());
                        
                        
                        // Rerun this playerThread
                        playerThread.setSocket(clientSocket);
                    } catch (ClassCastException e2) {
                        System.out.println("First message from client to server MUST be CLIENTHELLO or RECONNECT");
                        continue;
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