package scfd;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import scf.model.Player;
import scf.model.command.*;
import scf.model.command.error.Err_Badcommand;
import scf.model.command.error.Err_Gamefull;
import scf.model.command.error.Err_Nosuchgame;
import scf.model.command.response.Rpl_Gamecreated;
import scf.model.command.response.Rpl_Joinedgame;
import scf.model.command.response.Rpl_Serverhello;

public class PlayerThread extends Thread
{

    private Socket socket;
    private Player player;
    private GameThread gameThread;
    private final ConcurrentLinkedQueue<Command> porterMailbox;
    private final ConcurrentLinkedQueue<Command> gameMailbox;
    private boolean canPlaceDisc = false;



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
            handlePorterCommand((CreateGame) command);
        }
        
        if (command instanceof JoinGame) {
            handlePorterCommand((JoinGame) command);
        } 
        
        if (command instanceof LeaveGame) {
            handlePorterCommand((LeaveGame) command);
        }
        
        if (command instanceof GetGames) {
            handlePorterCommand((GetGames) command);
        }
        
        if (command instanceof PlaceDisc) {
            handlePorterCommand((PlaceDisc) command);
        }
    }
    
    
    
    public void handlePorterCommand(CreateGame command)
    {
        // Error: already playing a game
        if (this.gameThread != null) {
            sendResponse(new Err_Badcommand()); // Wrong error code, better one has yet to be specified
            return;
        }
        
        
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
        String gid = command.getGameId();
        GameThread thread = GameThreadMap.getInstance().get(gid);
        
        
        // Error: already playing a game
        if (this.gameThread != null) {
            sendResponse(new Err_Badcommand()); // Wrong error code, better one has yet to be specified
            return;
        }
        
        
        // Error: no such game
        if (thread == null) {
            sendResponse(new Err_Nosuchgame());
            return;
        }
        
        
        this.gameThread = thread;
        
        boolean success = this.gameThread.joinGame(this);
        
        if (!success) {
            // join game failed
            sendResponse(new Err_Gamefull());
            return;
        }
    }
    
    
    
    public void handlePorterCommand(LeaveGame command)
    {
        // Inform game thread about leaving of this.player
        System.out.println("LEAVE GAME FROM PORTER");
        this.gameThread.leaveGame(this);
        this.gameThread = null;
        this.canPlaceDisc = false;
    }
    
    
    
    public void handlePorterCommand(GetGames command)
    {
        System.out.println("player thread GETGAMES");
        GamesList list = new GamesList();
        
        for (GameThreadMap.Entry<String, GameThread> game : GameThreadMap.getInstance().entrySet()) {
            String challenger = game.getValue().getChallengerThread().getPlayer().getName();
            String opponent;
            
            try {
                opponent = game.getValue().getOpponentThread().getPlayer().getName();
            } catch (NullPointerException e) {
                opponent = "";
            }
            
            list.addGame(game.getKey(), challenger, opponent);
        }
        
        sendResponse(list);
    }
    
    public void handlePorterCommand(PlaceDisc command)
    {       
        if (!this.canPlaceDisc) {
            sendResponse(new Err_Badcommand());
            return;
        }
        
        System.out.println("player thread PLACEDISC");
        this.gameThread.placeDisc(this, command.getColumn());
    }
    
    
    
    public void handleGameCommand(Command command) {
        if (command instanceof Victory) {
            this.gameThread = null;
            this.canPlaceDisc = false;
        }
        
        if (command instanceof GameStart) {
            this.canPlaceDisc = true;
        }
        
        sendResponse(command);
    }



    public Player getPlayer()
    {
        return player;
    }
    
    
    
    public MoveResult getLastMoveResult() {
        if (this.gameThread == null) {
            return null;
        }
        
        return new MoveResult(this.gameThread.getGame().getStringBoard(), this.gameThread.getPlayerWithToken());
    }
}