package scfd;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.text.*;

public class PlayerThread implements Runnable
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
			
			// Command serverHello = Parser.getInstance().parse(this.in.readLine());
			// this.user = serverHello.getUser();
			this.playerID = this.in.readLine();
			System.out.println("Client chose name " + this.playerID);
		} catch (IOException e) {
			System.out.println("Gnah");
		}
	}
	
	
	
	
	public void run()
	{
		try {
			// Read loop
			String line;
			while ((line = in.readLine()) != null) {
				// Command cmd = Parser.getInstance().parse(line);
				// this.out.println(cmd.toString());
				System.out.println(this.playerID + " said: " + line);
			}
			
			
			// End of stream
			System.out.println(this.playerID + " disconnected");
		} catch (IOException e) {
			System.out.println("Gnah");
		}
	}
}