package scf.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;



/**
 *
 * @author paddya
 */
public class Client
{

    public static void main(String[] args) throws IOException
    {

        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        //System.out.print("Server IP: ");

        String ip = "127.0.0.1"; //scanner.next();

        System.out.print("Port: ");

        Integer port = scanner.nextInt();

        System.out.println(String.format("Connecting to %s on port %d.", ip, port));

        Socket clientSocket = new Socket(ip, port);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
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

    }
}


