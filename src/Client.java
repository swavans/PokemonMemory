
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Client extends JFrame implements Runnable, Serializable
{
	private String host = "localhost";

	private DataInputStream fromServer;
	private DataOutputStream toServer;
	private ObjectInputStream fromServerObj;
	private ObjectOutputStream toServerObj;
	private String currentQuestionItem;

	JPanel contentPane;
	JPanel pokePane;
	JPanel buttonPane;
	JPanel questionPane;

	private Pokemon pokemonChoose = null;

	private volatile ArrayList<Pokemon> pokemons = new ArrayList();
	private ArrayList<ArrayList> allQuestions = new ArrayList<>();
	private ArrayList<String> types;
	private ArrayList<String> types2;
	private ArrayList<String> colors;
	private ArrayList<String> generations;
	private ArrayList<String> pokeNames;
	private int selectedQuestionNumber;

	int i = 0;

	private boolean myTurn = false;
	private volatile int gameState = 0;
	private Thread thread;
	protected volatile boolean sendAnswer = false;
	private volatile int gameStateP2 = 0;

	// de geselecteerde vraag die wordt verstuurd
	String selectQuestion = "";

	// setup for questionspane
	JComboBox<String> vragen;

	public static void main(String[] args)
	{
		new Client();

	}
	public boolean getAnswer(){
		return sendAnswer;
	}
	public Client()
	{
		// initialisation JFrame
		super("Player");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// setup JPanels
		contentPane = new JPanel(new BorderLayout());

		pokePane = new JPanel(new GridLayout(5, 3));
		buttonPane = new JPanel(new FlowLayout());
		questionPane = new JPanel(new GridLayout(2, 1));
		questionPane.setPreferredSize(new Dimension(400, 600));

		contentPane.add(pokePane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		contentPane.add(questionPane, BorderLayout.EAST);

		connect();

		// setup data pokemon load
		File f = new File("Files");
		fill(f);

		// method to add the buttons
		choosePokemon();
		buttons();
		questions();

		// initialisation JFrame
		setContentPane(contentPane);
		setVisible(true);
		setResizable(false);
		setSize(1024, 600);
		setLocationRelativeTo(null);

	}

	@Override
	public void run()
	{
		
		while(gameState == 0)
		{
			if(getAnswer())
			{
				try
				{
					gameStateP2 = fromServer.readInt();
				} catch (IOException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				System.out.println("sA");
				try
				{
					procesAnswer(fromServer.readBoolean());
					if(pokemons.size() == 1)
					{
						gameState = 1;
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendAnswer =false;
				myTurn = false;
			}
			
			try
			{
				toServer.writeInt(gameState);
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(gameState == 1 || gameStateP2 == 2)
		{
			System.out.println("WINNER");
			
//			try
//			{
//				toServer.writeInt(gameState);
//			} catch (IOException e1)
//			{
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
		}
		if(gameState == 2 || gameStateP2 == 1)
		{
			System.out.println("LOSE");
			
//			try
//			{
//				toServer.writeInt(gameState);
//			} catch (IOException e1)
//			{
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		}
	}

	private void connect()
	{
		Socket socket;
		try
		{
			socket = new Socket(host, 8000);

			// setup dataStream to and from server
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServerObj = new ObjectInputStream(socket.getInputStream());
			toServerObj = new ObjectOutputStream(socket.getOutputStream());
			this.setTitle(fromServer.readUTF());

		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		thread = new Thread(this);
		thread.start();

	}

	private void choosePokemon()
	{
		// making the grid with buttons
		for (int x = 0; x < 3; x++)
		{
			for (int y = 0; y < 5; y++)
			{
				if (i < 13)
				{
					Pokemon p = pokemons.get(i);
					Image img = null;
					try
					{
						img = ImageIO.read(new File("Data" + "\\" + pokemons.get(i).getName() + ".png"));
					} catch (IOException e2)
					{
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					JButton b = new JButton(new ImageIcon(img.getScaledInstance(70, 100, Image.SCALE_SMOOTH)));
					pokePane.add(b);
					b.addActionListener(new ActionListener()
					{

						@Override
						public void actionPerformed(ActionEvent e)
						{
							pokemonChoose = p;
							try
							{
								toServerObj.writeObject(pokemonChoose);
							} catch (IOException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});

					i++;
				}

			}
		}
	}

	// aanmaken van de buttons met hun listener
	private void buttons()
	{
		// setup send, quit, new game buttons

		JButton send = new JButton("Transfer Pokemon");
		send.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					myTurn = fromServer.readBoolean();
					System.out.println("LOLZ");
				} catch (IOException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (myTurn)
				{

					try
					{
						sendAnswer = true;
						System.out.println("TRUE");
						toServer.writeUTF(selectQuestion);

					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});

		JButton quit = new JButton("QUIT");
		quit.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);

			}
		});

		JButton ngame = new JButton("NEW GAME");
		ngame.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		// add buttons to his JPanel
		buttonPane.add(ngame);
		buttonPane.add(quit);
		buttonPane.add(send);
	}

	private void questions()
	{
		// setup questionsbox
		vragen = new JComboBox<String>();
		vragen.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
			selectedQuestionNumber = vragen.getSelectedIndex();
				if (allQuestions.size() > 0)
				{
					selectQuestion = (String) allQuestions.get(selectedQuestionNumber).get(1);

				}
			}
		});
		questionPane.add(vragen);

		// setup dialog

		randomFillCombo();
	}

	// het vullen van de combobox met de vragen die worden getoond
	private void randomFillCombo()
	{
		

		allQuestions.clear();
		vragen.removeAllItems();
		for (int i = 0; i < 5; i++)
		{
			int g = (int) (Math.random() * 4);
			switch (g)
			{
			// names
			case 0:
				if (pokeNames.size() > 0)
				{
					String name = (String) pokeNames.get((int) (Math.random() * (pokeNames.size())));

					ArrayList<String> question = new ArrayList<>();
					question.add("Is deze Pokemon ");
					question.add(name);
					question.add("name");
					allQuestions.add(question);
				} break;
				// types
			case 1:
				if (types.size() > 0)
				{
					String type = (String) types.get((int) (Math.random() * (types.size() - 1)));
					ArrayList<String> question2 = new ArrayList<>();
					question2.add("Is het type van de Pokemon ");
					question2.add(type);
					question2.add("type");
					allQuestions.add(question2);
				} 
				break;
				// colors
			case 2:
				if (colors.size() > 0)
				{
					String color = (String) colors.get((int) (Math.random() * (colors.size() - 1)));
					ArrayList<String> question3 = new ArrayList<>();
					question3.add("Is de kleur van deze pokemon ");
					question3.add(color);
					question3.add("color");
					allQuestions.add(question3);
				} 
				break;
				// generations
			case 3:
				if (generations.size() > 0)
				{
					String gen = (String) generations.get((int) (Math.random() * (generations.size() - 1)));
					ArrayList<String> question4 = new ArrayList<>();
					question4.add("Is deze pokemon van generatie ");
					question4.add(gen);
					question4.add("generation");
					allQuestions.add(question4);
				} 
				break;
			}
		}
		for (ArrayList<String> list : allQuestions)
		{
			String vraag = "";
			for (int p = 0; p < list.size() - 1; p++)
			{
				vraag = vraag + list.get(p);
			}
			vraag = vraag + " ?";
			vragen.addItem(vraag);
		}
		 selectQuestion = vragen.getItemAt(0);
	}

	// ophalen van alle .pkb files en deze omzetten naar Pokemon objects
	private void fill(File file)
	{
		Set<String> pNames = new HashSet<>();
		Set<String> pTypes = new HashSet<>();
		Set<String> pGen = new HashSet<>();
		Set<String> pColor = new HashSet<>();
		// search .pkb files
		if (file.exists())
		{
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				for (int i = 0; files != null && i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						fill(files[i]);
					}
					if (getFileExtension(files[i]).equals("pkb"))
					{
						// TODO
						Pokemon p = new Pokemon(files[i].getName());
						pokemons.add(p);

						pNames.add(p.getName());

						for (String type : p.getTypes())
						{
							pTypes.add(type);
						}

						pColor.add(p.getColor());

						pGen.add(p.getGeneration() + "");

					}
				}
			}
		}
		Collections.sort(pokemons, Pokemon.GenComparator);
		pokeNames = new ArrayList<>(pNames);
		types = new ArrayList<>(pTypes);
		types2 = new ArrayList<>(types);
		generations = new ArrayList<>(pGen);
		colors = new ArrayList<>(pColor);

	}

	// opvragen wat de extension is van een object
	public String getFileExtension(File file)
	{
		// search fileextension of the file
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	public void procesAnswer(boolean answer)
	{
		switch ((String) allQuestions.get(selectedQuestionNumber).get(2))
		{
		case ("name"):
			if (answer)
			{
				procesAnswerTrue(pokeNames);
			} else
				procesAnswerFalse(pokeNames);
			break;
		case ("type"):
			if (answer)
			{
				procesAnswerTrue(types);
			} else
				procesAnswerFalse(types);
			break;
		case ("generation"):
			if (answer)
			{
				procesAnswerTrue(generations);
			} else
				procesAnswerFalse(generations);
			break;
		case "color":
			if (answer)
			{
				procesAnswerTrue(colors);
			} else
				procesAnswerFalse(colors);
			break;
		}
		randomFillCombo();
		createNewPokemonList(answer);
		generateList();
		// if(pokeNames.size()>0 || types.size()>0||
		// generations.size()>0||colors.size()>0 )
		}

	private void procesAnswerTrue(ArrayList<String> list)
	{
		list.clear();
		list.add(selectQuestion);
	}

	private void procesAnswerFalse(ArrayList<String> list)
	{
		Iterator<String> itr = list.iterator();
		while (itr.hasNext())
		{
			String s = itr.next();
			if (s.equals(selectQuestion))
			{
				itr.remove();
			}
		}
	}

	private void createNewPokemonList(boolean answer)
	{
		String iets = selectQuestion;
		Iterator<Pokemon> pokeitr = pokemons.iterator();

		while (pokeitr.hasNext())
		{
			Pokemon p = pokeitr.next();

			if (!(pokeNames.contains(p.getName())))
			{
				pokeitr.remove();
			}

			else if (!(generations.contains((p.getGeneration() + ""))))
			{
				pokeitr.remove();
			}

			else if (!(colors.contains(p.getColor())))
			{
				pokeitr.remove();
			}

			if (types2.contains(selectQuestion))
			{
				ArrayList<String> t3 = new ArrayList<>(types);
				if ((!(p.getTypes().contains(selectQuestion))) && answer)
					pokeitr.remove();
				else if (p.getTypes().contains(selectQuestion) && !answer)
					pokeitr.remove();
			}

		}

	}

	private void generateList()
	{
		Set<String> pNames = new HashSet<>();
		Set<String> pTypes = new HashSet<>();
		Set<String> pGen = new HashSet<>();
		Set<String> pColor = new HashSet<>();
		for (Pokemon p : pokemons)
		{
			pNames.add(p.getName());

			for (String type : p.getTypes())
			{
				pTypes.add(type);
			}

			pColor.add(p.getColor());

			pGen.add(p.getGeneration() + "");
		}
		pokeNames = new ArrayList<>(pNames);
		types = new ArrayList<>(pTypes);
		types2 = new ArrayList<>(types);
		generations = new ArrayList<>(pGen);
		colors = new ArrayList<>(pColor);

	}
}
