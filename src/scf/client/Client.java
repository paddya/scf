package scf.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.GameListEntry;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.GameStart;
import scf.model.command.GamesList;
import scf.model.command.MoveResult;
import scf.model.command.Reconnect;
import scf.model.command.Victory;
import scf.model.command.response.Rpl_Discplaced;
import scf.model.command.response.Rpl_Gamecreated;
import scf.model.command.response.Rpl_Joinedgame;
import scf.parser.Parser;
import scf.parser.exception.ParserException;



/**
 *
 * @author paddya
 */
public class Client extends Thread
{

    private Socket clientSocket;
    private DataOutputStream outToServer;

    private String username = null;
    private Boolean isChallenger = null;
    private Boolean hasToken = null;
    
    private Thread handlerThread;
    private ConcurrentLinkedQueue<Command> mailbox = new ConcurrentLinkedQueue<>();
    
    private boolean waitingForSyncResponse = false;

    public static void main(String[] args)
    {

        Client client = new Client();

        client.start();

    }


    
    @Override
    public void run()
    {
        try {
            Scanner scanner = new Scanner(System.in).useDelimiter("\n");

            //System.out.print("Server IP: ");

            String ip = "127.0.0.1"; //scanner.next();

            System.out.print("Port: ");

            Integer port = scanner.nextInt();


            System.out.println(String.format("Connecting to %s on port %d.", ip, port));

            clientSocket = new Socket(ip, port);

            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            
            ResponseHandler handler = new ResponseHandler(clientSocket, this);
            
            handlerThread = new Thread(handler);
            
            handlerThread.start();
            
            Command response;

            do {
                System.out.print("Username: ");

                username = scanner.next();
                
                System.out.print("Reconnect? ");
                
                String reconnect = scanner.next();
                
                if (reconnect.equals("y")) {
                    response = sendCommandAndWaitForResponse(new Reconnect(username));
                } else {
                    response = sendCommandAndWaitForResponse(new ClientHello(username)); 
                }

                handleCommand(response);
                
            } while (response instanceof scf.model.command.error.Error);



            boolean quitFlag = false;

            while (!quitFlag && scanner.hasNextLine()) {
                if (scanner.hasNext()) {

                    String userCommand = scanner.next();

                    if (userCommand.equals("exit")) {
                        quitFlag = true;
                    } else {
                        try {
                            Command cmd = sendCommandAndWaitForResponse(Parser.parse(userCommand));

                            handleCommand(cmd);
                            
                        } catch (ParserException ex) {
                            System.out.println("Invalid command!");
                            if (ex.getMessage().length() > 0) {
                                System.out.println(ex.getMessage());
                            }
                            
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }
            if (!quitFlag) {
                System.out.println("Connection closed by server.");    
            }
            
            clientSocket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private Command sendCommandAndWaitForResponse(Command cmd)
    {
        Command response = null;
        String line = "";
        try {
            
            outToServer.writeBytes(cmd.toString() + "\n");
            
            synchronized (this) {
                this.waitingForSyncResponse = true;
                this.wait();
            }
            
            return mailbox.poll();
            
        } catch (IOException ex) {
            System.out.println("Something went wrong while trying to submit stuff to the server.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response;
    }
    
    private void sendCommandAsync(Command cmd)
    {
        try {
            outToServer.writeBytes(cmd.toString() + "\n");
            
        } catch (IOException ex) {
            System.out.println("Something went wrong while trying to submit stuff to the server.");
        }
    }
    
    public synchronized void handleSyncResponse(Command cmd)
    {
        mailbox.add(cmd);
        this.waitingForSyncResponse = false;
        notifyAll();
    }
    
    public synchronized void handleCommand(Command cmd)
    {
        //System.out.println(cmd.toString());
        if (cmd instanceof Victory) {
            handleCommand((Victory) cmd);
        }
        
        if (cmd instanceof GamesList) {
            handleCommand((GamesList) cmd);
        }
        
        if (cmd instanceof MoveResult) {
            handleCommand((MoveResult) cmd);
        }
        
        if (cmd instanceof GameStart) {
            handleCommand((GameStart) cmd);
        }
        
        if (cmd instanceof Rpl_Gamecreated) {
            isChallenger = true;
        }
        
        if (cmd instanceof Rpl_Joinedgame) {
            isChallenger = false;
        }
        
        if (cmd instanceof Rpl_Discplaced) {
            System.out.println("Waiting for opponent.");
        }
    }
    
    public synchronized void handleCommand(Victory cmd)
    {
        System.out.println("You win!");
    }
    
    public synchronized void handleCommand(GamesList cmd)
    {
        for (GameListEntry game : cmd.getGames()) {
            System.out.println(game.getGameID() + "\t\t" + game.getChallengerName() + "\t\t" + game.getOpponentName());
        }
    }
    
    public synchronized void handleCommand(MoveResult cmd)
    {
        if (isChallenger != null) {
            String userSymbol = isChallenger ? "x" : "o";
            System.out.printf("%s are yours. ", userSymbol); 
        } else {
            System.out.print("We have no fucking information who you are.");
        }
        
        printTokenNotice();
        
        String[][] board = cmd.getBoard();
        hasToken = cmd.getPlayerWithToken().equals(username);
        // iterate in reverse for printing
        for (int i = board.length - 1; i >= 0; i--) {
            
            for (int k = 0; k < board[i].length; k++) {
                System.out.print(board[i][k] + "\t");
            }
            
            System.out.print("\n");   
        }
        
        
    }

    public synchronized void handleCommand(GameStart cmd)
    {
        hasToken = cmd.getPlayerWithToken().equals(username);
        
        System.out.printf("Game started. Your opponent is %s\n", cmd.getOpponentName());
        printTokenNotice();
        
    }

    public boolean isWaitingForSyncResponse()
    {
        return waitingForSyncResponse;
    }



    public void setWaitingForSyncResponse(boolean waitingForSyncResponse)
    {
        this.waitingForSyncResponse = waitingForSyncResponse;
    }
    
    private void printTokenNotice()
    {
        if (hasToken) {
            System.out.println("You have the token.");
        } else {
            System.out.println("Your opponent has the token.");
        }
    }
    
    
}


