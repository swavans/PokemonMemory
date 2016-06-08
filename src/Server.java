
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
			// setup server
			ServerSocket server = new ServerSocket(8000);

			while (true)
			{
				// accept player1
				Socket player1 = server.accept();
				// accept player2
				Socket player2 = server.accept();

				// setup class which handle a who is it game
				HandleAGame game = new HandleAGame(player1, player2);

				// setup thread
				new Thread(game).start();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	class HandleAGame implements Runnable
	{
		private Socket player1;
		private Socket player2;

		// setup dataStream player1 and player2
		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private ObjectInputStream fromPlayer1Obj;
		private ObjectOutputStream toPlayer1Obj;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;
		private ObjectInputStream fromPlayer2Obj;
		private ObjectOutputStream toPlayer2Obj;

		private Pokemon player1P = null;
		private Pokemon player2P = null;
		private int turn = 0;
		
		private int gameS1 =0;
		private int gameS2 = 0;

		// Constructor
		public HandleAGame(Socket player1, Socket player2)
		{
			this.player1 = player1;
			this.player2 = player2;
			
			// initialisation dataStream for player1
			try
			{
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				toPlayer1Obj = new ObjectOutputStream(player1.getOutputStream());
				fromPlayer1Obj = new ObjectInputStream(player1.getInputStream());
				toPlayer1.writeUTF("Player1");
				// initialisation dataStream for player2
				toPlayer2 = new DataOutputStream(player2.getOutputStream());
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				toPlayer2Obj = new ObjectOutputStream(player2.getOutputStream());
				fromPlayer2Obj = new ObjectInputStream(player2.getInputStream());
				toPlayer2.writeUTF("Player2");

			}
			catch(Exception e)
			{
				System.exit(0);
			}

		}

		@Override
		public void run()
		{
			try
			{
				// playing the game
				while (true)
				{
					if(player1P == null || player2P == null)
					{
						if (player1P == null)
						{
							try
							{
								player1P = (Pokemon) fromPlayer1Obj.readObject();
							
							} catch (ClassNotFoundException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally
							{
								//System.out.println("p1: " + player1P.toString());
							}
						}
						if (player2P == null)
						{
							try
							{
								player2P = (Pokemon) fromPlayer2Obj.readObject();
							} catch (ClassNotFoundException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally
							{
								//System.out.println("p2: " + player2P.toString());
							}
						}
					}
					
					else
					{

						turn++;
						if (turn % 2 == 1)
						{
							String question = null;
							// set turn true player 1
							toPlayer1.writeBoolean(true);
							
							
							//System.out.println("send turn p1");
							while (question == null)
							{
								question = fromPlayer1.readUTF();
								
							}
							//System.out.println(question);
							if (question != null)
							{
								toPlayer1.writeBoolean(checkAwnser(question, player2P));
								//System.out.println(checkAwnser(question, player2P));
							}
							
							
						} else
						{
							String question = null;
							// set turn true player 1
							toPlayer2.writeBoolean(true);
							
							
							while (question == null)
							{
								question = fromPlayer2.readUTF();
							}
							if (question != null)
							{
								toPlayer2.writeBoolean(checkAwnser(question, player1P));

							}
							
							
						}
					}

				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private boolean checkAwnser(String question, Pokemon p)
		{
			for (String t : p.getTypes())
			{
				if (t.equals(question))
				{
					return true;
				}
			}
			if (question.equals(p.getName()) || question.equals(p.getColor())
					|| question.equals(p.getGeneration() + ""))
			{
				return true;
			} else
			{
				return false;
			}
		}
	}
}
