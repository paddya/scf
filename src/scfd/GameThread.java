package scfd;

import java.util.concurrent.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.*;
import scf.model.command.response.Rpl_Leftgame;




public class GameThread extends Thread
{

    private String gameID;
    private PlayerThread challengerThread;
    private PlayerThread opponentThread;
    private ConcurrentLinkedQueue<Command> challengerMailbox;
    private ConcurrentLinkedQueue<Command> opponentMailbox;
    private boolean die;

    
    
    public synchronized void enqueue(Command cmd, String playerID)
    {
        if (playerID.equals(this.challengerThread.getPlayer().getName())) {
            this.challengerMailbox.add(cmd);
        } else if (playerID.equals(this.opponentThread.getPlayer().getName())) {
            this.opponentMailbox.add(cmd);
        } else {
            // throw new YouStupidException
        }
        
        // Notify
        notifyAll();
    }

    
    
    public GameThread(PlayerThread challengerThread)
    {
        this.die = false;
        this.gameID = generateString(32);
        this.challengerThread = challengerThread;
        this.challengerMailbox = new ConcurrentLinkedQueue<>();
        this.opponentMailbox = new ConcurrentLinkedQueue<>();
    }
    
    
    
    public String getGameID() {
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

    
    
    
    public void joinGame(PlayerThread opponentThread)
    {
        this.opponentThread = opponentThread;


        // Start the game
        this.start();
    }

    
    
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
        if (cmd instanceof LeaveGame) {
            // Challenger wants to leave the game
            System.out.println("Challenger left the game");
            
            
            // Inform challenger player thread
            this.challengerThread.getMailbox().add(new Rpl_Leftgame());
            
            
            // Set die flag and inform thread
            synchronized (this) {
                System.out.println("going to wake up");
                this.die = true;
                notifyAll();
            }
        }
    }
    
    
    
    private void handleOpponentCommand(Command cmd)
    {
        // To Do: Implement handler
    }

    
    
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Testing GameThread");
    }
}