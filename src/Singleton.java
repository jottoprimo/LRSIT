import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Evgenij on 16.11.2016.
 */
public class Singleton implements Serializable {


    private ArrayList<House> houses = new ArrayList<>();
    private static Singleton singleton;

    private Singleton() {
    }

    public static Singleton getInstance(){
        if (singleton==null){
            singleton = new Singleton();
        }
        return singleton;
    }

    public ArrayList<House> getHouses(){
        return houses;
    }

    public void setHouses(ArrayList<House> houses) {
        this.houses = houses;
    }
}
