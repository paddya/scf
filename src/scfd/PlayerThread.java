package scfd;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.Player;
import scf.model.command.*;
import scf.parser.Parser;

public class PlayerThread extends Thread
{

    private Socket socket;
    private Player player;
    private String game;
    private GameThread gameThread;
    private BufferedReader in;
    private PrintWriter out;
    private final Object mutex;



    public PlayerThread(Socket socket, String playerID)
    {
        this.mutex = new Object();
        this.setSocket(socket);
//         this.player = LOOKUP(playerID)
        this.player = new Player();
        this.player.setName(playerID);
    }
    
    
    
    public Socket getSocket() {
        return this.socket;
    }
    
    
    
    public final void setSocket(Socket socket) {
        // Close old socket
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ex) {
                // won’t happen, fuckers
            }
        }
        
        
        // Set new socket
        this.socket = socket;
        
        
        // Set up read and write streams
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Corrupted socket");
        }
        
        
        // Notify waiting thread
        synchronized (this.mutex) {
            this.mutex.notifyAll();
        }
    }



    @Override
    public void run()
    {
        try {
            // Read loop
            String line;
            while (true) {
                line = in.readLine();
                
                if (line != null) {
                    System.out.println(this.player.getName() + "(" + Thread.currentThread().getId() + ") said: " + line);
                } else {
                    // End of stream
                    System.out.println(this.player.getName() + " disconnected");
                    this.socket.close();
                    
                    // Wait until the same client reconnects
                    synchronized (this.mutex) {
                        try {
                            this.mutex.wait();
                        } catch (InterruptedException ex) {
                            System.out.println("InterruptedException");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Gnah");
        }
    }
}