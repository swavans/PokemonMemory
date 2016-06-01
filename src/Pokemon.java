

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;

public class Pokemon implements Comparable<String>, Serializable
{
	public static void main(String[] args)
	{
		
		try(BufferedReader br = new BufferedReader(new FileReader("Data//Pokemon.txt"))) {

			String sCurrentLine;


			while ((sCurrentLine = br.readLine()) != null) {
				String[] addPokemon = sCurrentLine.split(" ");
				String[] types = addPokemon[1].split(",");
				ArrayList<String> pTypes = new ArrayList<>();
				for(int i = 0; i<types.length;i++)
					pTypes.add(types[i]);
				new Pokemon(addPokemon[0], Integer.parseInt(addPokemon[2]), addPokemon[4], pTypes);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private ArrayList<String> types;
    private String name;
    private int generation;
    private String color;

    //Constructor of the class Pokemon
    public Pokemon(String name, int generation, String color, ArrayList<String> types)
    {
        this.types = types;
        this.name = name;
        this.generation = generation;
        this.color = color;
        capturePokemon();
    }

    public Pokemon(String pokemonName)
    {
        try{
            Pokemon temp = load(pokemonName);
            this.types = temp.getTypes();
            this.name = temp.getName();
            this.generation = temp.getGeneration();
            this.color = temp.getColor();
        }
        catch(Exception e){}

    }

    //method to add a type to the list of types
    public void addType(String type)
    {
        types.add(type);
    }

    //method which returns the arraylist with types 
    public ArrayList<String> getTypes()
    {
        return types;
    }

    //method which returns the pokemon's name
    public String getName()
    {
        return name;
    }

    //method which returns the pokemon's generation
    public int getGeneration()
    {
        return generation;
    }

    //method which returns the pokemon's color
    public String getColor()
    {
        return color;
    }

    @Override
    public int compareTo(String t)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void capturePokemon()throws FileSystemNotFoundException{
        try {
            FileOutputStream fos = new FileOutputStream("Files/"+ name+ ".pkb");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            System.out.println(this.getName() + " has been stored at Bill's PC");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Pokemon load(String pokemon)throws FileNotFoundException{
        try{
            FileInputStream fis = new FileInputStream("Files/"+ pokemon);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Object object = ois.readObject();
            ois.close();
            if(object instanceof Pokemon){
                System.out.println(pokemon +" has been caught succesfully");
                Pokemon temp = (Pokemon) object;

                return temp;
            }
            return null;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Comparator<Pokemon> GenComparator = new Comparator<Pokemon>() 
    {

		public int compare(Pokemon p1, Pokemon p2) 
		{
		
			int g1 = p1.getGeneration();
			int g2 = p2.getGeneration();
			
			return g1 - g2;
		}

    };
    
    public String toString()
    {
    	return (this.getName() + " "+ this.getColor()+ " " + this.getGeneration()+ " "+ this.getTypes().toString()); 
    }

	
}
