import java.util.ArrayList;

/**
 * Created by Evgenij on 16.11.2016.
 */
public class Singleton {
    private ArrayList<ImagePanel> images = new ArrayList<>();
    private static Singleton singleton;

    private Singleton() {
    }

    public static Singleton getInstance(){
        if (singleton==null){
            singleton = new Singleton();
        }
        return singleton;
    }

    public ArrayList<ImagePanel> getImages(){
        return images;
    }
}
