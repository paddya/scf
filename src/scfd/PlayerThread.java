package scfd;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.Player;
import scf.model.command.*;
import scf.model.command.response.Rpl_Discplaced;
import scf.model.command.response.Rpl_Gamecreated;
import scf.model.command.response.Rpl_Leftgame;
import scf.model.command.response.Rpl_Serverhello;

public class PlayerThread extends Thread
{

    private Socket socket;
    private Player player;
    private GameThread gameThread;
    private final ConcurrentLinkedQueue<Command> porterMailbox;
    private final ConcurrentLinkedQueue<Command> gameMailbox;



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
        
        
        // Notify waiting thread
//        synchronized (this) {
//            notifyAll();
//        }
    }



    @Override
    public void run()
    {
        System.out.println("player running");
        
        sendResponse(new Rpl_Serverhello());
        
        while (true) {
            // Check porter mailbox
            while (!this.porterMailbox.isEmpty()) {
                handlePorterCommand(this.porterMailbox.poll());
            }


            // Check game mailbox
            while (!this.gameMailbox.isEmpty()) {
                handleGameCommand(this.gameMailbox.poll());
            }


            // Sleep like a baby
            try {
                synchronized (this) {
                    System.out.println("Waiting");
                    wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }
        }
    }
    
    public void sendResponse(Command cmd)
    {
        // Theoretically not thread safe but practically never clashing with porter
        try {
            new DataOutputStream(this.socket.getOutputStream()).writeBytes(cmd.toString() + "\n");
        } catch (IOException ex) {
            System.out.println("Oh my");
        }
    }
    
    
    
    public void handlePorterCommand(Command command)
    {
        System.out.println("handlePorterCommand");
        if (command instanceof CreateGame) {
            handlePorterCommand((CreateGame)command);
        }
        
        if (command instanceof JoinGame) {
            handlePorterCommand((JoinGame)command);
        } 
        
        if (command instanceof LeaveGame) {
            handlePorterCommand((LeaveGame)command);
        }
    }
    
    
    
    public void handlePorterCommand(CreateGame command)
    {
        // Create new game thread
        this.gameThread = new GameThread(this);
        System.out.println("New game created with gameID: " + this.gameThread.getGameID());
        
        
        // Save gameThread for future joins
        GameThreadMap.getInstance().put(gameThread.getGameID(), gameThread);
        
        
        // Run gameThread
        Server.pool.execute(this.gameThread);
        
        
        // Reply with a message of great success
        sendResponse(new Rpl_Gamecreated());
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
        System.out.println("LEAVE GAME FROM PORTER");
        this.gameThread.leaveGame(this);
    }
    
    
    
    public void handleGameCommand(Command command) {
        sendResponse(command);
    }



    public Player getPlayer()
    {
        return player;
    }
}