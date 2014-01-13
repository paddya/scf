/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scf.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.Command;
import scf.model.command.response.Reply;
import scf.parser.Parser;
import scf.parser.exception.ParserException;





/**
 *
 * @author paddya
 */
public class ResponseHandler implements Runnable
{

    private final Socket clientSocket;
    private final Client mainThread;



    public ResponseHandler(Socket clientSocket, Client mainThread)
    {
        this.clientSocket = clientSocket;
        this.mainThread = mainThread;
    }
    
    @Override
    public void run()
    {
        try {
            Boolean listening = true;
            
            Scanner scanner = new Scanner(clientSocket.getInputStream()).useDelimiter("\n");
            
            while (listening) {
                
                String line = scanner.next();
                
                if (line != null) {
                    try {
                        Command response = Parser.parse(line);
                        
                        if (mainThread.isWaitingForSyncResponse()) {
                            mainThread.handleSyncResponse(response);
                        } else {
                            mainThread.handleCommand(response);
                        }
                        
                        
                        
                    } catch (ParserException ex) {
                        Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            
            }
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}


