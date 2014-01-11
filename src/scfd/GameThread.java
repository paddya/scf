package scfd;

import java.util.concurrent.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.*;
import scf.model.command.response.Rpl_Gamecreated;
import scf.model.command.response.Rpl_Joinedgame;
import scf.model.command.response.Rpl_Leftgame;



public class GameThread extends Thread
{

    private String gameID;
    private PlayerThread challengerThread;
    private PlayerThread opponentThread;
    private ConcurrentLinkedQueue<Command> challengerMailbox;
    private ConcurrentLinkedQueue<Command> opponentMailbox;
    private boolean die;



    public String getGameID()
    {
        return this.gameID;
    }



    private static String generateString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random(System.nanoTime());
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }



    // API methods
    // -----------
    public GameThread(PlayerThread challengerThread)
    {
        this.die = false;
        this.gameID = generateString(32);
        this.challengerThread = challengerThread;
        this.challengerMailbox = new ConcurrentLinkedQueue<>();
        this.opponentMailbox = new ConcurrentLinkedQueue<>();
    }



    public synchronized void joinGame(PlayerThread opponentThread)
    {   
        this.opponentThread = opponentThread;
        this.opponentMailbox.add(new JoinGame(this.gameID));
        
        notifyAll();
    }



    public synchronized void leaveGame(PlayerThread leavingThread)
    {
        // Remove game from games list
        GameThreadMap.getInstance().remove(this.gameID);
        
        
        if (leavingThread.equals(this.challengerThread)) {
            this.challengerMailbox.add(new LeaveGame());
        }

        if (leavingThread.equals(this.opponentThread)) {
            this.opponentMailbox.add(new LeaveGame());
        }
        
        notifyAll();
    }



    // Game thread logic
    // -----------------
    @Override
    public void run()
    {
        System.out.println("GameThread is running ...");

        while (true) {
            // Handle new command from challenger
            Command c = null;
            c = this.challengerMailbox.poll();

            if (c != null) {
                System.out.println("new command from challenger");
                handleChallengerCommand(c);
            }


            // Handle new command from opponent
            Command o = null;
            o = this.opponentMailbox.poll();

            if (o != null) {
                System.out.println("new command from opponent");
                handleOpponentCommand(o);
            }


            // Should I stay or should I go?
            synchronized (this) {
                if (this.die) {
                    System.out.println("breaking");
                    break;
                }
            }


            // Wait for next input on queues
            synchronized (this) {
                try {
                    System.out.println("going to sleep");
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.out.println("GameThread is shutting down ...");
    }



    private void handleChallengerCommand(Command cmd)
    {
        System.out.println("handle challenger command");
        if (cmd instanceof LeaveGame) {
            // Challenger leaves
            this.challengerThread.deliverGame(new Rpl_Leftgame());


            // Opponent wins
            if (this.opponentThread != null) {
                this.opponentThread.deliverGame(new OpponentLeft());
                this.opponentThread.deliverGame(new Victory());
            }
            
            
            // End run loop
            this.die = true;
        }
    }



    private void handleOpponentCommand(Command cmd)
    {
        System.out.println("handle opponent command");
        if (cmd instanceof LeaveGame) {
            // Opponent leaves
            this.opponentThread.deliverGame(new Rpl_Leftgame());


            // Challenger wins
            this.challengerThread.deliverGame(new OpponentLeft());
            this.challengerThread.deliverGame(new Victory());
            
            
            // End run loop
            this.die = true;
        }


        if (cmd instanceof JoinGame) {
            // Opponent joins
            this.opponentThread.deliverGame(new Rpl_Joinedgame());


            // Inform challenger and opponent
            this.challengerThread.deliverGame(new GameStart());
            this.opponentThread.deliverGame(new GameStart());
        }
    }



    public PlayerThread getChallengerThread()
    {
        return challengerThread;
    }



    public PlayerThread getOpponentThread()
    {
        return opponentThread;
    }
}