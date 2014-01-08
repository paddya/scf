package scfd;

import java.util.concurrent.*;

public class GameThread extends Thread
{

	private String gameID;
	private String challengerID;
	private String opponentID;
	private ConcurrentLinkedQueue<Command> challengerQueueIn;
	private ConcurrentLinkedQueue<Command> challengerQueueOut;
	private ConcurrentLinkedQueue<Command> opponentQueueIn;
	private ConcurrentLinkedQueue<Command> opponentQueueOut;
	private Object mutexIn;
	private Object challengerMutexOut;
	private Object opponentMutexOut;
	
	
	
	public void enqueue(Command cmd, String playerID)
	{
		if (playerID.equals(challengerID)) {
			synchronized (this.mutexIn) {
				this.challengerQueueIn.add(cmd);
				this.mutexIn.notifyAll();
			}
		} else if (playerID.equals(opponentID)) {
			synchronized (this.mutexIn) {
				this.opponentQueueIn.add(cmd);
				this.mutexIn.notifyAll();
			}
		} else {
			// throw new YouStupidException
		}
	}
	
	
	
	public Command blockinglyDequeue(String playerID)
	{
		Command cmd;
		
		if (playerID.equals(challengerID)) {
			while ((cmd = this.challengerQueueOut.poll()) == null) {
				try {
					synchronized (this.challengerMutexOut) {
						this.challengerMutexOut.wait();
					}
				} catch (InterruptedException e) {
					System.out.println("Interrupted, ya");
				}
			}
		} else if (playerID.equals(opponentID)) {
			while ((cmd = this.opponentQueueOut.poll()) == null) {
				try {
					synchronized (this.opponentMutexOut) {
						this.opponentMutexOut.wait();
					}
				} catch (InterruptedException e) {
					System.out.println("Interrupted, ya");
				}
			}
		} else {
			// throw new YouStupidException
			cmd = null;
		}
		
		return cmd;
	}
	
	
	
	public GameThread(String challengerID)
	{
		this.challengerID = challengerID;
		this.challengerQueueIn = new ConcurrentLinkedQueue<>();
		this.challengerQueueOut = new ConcurrentLinkedQueue<>();
		this.opponentQueueIn = new ConcurrentLinkedQueue<>();
		this.opponentQueueOut = new ConcurrentLinkedQueue<>();
		this.mutexIn = new Object();
	}
	
	
	
	public void joinGame(String opponentID)
	{
		this.opponentID = opponentID;
		
		
		// Start the game
		this.start();
	}
	
	
	
	@Override
	public void run()
	{
		while (true) {
			// Handle new command from challenger
			Command c = this.challengerQueueIn.poll();
			
			if (c != null) {
				System.out.println("new command from challenger");
			}
			
			
			// Handle new command from opponent
			Command o = this.opponentQueueIn.poll();
			
			if (o != null) {
				System.out.println("new command from opponent");
			}
			
			
			// Wait for next command
			try {
				synchronized (this.mutexIn) {
					this.mutexIn.wait();
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted, ya");
			}
		}
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Testing GameThread");
		
		GameThread gt = new GameThread("patrick");
		gt.joinGame("markus");
		
		sleep(2000);
		
		gt.enqueue(new Command(), "markus");
		
		sleep(3000);
		
		gt.enqueue(new Command(), "patrick");
	}
}