package scfd;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.Player;
import scf.model.command.*;

public class PlayerThread extends Thread
{

    private Socket socket;
    private Player player;
    private GameThread gameThread;
    private final ConcurrentLinkedQueue<Command> porterMailbox;
    private final ConcurrentLinkedQueue<Command> gameMailbox;
    private PrintWriter out;



    public PlayerThread(Socket socket, String playerID)
    {
        this.porterMailbox = new ConcurrentLinkedQueue<>();
        this.gameMailbox = new ConcurrentLinkedQueue<>();
        this.setSocket(socket);
        this.player = new Player();
        this.player.setName(playerID);
    }
    
    
    
    public synchronized void deliverPorter(Command cmd) {
        this.porterMailbox.add(cmd);
        notifyAll();
    }
    
    
    
    public synchronized void deliverGame(Command cmd) {
        this.gameMailbox.add(cmd);
        notifyAll();
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
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Corrupted socket");
        }
        
        
        // Notify waiting thread
//        synchronized (this) {
//            notifyAll();
//        }
    }



    @Override
    public void run()
    {
        System.out.println("player running");
        
        while (true) {
            // Check porter mailbox
            Command pcmd = this.porterMailbox.poll();
            if (pcmd != null) {
                handlePorterCommand(pcmd);
            }


            // Check game mailbox
            Command gcmd = this.gameMailbox.poll();
            if (gcmd != null) {
                handleGameCommand(gcmd);
            }


            // Sleep like a baby]
            try {
                synchronized (this) {
                    System.out.println("Waiting");
                    wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }
            
            System.out.println("Waking up");
            int a = 3+5;
        }
    }
    
    public void sendResponse(Command cmd)
    {
        out.print(cmd.toString() + "\n");
    }
    
    
    
    public void handlePorterCommand(Command command)
    {
        System.out.println("handlePorterCommand");
//        if (command instanceof CreateGame) {
//            handlePorterCommand((CreateGame)command);
//        }
//        
//        if (command instanceof JoinGame) {
//            handlePorterCommand((JoinGame)command);
//        } 
//        
//        if (command instanceof LeaveGame) {
//            handlePorterCommand((LeaveGame)command);
//        }
    }
    
    
    
    public void handlePorterCommand(CreateGame command)
    {
        // Create new game thread
        this.gameThread = new GameThread(this);
        System.out.println("New game created with gameID: " + this.gameThread.getGameID());
        
        
        // Save gameThread for future joins
        GameThreadMap.getInstance().put(gameThread.getGameID(), gameThread);
    }
    
    
    
    public void handlePorterCommand(JoinGame command)
    {
        // Join an existing game thread
        String gid = command.getGameId();
        this.gameThread = GameThreadMap.getInstance().get(gid);
        this.gameThread.joinGame(this);
    }
    
    
    
    public void handlePorterCommand(LeaveGame command)
    {
        // Inform game thread about leaving of this.player
        this.gameThread.enqueue(command, this.player.getName());
        
        
        // Handle the response
//        Command res = this.gameThread.blockinglyDequeue(this.player.getName());
    }
    
    
    
    public void handleGameCommand(Command command) {
        
    }



    public Player getPlayer()
    {
        return player;
    }



    public ConcurrentLinkedQueue<Command> getMailbox()
    {
        return porterMailbox;
    }
}




class SocketHandler extends Thread
{
    private Socket socket;
    private PlayerThread playerThread;
    
    
    public SocketHandler(PlayerThread playerThread)
    {
        this.playerThread = playerThread;
    }


    @Override
    public void run()
    {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String line = in.readLine();

                if (line != null) {
                    this.playerThread.getMailbox();
                } else {
                    // End of stream
                    this.socket.close();

                    // Wait until the same client reconnects
                    synchronized (this) {
                        try {
                            wait();
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