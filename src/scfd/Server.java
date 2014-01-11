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
import scf.model.command.error.Err_Nicknameinuse;
import scf.model.command.response.Rpl_Reconnected;
import scf.model.command.response.Rpl_Serverhello;
import scf.parser.Parser;
import scf.parser.exception.ParserException;



/**
 * The server is the network front end it handles client connections
 * (CLIENTHELLO) and reconnects (RECONNECT)
 *
 * @author Markus Schlegel
 */
public class Server
{

    public static ExecutorService pool;



    public static void main(String[] args) throws IOException
    {
        final ServerSocket serverSocket;
        int port = 13370 + (int) (Math.random() * 100); // Randomized for debugging purposes
        System.out.println("Server port: " + port);
        Thread serverThread;


        // Initialize listening socket and handler
        Server.pool = Executors.newCachedThreadPool();
        serverSocket = new ServerSocket(port);
        serverThread = new Thread(new Listener(serverSocket));
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



    public Listener(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }



    @Override
    public void run()
    {
        try {
            // Accept loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Porter porter = new Porter(clientSocket);
                Server.pool.execute(porter);
            }
        } catch (IOException e) {
            System.out.println("Failed to handle server socket");
        } finally {
            Server.pool.shutdown();
            System.out.println("Thread pool shutting down");

            try {
                Server.pool.awaitTermination(1L, TimeUnit.SECONDS);
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    }
}