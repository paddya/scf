
package scfd;

import scfd.PlayerThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.*; 
import java.net.*;
import java.util.Date;
import java.text.*;





public class Server
{

	public static void main(String[] args) throws IOException
	{
		final ExecutorService pool;
		final ServerSocket serverSocket;
		int port = 13370;
		Thread serverThread;
	
		
		// Initialize listening socket and handler
		pool = Executors.newCachedThreadPool();
		serverSocket = new ServerSocket(port);
		serverThread = new Thread(new Listener(pool, serverSocket));
		serverThread.start();
		
		
		// Shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run() {
				pool.shutdown();
				
				try {
					pool.awaitTermination(2L, TimeUnit.SECONDS);
					if (!serverSocket.isClosed()) {
						serverSocket.close();
					}
				} catch (IOException e) {
				} catch (InterruptedException e) {}
			}
		});
	}
}







class Listener implements Runnable {

	private final ServerSocket serverSocket;
	private final ExecutorService pool;
	
	
	
	
	public Listener(ExecutorService pool, ServerSocket serverSocket)
	{
		this.pool = pool;
		this.serverSocket = serverSocket;
	}
	
	
	
	
	public void run()
	{
		try {
			// Accept loop
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client");
 				pool.execute(new PlayerThread(clientSocket));
			}
		} catch (IOException e) {
			System.out.println("Failed to handle server socket");
		} finally {
			pool.shutdown();
			
			try {
				pool.awaitTermination(4L, TimeUnit.SECONDS);
				if (!serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {
			} catch (InterruptedException e) {}
		}
	}
}