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
    private LinkedBlockingQueue<Command> challengerQueueIn;
    private LinkedBlockingQueue<Command> challengerQueueOut;
    private LinkedBlockingQueue<Command> opponentQueueIn;
    private LinkedBlockingQueue<Command> opponentQueueOut;
    private boolean endGameFlag;

    
    
    public void enqueue(Command cmd, String playerID)
    {
        if (playerID.equals(challengerID)) {
            try {
                this.challengerQueueIn.put(cmd);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (playerID.equals(opponentID)) {
            try {
                this.opponentQueueIn.put(cmd);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // throw new YouStupidException
        }
    }

    
    
    public Command blockinglyDequeue(String playerID)
    {
        Command cmd = null;

        if (playerID.equals(challengerID)) {
            try {
                cmd = this.challengerQueueOut.poll(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (playerID.equals(opponentID)) {
            try {
                cmd = this.opponentQueueOut.poll(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return cmd;
    }

    
    
    public GameThread(String challengerID)
    {
        this.endGameFlag = false;
        this.gameID = generateString(32);
        this.challengerID = challengerID;
        this.challengerQueueIn = new LinkedBlockingQueue<>();
        this.challengerQueueOut = new LinkedBlockingQueue<>();
        this.opponentQueueIn = new LinkedBlockingQueue<>();
        this.opponentQueueOut = new LinkedBlockingQueue<>();
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
        
        while (!endGameFlag) {
            // Handle new command from challenger
            Command c = null;
            try {
                c = this.challengerQueueIn.poll(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (c != null) {
                System.out.println("new command from challenger");
                handleChallengerCommand(c);
            }


            // Handle new command from opponent
            Command o = null;
            try {
                o = this.opponentQueueIn.poll(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (o != null) {
                System.out.println("new command from opponent");
                handleOpponentCommand(o);
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
            
            try {
                // Inform challenger player thread
                this.challengerQueueOut.put(new Rpl_Leftgame());
            } catch (InterruptedException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // End execution of thread
            this.endGameFlag = true;
        }
    }
    
    
    
    private void handleOpponentCommand(Command cmd)
    {
        // To Do: Implement handler
    }

    
    
    
    public static void main(String[] args) throws InterruptedException
    {
        System.out.println("Testing GameThread");

        GameThread gt = new GameThread("patrick");
        gt.joinGame("markus");

        sleep(2000);

        gt.enqueue(new ClientHello("markus"), "markus");

        sleep(3000);

        gt.enqueue(new ClientHello("patrick"), "patrick");

        Command cmd = gt.blockinglyDequeue("markus");

        if (cmd != null) {
            System.out.println("new message for markus");
        }
    }
}