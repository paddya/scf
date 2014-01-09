package scfd;

import java.io.*;
import java.net.*;
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
        this.socket = socket;
        // this.player = LOOKUP(playerID)
        
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Corrupted socket");
        }
    }
    
    
    
    public Socket getSocket() {
        return this.socket;
    }
    
    
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
    
    public Object getMutex() {
        return this.mutex;
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
                    System.out.println(this.player.getName() + " said: " + line);
                } else {
                    // End of stream
                    System.out.println(this.player.getName() + " disconnected");
                    this.socket.close();
                    
                    // Wait until the same client reconnects
                    synchronized (this.getMutex()) {
                        try {
                            socket.wait();
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