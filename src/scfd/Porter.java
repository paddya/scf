/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scfd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import scf.model.command.ClientHello;
import scf.model.command.Command;
import scf.model.command.MoveResult;
import scf.model.command.Reconnect;
import scf.model.command.error.Err_Badcommand;
import scf.model.command.error.Err_Nicknameinuse;
import scf.model.command.response.Rpl_Reconnected;
import scf.model.command.response.Rpl_Serverhello;
import scf.parser.Parser;
import scf.parser.exception.ParserException;



/**
 * The porter is picky about clients to let in
 * It forwards every subsequent command to the corresponding player thread
 * @author markus
 */
public class Porter extends Thread
{

    private Socket socket;
    private PlayerThread playerThread;



    public Porter(Socket socket)
    {
        this.socket = socket;
    }



    @Override
    public void run()
    {
        System.out.println("Porter running");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;

            
            // Initialization loop
            while (playerThread == null) {
                line = in.readLine();

                if (line != null) {
                    try {
                        Command cmd = Parser.parse(line);
                        
                        
                        if (cmd instanceof ClientHello) {
                            // CLIENTHELLO
                            ClientHello clientHello = (ClientHello) cmd;

                            if (!PlayerThreadMap.getInstance().containsKey(clientHello.getPlayerID())) {
                                // Create new player thread
                                this.playerThread = new PlayerThread(this.socket, clientHello.getPlayerID());


                                // Save playerThread in PlayerThreadMap for future reconnects
                                PlayerThreadMap.getInstance().put(clientHello.getPlayerID(), this.playerThread);


                                // Run playerThread
                                Server.pool.execute(playerThread);
                            } else {
                                // Send error nickname in use
                                sendResponse(socket, new Err_Nicknameinuse());
                            }
                        } else if (cmd instanceof Reconnect) {
                            // RECONNECT
                            // Retrieve old playerThread for this player
                            Reconnect reconnect = (Reconnect) cmd;
                            this.playerThread = PlayerThreadMap.getInstance().get(reconnect.getPlayerID());
                            
                            if (this.playerThread != null) {
                                // Successful reconnect
                                sendResponse(socket, new Rpl_Reconnected());
                                
                                
                                // Set socket in player thread
                                this.playerThread.setSocket(socket);
                                
                                
                                // Get last MOVERESULT and forward to client
                                MoveResult mr = this.playerThread.getLastMoveResult();
                                if (mr != null) {
                                    sendResponse(socket, mr);
                                }
                            }
                        } else {
                            // Invalid command
                            throw new ParserException();
                        }
                    } catch (ParserException e) {
                        // Bad command, close connection
                        sendResponse(socket, new Err_Badcommand());
                        this.socket.close();
                        return;
                    }
                } else {
                    // End of stream, end of porter
                    this.socket.close();
                    return;
                }
            }
                
                
            // Main communication loop
            while (true) {
                line = in.readLine();

                if (line != null) {
                    try {
                        Command cmd = Parser.parse(line);
                        this.playerThread.deliverPorter(cmd);
                    } catch (ParserException e) {
                        // Bad command, close connection
                        sendResponse(socket, new Err_Badcommand());
                        this.socket.close();
                        return;
                    }
                } else {
                    // End of stream, end of porter
                    this.socket.close();
                    return;
                }
            }
        } catch (IOException ex) {
            System.out.println("Gnah");
        }
    }
    
    public void sendResponse(Socket clientSocket, Command cmd)
    {
        try {
            new DataOutputStream(clientSocket.getOutputStream()).writeBytes(cmd.toString() + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


