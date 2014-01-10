package scfd;

import java.io.*;
import java.net.*;
import scf.model.Player;
import scf.model.command.*;
import scf.parser.Parser;
import scf.parser.exception.ParserException;

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
                    
                    
                    // Try to parse and handle the command
                    try {
                        Command cmd = Parser.parse(line);
                        handleCommand(cmd);
                    } catch (ParserException ex) {
                        System.out.println("Parse exception");
                    }
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
    
    public void sendResponse(Command cmd)
    {
        out.print(cmd.toString() + "\n");
    }
    
    
    
    public void handleCommand(Command command)
    {
        if (command instanceof CreateGame) {
            handleCommand((CreateGame)command);
        }
        
        if (command instanceof JoinGame) {
            handleCommand((JoinGame)command);
        }
        
        if (command instanceof LeaveGame) {
            handleCommand((LeaveGame)command);
        }
    }
    
    
    
    public void handleCommand(CreateGame command)
    {
        // Create new game thread
        this.gameThread = new GameThread(this.player.getName());
        System.out.println("New game created with gameID: " + this.gameThread.getGameID());
        
        
        // Save gameThread for future joins
        GameThreadMap.getInstance().put(gameThread.getGameID(), gameThread);
    }
    
    
    
    public void handleCommand(JoinGame command)
    {
        // Join an existing game thread
        String gid = command.getGameId();
        this.gameThread = GameThreadMap.getInstance().get(gid);
        this.gameThread.joinGame(this.player.getName());
    }
    
    
    
    public void handleCommand(LeaveGame command)
    {
        // Inform game thread about leaving of this.player
        this.gameThread.enqueue(command, this.player.getName());
        
        
        // Handle the response
        Command res = this.gameThread.blockinglyDequeue(this.player.getName());
    }
}