package scf.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.GamesList;
import scf.model.command.Reconnect;
import scf.model.command.Victory;
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
    private BufferedReader inFromServer;

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
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            ResponseHandler handler = new ResponseHandler(clientSocket, this);
            
            handlerThread = new Thread(handler);
            
            handlerThread.start();
            
            Command response;

            do {
                System.out.print("Username: ");

                String username = scanner.next();
                
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

            while (!quitFlag) {
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
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

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
        System.out.println(cmd.toString());
        if (cmd instanceof Victory) {
            handleCommand((Victory) cmd);
        }
        
        if (cmd instanceof GamesList) {
            handleCommand((GamesList) cmd);
        }
    }
    
    public synchronized void handleCommand(Victory cmd)
    {
        System.out.println("You win!");
    }
    
    public synchronized void handleCommand(GamesList cmd)
    {
        System.out.println(cmd.toString());
    }
    



    public boolean isWaitingForSyncResponse()
    {
        return waitingForSyncResponse;
    }



    public void setWaitingForSyncResponse(boolean waitingForSyncResponse)
    {
        this.waitingForSyncResponse = waitingForSyncResponse;
    }
    
    
}


