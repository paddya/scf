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
    private String challengerID;
    private String opponentID;
    private ConcurrentLinkedQueue<Command> challengerQueueIn;
    private ConcurrentLinkedQueue<Command> challengerQueueOut;
    private ConcurrentLinkedQueue<Command> opponentQueueIn;
    private ConcurrentLinkedQueue<Command> opponentQueueOut;
    private boolean die;

    
    
    public synchronized void enqueue(Command cmd, String playerID)
    {
        if (playerID.equals(challengerID)) {
            this.challengerQueueIn.add(cmd);
        } else if (playerID.equals(opponentID)) {
            this.opponentQueueIn.add(cmd);
        } else {
            // throw new YouStupidException
        }
        
        // Notify
        notifyAll();
    }

    
    
    public Command blockinglyDequeue(String playerID)
    {
//        Command cmd = null;
//
//        if (playerID.equals(challengerID)) {
//            try {
//                cmd = this.challengerQueueOut.poll(5, TimeUnit.MINUTES);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        if (playerID.equals(opponentID)) {
//            try {
//                cmd = this.opponentQueueOut.poll(5, TimeUnit.MINUTES);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        return cmd;
        return null;
    }

    
    
    public GameThread(String challengerID)
    {
        this.die = false;
        this.gameID = generateString(32);
        this.challengerID = challengerID;
        this.challengerQueueIn = new ConcurrentLinkedQueue<>();
        this.challengerQueueOut = new ConcurrentLinkedQueue<>();
        this.opponentQueueIn = new ConcurrentLinkedQueue<>();
        this.opponentQueueOut = new ConcurrentLinkedQueue<>();
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

    
    
    
    public void joinGame(String opponentID)
    {
        this.opponentID = opponentID;


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
            c = this.challengerQueueIn.poll();

            if (c != null) {
                System.out.println("new command from challenger");
                handleChallengerCommand(c);
            }


            // Handle new command from opponent
            Command o = null;
            o = this.opponentQueueIn.poll();

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
            this.challengerID = null;
            System.out.println("Challenger left the game");
            
            
            // Inform challenger player thread
            this.challengerQueueOut.add(new Rpl_Leftgame());
            
            
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

        GameThread gt = new GameThread("markus");
        gt.joinGame("patrick");
        
        sleep(2000);
        
        gt.enqueue(new LeaveGame(), "markus");
    }
}