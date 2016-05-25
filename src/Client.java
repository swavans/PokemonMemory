

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Client extends JFrame implements Runnable, Serializable
{
	private String host = "localhost";
	
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	JPanel contentPane;
	JPanel pokePane;
	JPanel buttonPane;
	JPanel questionPane;
	
	private String pokemonChoose = "";
	
	private ArrayList<Pokemon> pokemons = new ArrayList();
	private ArrayList<String> questions = new ArrayList();
	private Set<String> types;
	private Set<String> colors;
	private Set<Integer> generations;
	
	int i = 0;
	
	String selectQuestion = "";
	
	//setup for questionspane
	JComboBox<String> vragen;
	JTextArea input;
	
	public static void main(String[] args)
	{
		new Client();

	}
	
	public Client()
	{
		//initialisation JFrame
		super("Player");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//setup JPanels
		contentPane = new JPanel(new BorderLayout());
		
		pokePane = new JPanel(new GridLayout(5, 3));
		buttonPane = new JPanel(new FlowLayout());
		questionPane = new JPanel(new GridLayout(2, 1));
		questionPane.setPreferredSize(new Dimension(400, 768));
		
		contentPane.add(pokePane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		contentPane.add(questionPane, BorderLayout.EAST);
		
		//setup data pokemon load
		File f = new File("Files");
		fill(f);
		
		//method to add the buttons 
		choosePokemon();
		buttons();
		questions();
		
		//initialisation JFrame
		setContentPane(contentPane);
		setVisible(true);
		setResizable(false);
		setSize(1024,768);
		setLocationRelativeTo(null);
		
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
	private void connect()
	{
		Socket socket;
		try
		{
			socket = new Socket(host, 8000);
			
			//setup dataStream to and from server
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
			
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void choosePokemon()
	{
		//making the grid with buttons
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 5; y++)
			{
				JButton b = new JButton(new ImageIcon("Data" + "\\" + pokemons.get(i).getName() + ".png"));
				pokePane.add(b);
				b.addActionListener(new ActionListener()
				{
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						pokemonChoose = pokemons.get(i).getName();
					}
				});
				
				i++;
			}
		}
	}
	
	private void buttons()
	{
		//setup send, quit, new game buttons
		
		JButton send = new JButton("SEND");
		send.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					//send the question to the server
					toServer.writeUTF(selectQuestion);
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				randomFillCombo();
				
			}
		});
		
		JButton quit = new JButton("QUIT");
		quit.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// TODO Auto-generated method stub
				
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
		
		//add buttons to his JPanel
		buttonPane.add(ngame);
		buttonPane.add(quit);
		buttonPane.add(send);
	}
	
	private void questions()
	{
		//setup questionsbox
		vragen = new JComboBox<String>();
		vragen.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				selectQuestion = (String) vragen.getSelectedItem();
				
			}
		});
		questionPane.add(vragen);
		
		//setup dialog
		input = new JTextArea();
		questionPane.add(input);
		
		fillCombobox();
		randomFillCombo();
	}
	
	private void fillCombobox()
	{
		//setup the questions
		for(String t : types)
		{
			String q = "Is het type van deze pokemon.. " + t + "?";
			questions.add(q);
		}
		for(String t : colors)
		{
			String q = "Is de kleur van deze pokemon.. " + t + "?";
			questions.add(q);
		}
		for(Integer t : generations)
		{
			String q = "Komt deze pokemon uit generations: " + t + "?";
			questions.add(q);
		}
		for(Pokemon p : pokemons)
		{
			String n = p.getName();
			String q = "Is deze pokemon: " + n + "?";
			questions.add(q);
		}
	}
	
	private void randomFillCombo()
	{
		//random questions to the JCombobox
		vragen.removeAll();
		for(int i = 0; i<5; i++)
		{
			int g = (int) Math.random() * 27; 
			vragen.addItem(questions.get(g));
			
			selectQuestion = vragen.getItemAt(0);
		}
	}
	
	private void fill(File file)
	{
		System.out.println("search pkb files");
		//search .pkb files 
		if(file.exists())
		{
			System.out.println(file);
			if(file.isDirectory())
			{
				System.out.println(file);
				File[] files = file.listFiles();
				for(int i = 0; files != null && i < files.length; i++)
				{
					if(files[i].isDirectory())
					{
						fill(files[i]);
					}
					if(getFileExtension(files[i]).equals("pkb"))
					{
						//TODO
						Pokemon p = new Pokemon(files[i].getName());
						pokemons.add(p);
						
						for(String type : p.getTypes())
						{
							types.add(type);
						}
						
						colors.add(p.getColor());
						
						generations.add(p.getGeneration());
						
					}
				}
			}
		}
	}
	
	public String getFileExtension(File file) 
	{
		//search fileextension of the file
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
	
	
	
}
