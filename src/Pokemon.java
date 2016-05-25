

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;

public class Pokemon implements Comparable<String>, Serializable
{
	public static void main(String[] args)
	{
		new Pokemon("Pikachu", 1, "Yellow", new ArrayList<String>());
		new Pokemon ("Pikachu.pkb");

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
}
