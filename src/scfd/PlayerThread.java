package scfd;

import java.io.*;
import java.net.*;
import scf.model.command.*;
import scf.parser.Parser;

public class PlayerThread extends Thread
{

    private Socket socket;
    private String playerID;
    private String game;
    private GameThread gameThread;
    private BufferedReader in;
    private PrintWriter out;



    public PlayerThread(Socket socket)
    {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);


            // Initialize
            String clientHello = in.readLine();

            if (clientHello == null) {
                // client hung up immediately
                this.socket.close();
                return;
            }

            try {
                ClientHello cmd = (ClientHello) Parser.parse(clientHello);
                this.playerID = cmd.getPlayerID();
            } catch (ClassCastException e) {
                System.out.println("Client did not send clienthello as first message");
                this.socket.close();
                return;
            }

            System.out.println("Client chose name " + this.playerID);
        } catch (IOException e) {
            System.out.println("Gnah");
        }
    }



    @Override
    public void run()
    {
        try {
            // Read loop
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(this.playerID + " said: " + line);
            }


            // End of stream
            System.out.println(this.playerID + " disconnected");
        } catch (IOException e) {
            System.out.println("Gnah");
        }
    }
}