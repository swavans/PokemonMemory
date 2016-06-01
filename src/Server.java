

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

public class Server extends JFrame
{
	public static void main(String[] args)
	{
		new Server();
	}
	
	public Server()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(300, 300);
	    setTitle("Server");
	    setVisible(true);
	    
		try
		{
			//setup server
			ServerSocket server = new ServerSocket(8000);
			
			while(true)
			{
				//accept player1
				Socket player1 = server.accept();
				//accept player2
				Socket player2 = server.accept();
				
				//setup class which handle a who is it game
				HandleAGame game = new HandleAGame(player1, player2);
				
				//setup thread
				new Thread(game).start();
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	class HandleAGame implements Runnable
	{
		private Socket player1;
		private Socket player2;
		
		//setup dataStream player1 and player2
		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;
		
		//Constructor 
		public HandleAGame(Socket player1, Socket player2)
		{
			this.player1 = player1;
			this.player2 = player2;
			
			try
			{
				//initialisation dataStream for player1
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				
				//initialisation dataStream for player2
				toPlayer2 = new DataOutputStream(player2.getOutputStream());
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				
				
				//playing the game
				while(true)
				{
					//TODO afhandeling van het spel
					
				}
				
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			try
			{
				//initialisation dataStream for player1
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				
				//initialisation dataStream for player2
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				toPlayer2 = new DataOutputStream(player2.getOutputStream());
				
				//playing the game
				while(true)
				{
					//TODO afhandeling van het spel
					
				}
				
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
