package scfd;

import java.util.concurrent.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.Board;
import scf.model.Game;
import scf.model.Player;
import scf.model.command.*;
import scf.model.command.response.Rpl_Discplaced;
import scf.model.command.response.Rpl_Joinedgame;
import scf.model.command.response.Rpl_Leftgame;



public class GameThread extends Thread
{

    private Game game;
    private PlayerThread challengerThread;
    private PlayerThread opponentThread;
    private ConcurrentLinkedQueue<Command> challengerMailbox;
    private ConcurrentLinkedQueue<Command> opponentMailbox;
    private boolean die;
    
    // track moves of players
    private PlaceDisc challengerMove;
    private PlaceDisc opponentMove;;



    public String getGameID()
    {
        return this.game.getId();
    }
    
    public String getPlayerWithToken()
    {
        return this.game.getPlayerWithToken().getName();
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
        this.game = new Game();
        this.game.setId(generateString(32));
        this.game.setChallenger(challengerThread.getPlayer());
        this.challengerThread = challengerThread;
        this.challengerMailbox = new ConcurrentLinkedQueue<>();
        this.opponentMailbox = new ConcurrentLinkedQueue<>();
    }



    public synchronized void joinGame(PlayerThread opponentThread)
    {   
        this.opponentThread = opponentThread;
        this.game.setOpponent(opponentThread.getPlayer());
        this.opponentMailbox.add(new JoinGame(this.getGameID()));
        
        notifyAll();
    }



    public synchronized void leaveGame(PlayerThread leavingThread)
    {
        // Remove game from games list
        GameThreadMap.getInstance().remove(this.getGameID());
        
        
        if (leavingThread.equals(this.challengerThread)) {
            this.challengerMailbox.add(new LeaveGame());
        }

        if (leavingThread.equals(this.opponentThread)) {
            this.opponentMailbox.add(new LeaveGame());
        }
        
        notifyAll();
    }
    
    public synchronized void placeDisc(PlayerThread playerThread, int column)
    {
        if (playerThread.equals(this.challengerThread)) {
            this.challengerMailbox.add(new PlaceDisc(column));
        }

        if (playerThread.equals(this.opponentThread)) {
            this.opponentMailbox.add(new PlaceDisc(column));
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
        
        if (cmd instanceof PlaceDisc) {
            handleMove(this.challengerThread, (PlaceDisc) cmd);
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
            
            Random rand = new Random();
            
            boolean challengerToken = rand.nextInt(2) == 0;
            
            this.game.getChallenger().setToken(challengerToken);
            this.game.getOpponent().setToken(!challengerToken);
            
            
            // Opponent joins
            this.opponentThread.deliverGame(new Rpl_Joinedgame());


            // Inform challenger and opponent
            this.challengerThread.deliverGame(new GameStart(getPlayerWithToken()));
            this.opponentThread.deliverGame(new GameStart(getPlayerWithToken()));
        }
        
        if (cmd instanceof PlaceDisc) {
            handleMove(this.opponentThread, (PlaceDisc) cmd);
        }
        
    }
    
    public synchronized void handleMove(PlayerThread thread, PlaceDisc cmd)
    {
        if (thread == this.opponentThread) {
            opponentMove = cmd;
        }
        
        if (thread == this.challengerThread) {
            challengerMove = cmd;
        }
        
        thread.deliverGame(new Rpl_Discplaced());
        
        if (challengerMove != null && opponentMove != null) {
            
            Player challenger = game.getChallenger();
            Player opponent = game.getOpponent();
            
            int firstMove = challenger.hasToken() ? challengerMove.getColumn() : opponentMove.getColumn();
            int secondMove = challenger.hasToken() ? opponentMove.getColumn() : challengerMove.getColumn();
            
            Board board = game.getBoard();
            
            if (firstMove == secondMove) {
                
                // we don't have a problem with two free rows in a column
                if (board.hasTwoFreeRowsInColumn(firstMove)) {
                    board.insertIntoColumn(game.getPlayerWithToken(), firstMove);
                    board.insertIntoColumn(game.getPlayerWithoutToken(), secondMove);
                } else {
                    board.insertIntoColumn(game.getPlayerWithToken(), firstMove);
                    System.out.printf("%s's move was discarded.", game.getPlayerWithoutToken());
                }
                
                // switch tokens, because the player with token had an advantage.
                boolean challengerToken = challenger.hasToken();
                
                challenger.setToken(!challengerToken);
                opponent.setToken(challengerToken);
                
            } else {
                if (board.insertIntoColumn(game.getPlayerWithToken(), firstMove)) {
                    System.out.printf("%s's move was successful.", game.getPlayerWithToken().getName());
                }
                if (board.insertIntoColumn(game.getPlayerWithoutToken(), secondMove)) {
                    System.out.printf("%s's move was successful.", game.getPlayerWithoutToken().getName());
                }
            }
            
            challengerThread.deliverGame(new MoveResult(game.getStringBoard(), getPlayerWithToken()));
            opponentThread.deliverGame(new MoveResult(game.getStringBoard(), getPlayerWithToken()));
            
            challengerMove = null;
            opponentMove = null;
            
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