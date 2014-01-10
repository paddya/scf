package scf.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.parser.Parser;
import scf.parser.exception.ParserException;



/**
 *
 * @author paddya
 */
public class Client
{
    
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    
    
    public static void main(String[] args)
    {
        
        Client client = new Client();
        
        client.run();

    }


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
            
            Command response;
            
            do {
                System.out.print("Username: ");

                String username = scanner.next();

                response = sendCommand(new ClientHello(username));
            } while(response instanceof scf.model.command.error.Error);
            
            
            
            boolean quitFlag = false;
            
            while (!quitFlag) {
                if (scanner.hasNext()) {
                    
                    String sentence = scanner.next();
                    outToServer.writeBytes(sentence + "\n");
                    //String modifiedSentence = inFromServer.readLine();
                    //System.out.println("FROM SERVER: " + modifiedSentence);
                }

            }
            
            clientSocket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Command sendCommand(Command cmd)
    {
        Command response = null;
        String line = "";
        try {
            outToServer.writeBytes(cmd.toString() + "\n");
            line = inFromServer.readLine();
            System.out.println(line);
            response = Parser.parse(line);
        } catch (IOException ex) {
            System.out.println("Something went wrong while trying to submit stuff to the server.");
        } catch (ParserException ex) {
            System.out.println("Response from server could not be parsed.");
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Message from server: " + line, ex);
        }
        
        return response;
    }
    
    
}


